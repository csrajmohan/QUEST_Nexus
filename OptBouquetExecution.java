package quest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Vector;

public class OptBouquetExecution 
{
	boolean time_limited_exec=true;
	int totalPlanExecution=0;
	long executionTime;
	
	int contPlans[][];
	int steps;
	int contourPlanCount[];
	double cost_limit[];
	double timeLimit[];
	int totalPlans;
	int finalPlanSet[];
	int dimension;
	int resolution;
	int contLocation[];
	String errorProneBaseRelation[];
	
	String queryStringForPlans[];
	
	//Holds tuples found in base relation through partial execution. 
	int foundTuples[];
	
	//Current Location in ESS Space.
	int currentLocation[];
	
	boolean changeLocation;
	
	//Used in moving in axis plan direction.
	int axisPlanLocation[][]; 
	
	//Hold the sequence in which axis plans are found.
	int contourFound[];
	
	//Holds max tuple of error prone base relations
	int maxTuples[];
	
	//Check List to hold which plan is executed on contour
	int planExecutionCheckList[][];
	
	//contour found. this will be greater then or equal to currentContour.
	int contour;
	
	int selectivityLocation[];
	
	double subOptimality;
	/*
	 * following variables are use in single plan execution
	 */
	int currentPlanNumber;
	int currentContour;
	boolean contourChanged;
	
	ProcessPartialExecutionPlan planStateObj;
	public OptBouquetExecution(AllObjects allObjects)
	{
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		
		contPlans = bouquetDataObj.getContourPlans();
		steps = bouquetDataObj.getTotalContours();
		contourPlanCount = bouquetDataObj.getContourPlansCount();
		cost_limit = bouquetDataObj.getCostLimit();
		timeLimit = bouquetDataObj.getTimeLimit();
		totalPlans = bouquetDataObj.getTotalPlans();
		finalPlanSet = bouquetDataObj.getFinalPlanSet();
		
		dimension = bouquetDataObj.getDimension();
		resolution = bouquetDataObj.getResolution();
		contLocation = bouquetDataObj.getContourLocationInESS();
		
		changeLocation = true;
		
		//Holds name of base relation those have error prone base selectivity.
		errorProneBaseRelation=bouquetDataObj.getErrorProneBaseRelationNames();
		
		PostgresRun pgRunObj = allObjects.getPostgresRunObj();
		queryStringForPlans = bouquetDataObj.queryStringForPlans;

		foundTuples= new int[dimension];
		Arrays.fill(foundTuples, 0);
		
		currentLocation = new int[dimension];
		Arrays.fill(currentLocation, 0);
		
		axisPlanLocation= new int[dimension][dimension]; 
		
		contourFound = new int[dimension];
		
	
		maxTuples = new int[dimension];//bouquetDataObj.getMaxTuplesForErrorProneBaseRelation();// 
		
		planExecutionCheckList = new int[steps][totalPlans];
		for(int i=0;i<steps;i++)
		{
			for(int j=0;j<totalPlans;j++)
			{
				planExecutionCheckList[i][j]=0;
			}
		}
		contour = 0;
		currentContour = 1;
		contourChanged = true;
		selectivityLocation = new int[dimension];
		Arrays.fill(selectivityLocation, 0);
		
		planStateObj = new ProcessPartialExecutionPlan();
	}
	
	void runOptBouquet(AllObjects allObjects)
	{
		int lastContour = -1;
		boolean contourChanged= false;
		boolean isSimulation = false;
		ExecutionInformationPanel execInfoPanelObj = allObjects.getExecutionInformationPanelObj();
		DrawContours drawContoursObj = allObjects.getDrawContoursObj();
		
		BouquetDriver bouquetDriverObj = allObjects.getBouquetDriverObj();
		
		Vector<String> textualPlan = new Vector<String>();
		
		int contourFindCount=1;
			
		//Selectivity movement.
		double selectivity[] = new double[dimension];
	
		int nextDimension[] = new int[dimension];
		
		//desired contour from currentLocation  

		//Axis Plan found
		int optPlan=0;


		long startTime=0,endTime=0;
		
		Arrays.fill(selectivity, 0.0);
		drawContoursObj.path(0, 0, steps);
		try 
		{
			ConnectDB connectDBObj = allObjects.getConnectDBObj();
			Connection con = connectDBObj.connection;
			Statement st = con.createStatement();
			ResultSet rs=null;
			for(int i=0;i<dimension;i++)
			{
				String query = "explain select * from "+errorProneBaseRelation[i];
				rs = st.executeQuery(query);
				rs.next();

				String PlanString = rs.getString(1);
				int startIndex = PlanString.indexOf("rows="); 
				int lastIndex = PlanString.indexOf(32, startIndex);
				String tuples = PlanString.substring(startIndex+5, lastIndex);
				maxTuples[i] = Integer.parseInt(tuples);
			}
		
			startTime = System.currentTimeMillis();
			while(true)
			{
				if(changeLocation)
				{
					Arrays.fill(contourFound, 0);
					for(int i=0;i<dimension;i++)
					{
						for(int j=0;j<dimension;j++)
						{
//							if(i==j)
//								axisPlanLocation[i][j] = currentLocation[j]+1;
//							else
								axisPlanLocation[i][j] = currentLocation[j];
						}
					}
					/*
					 * this array holds value of dimension which will be 
					 */
					for(int i=0;i<dimension;i++)
					{
						nextDimension[i]=i;
					}

					while(true)
					{
						int i;						
						for(i=0;i<dimension;i++)
						{
							if(contourFound[i] == 0)
							{
								int loc = addressCalc(resolution, axisPlanLocation[i], dimension);
								if(contLocation[loc]>=currentContour)
								{
									contourFound[i]=contourFindCount;
									contourFindCount++;
								}
								else if(axisPlanLocation[i][nextDimension[i]] < resolution -1)
								{
									axisPlanLocation[i][nextDimension[i]]++;
								}

								else
								{
									if(contourFound[i]==0)
									{
										if(nextDimension[i]==i)
										{
											nextDimension[i]=0;
											if(nextDimension[i]==i)
											{
												nextDimension[i]++;
											}
										}
										else
										{
											nextDimension[i]++;
											if(nextDimension[i]==i)
											{
												nextDimension[i]++;
											}
										}
									}
								}
							}
						}
						
						
						for(i=0;i<dimension;i++)
						{
							if(contourFound[i]==0)
								break;
						}
						if(i==dimension)
							break;
					}
					for(int i=0;i<dimension;i++)
					{
						int min = axisPlanLocation[0][i];
						for(int j=1;j<dimension;j++)
						{
							if(min > axisPlanLocation[j][i])
								min = axisPlanLocation[j][i];
						}
						currentLocation[i] = min;
					}
				}
				int index = maxInArray(contourFound);
			
				
				String planToExec=null;
				
				if(index != -1)
				{
					int loc = addressCalc(resolution, axisPlanLocation[index], dimension);
					contour = contLocation[loc];
					currentContour = contour;
					optPlan = bouquetDriverObj.getOptimalPlan(loc, finalPlanSet);
				}
				else
				{
					int j;
					for(j=0;j<contourPlanCount[contour-1];j++)
					{
						int plan = contPlans[contour-1][j];
						
						int planIndex = Arrays.binarySearch(finalPlanSet, 0, totalPlans, plan);
						if(planExecutionCheckList[contour-1][planIndex]==0)
						{
							optPlan = planIndex;
							if(cost_limit[contour-1] > bouquetDriverObj.AllPlanCosts[plan][addressCalc(resolution, currentLocation, dimension)])
								break;
						}
						changeLocation = false;
					}
					if(j==contourPlanCount[contour-1])
					{
						changeLocation = true;
						currentContour++;
						continue;
					}
				}


				planToExec = queryStringForPlans[optPlan];

				if(planExecutionCheckList[contour-1][optPlan]==0)
				{
					long planStartTime, planEndTime;
					
					int contourIndex = contour - 1;
					if(contourIndex != steps -1)
					{
						if(time_limited_exec)
						{
							double currentTimeLimit = timeLimit[contour-1];
							st.executeUpdate("set time_limit = "+currentTimeLimit);
						}
						else
						{
							double bounded_cost = cost_limit[contour-1];
							st.executeUpdate("set limit_cost = "+bounded_cost);
						}
					}

					System.out.println("contour="+contour);
					System.out.println("optPlan="+finalPlanSet[optPlan]);

					planExecutionCheckList[contour-1][optPlan] = 1;
					
					if(lastContour != contour-1)
					{
						contourChanged = true;
						lastContour = contour - 1;
					}
					else
						contourChanged = false;
					execInfoPanelObj.addPlanTree(optPlan, contour-1, cost_limit[contour-1], contourChanged);
					
					System.out.println("OPT BOUQUET EXEC: "+planToExec);
					planStartTime = System.currentTimeMillis();
					rs = st.executeQuery(planToExec);
					planEndTime = System.currentTimeMillis();
					
					drawContoursObj.changeContourColor(contour-1, optPlan, totalPlans,finalPlanSet);
					executionTime += planEndTime - planStartTime;
					String str=null;
					while(rs.next())
					{
						str = rs.getString(1);
						textualPlan.add(str);
						if(str.contains("Seq Scan") || str.contains("Index Scan") || str.contains("Bitmap Heap Scan"))
						{
							int i;
							for(i=0;i<dimension;i++)
							{
								if(str.contains(errorProneBaseRelation[i]))
									break;
							}
							if(i<dimension)
							{

								if(!str.contains("never executed"))
								{
									int startIndex = str.lastIndexOf("rows=");
									int lastIndex = str.indexOf(32,startIndex);
									int tuples = Integer.parseInt(str.substring(startIndex+5, lastIndex));
									int loopCount = Integer.parseInt(str.substring(lastIndex+7, str.length()-1));
									if(loopCount == 1 && foundTuples[i] < tuples)
										foundTuples[i] = tuples;
								}
							}
						}
					}
					textualPlan.remove(textualPlan.size()-1);
					textualPlan.remove(textualPlan.size()-1);
					execInfoPanelObj.neverExecNodeTree[contour-1][optPlan] = planStateObj.createStatePlanTree(textualPlan);
					for(int i =0;i<dimension;i++)
					{
						selectivity[i] = ((double)foundTuples[i]/maxTuples[i])*100.0;
					}
					int newLocation[] = selLocationToESSLocation(selectivity, resolution, dimension, bouquetDriverObj);

					int i;
					changeLocation = false;
					for(i=0;i<dimension;i++)
					{
						if(newLocation[i]>currentLocation[i])
						{
							currentLocation[i] = newLocation[i];
							changeLocation=true;
						}
						
						if(newLocation[i] > selectivityLocation[i])
						{
							selectivityLocation[i] = newLocation[i];
						}
					}
					drawContoursObj.path(selectivityLocation[0], selectivityLocation[1], steps);
					double execTime = (planEndTime - planStartTime)/1000.0;
					
					double totalTime = executionTime/1000.0;
					totalTime *= 10;
					totalTime = Math.round(totalTime);
					totalTime /= 10;
					
					totalPlanExecution++;
					
					execInfoPanelObj.addPostExecutionInfo(optPlan, contour-1,execTime, null,isSimulation);
					
					if(!str.contains("Not Completed"))
					{
						System.out.println("Execution Completed");
						PerformanceComparisionPanel executionResultPanelObj = allObjects.getExecutionResultPanelObj();
						double execTimeInSec = executionTime/1000.0;
						executionResultPanelObj.setOptimisedBouquetTime(execTimeInSec, allObjects);
						
						double optimalPlanExecTime = executionResultPanelObj.executionTime[1];
						subOptimality = execTimeInSec / optimalPlanExecTime;
						execInfoPanelObj.addBouquetExecutionFinishedInformation(execTimeInSec, totalPlanExecution);
						break;
					}
			
				}
				else
				{
					changeLocation = false;
				}
			}
			rs.close();
			st.close();
		
		}
		catch(Exception e)
		{
			System.out.println("Execption in runOptBouquet:"+e);
			e.printStackTrace();
		}
		
		endTime = System.currentTimeMillis();
	}
	
	void preprocessOptBouquetSinglePlanRun(AllObjects allObjects)
	{
		try
		{
			ConnectDB connectDBObj = allObjects.getConnectDBObj();
			Connection con = connectDBObj.connection;
			Statement st = con.createStatement();
			ResultSet rs=null;
			for(int i=0;i<dimension;i++)
			{
				String query = "explain select * from "+errorProneBaseRelation[i];
				rs = st.executeQuery(query);
				rs.next();

				String PlanString = rs.getString(1);
				int startIndex = PlanString.indexOf("rows="); 
				int lastIndex = PlanString.indexOf(32, startIndex);
				String tuples = PlanString.substring(startIndex+5, lastIndex);
				maxTuples[i] = Integer.parseInt(tuples);
			}
		}
		catch(Exception e)
		{
			System.out.println("Exeception in Opt Bouquet single Run: "+e);
			e.printStackTrace();
		}
	}
	
	
	boolean runOptBouquetSinglePlan(AllObjects allObjects)
	{
		boolean isSimulation = false;
		boolean executionCompleted = false;
		ExecutionInformationPanel execInfoPanelObj = allObjects.getExecutionInformationPanelObj();
		DrawContours drawContoursObj = allObjects.getDrawContoursObj();
		
		BouquetDriver bouquetDriverObj = allObjects.getBouquetDriverObj();
		
		Vector<String> textualPlan = new Vector<String>();
		
		int contourFindCount=1;
		
		//Selectivity movement.
		double selectivity[] = new double[dimension];
		
		int nextDimension[] = new int[dimension];
		
		//Axis Plan found
		int optPlan=0;
		
		Arrays.fill(selectivity, 0.0);
		drawContoursObj.path(0, 0, steps);

		boolean breakLoop = false;
		while(true)
		{
			if(changeLocation)
			{
				Arrays.fill(contourFound, 0);
				for(int i=0;i<dimension;i++)
				{
					for(int j=0;j<dimension;j++)
					{
						axisPlanLocation[i][j] = currentLocation[j];
					}
				}
				for(int i=0;i<dimension;i++)
				{
					nextDimension[i]=i;
				}

				while(true)
				{
					int i;
					for(i=0;i<dimension;i++)
					{
						if(axisPlanLocation[i][nextDimension[i]] < resolution-1 && contourFound[i] == 0)
						{
							axisPlanLocation[i][nextDimension[i]]++;

							int loc = addressCalc(resolution, axisPlanLocation[i], dimension);
							if(contLocation[loc] >= currentContour)
							{
								contourFound[i]=contourFindCount;
								contourFindCount++;
							}
						}
						else
						{
							if(contourFound[i]==0)
							{
								if(nextDimension[i]==i)
								{
									nextDimension[i]=0;
									if(nextDimension[i]==i)
										nextDimension[i]++;
								}
								else
								{
									nextDimension[i]++;
									if(nextDimension[i]==i)
									{
										nextDimension[i]++;
									}
								}
							}
						}
					}


					for(i=0;i<dimension;i++)
					{
						if(contourFound[i]==0)
							break;
					}
					if(i==dimension)
						break;
				}
				for(int i=0;i<dimension;i++)
				{
					int min = axisPlanLocation[0][i];
					for(int j=1;j<dimension;j++)
					{
						if(min > axisPlanLocation[j][i])
							min = axisPlanLocation[j][i];
					}
					currentLocation[i] = min;
				}
			}
			int index = maxInArray(contourFound);

			if(index != -1)
			{
				int loc = addressCalc(resolution, axisPlanLocation[index], dimension);
				contour = contLocation[loc];
				currentContour = contour;
				optPlan = bouquetDriverObj.getOptimalPlan(loc, finalPlanSet);
				breakLoop = true;
			}
			else
			{
				int j;
				for(j=0;j<contourPlanCount[contour-1];j++)
				{
					int plan = contPlans[contour-1][j];

					int planIndex = Arrays.binarySearch(finalPlanSet, 0, totalPlans, plan);
					if(planExecutionCheckList[contour-1][planIndex]==0)
					{
						optPlan = planIndex;
						if(cost_limit[contour-1] > bouquetDriverObj.AllPlanCosts[plan][addressCalc(resolution, currentLocation, dimension)])
						{
							breakLoop = true;
							break;
						}
					}
					changeLocation = false;
				}
				if(j==contourPlanCount[contour-1])
				{
					changeLocation = true;
					currentContour++;
				}
			}
			if(breakLoop)
				break;
		}

		try 
		{
			ConnectDB connectDBObj = allObjects.getConnectDBObj();
			Connection con = connectDBObj.connection;
			Statement st = con.createStatement();
			
			String planToExec = queryStringForPlans[optPlan];

			if(planExecutionCheckList[contour-1][optPlan]==0)
			{
				long planStartTime, planEndTime;
				
				if(time_limited_exec)
				{
					double currentTimeLimit = timeLimit[contour-1];
					st.executeUpdate("set time_limit = "+currentTimeLimit);
				}
				else
				{
					double bounded_cost = cost_limit[contour-1];
					st.executeUpdate("set limit_cost = "+bounded_cost);
				}
				System.out.println("contour="+contour);
				System.out.println("optPlan="+finalPlanSet[optPlan]);

				planExecutionCheckList[contour-1][optPlan] = 1;

				execInfoPanelObj.addPlanTree(optPlan, contour-1, cost_limit[contour-1], false);

				planStartTime = System.currentTimeMillis();
				ResultSet rs = st.executeQuery(planToExec);
				planEndTime = System.currentTimeMillis();

				drawContoursObj.changeContourColor(contour-1, optPlan, totalPlans,finalPlanSet);
				executionTime += planEndTime - planStartTime;
				String str=null;
				while(rs.next())
				{
					str = rs.getString(1);
					textualPlan.add(str);
					if(str.contains("Seq Scan") || str.contains("Index Scan") || str.contains("Bitmap Heap Scan"))
					{
						int i;
						for(i=0;i<dimension;i++)
						{
							if(str.contains(errorProneBaseRelation[i]))
								break;
						}
						if(i<dimension)
						{

							if(!str.contains("never executed"))
							{
								int startIndex = str.lastIndexOf("rows=");
								int lastIndex = str.indexOf(32,startIndex);
								int tuples = Integer.parseInt(str.substring(startIndex+5, lastIndex));
								int loopCount = Integer.parseInt(str.substring(lastIndex+7, str.length()-1));
								if(loopCount == 1 && foundTuples[i] < tuples)
									foundTuples[i] = tuples;
							}
						}
					}
				}
				textualPlan.remove(textualPlan.size()-1);
				textualPlan.remove(textualPlan.size()-1);
				execInfoPanelObj.neverExecNodeTree[contour-1][optPlan] = planStateObj.createStatePlanTree(textualPlan);
				for(int i =0;i<dimension;i++)
				{
					selectivity[i] = ((double)foundTuples[i]/maxTuples[i])*100.0;
				}
				int newLocation[] = selLocationToESSLocation(selectivity, resolution, dimension, bouquetDriverObj);

				int i;
				changeLocation = false;
				for(i=0;i<dimension;i++)
				{
					if(newLocation[i]>currentLocation[i])
					{
						currentLocation[i] = newLocation[i];
						changeLocation=true;
					}

					if(newLocation[i] > selectivityLocation[i])
					{
						selectivityLocation[i] = newLocation[i];
					}
				}
				drawContoursObj.path(selectivityLocation[0], selectivityLocation[1], steps);
				double execTime = (planEndTime - planStartTime)/1000.0;
//				execTime *= 10;
//				execTime = Math.round(execTime);
//				execTime /= 10;

				double totalTime = executionTime/1000.0;
				totalTime *= 10;
				totalTime = Math.round(totalTime);
				totalTime /= 10;

				execInfoPanelObj.addPostExecutionInfo(optPlan, contour-1,execTime, null, isSimulation);
				
				totalPlanExecution++;
				if(!str.contains("Not Completed"))
				{
					System.out.println("Execution Completed");
					executionCompleted = true;
					
					PerformanceComparisionPanel executionResultPanelObj = allObjects.getExecutionResultPanelObj();
					executionResultPanelObj.setOptimisedBouquetTime(executionTime/1000.0, allObjects);
					execInfoPanelObj.addBouquetExecutionFinishedInformation(executionTime/1000.0, totalPlanExecution);
				}
				rs.close();
			}
			else
			{
				changeLocation = false;
			}
			st.close();
		}
		catch(Exception e)
		{
			System.out.println("Execption in runOptBouquet:"+e);
			e.printStackTrace();
		}

		return(executionCompleted);
	}
	
	
	
	
	
	//Column major 
	int addressCalc(int resolution,int index[], int d)
	{
		for(int i=0;i<d;i++)
		{
			if(index[i]>=resolution)
			{
				System.err.println("Error: Indeces are out of range");
				System.exit(0);
			}
		}
		int address = 0;
		for(int i=d-1;i>=0;i--)
		{
			int temp = index[i];
			for(int j=i-1;j>=0;j--)
			{
				temp = temp * resolution;
			}
			address += temp;
		}
		
		return(address);
	}
	int maxInArray(int arr[])
	{
		int max=0;
		int index=-1;
		for(int i=0;i<arr.length;i++)
		{
			if(max<arr[i])
			{
				max=arr[i];
				index=i;
			}
		}
		if(index != -1)
			arr[index] = -1;
		return(index);
	}
	
	
	int minInArray(int arr[])
	{
		int min = Integer.MAX_VALUE;
		int index=-1;
		for(int i=0;i<arr.length;i++)
		{
			if(min>arr[i])
			{
				min=arr[i];
				index=i;
			}
		}
		if(index != -1)
			arr[index] = Integer.MAX_VALUE;
		return(index);
	}
	//because of exp selectivity location in ESS space.
	int[] selLocationToESSLocation(double selectivity[],int resolution, int dimension, BouquetDriver bouquetDriverObj)
	{
		int flag[] = new int [dimension];
		int loc[] = new int[dimension];
		Arrays.fill(flag, 0);
		for(int i=0;i<resolution;i++)
		{
			for(int j=0;j<dimension;j++)
			{
				if(selectivity[j] < bouquetDriverObj.picsel[i] && flag[j]==0)
				{
					loc[j] = Math.max(i-1, 0);
					flag[j] = 1;
				}
			}
		}
		return(loc);
	}
}
