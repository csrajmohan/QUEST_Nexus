package quest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Vector;

public class BasicBouquetExecution 
{
	boolean time_limited_exec=true;			// For time limited execution, this flag should be true and for cost limited execution this flag should be false.
	
	int totalPlanExecution;
	long executionTime;						//total bouquet execution time (time spent inside database engine)
	
	int contPlans[][];						//holds plans for each contour
	int totalcontours;					
	int contourPlanCount[];					//Number of plans lie on contour
	double cost_limit[];
	double timeLimit[];
	int totalPlans;							//total plans in plan bouquet
	int finalPlanSet[];						//Sorted list of plans of plan bouquet
	String bouquetPath;						
	
	String queryStringForPlans[];			//this holds query string through which POSP plans can be generated. It is got from PServerLog
	
	double subOptimality;
	/*
	 * following variables are use in single plan execution
	 */
	
	int currentPlanNumber;
	int currentContour;
	boolean contourChanged;
	public BasicBouquetExecution(AllObjects allObjects)
	{
		allObjects.setBasicBouquetExecutionObj(this);
		
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		contPlans = bouquetDataObj.getContourPlans();
		totalcontours = bouquetDataObj.getTotalContours();
		contourPlanCount = bouquetDataObj.getContourPlansCount();
		cost_limit = bouquetDataObj.getCostLimit();
		timeLimit = bouquetDataObj.getTimeLimit();
		totalPlans = bouquetDataObj.getTotalPlans();
		finalPlanSet = bouquetDataObj.getFinalPlanSet();
		bouquetPath = bouquetDataObj.getBouquetLocation();

		queryStringForPlans = bouquetDataObj.queryStringForPlans;
		
		currentPlanNumber = -1;
		currentContour = 0;
		contourChanged = true;
	}
	
	void runBasicBouquet(AllObjects allObjects)
	{	
		boolean isSimulation = false;
		ExecutionInformationPanel execInfoPanelObj = allObjects.getExecutionInformationPanelObj();
		DrawContours drawContoursObj = allObjects.getDrawContoursObj();
		
		ProcessPartialExecutionPlan planStateObj = new ProcessPartialExecutionPlan();
		
		Vector<String> textualPlan = new Vector<String>();
		
		long startTime=0,endTime=0;
		
		try 
		{
			ConnectDB connectDBObj = allObjects.getConnectDBObj();
			Connection con = connectDBObj.connection;
			Statement st = con.createStatement();
			/*
			 * Executing plans of each contour
			 */

			System.out.println();
			System.out.println();
			if(time_limited_exec)
			{
				System.out.print("Coutour\t\t");
				System.out.print("Plan\t\t");
				System.out.print("Time given\t");
				System.out.print("Time taken\t\t");
				System.out.print("Total Time\t\t");
				System.out.print("Status");
			}
			else
			{
				System.out.print("Coutour\t\t");
				System.out.print("Plan\t\t");
				System.out.print("Cost Given\t\t");
				System.out.print("Time Taken\t\t");
				System.out.print("Total Time\t\t");
				System.out.print("Status");
			}
			System.out.println();
			System.out.println("-------------------------------------------------------------------------------------------------------------------------");
			ResultSet rs=null;
			
			startTime=System.currentTimeMillis();
			
			
			for(int i=0;i<totalcontours;i++)
			{
				boolean contourChanged = true;
			
				int j=0;
				long contourStartTime, contourEndTime;
				float contourAvgTime;
				contourStartTime = System.currentTimeMillis();
				for(j=0;j<contourPlanCount[i];j++)
				{
					long planStartTime, planEndTime;
					int currentPlanToExec = contPlans[i][j];
					int indexInFinalList = Arrays.binarySearch(finalPlanSet, 0, totalPlans, currentPlanToExec);
					String planToExec = queryStringForPlans[indexInFinalList];
				
					if(i!=totalcontours-1)
					{
						if(time_limited_exec)
						{
							double currentTimeLimit = timeLimit[i];
							st.executeUpdate("set time_limit = "+currentTimeLimit);
						}
						else
						{
							double bounded_cost = cost_limit[i];
							st.executeUpdate("set limit_cost = "+bounded_cost);
						}
					}

					execInfoPanelObj.addPlanTree(indexInFinalList, i, cost_limit[i], contourChanged);
					contourChanged = false;
					
					System.out.println("BASIC BOUQUET EXEC: "+planToExec);
					
					planStartTime = System.currentTimeMillis();  
					rs = st.executeQuery(planToExec);
					planEndTime = System.currentTimeMillis();
					
					
					String query_status=null;
					while (rs.next()) 
					{
						query_status = rs.getString(1);
						textualPlan.add(query_status);
					}
					textualPlan.remove(textualPlan.size()-1);		//Removing completed/Not Completed.
					textualPlan.remove(textualPlan.size()-1);		//Removing execution time string
					execInfoPanelObj.neverExecNodeTree[i][indexInFinalList] = planStateObj.createStatePlanTree(textualPlan);
					
					executionTime += planEndTime-planStartTime;
					if(time_limited_exec)
						System.out.printf("%4d\t\t%4d\t\t%7.2f\t\t%7.2f\t\t%10.2f\t\t%20s",i,currentPlanToExec, timeLimit[i]/1000.0,(planEndTime-planStartTime)/1000.0,(executionTime)/1000.0,query_status);
					else
						System.out.printf("%4d\t\t%4d\t\t%10.2f\t\t%7.2f\t\t%10.2f\t\t%20s",i,currentPlanToExec, cost_limit[i],(planEndTime-planStartTime)/1000.0,(executionTime)/1000.0,query_status);

					double execTime = (planEndTime-planStartTime)/1000.0;
//					execTime *= 10.0;
//					execTime = Math.round(execTime);
//					execTime /= 10.0;
					
					double totalTime = (executionTime)/1000.0;
					totalTime *= 10;
					totalTime = Math.round(totalTime);
					totalTime /=10;
					
					execInfoPanelObj.addPostExecutionInfo(indexInFinalList, i, execTime, null, isSimulation);
					drawContoursObj.changeContourColor(i, indexInFinalList, totalPlans, finalPlanSet);
					
					totalPlanExecution++;
					if(query_status.equals("Completed"))
					{
						System.out.println();
						PerformanceComparisionPanel executionResultPanelObj = allObjects.getExecutionResultPanelObj();
						double execTimeInSec = executionTime/1000.0;
						executionResultPanelObj.setBasicBouquetTime(execTimeInSec, allObjects);
						
						double optimalPlanExecTime = executionResultPanelObj.executionTime[1];			//executionTime[1] contains optimal plan execution time
						subOptimality = execTimeInSec / optimalPlanExecTime;
						
						execInfoPanelObj.addBouquetExecutionFinishedInformation(execTimeInSec, totalPlanExecution);					//+4 should be removed
//						index = planToExec.indexOf("FPC");
//						finalPlan = planToExec.substring(index);
						break;
					}
					System.out.println();
				}

				contourEndTime = System.currentTimeMillis();
				contourAvgTime = (float)(contourEndTime-contourStartTime)/j;
				System.out.println();
				System.out.println();
				System.out.printf("avg. plan execution time on this contour=%10.2f",contourAvgTime/1000.0);
				System.out.println();
				System.out.println("-------------------------------------------------------------------------------------------------------------------------");
				if(j<contourPlanCount[i])
				{
					break;
				}
			}
			endTime=System.currentTimeMillis();

			rs.close();
			st.close();
		}
		catch (Exception e)
		{
			System.out.println("error in database connection" + e);
			e.printStackTrace();
		}

		System.out.printf("Total Plan Bouquet Execution Time (including driver time)= %10.2f",(endTime-startTime)/1000.0);
		System.out.println();
		System.out.printf("Plans Execution Time (only plans)=%10.2f",executionTime/1000.0);
//		mainFrameObj.addResult();

	}

	boolean runBasicBouquetSinglePlan(AllObjects allObjects)
	{	
		boolean isSimulation = false;
		boolean executionCompleted = false;
		ExecutionInformationPanel execInfoPanelObj = allObjects.getExecutionInformationPanelObj();
		DrawContours drawContoursObj = allObjects.getDrawContoursObj();
//		PostgresRun pgRunObj = allObjects.getPostgresRunObj();
//		String queryStringForPlans[] = pgRunObj.queryStringForPlans;
		
		ProcessPartialExecutionPlan planStateObj = new ProcessPartialExecutionPlan();
		
		Vector<String> textualPlan = new Vector<String>();
		
		
//		double time_limit[] = new double [steps];

//		if(time_limited_exec == true)
//		{
////			time_limit[0] = 663;				//first contour time limit in milliseconds.
////			time_limit[0] = 1000;
////			time_limit[0] = 2125;
//			for(int s=1; s<steps; s++)
//			{
//				time_limit[s] = time_limit[s-1]*2;
//			}
//			String maxTimeFile = bouquetPath +"/lastContourTime";
//			try
//			{
//				FileReader f = new FileReader(maxTimeFile);
//				BufferedReader br = new BufferedReader (f);
//				br.close();
//				String maxtime = br.readLine().trim();
//				double maxTime = Double.parseDouble(maxtime);
//				time_limit[steps-1] = maxTime;
//				for(int s = steps-2 ; s>=0 ; s++)
//				{
//					time_limit[s] = time_limit[s+1] / 2;
//				}
//			}
//			catch(Exception e)
//			{
//				System.out.println("Execption in Basic Bouquet Execution: "+e);
//				e.printStackTrace();
//			}
//		}
		try 
		{
			ConnectDB connectDBObj = allObjects.getConnectDBObj();
			Connection con = connectDBObj.connection;
			Statement st = con.createStatement();
			/*
			 * Executing plans of each contour
			 */


			System.out.println();
			System.out.println();
			if(time_limited_exec)
			{
				System.out.print("Coutour\t\t");
				System.out.print("Plan\t\t");
				System.out.print("Time given\t");
				System.out.print("Time taken\t\t");
				System.out.print("Total Time\t\t");
				System.out.print("Status");
			}
			else
			{
				System.out.print("Coutour\t\t");
				System.out.print("Plan\t\t");
				System.out.print("Cost Given\t\t");
				System.out.print("Time Taken\t\t");
				System.out.print("Total Time\t\t");
				System.out.print("Status");
			}
			System.out.println();
			System.out.println("-------------------------------------------------------------------------------------------------------------------------");
			ResultSet rs=null;
//			execInfoPanelObj.startTimerClock();
			
			currentPlanNumber++;
			if(currentPlanNumber >= contourPlanCount[currentContour])
			{
				currentContour++;
				currentPlanNumber = 0;
				contourChanged = true;
				System.out.println("-------------------------------------------------------------------------------------------------------------------------");
			}

			if(currentContour >= totalcontours)
			{
				return(true);
			}
			int currentPlanToExec = contPlans[currentContour][currentPlanNumber];
			int indexInFinalList = Arrays.binarySearch(finalPlanSet, 0, totalPlans, currentPlanToExec);
			String planToExec = queryStringForPlans[indexInFinalList];
			
			if(currentContour != totalcontours-1)
			{
				if(time_limited_exec)
				{
					double currentTimeLimit = timeLimit[currentContour];
					st.executeUpdate("set time_limit = "+currentTimeLimit);
				}
				else
				{
					double bounded_cost = cost_limit[currentContour];
					st.executeUpdate("set limit_cost = "+bounded_cost);
				}
			}
			execInfoPanelObj.addPlanTree(indexInFinalList, currentContour, cost_limit[currentContour], contourChanged);
			contourChanged = false;
			long planStartTime = System.currentTimeMillis();  
			rs = st.executeQuery(planToExec);
			long planEndTime = System.currentTimeMillis();

			String query_status=null;
			while (rs.next()) 
			{
				query_status = rs.getString(1);
				textualPlan.add(query_status);
			}
			textualPlan.remove(textualPlan.size()-1);		//Removing completed/Not Completed string.
			textualPlan.remove(textualPlan.size()-1);		//Removing execution time string
			execInfoPanelObj.neverExecNodeTree[currentContour][indexInFinalList] = planStateObj.createStatePlanTree(textualPlan);

			executionTime += planEndTime-planStartTime;

			if(time_limited_exec)
				System.out.printf("%4d\t\t%4d\t\t%7.2f\t\t%7.2f\t\t%10.2f\t\t%20s",currentContour,currentPlanToExec, timeLimit[currentContour]/1000.0,(planEndTime-planStartTime)/1000.0,(executionTime)/1000.0,query_status);
			else
				System.out.printf("%4d\t\t%4d\t\t%10.2f\t\t%7.2f\t\t%10.2f\t\t%20s",currentContour,currentPlanToExec, cost_limit[currentContour],(planEndTime-planStartTime)/1000.0,(executionTime)/1000.0,query_status);

			double execTime = (planEndTime-planStartTime)/1000.0;
//			execTime *= 10.0;
//			execTime = Math.round(execTime);
//			execTime /= 10.0;

			double totalTime = (executionTime)/1000.0;
			totalTime *= 10;
			totalTime = Math.round(totalTime);
			totalTime /=10;

			execInfoPanelObj.addPostExecutionInfo(indexInFinalList, currentContour, execTime, null, isSimulation);
			drawContoursObj.changeContourColor(currentContour, indexInFinalList, totalPlans, finalPlanSet);

			if(query_status.equals("Completed"))
			{
				PerformanceComparisionPanel executionResultPanelObj = allObjects.getExecutionResultPanelObj();
				executionResultPanelObj.setBasicBouquetTime(executionTime/1000.0, allObjects);
				executionCompleted = true;
				double optimalPlanExecTime = executionResultPanelObj.executionTime[1];
				double execTimeInSec = executionTime/1000.0;
				subOptimality = execTimeInSec / optimalPlanExecTime;
				System.out.println();
				execInfoPanelObj.addBouquetExecutionFinishedInformation(executionTime/1000.0, totalPlanExecution);
			}
			System.out.println();
			totalPlanExecution++;
			rs.close();
			st.close();
		}
		catch (Exception e)
		{
			System.out.println("error in database connection" + e);
			e.printStackTrace();
		}
		System.out.println();
		System.out.printf("Plans Execution Time (only plans)=%10.2f",executionTime/1000.0);
		return(executionCompleted);
	}
}
