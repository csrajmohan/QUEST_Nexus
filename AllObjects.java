package quest;
public class AllObjects 
{
	public boolean demo_mode = true;
	
	private ConnectDB connectDBObj;
	private MainFrame mainFrameObj;
	private BouquetData bouquetDataObj;
	private BouquetDriver bouquetDriverObj;
	private PostgresRun pgRunObj;
	
	private CostDiagramPanel costDigObj;
	private BouquetPanel bouquetPanelObj;
	private NativeOptimizerPanel nativeOptimizerPanelObj;
	private PerformanceComparisionPanel execResultPanelObj;
	
	private DrawContours drawContoursObj;
	private ExecutionInformationPanel execInfoPanel;
	
	private BasicBouquetExecution basicBouquetExecutionObj;

	public ConnectDB getConnectDBObj()
	{
		return(connectDBObj);
	}
	MainFrame getMainFrameObj()
	{
		return(mainFrameObj);
	}
	BouquetData getBouquetDataObj()
	{
		return(bouquetDataObj);
	}
	BouquetDriver getBouquetDriverObj()
	{
		return(bouquetDriverObj);
	}
	PostgresRun getPostgresRunObj()
	{
		return(pgRunObj);
	}
	CostDiagramPanel getCostDiagramPanelObj()
	{
		return(costDigObj);
	}
	BouquetPanel getBouquetPanelObj()
	{
		return(bouquetPanelObj);
	}
	NativeOptimizerPanel getNativeOptimizerPanelObj()
	{
		return(nativeOptimizerPanelObj);
	}
	PerformanceComparisionPanel getExecutionResultPanelObj()
	{
		return(execResultPanelObj);
	}
	DrawContours getDrawContoursObj()
	{
		return(drawContoursObj);
	}
	ExecutionInformationPanel getExecutionInformationPanelObj()
	{
		return(execInfoPanel);
	}
	BasicBouquetExecution getBasicBouquetExecutionObj()
	{
		return(basicBouquetExecutionObj);
	}
	void setConnectDBObj(ConnectDB obj)
	{
		connectDBObj = obj;
	}
	void setMainFrameObj(MainFrame obj)
	{
		mainFrameObj = obj;
	}
	void setBouquetDataObj(BouquetData obj)
	{
		bouquetDataObj = obj;
	}
	void setBouquetDriverObj(BouquetDriver obj)
	{
		bouquetDriverObj = obj;
	}
	void setPostgresRunObj(PostgresRun obj)
	{
		pgRunObj = obj;
	}
	void setCostDiagramPanelObj(CostDiagramPanel obj)
	{
		costDigObj = obj;
	}
	void setBouquetPanelObj(BouquetPanel obj)
	{
		bouquetPanelObj = obj;
	}
	void setNativeOptimizerPanelObj(NativeOptimizerPanel obj)
	{
		nativeOptimizerPanelObj = obj;
	}
	void setExecutionResultPanelObj(PerformanceComparisionPanel obj)
	{
		execResultPanelObj = obj;
	}
	void setDrawContoursObj(DrawContours obj)
	{
		drawContoursObj = obj;
	}
	void setExecutionInformationPanelObj(ExecutionInformationPanel obj)
	{
		execInfoPanel = obj;
	}
	void setBasicBouquetExecutionObj(BasicBouquetExecution obj)
	{
		basicBouquetExecutionObj = obj;
	}
}
