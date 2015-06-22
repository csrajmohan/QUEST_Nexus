package quest;
import java.io.*;
import java.util.Arrays;
public class PostgresRun 
{
	boolean time_limited_exec=true;

	public PostgresRun(AllObjects allObjects)
	{
		allObjects.setPostgresRunObj(this);
	}
	public void run_postgres(AllObjects allObjects)
	{
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		int contPlans[][] = bouquetDataObj.getContourPlans();
		int steps = bouquetDataObj.getTotalContours();
		int contourPlanCount[] = bouquetDataObj.getContourPlansCount();
		int totalPlans = bouquetDataObj.getTotalPlans();
		
		String bouquetPath = bouquetDataObj.getBouquetLocation();

		int dimension = bouquetDataObj.getDimension();
		int finalPlansSet[] = new int[totalPlans];
		String queryStringForPlans[] = new String[totalPlans];
		
		String planWithExplain[] = new String[totalPlans];
 		double queryValues[]= bouquetDataObj.getQueryValues();
		double time_limit[] = new double [steps];
		
		String errorProneRelationNames[] = new String[dimension];
		
		String query_file_path = bouquetPath + "/PServerLog_XML";
		/*
		 * actual value for query.
		 * when a query is given for execution these values will be fetched from original query.
		 */
	
		finalPlansSet = bouquetDataObj.getFinalPlanSet();
		int finalPlansSetLength = bouquetDataObj.totalPlans;
		
		for(int i=0;i<finalPlansSetLength;i++)
		{
			System.out.print(finalPlansSet[i]+",");
		}
		System.out.println();
		
		try
		{
			FileReader f = new FileReader(query_file_path);
			BufferedReader br = new BufferedReader (f);
			int i=0;
			while (i<finalPlansSetLength)
			{
				String new_fpc_str,tempstr;
				String str =  br.readLine();
				int hash_index = str.indexOf('#');
				while (hash_index==-1)
				{
					str = br.readLine();
					hash_index = str.indexOf('#');
				}
				int plan_num = Integer.parseInt(str.substring(hash_index+1));
				if(plan_num==finalPlansSet[i])
				{
					/* <input query> fpc <xml plan to use>*/
					
					new_fpc_str = bouquetDataObj.getQuery();
					str = br.readLine();
					int lastbrace = str.lastIndexOf(')');
					tempstr = str.substring(0,lastbrace-1);
					
					new_fpc_str = "explain analyse " + new_fpc_str + tempstr + ";";
					
					String remainingPart = str.substring(lastbrace+1);
					
					String planStr="";

					String line=remainingPart;
					while(line.length()!=0)
					{
						planStr += line+" ";
						line = br.readLine();
					}

					queryStringForPlans[i]= new_fpc_str;//str+" "+planStr;
					planWithExplain[i]="explain "+planStr; 
					System.out.println(queryStringForPlans[i]);
					i++;
				}
			}
			br.close();
		}
		catch(Exception e)
		{
			System.out.println("Error occured in plan file reading:"+ e);
		}
		int fIndex = 0;
		
		bouquetDataObj.setQueryStringForPlans(queryStringForPlans);
		
		
		CreatePlanTree createPlanTree = new CreatePlanTree();
		createPlanTree.createAllPlanTreeStructure(planWithExplain, totalPlans, allObjects);
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