package quest;
import iisc.dsl.picasso.common.ds.DiagramPacket;
import iisc.dsl.picasso.common.ds.TreeNode;
public class BouquetData 
{
	String bouquetLocation;					//Absolute path of bouquet
	String query;
	DiagramPacket gdp;
	DiagramPacket reducedGDP;
	int dimesion;
	int resolution;
	int maxTuples[];
	double queryValues[];
	int totalContours;
	int contourPlans[][];
	int contourPlansCount[];
	int contourLocationInESS[];
	double costLimit[];
	double timeLimit[];
	double planLocationMaxCoordinate[][][];
	double planLocationMinCoordinate[][][];
	int finalPlanSet[];						//Sorted Plan Set.
	int totalPlans;
	String queryStringForPlans[];
	String errorProneBaseRelationNames[];
	String baseRelationNames[];
	String baseConditions[];
	int lemda;		
	double commonRatio = 2;
	
	//Anorexic reduction parameter.
	double actualSelectivity[];
	
	TreeNode planTreeRootNodes[];
	
	void setBouquetLocation(String location)
	{
		bouquetLocation = location;
	}
	void setQuery(String q)
	{
		query = q;
	}
	void setDiagramPacket(DiagramPacket p)
	{
		gdp = p;
	}
	void setReducedDiagramPacket(DiagramPacket p)
	{
		reducedGDP = p;
	}
	void setDimension(int dim)
	{
		this.dimesion = dim;
	}
	void setResolution(int res)
	{
		this.resolution = res;
	}
	void setMaxTuplesForErrorProneBaseRelation(int arr[])
	{
		this.maxTuples = arr;
	}
	void setQueryValues(double arr[])
	{
		this.queryValues = arr;
	}
	void setTotalContours(int totalCont)
	{
		this.totalContours = totalCont;
	}
	void setContourPlans(int contPlans[][])
	{
		this.contourPlans = contPlans;
	}
	void setContourPlansCount(int contPlansCount[])
	{
		this.contourPlansCount = contPlansCount;
	}
	void setContourLocationInESS(int contLoc[])
	{
		this.contourLocationInESS = contLoc;
	}
	void setCostLimit(double cost_limit[])
	{
		costLimit = cost_limit;
	}
	void setTimeLimit(double time_limit[])
	{
		timeLimit = time_limit;
	}
	void setPlanLocationMaxCoordinate(double arr[][][])
	{
		this.planLocationMaxCoordinate = arr;
	}
	void setPlanLocationMinCoordinate(double arr[][][])
	{
		this.planLocationMinCoordinate = arr;
	}
	void setFinalPlanSet(int finalPlan[])
	{
		this.finalPlanSet = finalPlan;
	}
	void setTotalPlans(int n)
	{
		totalPlans = n;
	}
	void setQueryStringForPlans(String str[])
	{
		queryStringForPlans = str;
	}
	void setCommonRatio(double ratio)
	{
		commonRatio = ratio;
	}
	void setErrorProneBaseRelationNames(String str[])
	{
		errorProneBaseRelationNames = str;
	}
	void setBaseRelationNames(String str[])
	{
		baseRelationNames = str;
	}
	void setBaseConditions(String str[])
	{
		baseConditions = str;
	}
	void setActualSelectivity(double arr[])
	{
		actualSelectivity = arr;
	}
	void setLemda(int l)
	{
		lemda = l;
	}
	void setPlanTreeRootNodes(TreeNode root[])
	{
		planTreeRootNodes = root;
	}
	
	String getBouquetLocation()
	{
		return(bouquetLocation);
	}
	String getQuery()
	{
		return(query);
	}
	DiagramPacket getDiagramPacket()
	{
		return(gdp);
	}
	DiagramPacket getReducedDiagramPacket()
	{
		return(reducedGDP);
	}
	int getDimension()
	{
		return(dimesion);
	}
	int getResolution()
	{
		return(resolution);
	}
	int[] getMaxTuplesForErrorProneBaseRelation()
	{
		return(maxTuples);
	}
	double[] getQueryValues()
	{
		return(queryValues);
	}
	int getTotalContours()
	{
		return(totalContours);
	}
	int[][] getContourPlans()
	{
		return(contourPlans);
	}
	int[] getContourPlansCount()
	{
		return(contourPlansCount);
	}
	int[] getContourLocationInESS()
	{
		return(contourLocationInESS);
	}
	double[] getCostLimit()
	{
		return(costLimit);
	}
	double[] getTimeLimit()
	{
		return(timeLimit);
	}
	double[][][] getPlanLocationMaxCoordinate()
	{
		return(planLocationMaxCoordinate);
	}
	double[][][] getPlanLocationMinCoordinate()
	{
		return(planLocationMinCoordinate);
	}
	int[] getFinalPlanSet()
	{
		return(finalPlanSet);
	}
	int getTotalPlans()
	{
		return(totalPlans);
	}
	double getCommonRatio()
	{
		return(commonRatio);
	}
	String[] getQueryStringForPlans()
	{
		return(queryStringForPlans);
	}
	String [] getErrorProneBaseRelationNames()
	{
		return(errorProneBaseRelationNames);
	}
	String [] getBaseRelationNames()
	{
		return(baseRelationNames);
	}
	String[] getBaseConditions()
	{
		return(baseConditions);
	}
	double[] getActualSelectivity()
	{
		return(actualSelectivity);
	}
	int getLemda()
	{
		return(lemda);
	}
	TreeNode[] getPlanTreeRootNodes()
	{
		return(planTreeRootNodes);
	}
}
