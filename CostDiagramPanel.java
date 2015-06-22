package quest;
/*
 * mainPanel
 * _____________________________________
 * |		input panel			|		|	
 * |____________________________|plan	|
 * |							|Set	|
 * |							|and	|
 * |							|contour|
 * |		mainSubPanel		|Info	|
 * |							|Box	|
 * |							|		|
 * |____________________________|		|
 * |		status panel		|		|
 * |____________________________|_______|
 * 
 * 
 * mainSubPanel
 *  _____________________________
 * |		costHeadingPanel	|	
 * |____________________________|
 * |							|
 * |							|
 * |							|
 * |reducedAndPOSPCostDiagram	|
 * |Panel						|
 * |							|
 * |							|
 * |							|
 * |____________________________|
 * 
 *  reducedAndPOSPCostDiagramPanel
 *  _____________________________
 * |			|				|	
 * |			|				|
 * |			|				|
 * |costDiagram	|reducedCost	|
 * |POSPPanel	|DiagramPanel	|
 * |			|				|
 * |			|				|
 * |			|				|
 * |			|				|
 * |			|				|
 * |____________|_______________|
 * 
 * 
 *  costDiagramPOSPPanel
 *  _____________________________
 * |	totalPlansPOSPInfoPanel	|	
 * |____________________________|
 * |							|
 * |							|
 * |							|
 * |		costDiagramPOSP		|
 * |							|
 * |							|
 * |							|
 * |							|
 * |____________________________|
 * 
 * 
 */


import iisc.dsl.picasso.common.PicassoConstants;
import iisc.dsl.picasso.common.ds.DiagramPacket;
import iisc.dsl.picasso.common.ds.TreeNode;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
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
import org.jfree.ui.TextAnchor;
import org.omg.CORBA.Environment;

import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
public class CostDiagramPanel implements ActionListener
{
	JPanel mainPanel;				//top panel of tab
	JPanel pnlStatusBar;				
	JLabel statusLabel;
	
	/*
	 * input panel components
	 */
	JTextField anorexicReduction;
	
	JButton reducePlans;
	JButton findContours;
	
	JLabel lblCommonRatio;    /* added by rajmohan in july 2014 */
	JTextField txtCommonRatio;
	DefaultCategoryDataset msoSubOptimalityDataset;
	
	
	/*
	 * component for holding 3-D diagram 
	 */
	Component cmpntReducedCostDiag;
	
	int totalPlans;
	/*
	 * components for showing plans tree structure
	 */
	JRadioButton rdoBouquetPlans[];
	JButton btnShowBouquetPlan;
	TreeNode planTreeRootNodes[];
	
	Box boxPlanSetAndContourInfo;
	
	JPanel pnlCombinedCostDiagAndHeading;
	
	JPanel pnlCostDiagramMain;		//Holds cost diagram before reduction
	JPanel pnlReducedCostDiagMain;		//Holds cost diagram after reduction
	JPanel contourInfoPanel;			//Holds contours information panel.
	JPanel msoSubOptimalityPanel;
	
	AllObjects allObjects;
	public CostDiagramPanel(AllObjects allObject)
	{
		allObject.setCostDiagramPanelObj(this);
		this.allObjects = allObject;
		
		mainPanel = new JPanel();
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setLayout(new BorderLayout());

	}
	void addComponentsToPanel(AllObjects allObjects)
	{
		int totalContours;
		DiagramPacket gdp;
		boolean showHorizontalPlanes = false;
		
		mainPanel.removeAll();
		
		/** Modified by Rajmohan in July 2014 */
		
		/******************************** NON-GUI Actions ***********************************/
		
		/* Get Plan cost diagram */
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		totalContours = bouquetDataObj.getTotalContours();
		gdp = bouquetDataObj.getDiagramPacket();

		DrawCostDiagram obj =new DrawCostDiagram();
		
		/* draw cost diagram with full plans in POSP. It is drawn without horizontal planes */
		Component costDiagramPOSP = obj.drawCostDiagram(gdp, totalContours-1, showHorizontalPlanes, allObjects);
		
		
		/********************************* GUI Actions ***************************************/
		
		JLabel lblTotalPlans1;
		JLabel lblOrignalCostDiag;
		
		/* Create heading label for original cost diagram */
		lblTotalPlans1 = QUESTUtility.createHeadingLabel("(Total Plans = "+gdp.getMaxPlanNumber() + ")");
		lblOrignalCostDiag = QUESTUtility.createHeadingLabel("Original Cost Diagram  ");
		lblTotalPlans1.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblOrignalCostDiag.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		/* Create a panel to hold heading labels and Original Cost Diagram in a Box Layout(vertically) */
		pnlCostDiagramMain = new JPanel();
		pnlCostDiagramMain.setBackground(Color.WHITE);
		pnlCostDiagramMain.setLayout(new BoxLayout(pnlCostDiagramMain, BoxLayout.Y_AXIS));
		
		/* Add heading label and Original Cost Diagram to OriginalCostDiagram Main Panel */
		pnlCostDiagramMain.add(Box.createVerticalStrut(30));
		pnlCostDiagramMain.add(lblOrignalCostDiag);
		pnlCostDiagramMain.add(lblTotalPlans1);
		pnlCostDiagramMain.add(costDiagramPOSP);

		/* Create an empty Reduced Cost Diagram Main Panel */
		pnlReducedCostDiagMain = new JPanel();
		pnlReducedCostDiagMain.setBackground(Color.WHITE);
		pnlReducedCostDiagMain.setLayout(new BoxLayout(pnlReducedCostDiagMain, BoxLayout.Y_AXIS));
		

		/* Create a panel for holding Combined cost diagrams */
		JPanel pnlCombinedCostDiagrams = new JPanel();
		pnlCombinedCostDiagrams.setBackground(Color.WHITE);
		pnlCombinedCostDiagrams.setLayout(new GridLayout(1, 2));
		
		/* Add original and reduced cost diagram panels to combined cost diagram panel */
		pnlCombinedCostDiagrams.add(pnlCostDiagramMain);
		pnlCombinedCostDiagrams.add(pnlReducedCostDiagMain);
		
		/* Create a panel that will hold Combined Cost Diagram panel and Parameter Input panel into a single panel */
		pnlCombinedCostDiagAndHeading = new JPanel();
		pnlCombinedCostDiagAndHeading.setBackground(Color.WHITE);
		pnlCombinedCostDiagAndHeading.setLayout(new BorderLayout());
		pnlCombinedCostDiagAndHeading.add(BorderLayout.CENTER,pnlCombinedCostDiagrams);
		
		/* Get the Parameter Input panel populated */
		JPanel pnlBouquetPrmtrEntry = getInputPanel();
		pnlCombinedCostDiagAndHeading.add(BorderLayout.SOUTH, pnlBouquetPrmtrEntry);
		
		/* Create and populate status bar panel for showing status */
		pnlStatusBar = new JPanel();
		statusLabel = new JLabel("");
		
		statusLabel.setFont(new Font(QUESTConstants.TEXT_FONT, Font.PLAIN, QUESTConstants.STATUS_PANEL_FONT_SIZE));
		statusLabel.setText("Status: Showing Cost Diagram");
		statusLabel.setForeground(Color.WHITE);
	
		pnlStatusBar.setBackground(new Color(QUESTConstants.STATUS_PANEL_COLOR));
		pnlStatusBar.add(statusLabel);
		
		boxPlanSetAndContourInfo = Box.createVerticalBox();
		
		/* Finally populate the main panel */
		mainPanel.add(BorderLayout.CENTER, pnlCombinedCostDiagAndHeading);
		mainPanel.add(BorderLayout.SOUTH, pnlStatusBar);
		mainPanel.add(BorderLayout.EAST, boxPlanSetAndContourInfo);
		
		mainPanel.revalidate();
		mainPanel.repaint();
	}
	
	/*
	 * this function returns input panel.
	 */
	JPanel getInputPanel()
	{
		/*
		 * components of input panel are initialized
		 */
		int font_size = 18;
		if(allObjects.demo_mode)
			font_size = 15;
		
		JLabel anorexicReductionLabel = QUESTUtility.createLabel("Anorexic Reduction Parameter (%)", font_size);
		anorexicReductionLabel.setFont(new Font("Arial",Font.BOLD,font_size));// rajmohan
		
		anorexicReduction = new JTextField(3);
		anorexicReduction.setFont(new Font("Arial",Font.BOLD,font_size));
		anorexicReduction.setMaximumSize(anorexicReduction.getPreferredSize());
		anorexicReduction.setText("20");    // default text added by rajmohan in july 2014
		
		/* Added by Rajmohan in July 2014 */
		/* geometric progression common ratio parameter 'r' */
		lblCommonRatio = QUESTUtility.createLabel("Contour Cost Ratio", font_size);
		lblCommonRatio.setFont(new Font("Arial",Font.BOLD,font_size));
		txtCommonRatio = new JTextField(3);
		txtCommonRatio.setFont(new Font("Arial",Font.BOLD,font_size));
		txtCommonRatio.setColumns(2);
		
		txtCommonRatio.setMaximumSize(txtCommonRatio.getPreferredSize());
		lblCommonRatio.setEnabled(false);
		txtCommonRatio.setEnabled(false);
		
		reducePlans = QUESTUtility.createButton("Reduce Plans", font_size);
		reducePlans.setFont(new Font("Arial",Font.BOLD,font_size));
		
		findContours = QUESTUtility.createButton("Find Isocost Contours", font_size);
		findContours.setFont(new Font("Arial",Font.BOLD,font_size));
		
		reducePlans.addActionListener(this);
		findContours.addActionListener(this);
		
		findContours.setEnabled(false);
		
		/*
		 * input panel is initialized and all components are added.
		 */
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		inputPanel.setPreferredSize(new Dimension(200, QUESTConstants.INPUT_PANEL_HEIGHT));
		inputPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		inputPanel.setBackground(new Color(QUESTConstants.backgroundColor2));
		
		inputPanel.add(Box.createGlue());
		inputPanel.add(anorexicReductionLabel);
		inputPanel.add(Box.createHorizontalStrut(10));
		inputPanel.add(anorexicReduction);
		inputPanel.add(Box.createHorizontalStrut(10));
		inputPanel.add(reducePlans);
		
		/* Added by Rajmohan in July 2014 */
		/* geometric progression common ratio parameter 'r' label and textbox are added to panel */
		inputPanel.add(Box.createGlue());
		inputPanel.add(Box.createHorizontalStrut(10));
		inputPanel.add(lblCommonRatio);
		inputPanel.add(Box.createHorizontalStrut(10));
		inputPanel.add(txtCommonRatio);
		inputPanel.add(Box.createHorizontalStrut(10));
		
//		inputPanel.add(Box.createGlue());
		inputPanel.add(findContours);
		inputPanel.add(Box.createGlue());
		
		return(inputPanel);
	}

	
	private JPanel getPlanSetPanel(int width, int height, BouquetData bouquetDataObj)
	{	
		int font_size = 20;
		if(allObjects.demo_mode)
			font_size = 15;
		
		int finalPlanSetArray[] = bouquetDataObj.getFinalPlanSet();
		
		rdoBouquetPlans = new JRadioButton[totalPlans];
		ButtonGroup btngrpBouquetPlans = new ButtonGroup();
		Box boxBouquet =  new Box(BoxLayout.PAGE_AXIS);
		
		for(int i = 0;i < totalPlans;i++)
		{		
			rdoBouquetPlans[i] = QUESTUtility.createRadioButton("Plan P"+(i+1), PicassoConstants.color[finalPlanSetArray[i] % PicassoConstants.color.length]);
			rdoBouquetPlans[i].setFont(new Font("Arial",Font.BOLD,font_size));
			rdoBouquetPlans[i].setAlignmentX(Component.CENTER_ALIGNMENT);
			btngrpBouquetPlans.add(rdoBouquetPlans[i]);
			boxBouquet.add(rdoBouquetPlans[i]);
			boxBouquet.add(Box.createVerticalGlue());
		}
		
		btnShowBouquetPlan = new JButton("Show Plan");
		Font f;
		
		if(allObjects.demo_mode)
			f = new Font("Times New Roman", Font.BOLD, font_size);
		else
			f = new Font("Times New Roman", Font.BOLD, QUESTConstants.BUTTON_FONT_SIZE);
		
		btnShowBouquetPlan.setFont(f);
		
		btnShowBouquetPlan.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnShowBouquetPlan.addActionListener(this);
		
		boxBouquet.add(btnShowBouquetPlan);
		
		
		JScrollPane scrlBouquetBox = new JScrollPane(boxBouquet);
		scrlBouquetBox.setPreferredSize(new Dimension(width, height));
		scrlBouquetBox.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrlBouquetBox.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrlBouquetBox.setViewportBorder(null);
		
		JPanel panel = new JPanel();
		
		panel.setBackground(Color.WHITE);
		panel.add(scrlBouquetBox);
		
		panel.setBorder(BorderFactory.createTitledBorder(null, "Plan Bouquet", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial",Font.BOLD,font_size), Color.BLACK));
		
		return(panel);
	}
	
	/*
	 * this function returns panel containing contours with their cost and plans lying on those contours
	 */
	private JPanel getContoursInformationPanel(int width, int height)
	{
		int font_size = 20 ;
		
		if(allObjects.demo_mode)
			font_size = 15;
		
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		
		int totalContours = bouquetDataObj.getTotalContours();
		int contourPlansCount[] = bouquetDataObj.getContourPlansCount();
		int contourPlans[][] = bouquetDataObj.getContourPlans();
//		double costLimit[] = bouquetDataObj.getCostLimit();
		int finalPlanSet[] = bouquetDataObj.getFinalPlanSet();
		
		JPanel contourPanel = new JPanel();
		contourPanel.setBackground(Color.WHITE);
			
		Box infoVBox = Box.createVerticalBox();
		
//		NumberFormat formatter = new DecimalFormat("#.#E0");
		JLabel infoLabels[][] = new JLabel[totalContours][3];
		
		JLabel lbltemp;
		Font fonttemp;
		fonttemp = new Font(QUESTConstants.TEXT_FONT, Font.BOLD, font_size);
		for(int i=0;i<totalContours;i++)
		{	
			lbltemp = new JLabel("IC "+(i+1));
			
			lbltemp.setFont(fonttemp);
			
			infoLabels[i][0] = lbltemp;

//			infoLabels[i][1] = QUESTUtility.createHeadingLable("Cost = "+(int)costLimit[i]);
//			double multi = Math.pow(0.5, (totalContours-1-i));
			double multi = Math.pow(1.0/(bouquetDataObj.getCommonRatio()), (totalContours-1-i));
			
			
			lbltemp = new JLabel("Cost = "+String.format("%3.2f", multi)+" * MaxCost");
//			Font f = new Font(QUESTConstants.TEXT_FONT, Font.PLAIN, QUESTConstants.MEDIUM_FONT_SIZE);		//For demo
			lbltemp.setFont(fonttemp);
			
			infoLabels[i][1] = lbltemp;
//			QUESTUtility.createContourLabel("Cost = "+String.format("%3.2f", multi)+" * MaxCost");
//			infoLabels[i][1] = QUESTUtility.createHeadingLable("Cost = "+String.format("%3.2f", multi)+"*"+"(<html>ax<sup>2</sup>+bx+c</html>)");
			
			String str = "";
			for(int j=0;j<contourPlansCount[i];j++)
			{
				str += "P"+(Arrays.binarySearch(finalPlanSet, contourPlans[i][j])+1);
				if(j!=contourPlansCount[i]-1)
					str += ", ";
			}
			
			lbltemp = new JLabel("Plans = "+str);
			lbltemp.setFont(fonttemp);
			infoLabels[i][2] = lbltemp;
			
//			infoLabels[i][0].setFont(f);
//			infoLabels[i][1].setFont(f);
//			infoLabels[i][2].setFont(f);
			
			infoVBox.add(infoLabels[i][0]);
			infoVBox.add(infoLabels[i][1]);
			infoVBox.add(infoLabels[i][2]);
			infoVBox.add(new JSeparator(JSeparator.HORIZONTAL));
		}
		
		JScrollPane infoPanelScroll = new JScrollPane(infoVBox);
//		infoPanelScroll.setPreferredSize(new Dimension(310, 400));			//For demo
//		infoPanelScroll.setPreferredSize(new Dimension(250, 270));
		infoPanelScroll.setPreferredSize(new Dimension(width, height));	
		infoPanelScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		infoPanelScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		infoPanelScroll.setViewportBorder(null);
		
		contourPanel.add(infoPanelScroll);
//		contourPanel.setBorder(BorderFactory.createTitledBorder(null, "Isocost Contours (IC) Information", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial",Font.BOLD,17), Color.BLACK));
		contourPanel.setBorder(BorderFactory.createTitledBorder(null, "Isocost Contours (IC)", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial",Font.BOLD,font_size), Color.BLACK));
//		contourPanel.setBorder(BorderFactory.createTitledBorder("Contours Information"));
		return(contourPanel);
	}
	
	/* Added by rajmohan in July 2014 
	 * Calculates MSO bound based on Lambda and Rho
	 */

	JPanel getMSOSubOptimalityBarChart(int width, int height)
	{
		msoSubOptimalityDataset = new DefaultCategoryDataset();
		
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		int contourPlansCount[] = bouquetDataObj.getContourPlansCount();
		int totalContours = bouquetDataObj.getTotalContours();
		int rho = 0;
		
		for(int i = 0;i < totalContours;i++)
		{
			if(rho < contourPlansCount[i])
				rho = contourPlansCount[i];
		}
		
		int lambda = bouquetDataObj.getLemda();
		double ratio = bouquetDataObj.getCommonRatio();
		
		double MSO = rho *((ratio * ratio)/(ratio - 1)) * (1 + lambda / 100.0);
		
		/* optimal mso is calculated with lambda = 20 and r = 2 */
		double optimal_mso = 4.0 * rho * (1 + 20 / 100.0);
		
		System.out.println("Optimal MSO = "+optimal_mso);
		System.out.println("MSO = "+MSO);
		
		int a = 1;
		while(a < MSO)
		{
			a = a * 2;
		}
		String cur_values = "(" + bouquetDataObj.getLemda() + "%, " + bouquetDataObj.getCommonRatio() + ")";
		msoSubOptimalityDataset.addValue(MSO, "parameters", cur_values);
	
		JFreeChart chart = ChartFactory.createStackedBarChart
				("MSO Guarantee(log scale)","Bouquet Parameters", "Sub-opt", msoSubOptimalityDataset, 
						PlotOrientation.VERTICAL, false,true, false);
		
		CategoryPlot plot = chart.getCategoryPlot(); 

		
		Marker start = new ValueMarker(optimal_mso);
		start.setLabel("Default");	
		start.setLabelPaint(Color.RED);
		
		start.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        start.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        
		start.setLabelOffset(new RectangleInsets(10, 0, 10, 0));
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

		CategoryAxis domain = plot.getDomainAxis();
		domain.setTickLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_TICK_FONT_SIZE));		//For demo
		domain.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE));
	
		
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setMaximumBarWidth(0.1);
		
		renderer.setSeriesPaint(0, Color.BLUE);
		renderer.setSeriesPaint(1, Color.GREEN);
		
		LogAxis range = new LogAxis("MSO");
		range.setBase(2);
		range.setTickUnit(new NumberTickUnit(1));
		range.setRange(1, a);
		range.setAutoRange(false);
		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.AXIS_TICK_FONT_SIZE));		//For demo
		range.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE));
		
		plot.setRangeAxis(range);
		
	    StackedBarRenderer r = (StackedBarRenderer) plot.getRenderer();
	    r.setBase(0.001);
		
		ChartPanel cPanel = new ChartPanel(chart);
		cPanel.setPreferredSize(new Dimension(width, height));
		return(cPanel);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == reducePlans)
		{
			MainFrame mainFrameObj = allObjects.getMainFrameObj();
			mainFrameObj.tabbedPaneOpenFirstTime[QUESTConstants.BOUQUET_PANE] = true;
			mainFrameObj.allTabs.setEnabledAt(QUESTConstants.BOUQUET_PANE, false);
			pnlReducedCostDiagMain.removeAll();
			boxPlanSetAndContourInfo.removeAll();
			
			String str = anorexicReduction.getText();
			if(str.equals(null)||(!str.matches("\\d+")))
			{
				JOptionPane.showMessageDialog(new JFrame(),
						"Enter valid reduction parameter",
					    "Warning",
					    JOptionPane.WARNING_MESSAGE);
			}
			else
			{
				boolean showPlanes = false;
				int lamda  = Integer.parseInt(str);
				
				/* added by rajmohan in july 2014
				 *  If reduction parameter is zero, nothing is to be done at all. 
				 *  Just reproduce original cost diagram in place of reduced cost diagram.
				 */
				if(lamda == 0)   /*TODO*/
				{
					
				}
				
				BouquetDriver bouquetDriverObj = allObjects.getBouquetDriverObj();
				BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
				
				/* Call the main method which does plan reduction */
				bouquetDriverObj.reducePlans(allObjects, lamda);
			
				totalPlans = bouquetDataObj.getTotalPlans();
				
				DiagramPacket reducedGDP = bouquetDataObj.getReducedDiagramPacket();
				
				PostgresRun pgRunObj = new PostgresRun(allObjects);
				pgRunObj.run_postgres(allObjects);

				planTreeRootNodes = bouquetDataObj.getPlanTreeRootNodes();
				
				DrawCostDiagram obj = new DrawCostDiagram();
				cmpntReducedCostDiag = obj.drawCostDiagram(reducedGDP, 0, showPlanes, allObjects);		// 0 is for total horizontal planes
				
				
				/*****************************************  GUI ACTIONS ***************************************/
				
				JLabel lblReducedPlans;
				JLabel lblReducedCostDiag;
				
				/* Create heading label for original cost diagram */
				lblReducedCostDiag = QUESTUtility.createHeadingLabel("Anorexic Cost Diagram  ");
				lblReducedPlans = QUESTUtility.createHeadingLabel("(Total Plans = "+reducedGDP.getNoOfPlans() + ")");
				
				lblReducedPlans.setAlignmentX(Component.CENTER_ALIGNMENT);
				lblReducedCostDiag.setAlignmentX(Component.CENTER_ALIGNMENT);
				
				/* Add heading label and Original Cost Diagram to OriginalCostDiagram Main Panel */
				pnlReducedCostDiagMain.add(Box.createVerticalStrut(30));
				pnlReducedCostDiagMain.add(lblReducedCostDiag);
				pnlReducedCostDiagMain.add(lblReducedPlans);
				pnlReducedCostDiagMain.add(cmpntReducedCostDiag);
				
				/************************************* </GUI ACTIONS> ******************************************/
				
				pnlCombinedCostDiagAndHeading.revalidate();
				pnlCombinedCostDiagAndHeading.repaint();
				
				statusLabel.setText("Status: Showing Reduced Cost Diagram");

				int width  = (int)(QUESTConstants.SCREEN_WIDTH / 7);
				int height = (int)(QUESTConstants.SCREEN_HEIGHT / 5);

				JPanel planSetPanel = getPlanSetPanel(width,height,bouquetDataObj);
				boxPlanSetAndContourInfo.add(planSetPanel);
				boxPlanSetAndContourInfo.add(Box.createVerticalGlue());

				pnlReducedCostDiagMain.revalidate();
				pnlReducedCostDiagMain.repaint();

				findContours.setEnabled(true);
				
				/* added by rajmohan in july 2014 */
				lblCommonRatio.setEnabled(true);
				txtCommonRatio.setEnabled(true);
				txtCommonRatio.setText("2");
				
				
				mainPanel.revalidate();
				mainPanel.repaint();
				
//				BouquetPanel objBouquetPanel = allObjects.getBouquetPanelObj();
//				objBouquetPanel.resetPanel();
//				objBouquetPanel.initializePanel(allObjects);
			}
		}
		if(e.getSource() == findContours)
		{
			
			/* Common ratio Validity check is added by rajmohan in july 2014 */
			String str = txtCommonRatio.getText();
			/*[0-9]{1,2}(\\.[0-9]*)? ===> one or two digits before decimal point. one digit after
			 * decimal point. decimal point is optional */
			if(str.equals(null) || (!str.matches("[0-9]+(\\.[0-9]*)?")) ||  Double.parseDouble(str) <= 1)
			{
				JOptionPane.showMessageDialog(new JFrame(),
						"Enter valid Common Ratio parameter",
					    "Warning",
					    JOptionPane.WARNING_MESSAGE);
			}
			else
			{
				
				pnlReducedCostDiagMain.removeAll();
				
				/* removing contours information from vertical box */
				if(contourInfoPanel != null)
					boxPlanSetAndContourInfo.remove(contourInfoPanel);
				
				if(msoSubOptimalityPanel != null)
					boxPlanSetAndContourInfo.remove(msoSubOptimalityPanel);
				
				/* modified by rajmohan in july 2014 */
				double common_ratio  = Double.parseDouble(str);
				BouquetDriver bouquetDriverObj = allObjects.getBouquetDriverObj();
				BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
				
				bouquetDataObj.setCommonRatio(common_ratio);
			
				bouquetDriverObj.worstCG_Solution_expo(allObjects);		
				
				int totalContours = bouquetDataObj.getTotalContours();
				DiagramPacket reducedGDP = bouquetDataObj.getReducedDiagramPacket();
				
				boolean showPlanes = true;
				DrawCostDiagram obj = new DrawCostDiagram();
				Component cmpntReducedCostDiagWithContours = obj.drawCostDiagram(reducedGDP, totalContours, showPlanes, allObjects);
				
				/*****************************************  GUI ACTIONS ***************************************/
				
				JLabel lblReducedPlans;
				JLabel lblReducedCostDiag;
				
				/* Create heading label for original cost diagram */
				lblReducedCostDiag = QUESTUtility.createHeadingLabel("Anorexic Cost Diagram  ");
				lblReducedPlans = QUESTUtility.createHeadingLabel("(Total Plans = "+reducedGDP.getNoOfPlans() + ")");
				
				lblReducedPlans.setAlignmentX(Component.CENTER_ALIGNMENT);
				lblReducedCostDiag.setAlignmentX(Component.CENTER_ALIGNMENT);
				
				/* Add heading label and Original Cost Diagram to OriginalCostDiagram Main Panel */
				pnlReducedCostDiagMain.add(Box.createVerticalStrut(30));
				pnlReducedCostDiagMain.add(lblReducedCostDiag);
				pnlReducedCostDiagMain.add(lblReducedPlans);
				pnlReducedCostDiagMain.add(cmpntReducedCostDiagWithContours);
				
				/************************************* </GUI ACTIONS> ******************************************/
				
//				JLabel l = QUESTUtility.createHeadingLabel("(Total Plans = "+reducedGDP.getNoOfPlans() + ")");//reducedGDP.getMaxPlanNumber());
//				
//				JPanel costDigInfoPanel = new JPanel();
//				costDigInfoPanel.setBackground(Color.WHITE);
//				costDigInfoPanel.setLayout(new BoxLayout(costDigInfoPanel, BoxLayout.X_AXIS));
//	
//				costDigInfoPanel.add(Box.createGlue());
//				costDigInfoPanel.add(Box.createVerticalStrut(20));
//				costDigInfoPanel.add(QUESTUtility.createHeadingLabel("Anorexic Cost Diagram  "));
////				costDigInfoPanel.add(Box.createGlue());
//				costDigInfoPanel.add(l);
//				costDigInfoPanel.add(Box.createGlue());
//				
//				pnlReducedCostDiagMain.add(BorderLayout.NORTH, costDigInfoPanel);
				pnlReducedCostDiagMain.add(BorderLayout.CENTER, cmpntReducedCostDiagWithContours);
				
				///////////////////////////////////////////////////////////////////////
				
				int width1  = (int)(QUESTConstants.SCREEN_WIDTH / 7);
				int height1 = (int)(QUESTConstants.SCREEN_HEIGHT / 3);
				
				int panelWidth;
				int contourPlotheight;
				int suboptimalityBarPanelHeight;
				panelWidth = (int)(QUESTConstants.SCREEN_WIDTH/3.6);
				
				contourPlotheight = (int)(height1/1.5);
				suboptimalityBarPanelHeight = height1/2;
				
				if(panelWidth<contourPlotheight)
				{
					contourPlotheight = panelWidth - (int)(panelWidth*0.1);
				}
				else
				{
					panelWidth = contourPlotheight + (int)(contourPlotheight *0.25);
				}
				
				msoSubOptimalityPanel = getMSOSubOptimalityBarChart(panelWidth, suboptimalityBarPanelHeight);
				
//				Box topVBox = Box.createVerticalBox();
//				topVBox.add(Box.createGlue());
//				topVBox.add(subOptBarsPanel);
				
				//////////////////////////////////////////////////////////////////////////////////////////
	
				pnlCombinedCostDiagAndHeading.revalidate();
				pnlCombinedCostDiagAndHeading.repaint();
				
				statusLabel.setText("Status: Showing Isocost Planes");
				
				int width  = (int)(QUESTConstants.SCREEN_WIDTH / 7);
				int height = (int)(QUESTConstants.SCREEN_HEIGHT / 3);
				
				contourInfoPanel = getContoursInformationPanel(width, height);
				boxPlanSetAndContourInfo.add(contourInfoPanel);
				
				boxPlanSetAndContourInfo.add(msoSubOptimalityPanel);
				boxPlanSetAndContourInfo.add(Box.createGlue());		// added by rajmohan
				
				MainFrame mainFrameObj = allObjects.getMainFrameObj();
				mainFrameObj.allTabs.setEnabledAt(QUESTConstants.BOUQUET_PANE, true);
				
				pnlReducedCostDiagMain.revalidate();
				pnlReducedCostDiagMain.repaint();
				
				mainPanel.revalidate();
				mainPanel.repaint();
				
				MainFrame objMainframe = allObjects.getMainFrameObj();
				
				/* to handle modications to lambda and alpha dynamically 
				 * added by rajmohan on aug 23, 2014 */
				objMainframe.tabbedPaneOpenFirstTime[QUESTConstants.BOUQUET_PANE] = true;
				objMainframe.tabbedPaneOpenFirstTime[QUESTConstants.RESULT_PANE] = true;
//				BouquetPanel objBouquetPanel = allObjects.getBouquetPanelObj();
//				objBouquetPanel.resetPanel();
//				objBouquetPanel.initializePanel(allObjects);
			}
		}
		if(e.getSource() == btnShowBouquetPlan)
		{
			int i;
			for(i=0;i<totalPlans;i++)
			{
				if(rdoBouquetPlans[i].isSelected())
					break;
			}
			if(i < totalPlans)
			{
				boolean showNeverExecNodes = false;
				PicassoUtil.displayTree(planTreeRootNodes[i], null, showNeverExecNodes);
			}
			else
			{
				JOptionPane.showMessageDialog(new JFrame(),
					    "Select Plan.",
					    "Warning",
					    JOptionPane.WARNING_MESSAGE);
			}
		}
	}
}
