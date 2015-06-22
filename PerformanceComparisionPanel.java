package quest;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
public class PerformanceComparisionPanel 
{
	JPanel mainPanel;
	double executionTime[];
	boolean isExecutionTimeSet[];
	PerformanceComparisionPanel(AllObjects allObjects)
	{
		allObjects.setExecutionResultPanelObj(this);
		mainPanel = new JPanel();
		executionTime = new double[4];
		isExecutionTimeSet = new boolean[4];
	}
	void setNativeOptimizerExecTime(double time, AllObjects allObjects)
	{
		executionTime[0] = time;
		isExecutionTimeSet[0] = true;
		int i;
		for(i=0;i<4;i++)
			if(!isExecutionTimeSet[i])
				break;
		if(i==4)
		{
			MainFrame mainFrameObj = allObjects.getMainFrameObj();
			mainFrameObj.allTabs.setEnabledAt(QUESTConstants.RESULT_PANE, true);
		}
	}
	
	void setOptimalPlanExecutionTime(double time, AllObjects allObjects)
	{
		executionTime[1] = time;
		isExecutionTimeSet[1] = true;
		int i;
		for(i=0;i<4;i++)
			if(!isExecutionTimeSet[i])
				break;
		if(i==4)
		{
			MainFrame mainFrameObj = allObjects.getMainFrameObj();
			mainFrameObj.allTabs.setEnabledAt(QUESTConstants.RESULT_PANE, true);
		}
	}
	void setBasicBouquetTime(double time, AllObjects allObjects)
	{
		executionTime[2] = time;
		isExecutionTimeSet[2] = true;
		int i;
		for(i=0;i<4;i++)
			if(!isExecutionTimeSet[i])
				break;
		if(i==4)
		{
			MainFrame mainFrameObj = allObjects.getMainFrameObj();
			mainFrameObj.allTabs.setEnabledAt(QUESTConstants.RESULT_PANE, true);
		}
	}
	void setOptimisedBouquetTime(double time, AllObjects allObjects)
	{
		executionTime[3] = time;
		isExecutionTimeSet[3] = true;
		int i;
		for(i=0;i<4;i++)
			if(!isExecutionTimeSet[i])
				break;
		if(i==4)
		{
			MainFrame mainFrameObj = allObjects.getMainFrameObj();
			mainFrameObj.allTabs.setEnabledAt(QUESTConstants.RESULT_PANE, true);
		}
	}
	void showResultBarGraph()
	{
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		dataset.setValue(executionTime[0]/executionTime[1], "PGSubOpt", "PostgreSQL Optimizer");
		dataset.setValue(executionTime[2]/executionTime[1], "BouSubOpt", "Basic Bouquet");
		dataset.setValue(executionTime[3]/executionTime[1], "OptSubOpt", "Optimized Bouquet");

		
	
		JFreeChart chart = ChartFactory.createStackedBarChart
				("Sub-optimality (log scale)","Method of Execution", "Sub-optimality", dataset, 
						PlotOrientation.VERTICAL, false,true, false);

		CategoryPlot plot = chart.getCategoryPlot(); 
		plot.setRangeGridlinePaint(Color.WHITE); 

		
		
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setMaximumBarWidth(0.1);
		

		renderer.setBarPainter(new StandardBarPainter());
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesPaint(1, Color.BLUE);
		renderer.setSeriesPaint(2, Color.GREEN);
		
		CategoryAxis domain = plot.getDomainAxis();
		domain.setTickLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_TICK_FONT_SIZE));
		domain.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE));
		
		LogAxis range = new LogAxis("Sub-optimality");
		range.setBase(2);
		range.setTickUnit(new NumberTickUnit(1));

		range.setRange(1, 32);
		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.AXIS_TICK_FONT_SIZE));
		range.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE));
		plot.setRangeAxis(range);
		
	    StackedBarRenderer r = (StackedBarRenderer) plot.getRenderer();
	    r.setBase(0.001);
		ChartPanel cPanel = new ChartPanel(chart);

		cPanel.setPreferredSize(new Dimension(750, 500));
		mainPanel.add(cPanel);
		mainPanel.setBackground(Color.WHITE);
	}
}
