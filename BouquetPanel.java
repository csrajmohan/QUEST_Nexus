package quest;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
public class BouquetPanel implements ActionListener 
{
	AllObjects allObjects;
	
	JPanel mainPanel;							//top panel
	JPanel southPanel;							//status panel
	JPanel simulationBarPanel;					//bar chart panel for simulation
	JPanel barPanel;							//bar chart panel for real execution
	ExecutionInformationPanel execInfoPanelObj;	//plans tree structure is shown on this panel 
	
	/*
	 * Components for input panel
	 */
	JButton basicBouquetButton;					
	JButton optBouquetButton;
	JButton resetButton;
	JButton nextPlanButton;
	JButton selectivityInputButton;

	/*
	 * selectivity location for simulated execution
	 */
	JTextField selX;
	JTextField selY;
	
	JComboBox<String> executionMode;
	JComboBox<String> executionMethod;
	
	/*
	 * status panel labels
	 */
	JLabel timeLabel;
//	JLabel planContourLabel;
	JLabel executionStatusLabel;
	
	boolean executionFlag;
	
	/*
	 * sub-optimality bar chart data structures
	 */
	DefaultCategoryDataset dataset;
	DefaultCategoryDataset simulationDataset;
	DefaultCategoryDataset subOptimalityDataset;
	
	/*
	 * basic and optimized bouquet objects
	 */
	BasicBouquetExecution basicBouquetObj;
	OptBouquetExecution optBouquetObj;
	
	BasicBouquetSimulation basicBouquetSimulation;
	double optCost;
	
	boolean isBasicBouquetExec;
	boolean isSimulatedExecution;
	
	int contourForSimulation;
	int planForSimulation;
	public BouquetPanel(AllObjects allObjects)
	{
		allObjects.setBouquetPanelObj(this);
		this.allObjects = allObjects;
		mainPanel = new JPanel();
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setLayout(new BorderLayout());
//		contourForSimulation = -1;
	}
	void initializePanel(AllObjects allObjects)
	{
//		allObjects.setBouquetPanelObj(this);
//		this.allObjects = allObjects;
//		mainPanel = new JPanel();
//		mainPanel.setBackground(Color.WHITE);
//		mainPanel.setLayout(new BorderLayout());
		contourForSimulation = -1;
	}
	void addComponentsToPanel(AllObjects allObjects)
	{
		int barPanelHeight;
		
		barPanelHeight = (int)QUESTConstants.SCREEN_HEIGHT / 5;
		
		mainPanel.removeAll();
		
		JPanel inputPanel = getInputPanel();
		
		int contoursPanelHeight = (int)(QUESTConstants.SCREEN_HEIGHT - QUESTConstants.HEAD_PANEL_HEIGHT - QUESTConstants.INPUT_PANEL_HEIGHT - barPanelHeight - QUESTConstants.STATUS_PANEL_HEIGHT - 50);
		
		/*
		 * this panel contains contours plot panel, sub-optimality bar chart and text fields for inputing selectivity.
		 */
		JPanel contourPanel = getWestPanel(allObjects, contoursPanelHeight);
		
		/*
		 * During bouquet execution plans tree are shown on this panel.
		 */
		JPanel plansExecutionPanel = getPlansExecutionPanel(allObjects);
		
		JPanel statusPanel = getStatusPanel();
		
		/*
		 * plans bar for real execution
		 */
		barPanel = getPlanBars(allObjects, barPanelHeight);			
		
		/*
		 * plans bar for abstract execution
		 */
		simulationBarPanel = getSimulationPlanBars(allObjects, barPanelHeight);	
		
		JLabel l = new JLabel("Plans Execution Timeline");
		l.setFont(new Font(QUESTConstants.TEXT_FONT, Font.BOLD, QUESTConstants.MEDIUM_FONT_SIZE));		//For demo
//		l.setFont(new Font(QUESTConstants.TEXT_FONT, Font.BOLD, 16));
		
		JPanel p = new JPanel();
		p.setBackground(Color.WHITE);
		p.add(l);
		
		Box vBox = Box.createVerticalBox();
		vBox.add(p);
		vBox.add(plansExecutionPanel);
		
		/*
		 * status panel
		 */
		southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(BorderLayout.CENTER, simulationBarPanel);
		southPanel.add(BorderLayout.SOUTH, statusPanel);
		
		mainPanel.add(BorderLayout.NORTH, inputPanel);
		mainPanel.add(BorderLayout.CENTER, vBox);
		mainPanel.add(BorderLayout.WEST, contourPanel);
		mainPanel.add(BorderLayout.SOUTH, southPanel);
		mainPanel.revalidate();
		mainPanel.repaint();
		
	}
	/*
	 * this function returns input panel for bouquet execution tab
	 */
	private JPanel getInputPanel()
	{
		int font_size;
		
		if(allObjects.demo_mode)
			font_size = 12;
		else
			font_size = 18;
		
		JLabel executionModeLabel = QUESTUtility.createLabel("Execution Mode ",font_size );
		
		executionMode = new JComboBox<String>();
		executionMode.addItem("Abstract Execution");
		executionMode.addItem("Real Execution");
		executionMode.addActionListener(this);
		executionMode.setMaximumSize( executionMode.getPreferredSize() );

		JLabel executionMethodLabel = QUESTUtility.createLabel("Method of Execution ", font_size);
		
		executionMethod = new JComboBox<String>();
		executionMethod.addItem("without user control");
		executionMethod.addItem("with user control");
		executionMethod.addActionListener(this);
		executionMethod.setMaximumSize( executionMethod.getPreferredSize() );
			
		basicBouquetButton = QUESTUtility.createButton("Run Basic Bouquet", font_size);
		basicBouquetButton.addActionListener(this);
		
		optBouquetButton = QUESTUtility.createButton("Run Enhanced Bouquet", font_size);
		optBouquetButton.addActionListener(this);
		optBouquetButton.setEnabled(false);

		resetButton = QUESTUtility.createButton("Reset", font_size);
		resetButton.addActionListener(this);
		
		nextPlanButton = QUESTUtility.createButton("Execute Next Plan",font_size);
		nextPlanButton.setEnabled(false);
		nextPlanButton.addActionListener(this);
		nextPlanButton.setVisible(false);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		inputPanel.setPreferredSize(new Dimension(200, QUESTConstants.INPUT_PANEL_HEIGHT));
		inputPanel.setBackground(new Color(QUESTConstants.backgroundColor2));
		
		inputPanel.add(Box.createGlue());
		inputPanel.add(executionModeLabel);
		inputPanel.add(executionMode);
		inputPanel.add(Box.createHorizontalStrut(30));
		inputPanel.add(executionMethodLabel);
		inputPanel.add(executionMethod);
		inputPanel.add(Box.createGlue());
		inputPanel.add(basicBouquetButton);
		inputPanel.add(optBouquetButton);
		inputPanel.add(resetButton);
		inputPanel.add(nextPlanButton);
		inputPanel.add(Box.createGlue());
		
		inputPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));	
		
		return(inputPanel);
	}
	/*
	 * this function returns panel containing contours plot panel, sub-optimality bar chart, and text fields for entering selectivity.
	 */
	private JPanel getWestPanel(AllObjects allObjects, int height)
	{
		int panelWidth;
		int contourPlotheight;
		int suboptimalityBarPanelHeight;
		panelWidth = (int)(QUESTConstants.SCREEN_WIDTH/3.6);
		
		contourPlotheight = (int)(height/1.8);
		suboptimalityBarPanelHeight = height/4;
		
		if(panelWidth<contourPlotheight)
		{
			contourPlotheight = panelWidth - (int)(panelWidth*0.1);
		}
		else
		{
			panelWidth = contourPlotheight + (int)(contourPlotheight *0.25);
		}
		
		/*
		 * It will plot contours on xy line chart.
		 */
		DrawContours drawContoursObj = new DrawContours(allObjects);
		JPanel contoursPlotPanel = drawContoursObj.drawContour(allObjects, panelWidth, contourPlotheight);
		
		/*
		 * selectivity entering components are initialized.
		 */
		int font_size;
		
		if(allObjects.demo_mode)
			font_size = 12;
		else
			font_size = 16;
		
		JLabel sel = QUESTUtility.createLabel("Actual Sel.", font_size);
			
		selX = new JTextField(3);
		selX.setMaximumSize(selX.getPreferredSize());
		selY = new JTextField(3);
		selY.setMaximumSize(selY.getPreferredSize());
		
		if(allObjects.demo_mode)
			selectivityInputButton = QUESTUtility.createButton("Enter Sel.", 12);
		else
			selectivityInputButton = QUESTUtility.createButton("Enter Sel.", QUESTConstants.BUTTON_FONT_SIZE);
		
		selectivityInputButton.addActionListener(this);
		
		JPanel selPanel = new JPanel();
		selPanel.setLayout(new BoxLayout(selPanel, BoxLayout.X_AXIS));
		selPanel.setBackground(Color.WHITE);
		
		selPanel.add(Box.createGlue());
		selPanel.add(sel);
		selPanel.add(Box.createGlue());
		
		selPanel.add(QUESTUtility.createLabel("x = ", font_size));
		selPanel.add(selX);
		selPanel.add(Box.createGlue());
		
		
		selPanel.add(QUESTUtility.createLabel("y = ", font_size));
		selPanel.add(selY);
		selPanel.add(Box.createGlue());
		selPanel.add(selectivityInputButton);
		selPanel.add(Box.createGlue());
		
		/*
		 * sub-optimality bar chart is initialized.
		 */
		JPanel subOptBarsPanel = getSubOptimalityBarChart(panelWidth, suboptimalityBarPanelHeight);
		
		Box topVBox = Box.createVerticalBox();
		topVBox.add(contoursPlotPanel);
		topVBox.add(Box.createGlue());
		topVBox.add(selPanel);
		topVBox.add(Box.createGlue());
		topVBox.add(subOptBarsPanel);
		
		JPanel contourPanel = new JPanel();
		contourPanel.setLayout(new BorderLayout());
		contourPanel.setBackground(Color.WHITE);
		contourPanel.add(BorderLayout.CENTER, topVBox);
		return(contourPanel);
		
	}
	private JPanel getPlansExecutionPanel(AllObjects allObjects)
	{
		execInfoPanelObj = new ExecutionInformationPanel(allObjects);
		
		JPanel execInfoPanel = execInfoPanelObj.getPanel();
		return(execInfoPanel);
	}
	
	/*
	 * this function returns plan bar chart panel for real execution
	 */
	private JPanel getPlanBars(AllObjects allObjects, int height)
	{
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
	
		int totalContours = bouquetDataObj.getTotalContours();
		int contourPlansCount[] = bouquetDataObj.getContourPlansCount();
		int contourPlans[][] = bouquetDataObj.getContourPlans();
		int finalPlanSet[] = bouquetDataObj.getFinalPlanSet();
		double timeLimit[] = bouquetDataObj.getTimeLimit();
		
		dataset = new DefaultCategoryDataset();
		
		for(int i=0;i<totalContours;i++)
		{
			for(int j=0;j<contourPlansCount[i];j++)
			{
				int plan = Arrays.binarySearch(finalPlanSet, contourPlans[i][j]);
				
				dataset.addValue(0, "runningValue", "IC"+(i+1)+"_P"+(plan+1));
				dataset.addValue(timeLimit[i]/1000, "initialValue", "IC"+(i+1)+"_P"+(plan+1));
			}
		}
		
		JFreeChart chart = ChartFactory.createStackedBarChart
				("Budgeted Plan Execution","IsocostContour_Plan", "Time", dataset, 
						PlotOrientation.VERTICAL, false,true, false);
		
		chart.getTitle().setFont(new Font(QUESTConstants.COMPONENT_FONT, Font.BOLD, 18));		//For demo
//		chart.getTitle().setFont(new Font(QUESTConstants.COMPONENT_FONT, Font.BOLD, 12));
		
		chart.setBackgroundPaint(Color.WHITE);
		
		CategoryPlot p = chart.getCategoryPlot(); 
		
		p.setRangeGridlinesVisible(true);
		p.setBackgroundPaint(Color.lightGray);
//		p.setBackgroundPaint(Color.WHITE);
		p.setRangeGridlinePaint(Color.BLACK); 
		p.getRenderer().setSeriesPaint(1, Color.WHITE);
		p.getRenderer().setSeriesPaint(0, Color.BLUE);


		BarRenderer barRenderer = (BarRenderer)p.getRenderer();
		barRenderer.setBarPainter(new StandardBarPainter());
		
	    StackedBarRenderer r = (StackedBarRenderer) p.getRenderer();
	    r.setBase(0.001);
		
//		CategoryAxis axis = p.getDomainAxis();
//		axis.setTickLabelFont(new Font("Serif", Font.PLAIN, 10));
		
	    double minTimeLimit = timeLimit[0]/1000;
	    double maxTimeLimit = timeLimit[totalContours-1]/1000;
	    double value = 2.0;
	    while(value>=minTimeLimit)
	    	value /= 2;
		LogAxis range = new LogAxis("Time (log scale)");
		range.setBase(2);
		range.setTickUnit(new NumberTickUnit(1));
		range.setRange(value, maxTimeLimit);
		range.setAutoRange(false);
//		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.AXIS_TICK_FONT_SIZE));			//For demo
//		range.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_SAMLL_FONT_SIZE));
		
		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		range.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
		p.setRangeAxis(range);

	
		BarRenderer renderer = (BarRenderer) p.getRenderer();
		renderer.setMaximumBarWidth(0.01);
		
		
        CategoryAxis xAxis = (CategoryAxis)p.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
//		xAxis.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_SAMLL_FONT_SIZE));		//For demo
        xAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
       
		ChartPanel cPanel = new ChartPanel(chart);
//		cPanel.setPreferredSize(new Dimension(700, 300));				//uncomment
//		cPanel.setPreferredSize(new Dimension(700, 230));		//For demo
//		cPanel.setPreferredSize(new Dimension(700, 170));
		cPanel.setPreferredSize(new Dimension(700, height));
		return(cPanel);
	}
	
	/*
	 * this function returns plan bar chart panel for abstract execution
	 */
	private JPanel getSimulationPlanBars(AllObjects allObjects, int height)
	{
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
	
		int totalContours = bouquetDataObj.getTotalContours();
		int contourPlansCount[] = bouquetDataObj.getContourPlansCount();
		int contourPlans[][] = bouquetDataObj.getContourPlans();
		int finalPlanSet[] = bouquetDataObj.getFinalPlanSet();
		
		double time_limit[] = new double[totalContours];
		time_limit[totalContours-1] = 4.0;
		for(int i=totalContours-2;i>=0;i--)
		{
			time_limit[i] = time_limit[i+1]/2;
		}
		
		simulationDataset = new DefaultCategoryDataset();
		for(int i=0;i<totalContours;i++)
		{
			for(int j=0;j<contourPlansCount[i];j++)
			{
				int plan = Arrays.binarySearch(finalPlanSet, contourPlans[i][j]);
				simulationDataset.addValue(time_limit[i], "initialValue", "IC"+(i+1)+"_P"+(plan+1));
				simulationDataset.addValue(0, "runningValue", "IC"+(i+1)+"_P"+(plan+1));
			}
		}

		JFreeChart chart = ChartFactory.createStackedBarChart
				("Budgeted Plan Execution","IsocostContour_Plan", "Time", simulationDataset, 
						PlotOrientation.VERTICAL, false,true, false);
		
		chart.getTitle().setFont(new Font(QUESTConstants.COMPONENT_FONT, Font.BOLD, 18));		//For demo
//		chart.getTitle().setFont(new Font(QUESTConstants.COMPONENT_FONT, Font.BOLD, 12));
		chart.setBackgroundPaint(Color.WHITE);
		CategoryPlot p = chart.getCategoryPlot(); 
		p.setRangeGridlinesVisible(true);
//		p.setBackgroundPaint(Color.WHITE);
		p.setRangeGridlinePaint(Color.BLACK); 
		p.getRenderer().setSeriesPaint(0, Color.WHITE);
		p.getRenderer().setSeriesPaint(1, Color.BLUE);

		BarRenderer barRenderer = (BarRenderer)p.getRenderer();
		barRenderer.setBarPainter(new StandardBarPainter());
		
	    StackedBarRenderer r = (StackedBarRenderer) p.getRenderer();
	    r.setBase(0.001);
		
//		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
//		BarRenderer.setDefaultBarPainter(new StandardBarPainter());
		
		
//		CategoryAxis axis = p.getDomainAxis();
//		axis.setTickLabelFont(new Font("Serif", Font.PLAIN, 10));
		
	    double minRange = time_limit[0]/2;
	    double maxRange = time_limit[totalContours-1] * 2;

		LogAxis range = new LogAxis("Time (log scale)");
		range.setBase(2);
		range.setTickUnit(new NumberTickUnit(1));
		range.setRange(minRange, maxRange);
		range.setAutoRange(false);
//		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.AXIS_TICK_FONT_SIZE));			//For demo
//		range.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_SAMLL_FONT_SIZE));
		
		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		range.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
		p.setRangeAxis(range);

	
		BarRenderer renderer = (BarRenderer) p.getRenderer();
		renderer.setMaximumBarWidth(0.01);
		
		
        CategoryAxis xAxis = (CategoryAxis)p.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
//        xAxis.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_SAMLL_FONT_SIZE));		//For demo
        xAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
		
		ChartPanel cPanel = new ChartPanel(chart);
//		cPanel.setPreferredSize(new Dimension(700, 240));		//For demo
//		cPanel.setPreferredSize(new Dimension(700, 170));
		cPanel.setPreferredSize(new Dimension(700, height));
		return(cPanel);
	}
	
	/*
	 * this function returns status panel.
	 */
	private JPanel getStatusPanel()
	{		
		Font f = new Font(QUESTConstants.TEXT_FONT,Font.PLAIN,QUESTConstants.STATUS_PANEL_FONT_SIZE);
		
		executionStatusLabel = new JLabel("");
		executionStatusLabel.setFont(f);
		executionStatusLabel.setForeground(Color.WHITE);
		
		timeLabel = new JLabel("");
		timeLabel.setFont(f);
		timeLabel.setForeground(Color.WHITE);
		
		JPanel statusPanel = new JPanel();
		statusPanel.setPreferredSize(new Dimension(200, QUESTConstants.STATUS_PANEL_HEIGHT));
		statusPanel.setBackground(new Color(QUESTConstants.STATUS_PANEL_COLOR));
		
		statusPanel.add(executionStatusLabel);
		statusPanel.add(timeLabel);
		
		return(statusPanel);
	}
	
	/*
	 * this function returns sub-optimality bar chart panel for basic and optimized bouquet execution
	 */
	JPanel getSubOptimalityBarChart(int width, int height)
	{
		subOptimalityDataset = new DefaultCategoryDataset();
		subOptimalityDataset.addValue(0.0001, "basicBouquet", "Basic Bouquet");
		subOptimalityDataset.addValue(0.0001, "optBouquet", "Enhanced Bouquet");
	
		
		JFreeChart chart = ChartFactory.createStackedBarChart
				("Sub-optimality (log scale)","Execution Method", "Sub-opt", subOptimalityDataset, 
						PlotOrientation.VERTICAL, false,true, false);
		
		if(allObjects.demo_mode)
			chart.getTitle().setFont(new Font("Dialog", Font.PLAIN, 12));
		
		CategoryPlot plot = chart.getCategoryPlot(); 

		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		int contourPlansCount[] = bouquetDataObj.getContourPlansCount();
		int totalContours = bouquetDataObj.getTotalContours();
		int rho=0;
		for(int i=0;i<totalContours;i++)
		{
			if(rho<contourPlansCount[i])
				rho = contourPlansCount[i];
		}
		int lemda = bouquetDataObj.getLemda();
		double MSO = 4.0 * rho * (1+lemda/100.0);
		System.out.println("MSO="+MSO);
		int a=1;
		while(a<MSO)
		{
			a=a*2;
		}
		Marker start = new ValueMarker(MSO);
		start.setLabel("MSO Bound");
		start.setLabelAnchor(RectangleAnchor.BOTTOM);
//		start.setLabelOffset(RectangleInsets)
		start.setLabelPaint(Color.RED);
//		start.setLabelTextAnchor(TextAnchor.);
		start.setLabelOffset(new RectangleInsets(0, 0, 10, 0));
		
		if(allObjects.demo_mode)
			start.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
		else
			start.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_TICK_FONT_SIZE));		//For demo
		
		start.setPaint(Color.BLACK);
		float[] dashed = new float[] {10.0f, 10.0f};
		BasicStroke stroke = new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10.0f, dashed, 10.0f);

		start.setStroke(stroke);
		plot.addRangeMarker(start);
		
		plot.setRangeGridlinesVisible(false);
		plot.setBackgroundPaint(Color.WHITE);
		

		BarRenderer barRenderer = (BarRenderer)plot.getRenderer();
		barRenderer.setBarPainter(new StandardBarPainter());

//		NumberAxis range = (NumberAxis)plot.getRangeAxis();
//		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.AXIS_TICK_FONT_SIZE));
//		range.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE));
		CategoryAxis domain = plot.getDomainAxis();
		if(allObjects.demo_mode)
		{
			domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
			domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		}
		else
		{
			domain.setTickLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_TICK_FONT_SIZE));		//For demo
			domain.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE));
		}
		
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setMaximumBarWidth(0.1);
		
		renderer.setSeriesPaint(0, Color.BLUE);
		renderer.setSeriesPaint(1, Color.GREEN);
		
		
		LogAxis range = new LogAxis("Sub-optimality");
		range.setBase(2);
		range.setTickUnit(new NumberTickUnit(1));
		range.setRange(1, a);
//		range.setRange(1, 32);
		range.setAutoRange(false);
		
		if(allObjects.demo_mode)
		{
			range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
			range.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));			
		}
		else
		{
			range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.AXIS_TICK_FONT_SIZE));		//For demo
			range.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE));
		}
		plot.setRangeAxis(range);
		
	    StackedBarRenderer r = (StackedBarRenderer) plot.getRenderer();
	    r.setBase(0.001);
		
		ChartPanel cPanel = new ChartPanel(chart);
		if(allObjects.demo_mode)
			cPanel.setPreferredSize(new Dimension(350, 130));
		else
		{
//			cPanel.setPreferredSize(new Dimension(500, 190));		//For demo
//			cPanel.setPreferredSize(new Dimension(350, 130));
//			cPanel.setPreferredSize(new Dimension(500, 200));
			cPanel.setPreferredSize(new Dimension(width, height));
		}
		return(cPanel);
	}
	
	/*
	 * this function returns coordinate of selectivity location given in actualSelectivity[] array. 
	 */
	int[] getESSCoordinate(double actualSelectivity[], BouquetDriver bouquetDriverObj, int resolution, int dimension)
	{
		int flag[] = new int [dimension];
		int loc[] = new int[dimension];
		Arrays.fill(flag, 0);
		for(int i=0;i<resolution;i++)
		{
			for(int j=0;j<dimension;j++)
			{
				if(actualSelectivity[j] < bouquetDriverObj.picsel[i] && flag[j]==0)
				{
					loc[j] = Math.max(i-1, 0);
					flag[j] = 1;
				}
			}
		}
		return(loc);
	}
	/*
	 * this function calculates index of single dimension array for multi dimensions array.  
	 */
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
	public void actionPerformed(ActionEvent e)
	{
		/*
		 * determines abstract or real execution
		 */
		if(e.getSource() == executionMode)
		{
			int index = executionMode.getSelectedIndex();
			if(index == QUESTConstants.ABSTRACT_EXECUTION)
			{
				optBouquetButton.setEnabled(false);
				selectivityInputButton.setEnabled(true);
				selX.setEnabled(true);
				selY.setEnabled(true);
				southPanel.remove(barPanel);
				southPanel.add(BorderLayout.CENTER, simulationBarPanel);
				southPanel.revalidate();
				southPanel.repaint();
			}
			else										//Real Execution
			{
				optBouquetButton.setEnabled(true);
				selectivityInputButton.setEnabled(false);
				selX.setEnabled(false);
				selY.setEnabled(false);
				southPanel.remove(simulationBarPanel);
				southPanel.add(BorderLayout.CENTER, barPanel);
				southPanel.revalidate();
				southPanel.repaint();
			}
		}
		
		/*
		 * with user control or without user control
		 */
		else if(e.getSource() == executionMethod)
		{
			int index = executionMethod.getSelectedIndex();
			if(index == QUESTConstants.WITH_USER_CONTROL)
			{
				nextPlanButton.setVisible(true);
			}
			else					//without user control
			{
				nextPlanButton.setVisible(false);
			}
		}
		/*
		 * selectivity location is fetched from two text boxes.
		 * Then contour and plan is determine up to which execution needs to be done. 
		 */
		else if(e.getSource() == selectivityInputButton)
		{
			BouquetDriver bouquetDriverObj = allObjects.getBouquetDriverObj();
			BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
			int resolution = bouquetDataObj.getResolution();
			int dimension = bouquetDataObj.getDimension();
			double selectivity[] = new double[dimension];
			
			String selXStr = selX.getText();
			String selYStr = selY.getText();
			
			if(!selXStr.matches("[0-9]+\\.?+[0-9]*")||!selYStr.matches("[0-9]+\\.?+[0-9]*"))
			{
				JOptionPane.showMessageDialog(new JFrame(),
						"Enter valid selectivity location",
					    "Warning",
					    JOptionPane.WARNING_MESSAGE);
			}
			else
			{
		
				selectivity[0] = Double.parseDouble(selX.getText());
				selectivity[1] = Double.parseDouble(selY.getText());
				
				if(selectivity[0]>100||selectivity[1]>100)
				{
					JOptionPane.showMessageDialog(new JFrame(),
							"Enter selectivity location between 0% and 100%",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					int location[] = getESSCoordinate(selectivity, bouquetDriverObj, resolution, dimension);

					int loc = addressCalc(resolution, location, dimension); 
					int optimalPlan = bouquetDriverObj.newOptimalPlan[loc];
					optCost = bouquetDriverObj.AllPlanCosts[optimalPlan][loc];
					double costLimit[] = bouquetDataObj.getCostLimit();
					int contPlans[][] = bouquetDataObj.getContourPlans();
					int totalContours = bouquetDataObj.getTotalContours();
					int finalPlanList[] = bouquetDataObj.getFinalPlanSet();
					int i = 0;
					/*
					 * it will identify contour under which selectivity is located.
					 */
					while(optCost > costLimit[i])
					{
						i++;
					}
					if(i>=totalContours)
					{
						System.out.println("error in locating selectivity point");
					}

					int contour = i;

					int contoursPlanCount[] = bouquetDataObj.getContourPlansCount();

					/*
					 * in terms of selectivity
					 */
					double planLocationMaxCoordinate[][][] = bouquetDataObj.getPlanLocationMaxCoordinate();
					double planLocationMinCoordinate[][][] = bouquetDataObj.getPlanLocationMinCoordinate();

					int j;
					int actualPlan=0;
					/*
					 * plan that can execute query location is determined.  
					 */
					for(j=0;j<contoursPlanCount[i];j++)
					{
						int plan = contPlans[contour][j];
						int index = Arrays.binarySearch(finalPlanList, plan);
						if(selectivity[0]<planLocationMaxCoordinate[contour][index][0] && selectivity[1] < planLocationMinCoordinate[contour][index][1])
						{
							actualPlan = index;
							break;
						}
					}


					System.out.println("contour = "+contour);
					System.out.println("plan = "+(actualPlan+1));
					DrawContours drawContoursObj = allObjects.getDrawContoursObj();
					drawContoursObj.addSelectivityPoint(selectivity);
					planForSimulation = j;
					
					contourForSimulation = contour;
				}
			}			
		}
		else if(e.getSource() == basicBouquetButton)
		{
			int execModeIndex = executionMode.getSelectedIndex();
			int execMethodindex = executionMethod.getSelectedIndex();
			
			if(execModeIndex == QUESTConstants.ABSTRACT_EXECUTION)
			{
				if(contourForSimulation == -1)
				{
					JOptionPane.showMessageDialog(new JFrame(),
						    "Please enter the selectivity location.",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
				}
				else
				{
//					QUESTUtility.clearCache();
					startExecution();
					ExecutionInformationPanel execInfoPanelObj = allObjects.getExecutionInformationPanelObj();
					execInfoPanelObj.startExecution();
					isSimulatedExecution = true;
					if(execMethodindex == QUESTConstants.WITHOUT_USER_CONTROL)
					{
						basicBouquetSimulation = new BasicBouquetSimulation(allObjects, contourForSimulation, planForSimulation, optCost);
						new Thread(new Runnable() 
						{
							public void run() 
							{
								executionStatusLabel.setText("Status: Executing Query   ");

								basicBouquetSimulation.simulateBasicBouquet(allObjects);
								long executionTime = basicBouquetSimulation.executionTime;
								int totalPlanExecution = basicBouquetSimulation.totalPlanExecution;
								endExecution(executionTime, totalPlanExecution);
								subOptimalityDataset.setValue(basicBouquetSimulation.subOptimality, "basicBouquet", "Basic Bouquet");
							}
						}).start();
					}
					else
					{
						basicBouquetSimulation = new BasicBouquetSimulation(allObjects, contourForSimulation, planForSimulation, optCost);
						nextPlanButton.setEnabled(true);
						isBasicBouquetExec = true;
						executionStatusLabel.setText("Status: Waiting for user input   ");
					}
				}
			}
			else
			{
				QUESTUtility.clearCache();
				startExecution();
				ExecutionInformationPanel execInfoPanelObj = allObjects.getExecutionInformationPanelObj();
				execInfoPanelObj.startExecution();
				isSimulatedExecution = false;
				if(execMethodindex == QUESTConstants.WITHOUT_USER_CONTROL)
				{
//					final BasicBouquetExecution basicBouquetObj = new BasicBouquetExecution(allObjects);
					basicBouquetObj = new BasicBouquetExecution(allObjects);
					new Thread(new Runnable() 
					{
						public void run() 
						{
							basicBouquetObj.runBasicBouquet(allObjects);
							long executionTime = basicBouquetObj.executionTime;
							int totalPlanExecution = basicBouquetObj.totalPlanExecution;
							endExecution(executionTime, totalPlanExecution);
							subOptimalityDataset.setValue(basicBouquetObj.subOptimality, "basicBouquet", "Basic Bouquet");

						}
					}).start();
				}
				else
				{
					basicBouquetObj = new BasicBouquetExecution(allObjects);
					nextPlanButton.setEnabled(true);
					isBasicBouquetExec = true;
					executionStatusLabel.setText("Status: Waiting for user input   ");
				}
			}
		}
		else if(e.getSource() == optBouquetButton)
		{
			QUESTUtility.clearCache();
			startExecution();
			ExecutionInformationPanel execInfoPanelObj = allObjects.getExecutionInformationPanelObj();
			execInfoPanelObj.startExecution();
			int index = executionMethod.getSelectedIndex();
			
			if(index == QUESTConstants.WITHOUT_USER_CONTROL)
			{
				final OptBouquetExecution optBouquetObj = new OptBouquetExecution(allObjects);
				new Thread(new Runnable() 
				{
					public void run() 
					{
						optBouquetObj.runOptBouquet(allObjects);
						long executionTime = optBouquetObj.executionTime;
						int totalPlanExecution = optBouquetObj.totalPlanExecution;
						endExecution(executionTime, totalPlanExecution);
						
						subOptimalityDataset.setValue(optBouquetObj.subOptimality, "optBouquet", "Enhanced Bouquet");
					}
				}).start();
			}
			else
			{
				optBouquetObj = new OptBouquetExecution(allObjects);
				optBouquetObj.preprocessOptBouquetSinglePlanRun(allObjects);
				nextPlanButton.setEnabled(true);
				isBasicBouquetExec = false;
			}
		}
		/*
		 * this event is generated when next Plan button is clicked during with user control execution.
		 */
		else if(e.getSource() == nextPlanButton)
		{
			new Thread(new Runnable() 
			{
				public void run() 
				{
					nextPlanButton.setEnabled(false);
					executionStatusLabel.setText("Status: Executing Plan   ");
					if(isSimulatedExecution)
					{
						if(isBasicBouquetExec)
						{
							boolean executionCompleted = basicBouquetSimulation.simulateBasicBouquetSinglePlan(allObjects);
							if(executionCompleted)
							{
								subOptimalityDataset.setValue(basicBouquetSimulation.subOptimality, "basicBouquet", "Basic Bouquet");
								long executionTime = basicBouquetSimulation.executionTime;
								int totalPlanExecution = basicBouquetSimulation.totalPlanExecution;
								endExecution(executionTime, totalPlanExecution);
								JOptionPane.showMessageDialog(new JFrame(),
									    "Execution Completed",
									    "Message",
									    JOptionPane.PLAIN_MESSAGE);
								
							}
							else
							{
								nextPlanButton.setEnabled(true);
								executionStatusLabel.setText("Status: Waiting for user input   ");
							}
						}
						else
						{
							//Simulation for optimized bouquet is not done
						}
					}
					else
					{
						if(isBasicBouquetExec)			//basic bouquet
						{
							boolean executionCompleted = basicBouquetObj.runBasicBouquetSinglePlan(allObjects);
							if(executionCompleted)
							{
								long executionTime = basicBouquetObj.executionTime;
								int totalPlanExecution = basicBouquetObj.totalPlanExecution;
								endExecution(executionTime, totalPlanExecution);
								subOptimalityDataset.setValue(basicBouquetObj.subOptimality, "basicBouquet", "Basic Bouquet");
								JOptionPane.showMessageDialog(new JFrame(),
									    "Execution Completed",
									    "Message",
									    JOptionPane.PLAIN_MESSAGE);
							}
							else		
							{
								nextPlanButton.setEnabled(true);
								executionStatusLabel.setText("Status: Waiting for user input   ");
							}
						}
						else					//Optimized bouquet
						{
							boolean executionCompleted = optBouquetObj.runOptBouquetSinglePlan(allObjects);
							if(executionCompleted)
							{
								long executionTime = optBouquetObj.executionTime;
								int totalPlanExecution = optBouquetObj.totalPlanExecution;
								endExecution(executionTime, totalPlanExecution);
								subOptimalityDataset.setValue(optBouquetObj.subOptimality, "optBouquet", "Enhanced Bouquet");
								JOptionPane.showMessageDialog(new JFrame(),
									    "Execution Completed",
									    "Message",
									    JOptionPane.PLAIN_MESSAGE);
							}
							else
							{
								nextPlanButton.setEnabled(true);
								executionStatusLabel.setText("Status: Waiting for user input   ");
							}
						}
					}
				}
			}).start();
		}
		else if(e.getSource() == resetButton)
		{
//			contourForSimulation = -1;
//			addComponentsToPanel(allObjects);
			DrawContours drawContoursObj = allObjects.getDrawContoursObj();
			contourForSimulation = -1;
			drawContoursObj.clearSelectivityPoint();
			
			subOptimalityDataset.setValue(0.0001, "basicBouquet", "Basic Bouquet");
			subOptimalityDataset.setValue(0.0001, "optBouquet", "Enhanced Bouquet");
			
			
			resetPanel();
		}
	}
	void resetPanel()
	{
		
		/*
		 * contours plot cleaning
		 */
		DrawContours drawContoursObj = allObjects.getDrawContoursObj();
		drawContoursObj.resetContoursPlot(allObjects);
		
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		
		int totalContours = bouquetDataObj.getTotalContours();
		int contourPlansCount[] = bouquetDataObj.getContourPlansCount();
		int contourPlans[][] = bouquetDataObj.getContourPlans();
		int finalPlanSet[] = bouquetDataObj.getFinalPlanSet();
		double timeLimit[] = bouquetDataObj.getTimeLimit();
		
		/*
		 * real plan execution bar cleaning
		 */
		for(int i=0;i<totalContours;i++)
		{
			for(int j=0;j<contourPlansCount[i];j++)
			{
				int plan = Arrays.binarySearch(finalPlanSet, contourPlans[i][j]);
				dataset.setValue(timeLimit[i]/1000, "initialValue", "IC"+(i+1)+"_P"+(plan+1));
				dataset.setValue(0, "runningValue", "IC"+(i+1)+"_P"+(plan+1));
			}
		}
		
		/*
		 * simulated bars clearing
		 */
		double time_limit[] = new double[totalContours];
		time_limit[totalContours-1] = 4.0;
		for(int i=totalContours-2;i>=0;i--)
		{
			time_limit[i] = time_limit[i+1]/2;
		}
		

		simulationDataset.clear();
		simulationBarPanel.revalidate();
		simulationBarPanel.repaint();
		
		for(int i=0;i<totalContours;i++)
		{
			for(int j=0;j<contourPlansCount[i];j++)
			{
				int plan = Arrays.binarySearch(finalPlanSet, contourPlans[i][j]);
				simulationDataset.addValue(time_limit[i], "initialValue", "IC"+(i+1)+"_P"+(plan+1));
				simulationDataset.addValue(0, "runningValue", "IC"+(i+1)+"_P"+(plan+1));
			}
		}
		execInfoPanelObj.clearExecutionInformationPanel();
	}
	void startExecution()
	{
		resetPanel();
		executionStatusLabel.setText("");
		timeLabel.setText("");
		
		basicBouquetButton.setEnabled(false);
		optBouquetButton.setEnabled(false);
		resetButton.setEnabled(false);
		
		executionMode.setEnabled(false);
		executionMethod.setEnabled(false);
		
		startTimerClock();
	}
	void endExecution(long executionTime, int totalPlanExecution)
	{
		executionStatusLabel.setText("Status: Execution Completed   ");
		
		double time = executionTime / 1000.0;
		time *= 10.0;
		time = Math.round(time);
		time /= 10.0;
		
		int execModeIndex = executionMode.getSelectedIndex();
		if(execModeIndex == QUESTConstants.REAL_EXECUTION)
		{
			optBouquetButton.setEnabled(true);
		}
		basicBouquetButton.setEnabled(true);
		resetButton.setEnabled(true);
		
		executionMode.setEnabled(true);
		executionMethod.setEnabled(true);
		
		executionFlag = false;
	}
	void startTimerClock()
	{
		new Thread(new Runnable() 
		{
			public void run() 
			{
				final long start_time = System.currentTimeMillis();
				executionFlag = true;
				while(true && executionFlag)
				{
					long currentTime = System.currentTimeMillis();
					int time_elapsed = (int)((currentTime - start_time)/1000.0);
					int t = time_elapsed;
					int hours = (int)t/3600;
					t = t%3600;
					int min = (int)t/60;
					t = t%60;
					int sec = (int)t;
				
					timeLabel.setText("Total Execution Time ="+ String.format("%02d",hours)+":"+ String.format("%02d", min)+":"+ String.format("%02d",sec));
					
//					dataset.setValue(time_elapsed, "runningValue", "C"+(basicBouquetObj.currentContour+1)+"_P"+(basicBouquetObj.currentPlanNumber+1));
					
					try
					{
						Thread.sleep(500);
					}
					catch(Exception e)
					{
						
					}
				}
				JOptionPane.showMessageDialog(new JFrame(),
					    "Execution Completed",
					    "Message",
					    JOptionPane.PLAIN_MESSAGE);
			}
		}).start();
	}
}
