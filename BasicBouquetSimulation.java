package quest;
import java.util.Arrays;


public class BasicBouquetSimulation  
{
	int totalPlanExecution;
	long executionTime;
	
	int contPlans[][];
	int steps;
	int contourPlanCount[];
	double cost_limit[];
	int totalPlans;
	int finalPlanSet[];
	long time_limit[];

	double subOptimality;
	/*
	 * following variables are used in single plan execution
	 */
	int currentPlanNumber;
	int currentContour;
	boolean contourChanged;
	int lastPlan;
	int lastContour;
	double totalCost;
	double optCost;
	public  BasicBouquetSimulation(AllObjects allObjects, int lastContour, int lastPlan, double optCost)
	{
//		allObjects.setBasicBouquetExecutionObj(this);
		
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		contPlans = bouquetDataObj.getContourPlans();
		steps = bouquetDataObj.getTotalContours();
		contourPlanCount = bouquetDataObj.getContourPlansCount();
		cost_limit = bouquetDataObj.getCostLimit();
		totalPlans = bouquetDataObj.getTotalPlans();
		finalPlanSet = bouquetDataObj.getFinalPlanSet();
		
		time_limit = new long[steps];
		time_limit[steps-1] = 4000;
		for(int i=steps-2;i>=0;i--)
		{
			time_limit[i] = time_limit[i+1]/2;
		}
		
		currentPlanNumber = -1;
		currentContour = 0;
		contourChanged = true;
		this.lastPlan = lastPlan;
		this.lastContour = lastContour;
		this.optCost = optCost;
	}
	void simulateBasicBouquet(AllObjects allObjects)
	{	
		boolean isSimulation = true;
		ExecutionInformationPanel execInfoPanelObj = allObjects.getExecutionInformationPanelObj();
		DrawContours drawContoursObj = allObjects.getDrawContoursObj();
	
		double totalCost = 0.0;
		long startTime=0,endTime=0;
		try 
		{
			startTime=System.currentTimeMillis();
			
			for(int i=0;i<lastContour;i++)
			{
				boolean contourChanged = true;
				
				int j=0;
				for(j=0;j<contourPlanCount[i];j++)
				{
					long planStartTime, planEndTime;
					int currentPlanToExec = contPlans[i][j];
					int indexInFinalList = Arrays.binarySearch(finalPlanSet, 0, totalPlans, currentPlanToExec);


					execInfoPanelObj.addPlanTree(indexInFinalList, i, cost_limit[i], contourChanged);
					contourChanged = false;
					
					planStartTime = System.currentTimeMillis();  
					Thread.sleep(time_limit[i]);
					planEndTime = System.currentTimeMillis();

					executionTime += time_limit[i];

					double execTime = (planEndTime-planStartTime)/1000.0;
//					execTime *= 10.0;
//					execTime = Math.round(execTime);
//					execTime /= 10.0;
					
					execInfoPanelObj.addPostExecutionInfo(indexInFinalList, i, execTime, null, isSimulation);
					drawContoursObj.changeContourColor(i, indexInFinalList, totalPlans,finalPlanSet);
					
					totalPlanExecution++;
					totalCost += cost_limit[i];
				}
			}
			
			boolean contourChanged = true;
			for(int i=0; i<=lastPlan; i++)
			{
				long planStartTime, planEndTime;
				int currentPlanToExec = contPlans[lastContour][i];
				int indexInFinalList = Arrays.binarySearch(finalPlanSet, 0, totalPlans, currentPlanToExec);


				execInfoPanelObj.addPlanTree(indexInFinalList, lastContour, cost_limit[i], contourChanged);
				contourChanged = false;
				
				planStartTime = System.currentTimeMillis();  
				
				Thread.sleep(time_limit[lastContour]);
				
				planEndTime = System.currentTimeMillis();
				
//				executionTime += planEndTime-planStartTime;
				executionTime += time_limit[lastContour];

				double execTime = (planEndTime-planStartTime)/1000.0;
				execTime *= 10.0;
				execTime = Math.round(execTime);
				execTime /= 10.0;

				execInfoPanelObj.addPostExecutionInfo(indexInFinalList, lastContour, execTime, null, isSimulation);
				drawContoursObj.changeContourColor(lastContour, indexInFinalList, totalPlans,finalPlanSet);
				
				totalPlanExecution++;
				totalCost += cost_limit[i];
			}
			subOptimality = totalCost / optCost;
			System.out.println("Suboptimality="+subOptimality);
			System.out.println();
			
			PerformanceComparisionPanel executionResultPanelObj = allObjects.getExecutionResultPanelObj();
			executionResultPanelObj.setBasicBouquetTime(executionTime/1000.0, allObjects);
			
			endTime=System.currentTimeMillis();
		}
		catch (Exception e)
		{
			System.out.println("error in database connection" + e);
			e.printStackTrace();
		}

		System.out.printf("Total Plan Bouquet Execution Time (including driver time)= %10.2f",(endTime-startTime)/1000.0);
		System.out.println();
		System.out.printf("Plans Execution Time (only plans)=%10.2f",executionTime/1000.0);
	}
	boolean simulateBasicBouquetSinglePlan(AllObjects allObjects)
	{
		boolean isSimulation = true;
		boolean executionFinished = false;
		ExecutionInformationPanel execInfoPanelObj = allObjects.getExecutionInformationPanelObj();
		DrawContours drawContoursObj = allObjects.getDrawContoursObj();
	
		currentPlanNumber++;
		if(currentPlanNumber >= contourPlanCount[currentContour])
		{
			currentPlanNumber = 0;
			currentContour++;
		}
		long startTime=0,endTime=0;
		try 
		{
			startTime=System.currentTimeMillis();
			
			if(currentContour<lastContour)
			{
				boolean contourChanged = true;
				
				int j=0;
//				if(currentPlanNumber<contourPlanCount[currentContour])
//				{
					long planStartTime, planEndTime;
					int currentPlanToExec = contPlans[currentContour][currentPlanNumber];
					int indexInFinalList = Arrays.binarySearch(finalPlanSet, 0, totalPlans, currentPlanToExec);


					execInfoPanelObj.addPlanTree(indexInFinalList, currentContour, cost_limit[currentContour], contourChanged);
					contourChanged = false;
					
					planStartTime = System.currentTimeMillis();  
					Thread.sleep(time_limit[currentContour]);
					planEndTime = System.currentTimeMillis();

					executionTime += time_limit[currentContour];

					double execTime = (planEndTime-planStartTime)/1000.0;
//					execTime *= 10.0;
//					execTime = Math.round(execTime);
//					execTime /= 10.0;
					
					execInfoPanelObj.addPostExecutionInfo(indexInFinalList, currentContour, execTime, null, isSimulation);
					drawContoursObj.changeContourColor(currentContour, indexInFinalList, totalPlans,finalPlanSet);
					
					totalPlanExecution++;
					totalCost += cost_limit[currentContour];
//				}
			}
			else
			{
				if(currentPlanNumber<=lastPlan)
				{
					long planStartTime, planEndTime;
					int currentPlanToExec = contPlans[currentContour][currentPlanNumber];
					int indexInFinalList = Arrays.binarySearch(finalPlanSet, 0, totalPlans, currentPlanToExec);


					execInfoPanelObj.addPlanTree(indexInFinalList, currentContour, cost_limit[currentContour], contourChanged);
					contourChanged = false;
					
					planStartTime = System.currentTimeMillis();  
					Thread.sleep(time_limit[currentContour]);
					planEndTime = System.currentTimeMillis();

					executionTime += time_limit[currentContour];

					double execTime = (planEndTime-planStartTime)/1000.0;
//					execTime *= 10.0;
//					execTime = Math.round(execTime);
//					execTime /= 10.0;
					
					execInfoPanelObj.addPostExecutionInfo(indexInFinalList, currentContour, execTime, null, isSimulation);
					drawContoursObj.changeContourColor(currentContour, indexInFinalList, totalPlans,finalPlanSet);
					
					totalPlanExecution++;
					totalCost += cost_limit[currentContour];
				}
				if(currentPlanNumber == lastPlan)
				{
					executionFinished = true;
					subOptimality = totalCost / optCost;
				}
			}
			
			
//			boolean contourChanged = true;
//			for(int i=0; i<=lastPlan; i++)
//			{
//				long planStartTime, planEndTime;
//				int currentPlanToExec = contPlans[lastContour][i];
//				int indexInFinalList = Arrays.binarySearch(finalPlanSet, 0, totalPlans, currentPlanToExec);
//
//
//				execInfoPanelObj.addPlanTree(indexInFinalList, lastContour, cost_limit[i], contourChanged);
//				contourChanged = false;
//				
//				planStartTime = System.currentTimeMillis();  
//				
//				Thread.sleep(time_limit[lastContour]);
//				
//				planEndTime = System.currentTimeMillis();
//				
////				executionTime += planEndTime-planStartTime;
//				executionTime += time_limit[lastContour];
//
//				double execTime = (planEndTime-planStartTime)/1000.0;
//				execTime *= 10.0;
//				execTime = Math.round(execTime);
//				execTime /= 10.0;
//
//				execInfoPanelObj.addPostExecutionInfo(indexInFinalList, lastContour, execTime, null, isSimulation);
//				drawContoursObj.changeContourColor(lastContour, indexInFinalList, totalPlans);
//				
//				totalPlanExecution++;
//			}
//			ExecutionResultPanel executionResultPanelObj = allObjects.getExecutionResultPanelObj();
//			executionResultPanelObj.setBasicBouquetTime(executionTime/1000.0, allObjects);
			
//			endTime=System.currentTimeMillis();
		}
		catch (Exception e)
		{
			System.out.println("error in simulated basic bouquet with single plan" + e);
			e.printStackTrace();
		}
		return(executionFinished);
	}
}
