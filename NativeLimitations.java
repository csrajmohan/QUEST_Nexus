package quest;
import iisc.dsl.picasso.common.ds.TreeNode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Shape;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;

import java.awt.TextArea;
import java.awt.Panel;

import javax.swing.JButton;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ShapeUtilities;
import org.postgresql.util.PSQLException;

import java.awt.Window.Type;
import java.awt.Label;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.SwingConstants;

import java.awt.Component;

import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;


public class NativeLimitations extends JFrame {

	private JPanel contentPane;
	JPanel pnlQ1PlanTree;
	JPanel pnlQ1Chart;
	JTextArea queryInputTextAreaQ1;
	JTextArea queryInputTextAreaQ2;
	JButton btnQ1Execute;
	JButton btnQ1GetPlan;
	double Q1_exec_time;
	double Q2_exec_time;
	String Q1_str;
	String Q2_str;
	JButton btnQ2Execute;
	JButton btnQ2GetPlan;
	
	JLabel lblStatusMsg1;
	JLabel lblStatusMsg2;
	
	JPanel pnlQ2PlanTree, pnlQ1Center;
	JPanel pnlQ2East, pnlQ2Chart;
	boolean q2_first_plan_avbl = false, q1_first_plan_avbl = false;
	
	AllObjects allObjects;
	
	boolean executionFlag = true;
	
	
	XYSeries estimatedSelectivityPoint1, estimatedSelectivityPoint2;
	XYSeries actualSelectivityPoint1, actualSelectivityPoint2;
	XYItemRenderer ESSPlotRenderer1, ESSPlotRenderer2;
	DefaultCategoryDataset nativeSubOptData1, nativeSubOptData2;
	int estimatedTuples1[], estimatedTuples2[];
	int actualTuples1[], actualTuples2[];
	int maxTuples1[], maxTuples2[];
	String baseRelationQuery1[], baseRelationQuery2[];
	
	JButton btnQ1Cancel;
	JButton btnQ2Cancel;
	Connection conPgSql;
	PreparedStatement pstmtPgSql;
	JPanel pnlQ1East;
	private JLabel lblQ1PlanTree;
	private JLabel lblQ2PlanTree;
	private JLabel lblQ1ExecTime;
	private JLabel lblQ2ExecTime;

	String baseConditions1[];			
	String baseRelationNames1[];
	String baseConditions2[];			
	String baseRelationNames2[];
	
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					NativeLimitations frame = new NativeLimitations();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public NativeLimitations(AllObjects objallObjects) {
		
		q1_first_plan_avbl = false;
		q2_first_plan_avbl = false;
		Q1_str = "";
		Q2_str = "";
		Q1_exec_time = 0.0;
		Q2_exec_time = 0.0;
		allObjects = objallObjects;
		setType(Type.NORMAL);
		setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		setBounds(100, 100, 630, 300);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		String str = "";
		
		
		/* main south panel */
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlStatusBar = new JPanel();
		contentPane.add(pnlStatusBar, BorderLayout.SOUTH);
		
		lblStatusMsg1 = new JLabel("");
		lblStatusMsg1.setFont(new Font("Dialog", Font.BOLD, 12));
		pnlStatusBar.add(lblStatusMsg1);
		
		lblStatusMsg2 = new JLabel("");
		lblStatusMsg2.setFont(new Font("Dialog", Font.BOLD, 12));
		pnlStatusBar.add(lblStatusMsg2);
		
		JPanel pnlCenter = new JPanel();
		contentPane.add(pnlCenter);
		pnlCenter.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel pnlMainNorth = new JPanel();
		pnlMainNorth.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Query1", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
		pnlCenter.add(pnlMainNorth);
		pnlMainNorth.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlQ1West = new JPanel();
		pnlMainNorth.add(pnlQ1West, BorderLayout.WEST);
		pnlQ1West.setLayout(new BorderLayout(0, 0));
		
		queryInputTextAreaQ1 = new JTextArea(12, 25);
		queryInputTextAreaQ1.setFont(new Font("Dialog", Font.BOLD, 14));
		queryInputTextAreaQ1.setText(str);
		queryInputTextAreaQ1.setLineWrap(true);
		queryInputTextAreaQ1.selectAll();
		
		JScrollPane scrollPaneQ1 = new JScrollPane(queryInputTextAreaQ1, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		pnlQ1West.add(scrollPaneQ1, BorderLayout.CENTER);
		
		Panel pnlQ1Btns = new Panel();
		pnlQ1West.add(pnlQ1Btns, BorderLayout.SOUTH);
		
		btnQ1GetPlan = new JButton("Get Plan");
		btnQ1GetPlan.setFont(new Font("Dialog", Font.BOLD, 12));
		btnQ1GetPlan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				handle_get_plan_q1();
			}
		});
		pnlQ1Btns.add(btnQ1GetPlan);
		
		btnQ1Execute = new JButton("Execute");
		btnQ1Execute.setFont(new Font("Dialog", Font.BOLD, 12));
		btnQ1Execute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				handle_execute_q1();
			}
		});
		btnQ1Execute.setEnabled(false);
		pnlQ1Btns.add(btnQ1Execute);
		
		btnQ1Cancel = new JButton("Cancel");
		btnQ1Cancel.setFont(new Font("Dialog", Font.BOLD, 12));
		btnQ1Cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				handle_Q1_cancel();
			}
		});
		btnQ1Cancel.setVisible(false);
		pnlQ1Btns.add(btnQ1Cancel);
		
		pnlQ1Center = new JPanel();
		pnlMainNorth.add(pnlQ1Center, BorderLayout.CENTER);
		pnlQ1Center.setLayout(new BoxLayout(pnlQ1Center, BoxLayout.Y_AXIS));
		
		pnlQ1PlanTree = new JPanel();
		pnlQ1Center.add(pnlQ1PlanTree);
		
		lblQ1PlanTree = new JLabel("Plan Tree");
		lblQ1PlanTree.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblQ1PlanTree.setHorizontalAlignment(SwingConstants.CENTER);
		lblQ1PlanTree.setFont(new Font("Dialog", Font.BOLD, 14));
		lblQ1PlanTree.setVisible(false);
		
		pnlQ1Center.add(lblQ1PlanTree);
		pnlQ1Center.add(Box.createVerticalStrut(20));
		
		pnlQ1East = new JPanel();
		pnlMainNorth.add(pnlQ1East, BorderLayout.EAST);
		pnlQ1East.setLayout(new BoxLayout(pnlQ1East, BoxLayout.Y_AXIS));
		
		pnlQ1Chart = new JPanel();
		pnlQ1East.add(Box.createVerticalStrut(40));
		pnlQ1East.add(pnlQ1Chart);
		
		lblQ1ExecTime = new JLabel("Execution time: ");
		lblQ1ExecTime.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblQ1ExecTime.setHorizontalAlignment(SwingConstants.CENTER);
		lblQ1ExecTime.setFont(new Font("Dialog", Font.BOLD, 14));
		lblQ1ExecTime.setVisible(false);
		
		pnlQ1East.add(lblQ1ExecTime);
		pnlQ1East.add(Box.createVerticalStrut(20));
		
		/* Start of Query 2 */
		JPanel pnlMainSouth = new JPanel();
		pnlMainSouth.setBorder(new TitledBorder(null, "Query2", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlCenter.add(pnlMainSouth);
		pnlMainSouth.setLayout(new BorderLayout(0, 0));
						
		JPanel pnlQ2West = new JPanel();
		pnlMainSouth.add(pnlQ2West, BorderLayout.WEST);
		pnlQ2West.setLayout(new BorderLayout(0, 0));
		
		queryInputTextAreaQ2 = new JTextArea(12, 25);
		queryInputTextAreaQ2.setFont(new Font("Dialog", Font.BOLD, 14));
		queryInputTextAreaQ2.setLineWrap(true);
		queryInputTextAreaQ2.selectAll();
		
		JScrollPane scrollPaneQ2 = new JScrollPane(queryInputTextAreaQ2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		pnlQ2West.add(scrollPaneQ2, BorderLayout.CENTER);
		
		Panel pnlQ2Btns = new Panel();
		pnlQ2West.add(pnlQ2Btns, BorderLayout.SOUTH);
		
		btnQ2GetPlan = new JButton("Get Plan");
		btnQ2GetPlan.setFont(new Font("Dialog", Font.BOLD, 12));
		btnQ2GetPlan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handle_get_plan_q2();
			}
		});
		pnlQ2Btns.add(btnQ2GetPlan);
		
		btnQ2Execute = new JButton("Execute");
		btnQ2Execute.setFont(new Font("Dialog", Font.BOLD, 12));
		btnQ2Execute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handle_execute_q2();
			}
		});
		btnQ2Execute.setEnabled(false);
		pnlQ2Btns.add(btnQ2Execute);
		
		btnQ2Cancel = new JButton("Cancel");
		btnQ2Cancel.setFont(new Font("Dialog", Font.BOLD, 12));
		btnQ2Cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				handle_Q2_cancel();
			}
		});
		btnQ2Cancel.setVisible(false);
		pnlQ2Btns.add(btnQ2Cancel);
		
		JPanel pnlQ2Center = new JPanel();
		pnlMainSouth.add(pnlQ2Center, BorderLayout.CENTER);
		pnlQ2Center.setLayout(new BoxLayout(pnlQ2Center, BoxLayout.Y_AXIS));
		
		pnlQ2PlanTree = new JPanel();
		pnlQ2Center.add(pnlQ2PlanTree);
		
		lblQ2PlanTree = new JLabel("Plan Tree");
		lblQ2PlanTree.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblQ2PlanTree.setHorizontalAlignment(SwingConstants.CENTER);
		lblQ2PlanTree.setFont(new Font("Dialog", Font.BOLD, 14));
		lblQ2PlanTree.setVisible(false);
		
		pnlQ2Center.add(lblQ2PlanTree);
		pnlQ2Center.add(Box.createVerticalStrut(20));
		
//		pnlQ2East = new JPanel();
//		
//		pnlMainSouth.add(pnlQ2East, BorderLayout.EAST);
//		pnlQ2East.setLayout(new BoxLayout(pnlQ2East, BoxLayout.X_AXIS));
		pnlQ2East = new JPanel();
		pnlMainSouth.add(pnlQ2East, BorderLayout.EAST);
		pnlQ2East.setLayout(new BoxLayout(pnlQ2East, BoxLayout.Y_AXIS));
		
		pnlQ2Chart = new JPanel();
		pnlQ2East.add(Box.createVerticalStrut(40));
		pnlQ2East.add(pnlQ2Chart);
		
		lblQ2ExecTime = new JLabel("Execution time: ");
		lblQ2ExecTime.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblQ2ExecTime.setHorizontalAlignment(SwingConstants.CENTER);
		lblQ2ExecTime.setFont(new Font("Dialog", Font.BOLD, 14));
		lblQ2ExecTime.setVisible(false);
		
		pnlQ2East.add(lblQ2ExecTime);
		pnlQ2East.add(Box.createVerticalStrut(20));
		
	}
	
	void handle_Q1_cancel()
	{
		btnQ1Cancel.setVisible(false);
		btnQ1GetPlan.setEnabled(true);
		btnQ1Execute.setEnabled(true);
		
		btnQ2Cancel.setVisible(false);
		btnQ2GetPlan.setEnabled(true);
		if(q2_first_plan_avbl)
			btnQ2Execute.setEnabled(true);
		try
		{
			if(pstmtPgSql != null)
			{
				pstmtPgSql.cancel();
			}
		}
		catch(Exception psql)
		{
			psql.printStackTrace();
		}
	}
	void handle_Q2_cancel()
	{
		btnQ2Cancel.setVisible(false);
		btnQ2GetPlan.setEnabled(true);
		btnQ2Execute.setEnabled(true);
		
		btnQ1Cancel.setVisible(false);
		btnQ1GetPlan.setEnabled(true);
		if(q1_first_plan_avbl)
			btnQ1Execute.setEnabled(true);
		
		try
		{
			if(pstmtPgSql != null)
			{
				pstmtPgSql.cancel();
			}
		}
		catch(Exception psql)
		{
			psql.printStackTrace();
		}
	}
	
	/*
	 * This function parses output returned by database (in explain analyse format).
	 * It returns 'TreeNode' of root node of plan tree which is used in drawing plan tree.
	 */
	TreeNode getPlanTree(String query)
	{	
		Vector<String> textualPlan = new Vector<String>();
		TreeNode root = null;
		try
		{
			ConnectDB connectDBObj = allObjects.getConnectDBObj();
			Connection con = connectDBObj.connection;
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next())
			{
				String str = rs.getString(1);
				textualPlan.add(str);
			}
			
			CreatePlanTree createPlanTree = new CreatePlanTree();
			root = createPlanTree.getPlanStructure(textualPlan);
			rs.close();
			st.close();
		}
		catch(Exception e)
		{
			System.out.println("Execption is Native opt query execution: "+e);
			e.printStackTrace();
		}
		return(root);
	}
	
	void handle_get_plan_q1()
	{
		lblQ1ExecTime.setVisible(false);
		pnlQ1PlanTree.removeAll();
		pnlQ1Chart.removeAll();
		
		Q1_str = queryInputTextAreaQ1.getText().replaceAll("[\\t\\n\\r]+", " ");
		String query = Q1_str; 
		
		/*
		 * Getting TreeNode data structure of plan tree. 
		 * It will visually show plan in tree form. 
		 */
		TreeNode root = getPlanTree("explain "+query);
		DrawGraph drawGraphObj = new DrawGraph();
		double zoomLevel = 0.25;
		
		/*
		 * this function draws plan tree visually
		 */
		drawGraphObj.drawGraph(root, 0, zoomLevel);
		
		pnlQ1PlanTree.add(drawGraphObj.centerPanel);
		btnQ1Execute.setEnabled(true);

		/*
		 * Getting xy scatter plot chart for showing estimated and actual selectivity
		 */
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		JPanel chartPanel = getXYChart1(bouquetDataObj);
		
		pnlQ1Chart.add(Box.createHorizontalStrut(20));
		pnlQ1Chart.add(chartPanel);
		
		getEstimatedSelectivity1();
		
		lblQ1PlanTree.setVisible(true);

		pnlQ1Chart.revalidate();
		pnlQ1Chart.repaint();
		pnlQ1PlanTree.revalidate();
		pnlQ1PlanTree.repaint();
		q1_first_plan_avbl = true;
	}
	
	void handle_get_plan_q2()
	{
		lblQ2ExecTime.setVisible(false);
		pnlQ2PlanTree.removeAll();
		pnlQ2East.removeAll();
		
		Q2_str = queryInputTextAreaQ2.getText().replaceAll("[\\t\\n\\r]+", " ");
		String query = Q2_str;
		
		/*
		 * Getting TreeNode data structure of plan tree. 
		 * It will visually show plan in tree form. 
		 */
		TreeNode root = getPlanTree("explain "+query);
		DrawGraph drawGraphObj = new DrawGraph();
		double zoomLevel = 0.4;
		
		/*
		 * this function draws plan tree visually
		 */
		drawGraphObj.drawGraph(root, 0, zoomLevel);
		
		pnlQ2PlanTree.add(drawGraphObj.centerPanel);
		btnQ2Execute.setEnabled(true);

		/*
		 * Getting xy scatter plot chart for showing estimated and actual selectivity
		 */
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		JPanel chartPanel = getXYChart2(bouquetDataObj);
		
		pnlQ2East.add(Box.createHorizontalStrut(20));
		pnlQ2East.add(chartPanel);		
		getEstimatedSelectivity2();
		
		lblQ2PlanTree.setVisible(true);
		pnlQ2East.revalidate();
		pnlQ2East.repaint();
		pnlQ2PlanTree.revalidate();
		pnlQ2PlanTree.repaint();
		q2_first_plan_avbl = true;
	}
	
	/* This function create a xy scatter plot chart.
	   Through this chart, estimated and actual selectivities are shown */
	JPanel getXYChart1(BouquetData bouquetDataObj)
	{
		String errorProneBaseRelationNames[] = bouquetDataObj.getErrorProneBaseRelationNames();
		
		int height = (int)(QUESTConstants.SCREEN_HEIGHT / 4);
		
		XYSeriesCollection seriesCollection = new XYSeriesCollection();
		
		/* estimatedPoint and actualPoint are initialized but values are not put here */
		estimatedSelectivityPoint1 = new XYSeries("Estimated Selectivity");
		actualSelectivityPoint1 = new XYSeries("Actual Selectivity");

		seriesCollection.addSeries(estimatedSelectivityPoint1);
		seriesCollection.addSeries(actualSelectivityPoint1);

		JFreeChart chart = ChartFactory.createScatterPlot(
				"Error Selectivity Space(ESS) (log-log scale)", // chart title
				"Sel-1", // x axis label
				"Sel-2", // y axis label
				seriesCollection,
				PlotOrientation.VERTICAL,
				true, // include legend
				true, // tooltips
				false // urls
				);
		
//		chart.getTitle().setFont(new Font("Dialog", Font.BOLD, QUESTConstants.LARGE_FONT_SIZE));	//for demo
		chart.getTitle().setFont(new Font("Dialog", Font.BOLD, 12));
		LegendTitle legend = chart.getLegend();
//		Font labelFont = new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE);
//		Font labelFont = new Font("SansSerif", Font.BOLD, QUESTConstants.LARGE_FONT_SIZE);			//for demo
//		legend.setItemFont(labelFont);
		
		legend.setPosition(RectangleEdge.BOTTOM);
		
		XYPlot plot = chart.getXYPlot();
		
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRangeGridlinePaint(Color.BLACK);

		ESSPlotRenderer1 = plot.getRenderer();
		Shape s1 = new Ellipse2D.Double(0, 0, 10, 10);
//		Shape s1 = ShapeUtilities.createDiagonalCross(4.0f, 0.6f);
		ESSPlotRenderer1.setSeriesShape(0, s1);
		
		Shape s2 = ShapeUtilities.createDiamond(7.0f);
		ESSPlotRenderer1.setSeriesShape(1, s2);
		
		ESSPlotRenderer1.setSeriesVisibleInLegend(1, false);					//1 is for actual selectivity
		
		LogAxis x = new LogAxis(errorProneBaseRelationNames[0]+" selectivity");
		LogAxis y = new LogAxis(errorProneBaseRelationNames[1]+" selectivity");
		
//		x.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.LARGE_FONT_SIZE));		//For demo
//		y.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.LARGE_FONT_SIZE));
////		
//		x.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.LARGE_FONT_SIZE));
//		y.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.LARGE_FONT_SIZE));
		
//		x.setTickLabelFont(new Font("SansSerif", Font.PLAIN,12));		//For demo
//		y.setTickLabelFont(new Font("SansSerif", Font.PLAIN,12));
//		
//		x.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
//		y.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
		
		x.setBase(2);
		y.setBase(2);

		x.setTickUnit(new NumberTickUnit(2));
		y.setTickUnit(new NumberTickUnit(2));

		x.setRange(0.03125, 100);
		y.setRange(0.03125, 100);
		
		plot.setDomainAxis(x);
		plot.setRangeAxis(y);
		
		ChartPanel cPanel = new ChartPanel(chart);
		int width = (int)(height * 2);
		cPanel.setPreferredSize(new Dimension(width, height));		//for demo
//		cPanel.setPreferredSize(new Dimension(450, 200));
		cPanel.setMaximumSize(cPanel.getPreferredSize());
		return(cPanel);

	}
	/* This function calculates selectivity estimated by optimizer */
	void getEstimatedSelectivity1()
	{
		
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		int dimension = bouquetDataObj.getDimension();
		
		String errorProneBaseRelation[] = bouquetDataObj.getErrorProneBaseRelationNames();
		
		boolean retval = parse_input_query(Q1_str,allObjects, 1);
		if(retval == false)
		{
			JOptionPane.showMessageDialog(new JFrame(),
				    "Query 1 is not valid",
				    "Attention",
				    JOptionPane.PLAIN_MESSAGE);
			return;
		}
		String baseConditions[] = baseConditions1;
		String baseRelationNames[] = baseRelationNames1;
		
		estimatedTuples1 = new int[dimension];
		baseRelationQuery1 = new String[dimension];
		try
		{
			ConnectDB connectDBObj = allObjects.getConnectDBObj();
			Connection con = connectDBObj.connection;
			Statement st = con.createStatement();
			
			/*
			 * In this for loop, estimated tuples are calculated for each error-prone base relation.
			 *  It is done by executing command "explain select * from <relation> where <predicates>" (optimizer predicted number for total tuples).
			 */
			for(int i=0;i<dimension;i++)
			{
				int index = 0;
				while(! errorProneBaseRelation[i].equals(baseRelationNames[index]) )
					index++;

				String andPredicate = baseConditions[index].replaceAll(",", " and ");			//For base relation, combining base predicates with 'and'. 
				/*
				 * base condition contains predicates in form of: <prediacte1>,<predicate2>,<predicate2>,(extra comma in last) 
				 * so andPredicate = <prediacte1>and<predicate2>and<predicate2>and (extra and in last)
				 * Removing extra and
				 */
				andPredicate = andPredicate.substring(0, andPredicate.lastIndexOf("and"));

				baseRelationQuery1[i] = "select * from "+errorProneBaseRelation[i]+" where "+andPredicate;

				String q = "explain select * from "+errorProneBaseRelation[i]+" where "+andPredicate;

				ResultSet rs = st.executeQuery(q);
				if(rs.next())
				{
					String PlanString = rs.getString(1);
					int startIndex = PlanString.indexOf("rows="); 					//getting index of "rows=" string. startIndex will point to 'r'.
					int lastIndex = PlanString.indexOf(32, startIndex);				//Searching for space in string after "rows=" string.
					String tuples = PlanString.substring(startIndex+5, lastIndex);	//getting number of tuples in string format
					estimatedTuples1[i] = Integer.parseInt(tuples);

				}
				rs.close();
			}
			double estimatedSel[] = new double[dimension];
			maxTuples1 = new int[dimension];
			/*
			 * In this for loop, total tuples are calculated for each error-prone base relation.
			 * It is done by executing command "explain select * from <relation>" (optimizer predicted number for total tuples).
			 */
			for(int i=0;i<dimension;i++)
			{
				String q = "explain select * from "+errorProneBaseRelation[i];
				ResultSet rs = st.executeQuery(q);
				rs.next();

				String PlanString = rs.getString(1);
				int startIndex = PlanString.indexOf("rows="); 
				int lastIndex = PlanString.indexOf(32, startIndex);
				String tuples = PlanString.substring(startIndex+5, lastIndex);
				maxTuples1[i] = Integer.parseInt(tuples);

				/*
				 * optimizer estimated selectivity is calculated below.
				 */
				estimatedSel[i] = (((double)estimatedTuples1[i]/maxTuples1[i]) * 100.0);
				if(estimatedSel[i] > 100)
					estimatedSel[i] = 1.0;
				rs.close();
			}
			estimatedSelectivityPoint1.add(estimatedSel[0], estimatedSel[1]);
			bouquetDataObj.setMaxTuplesForErrorProneBaseRelation(maxTuples1);
			st.close();
		}
		catch(Exception e)
		{
			System.out.println("Execption is Native opt query execution: "+e);
			e.printStackTrace();
		}
	}
	
	JPanel getXYChart2(BouquetData bouquetDataObj)
	{
		String errorProneBaseRelationNames[] = bouquetDataObj.getErrorProneBaseRelationNames();
		
		int height = (int)(QUESTConstants.SCREEN_HEIGHT / 4);
		
		XYSeriesCollection seriesCollection = new XYSeriesCollection();
		
		/* estimatedPoint and actualPoint are initialized but values are not put here */
		estimatedSelectivityPoint2 = new XYSeries("Estimated Selectivity");
		actualSelectivityPoint2 = new XYSeries("Actual Selectivity");

		seriesCollection.addSeries(estimatedSelectivityPoint2);
		seriesCollection.addSeries(actualSelectivityPoint2);

		JFreeChart chart = ChartFactory.createScatterPlot(
				"Error Selectivity Space(ESS) (log-log scale)", // chart title
				"Sel-1", // x axis label
				"Sel-2", // y axis label
				seriesCollection,
				PlotOrientation.VERTICAL,
				true, // include legend
				true, // tooltips
				false // urls
				);
		
//		chart.getTitle().setFont(new Font("Dialog", Font.BOLD, QUESTConstants.LARGE_FONT_SIZE));	//for demo
		chart.getTitle().setFont(new Font("Dialog", Font.BOLD, 12));
		LegendTitle legend = chart.getLegend();
//		Font labelFont = new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE);
//		Font labelFont = new Font("SansSerif", Font.BOLD, QUESTConstants.LARGE_FONT_SIZE);			//for demo
//		legend.setItemFont(labelFont);
		
		legend.setPosition(RectangleEdge.BOTTOM);
		
		XYPlot plot = chart.getXYPlot();
		
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRangeGridlinePaint(Color.BLACK);

		ESSPlotRenderer2 = plot.getRenderer();
		Shape s1 = new Ellipse2D.Double(0, 0, 10, 10);
//		Shape s1 = ShapeUtilities.createDiagonalCross(4.0f, 0.6f);
		ESSPlotRenderer2.setSeriesShape(0, s1);
		
		Shape s2 = ShapeUtilities.createDiamond(7.0f);
		ESSPlotRenderer2.setSeriesShape(1, s2);
		
		ESSPlotRenderer2.setSeriesVisibleInLegend(1, false);					//1 is for actual selectivity
		
		LogAxis x = new LogAxis(errorProneBaseRelationNames[0]+" selectivity");
		LogAxis y = new LogAxis(errorProneBaseRelationNames[1]+" selectivity");
		
//		x.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.LARGE_FONT_SIZE));		//For demo
//		y.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.LARGE_FONT_SIZE));
////		
//		x.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.LARGE_FONT_SIZE));
//		y.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.LARGE_FONT_SIZE));
		
//		x.setTickLabelFont(new Font("SansSerif", Font.PLAIN,12));		//For demo
//		y.setTickLabelFont(new Font("SansSerif", Font.PLAIN,12));
//		
//		x.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
//		y.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
		
		x.setBase(2);
		y.setBase(2);

		x.setTickUnit(new NumberTickUnit(2));
		y.setTickUnit(new NumberTickUnit(2));

		x.setRange(0.03125, 100);
		y.setRange(0.03125, 100);
		
		plot.setDomainAxis(x);
		plot.setRangeAxis(y);
		
		ChartPanel cPanel = new ChartPanel(chart);
		int width = (int)(height * 2);
		cPanel.setPreferredSize(new Dimension(width, height));		//for demo
//		cPanel.setPreferredSize(new Dimension(450, 200));
		cPanel.setMaximumSize(cPanel.getPreferredSize());
		return(cPanel);

	}
	/* This function calculates selectivity estimated by optimizer */
	void getEstimatedSelectivity2()
	{
		
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		int dimension = bouquetDataObj.getDimension();
		String errorProneBaseRelation[] = bouquetDataObj.getErrorProneBaseRelationNames();
		
		boolean retval = parse_input_query(Q2_str, allObjects, 2);
		if(retval == false)
		{
			JOptionPane.showMessageDialog(new JFrame(),
				    "Query 2 is not valid",
				    "Attention",
				    JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		String baseConditions [] = baseConditions2;			
		String baseRelationNames [] = baseRelationNames2;
		
		estimatedTuples2 = new int[dimension];
		baseRelationQuery2 = new String[dimension];
		try
		{
			ConnectDB connectDBObj = allObjects.getConnectDBObj();
			Connection con = connectDBObj.connection;
			Statement st = con.createStatement();
			
			/*
			 * In this for loop, estimated tuples are calculated for each error-prone base relation.
			 *  It is done by executing command "explain select * from <relation> where <predicates>" (optimizer predicted number for total tuples).
			 */
			for(int i=0;i<dimension;i++)
			{
				int index = 0;
				while(! errorProneBaseRelation[i].equals(baseRelationNames[index]) )
					index++;

				String andPredicate = baseConditions[index].replaceAll(",", " and ");			//For base relation, combining base predicates with 'and'. 
				/*
				 * base condition contains predicates in form of: <prediacte1>,<predicate2>,<predicate2>,(extra comma in last) 
				 * so andPredicate = <prediacte1>and<predicate2>and<predicate2>and (extra and in last)
				 * Removing extra and
				 */
				andPredicate = andPredicate.substring(0, andPredicate.lastIndexOf("and"));

				baseRelationQuery2[i] = "select * from "+errorProneBaseRelation[i]+" where "+andPredicate;

				String q = "explain select * from "+errorProneBaseRelation[i]+" where "+andPredicate;

				ResultSet rs = st.executeQuery(q);
				if(rs.next())
				{
					String PlanString = rs.getString(1);
					int startIndex = PlanString.indexOf("rows="); 					//getting index of "rows=" string. startIndex will point to 'r'.
					int lastIndex = PlanString.indexOf(32, startIndex);				//Searching for space in string after "rows=" string.
					String tuples = PlanString.substring(startIndex+5, lastIndex);	//getting number of tuples in string format
					estimatedTuples2[i] = Integer.parseInt(tuples);

				}
				rs.close();
			}
			double estimatedSel[] = new double[dimension];
			maxTuples2 = new int[dimension];
			/*
			 * In this for loop, total tuples are calculated for each error-prone base relation.
			 * It is done by executing command "explain select * from <relation>" (optimizer predicted number for total tuples).
			 */
			for(int i=0;i<dimension;i++)
			{
				String q = "explain select * from "+errorProneBaseRelation[i];
				ResultSet rs = st.executeQuery(q);
				rs.next();

				String PlanString = rs.getString(1);
				int startIndex = PlanString.indexOf("rows="); 
				int lastIndex = PlanString.indexOf(32, startIndex);
				String tuples = PlanString.substring(startIndex+5, lastIndex);
				maxTuples2[i] = Integer.parseInt(tuples);

				/*
				 * optimizer estimated selectivity is calculated below.
				 */
				estimatedSel[i] = (((double)estimatedTuples2[i]/maxTuples2[i]) * 100.0);
				if(estimatedSel[i] > 100)
					estimatedSel[i] = 1.0;
				rs.close();
			}
			estimatedSelectivityPoint2.add(estimatedSel[0], estimatedSel[1]);
			bouquetDataObj.setMaxTuplesForErrorProneBaseRelation(maxTuples2);
			st.close();
		}
		catch(Exception e)
		{
			System.out.println("Execption is Native opt query execution: "+e);
			e.printStackTrace();
		}
	}

	void handle_execute_q1()
	{
		
		lblQ1ExecTime.setVisible(false);
		/* It removes entry for actual selectivity in xy scatter plot legend */
		ESSPlotRenderer1.setSeriesVisibleInLegend(1, false);
		
		setStatusStartQ1Execution();
		lblStatusMsg1.setText("Status: Executing Query with PostgreSQL Optimizer Plan    ");
		
		/* Dedicated thread executes query on the server */
		new Thread(new Runnable() 
		{
			public void run() 
			{
				QUESTUtility.clearCache();
				String query = Q1_str;
				executeQuery1(query);
	
				/* It adds actual selectivity entry in xy scatter plot legend  */
				ESSPlotRenderer1.setSeriesVisibleInLegend(1, true);
			}
		}).start();
		
		/*
		 * This thread runs time on status panel.
		 */
		new Thread(new Runnable() 
		{
			public void run() 
			{
				final long start_time = System.currentTimeMillis();
				while(executionFlag)
				{
					long currentTime = System.currentTimeMillis();
					int time_elapsed = (int)((currentTime - start_time)/1000.0);
					int t = time_elapsed;
					int hours = (int)t/3600;
					t = t%3600;
					int min = (int)t/60;
					t = t%60;
					int sec = (int)t;
				
					lblStatusMsg2.setText("Execution Time ="+ String.format("%02d",hours)+":"+ String.format("%02d", min)+":"+ String.format("%02d",sec));
				}
			}
		}).start();
	}
	
	void handle_execute_q2()
	{
		
		lblQ2ExecTime.setVisible(false);
		/* It removes entry for actual selectivity in xy scatter plot legend */
		ESSPlotRenderer2.setSeriesVisibleInLegend(1, false);
		
		setStatusStartQ2Execution();
		lblStatusMsg1.setText("Status: Executing Query with PostgreSQL Optimizer Plan    ");
		
		/* Dedicated thread executes query on the server */
		new Thread(new Runnable() 
		{
			public void run() 
			{
				QUESTUtility.clearCache();
				String query = Q2_str;
				executeQuery2(query);
	
				/* It adds actual selectivity entry in xy scatter plot legend  */
				ESSPlotRenderer2.setSeriesVisibleInLegend(1, true);
			}
		}).start();
		
		/*
		 * This thread runs time on status panel.
		 */
		new Thread(new Runnable() 
		{
			public void run() 
			{
				final long start_time = System.currentTimeMillis();
				while(executionFlag)
				{
					long currentTime = System.currentTimeMillis();
					int time_elapsed = (int)((currentTime - start_time)/1000.0);
					int t = time_elapsed;
					int hours = (int)t/3600;
					t = t%3600;
					int min = (int)t/60;
					t = t%60;
					int sec = (int)t;
				
					lblStatusMsg2.setText("Execution Time ="+ String.format("%02d",hours)+":"+ String.format("%02d", min)+":"+ String.format("%02d",sec));
				}
			}
		}).start();
	}
	
	/* Executes query and calculates actual selectivity for error-prone base relation */
	void executeQuery1(String query)
	{
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		String errorProneBaseRelation[] = bouquetDataObj.getErrorProneBaseRelationNames();
		int dimension = bouquetDataObj.getDimension();
		
		actualTuples1 = new int[dimension];
		
		actualSelectivityPoint1.clear();
		Arrays.fill(actualTuples1, -1);
		long startTime, endTime;
		try
		{
			ConnectDB connectDBObj = allObjects.getConnectDBObj();
//			Connection con = connectDBObj.connection;
//			Statement st = con.createStatement();
			
			System.out.println("NATIVE EXEC: "+query);
			conPgSql = connectDBObj.connection;
			pstmtPgSql = conPgSql.prepareStatement("explain analyse "+query);

			startTime = System.currentTimeMillis();
			ResultSet rs = pstmtPgSql.executeQuery();
//			ResultSet rs = st.executeQuery("explain analyse "+query);
			endTime = System.currentTimeMillis();
			
			System.out.println("Time taken = " + (endTime-startTime)/1000 + " (Native)");
			
			while(rs.next())
			{
				String str = rs.getString(1);
				if(str.contains("Seq Scan") || str.contains("Index Scan") || str.contains("Bitmap Heap Scan") || str.contains("Index Only Scan"))
				{
					int i;
					for(i = 0;i < dimension;i++)
					{
						if(str.contains(errorProneBaseRelation[i]))
							break;
					}
					if(i < dimension)
					{

						if(!str.contains("never executed"))
						{
							int startIndex = str.lastIndexOf("rows=");
							int lastIndex = str.indexOf(32,startIndex);
							int tuples = Integer.parseInt(str.substring(startIndex+5, lastIndex));
							int loopCount = Integer.parseInt(str.substring(lastIndex+7, str.length()-1));
							
							/*
							 * If Loop count is greater then 1 then current node is inner node of nested loop.
							 * So selectivity can't be determine of this node. 
							 */
							if(loopCount == 1)	
								actualTuples1[i] = tuples;
						}
					}
				}
			}
			/*
			 * For nodes having loop count greater then 1, selectivity is determined by extra query execution.
			 * In this execution, query contains only one relation (error prone base relation) and its corresponding base
			 * predicates. e.g. explain analyse select * from <relation> where <relation base predicates>
			 */
			for(int i=0;i<dimension;i++)
			{
				if(actualTuples1[i] == -1)
				{
					String q = "explain analyse "+ baseRelationQuery1[i];
					pstmtPgSql = conPgSql.prepareStatement(q);
					rs = pstmtPgSql.executeQuery();
					if(rs.next())
					{
						String str = rs.getString(1);
						int startIndex = str.lastIndexOf("rows=");
						int lastIndex = str.indexOf(32, startIndex);
						String tuples = str.substring(startIndex+5, lastIndex);
						actualTuples1[i] = Integer.parseInt(tuples);
					}
				}
			}
			
			/*
			 * Actual selectivity is calculated here.
			 */
			double actualSel[] = new double[dimension];
			for(int i=0;i<dimension;i++)
			{
				if(actualTuples1[i] == 0)
					actualTuples1[i] = maxTuples1[i]/10000;

				actualSel[i] = ((double)actualTuples1[i]/maxTuples1[i]) * 100.0;
			}
			bouquetDataObj.setActualSelectivity(actualSel);
			actualSelectivityPoint1.add(actualSel[0], actualSel[1]);
			rs.close();
//			st.close();
			pstmtPgSql.close();
						
			Q1_exec_time = (endTime - startTime)/1000.0;
			setStatusEndQ1Execution();
			
				
		}
		catch(PSQLException psql)
		{
			executionFlag = false;
			btnQ1Execute.setEnabled(true);
			btnQ1GetPlan.setEnabled(true);
			btnQ2GetPlan.setEnabled(true);
			btnQ1Cancel.setVisible(false);
			btnQ2Cancel.setVisible(false);
			if(q2_first_plan_avbl)
				btnQ2Execute.setEnabled(true);
			lblStatusMsg1.setText("Status: Execution Cancelled    ");
		}
		catch(Exception e)
		{
			System.out.println("Execption is Native opt query execution: "+e);
			e.printStackTrace();
		}
	}
	
	void executeQuery2(String query)
	{
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		String errorProneBaseRelation[] = bouquetDataObj.getErrorProneBaseRelationNames();
		int dimension = bouquetDataObj.getDimension();
		
		actualTuples2 = new int[dimension];
		
		actualSelectivityPoint2.clear();
		Arrays.fill(actualTuples2, -1);
		long startTime, endTime;
		try
		{
			ConnectDB connectDBObj = allObjects.getConnectDBObj();
//			Connection con = connectDBObj.connection;
//			Statement st = con.createStatement();
			
			System.out.println("NATIVE EXEC: "+query);
			conPgSql = connectDBObj.connection;
			pstmtPgSql = conPgSql.prepareStatement("explain analyse "+query);

			startTime = System.currentTimeMillis();
			ResultSet rs = pstmtPgSql.executeQuery();
//			ResultSet rs = st.executeQuery("explain analyse "+query);
			endTime = System.currentTimeMillis();
			
			System.out.println("Time taken = " + (endTime-startTime)/1000 + " (Native)");
			
			while(rs.next())
			{
				String str = rs.getString(1);
				if(str.contains("Seq Scan") || str.contains("Index Scan") || str.contains("Bitmap Heap Scan") || str.contains("Index Only Scan"))
				{
					int i;
					for(i = 0;i < dimension;i++)
					{
						if(str.contains(errorProneBaseRelation[i]))
							break;
					}
					if(i < dimension)
					{

						if(!str.contains("never executed"))
						{
							int startIndex = str.lastIndexOf("rows=");
							int lastIndex = str.indexOf(32,startIndex);
							int tuples = Integer.parseInt(str.substring(startIndex+5, lastIndex));
							int loopCount = Integer.parseInt(str.substring(lastIndex+7, str.length()-1));
							
							/*
							 * If Loop count is greater then 1 then current node is inner node of nested loop.
							 * So selectivity can't be determine of this node. 
							 */
							if(loopCount == 1)	
								actualTuples2[i] = tuples;
						}
					}
				}
			}
			/*
			 * For nodes having loop count greater then 1, selectivity is determined by extra query execution.
			 * In this execution, query contains only one relation (error prone base relation) and its corresponding base
			 * predicates. e.g. explain analyse select * from <relation> where <relation base predicates>
			 */
			for(int i=0;i<dimension;i++)
			{
				if(actualTuples2[i] == -1)
				{
					String q = "explain analyse "+ baseRelationQuery2[i];
					pstmtPgSql = conPgSql.prepareStatement(q);
					rs = pstmtPgSql.executeQuery();
					if(rs.next())
					{
						String str = rs.getString(1);
						int startIndex = str.lastIndexOf("rows=");
						int lastIndex = str.indexOf(32, startIndex);
						String tuples = str.substring(startIndex+5, lastIndex);
						actualTuples2[i] = Integer.parseInt(tuples);
					}
				}
			}
			
			/*
			 * Actual selectivity is calculated here.
			 */
			double actualSel[] = new double[dimension];
			for(int i=0;i<dimension;i++)
			{
				if(actualTuples2[i] == 0)
					actualTuples2[i] = maxTuples2[i]/10000;

				actualSel[i] = ((double)actualTuples2[i]/maxTuples2[i]) * 100.0;
			}
			bouquetDataObj.setActualSelectivity(actualSel);
			actualSelectivityPoint2.add(actualSel[0], actualSel[1]);
			rs.close();
//			st.close();
			pstmtPgSql.close();
			
			Q2_exec_time = (endTime - startTime)/1000.0;
			setStatusEndQ2Execution();
				
		}
		catch(PSQLException psql)
		{
			executionFlag = false;
			if(q1_first_plan_avbl)
				btnQ1Execute.setEnabled(true);
			
			btnQ2Execute.setEnabled(true);
			btnQ1GetPlan.setEnabled(true);
			btnQ2GetPlan.setEnabled(true);
			btnQ1Cancel.setVisible(false);
			btnQ2Cancel.setVisible(false);
			lblStatusMsg1.setText("Status: Execution Cancelled    ");
		}
		catch(Exception e)
		{
			System.out.println("Execption is Native opt query execution: "+e);
			e.printStackTrace();
		}
	}

	void setStatusStartQ1Execution()
	{
		lblStatusMsg2.setText("");
		
		btnQ1GetPlan.setEnabled(false);
		btnQ1Execute.setEnabled(false);
		btnQ1Cancel.setVisible(true);

		btnQ2GetPlan.setEnabled(false);
		btnQ2Execute.setEnabled(false);
		btnQ2Cancel.setVisible(false);
		
		executionFlag = true;
	}
	
	void setStatusEndQ1Execution()
	{
		executionFlag = false;
		
		btnQ1GetPlan.setEnabled(true);
		btnQ1Execute.setEnabled(true);
		btnQ1Cancel.setVisible(false);
		
		btnQ2GetPlan.setEnabled(true);
		if(q2_first_plan_avbl)
			btnQ2Execute.setEnabled(true);
		lblStatusMsg1.setText("Status: Execution Completed    ");
		lblQ1ExecTime.setText("Execution Time: " + Q1_exec_time + " s");
		lblQ1ExecTime.setVisible(true);
	}
	void setStatusStartQ2Execution()
	{
		lblStatusMsg2.setText("");
		
		btnQ2GetPlan.setEnabled(false);
		btnQ2Execute.setEnabled(false);
		btnQ2Cancel.setVisible(true);
		
		btnQ1GetPlan.setEnabled(false);
		btnQ1Execute.setEnabled(false);
		btnQ1Cancel.setVisible(false);
		
		executionFlag = true;
	}
	
	void setStatusEndQ2Execution()
	{
		executionFlag = false;
		
		btnQ2GetPlan.setEnabled(true);
		btnQ2Execute.setEnabled(true);
		btnQ2Cancel.setVisible(false);
		
		btnQ1GetPlan.setEnabled(true);
		if(q1_first_plan_avbl)
			btnQ1Execute.setEnabled(true);
		lblStatusMsg1.setText("Status: Execution Completed    ");
		lblQ2ExecTime.setText("Execution Time: " + Q2_exec_time + " s");
		lblQ2ExecTime.setVisible(true);
	}
	
	
	boolean parse_input_query(String queryInput, AllObjects allObjects, int query_no)
	{
		ConnectDB connectDBObj = allObjects.getConnectDBObj();
		String query = queryInput.trim();
		
		int fromIndex = query.indexOf("from");						//fromIndex will point to 'f' alphabet of 'from' word.
		int whereIndex = query.indexOf("where");					//whereIndex will point to 'w' alphabet of 'where' word.
		
		/*
		 * relationNames will contain relation names separated by comma.
		 */
		String relationNames = query.substring(fromIndex+4, whereIndex);			// fromIndex+4 will skip 'from' word.		
		StringTokenizer sToken = new StringTokenizer(relationNames, ",");
		
		String relations[] = new String[sToken.countTokens()];						//relation names are stored in relations[] array
		String relation_aliases[] = new String[sToken.countTokens()];						//relation names are stored in relations[] array
		int totalRelations = 0;														// total relations count
		while(sToken.hasMoreTokens())
		{
			//relations[totalRelations++] = (sToken.nextToken()).trim();
			String currentRelation = (sToken.nextToken()).trim();
			String alias = "";
			if(currentRelation.contains(" ")) {    // handling relation aliases
				alias = currentRelation.substring(currentRelation.indexOf(' '), currentRelation.length());
				currentRelation = currentRelation.substring(0, currentRelation.indexOf(' '));
			}
			relations[totalRelations] = currentRelation;
			relation_aliases[totalRelations++] = alias;
		}
		
		/*
		 * vector stores attributes for each relation 
		 */
		Vector<String> attributeNames[] = new Vector[totalRelations];
		
		/*
		 * Attributes name are fetched from pg_stats relation.
		 */
		try
		{
			Connection con = connectDBObj.connection;
			Statement st = con.createStatement();
			for(int i=0;i<totalRelations;i++)
			{
				attributeNames[i] = new Vector<String>();
				/*
				 * Reading attributes name from pg_stats
				 */
				ResultSet rs = st.executeQuery("select attname from pg_stats where tablename = '"+relations[i]+"'");
				boolean isRelationNameValid = false;
				while(rs.next())
				{
					String attribute = rs.getString(1);
					attributeNames[i].add(attribute);
					isRelationNameValid = true;
				}
				rs.close();
				if(!isRelationNameValid)										//This condition gets true when relation does not exists in pg_stats.
				{
					JOptionPane.showMessageDialog(new JFrame(),
							"\""+relations[i]+"\" relation does not exists",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
					return(false);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Execption in parse Query: "+e);
			e.printStackTrace();
		}
		
		/*
		 * predicateString contains predicates of string joined with 'and', 'or'.
		 */
		String predicateString = query.substring(whereIndex+5);			
		predicateString = predicateString.replaceAll(" and ", ",");
		predicateString = predicateString.replaceAll(" or ", ",");
		
		/*
		 * At this point, all 'and' and 'or' in predicate string are replaced by ','(comma).
		 */
		
		/*
		 * following string tokenizer separates different predicates and stores in predicates vector.
		 */
		sToken = new StringTokenizer(predicateString, ",");
		Vector<String> predicates = new Vector<String>();
		while(sToken.hasMoreTokens())
		{
			String str = sToken.nextToken().trim();
			if(str.length()>0)
				predicates.add(str);
		}
		
		/*
		 * last predicate might contain group by or order by clause.
		 * So that clause is handled differently.
		 */
		String lastPredicate = predicates.get(predicates.size()-1);
		String modifiedLastPredicate;
		int orderByIndex = lastPredicate.indexOf("order ");
		int groupByIndex = lastPredicate.indexOf("group ");
		/*
		 * if contains then order by and group by are removed.
		 */
		if(orderByIndex!=-1 || groupByIndex!=-1)
		{
			if(orderByIndex != -1 && orderByIndex<groupByIndex)
			{
				modifiedLastPredicate = lastPredicate.substring(0, orderByIndex).trim();
			}
			else if(orderByIndex == -1)
			{
				modifiedLastPredicate = lastPredicate.substring(0, groupByIndex).trim();
			}
			else if(groupByIndex == -1)
			{
				modifiedLastPredicate = lastPredicate.substring(0, orderByIndex).trim();
			}
			else
			{
				modifiedLastPredicate = lastPredicate.substring(0, groupByIndex).trim();
			}
			predicates.remove(predicates.size()-1);
			predicates.add(modifiedLastPredicate);
		}
		
		/*
		 * Here baseConditions contains base conditions for each relation separated by ',' (comma).
		 */
		String baseConditions[] = new String[totalRelations];
		/*
		 * joinConditions contain join conditions.
		 * joinConditions[i][j] contains join condition for i and j relations.  
		 */
		String joinConditions[][] = new String[totalRelations][totalRelations];
		for(int i=0;i<totalRelations;i++)
		{
			baseConditions[i] = "";
			for(int j=0;j<totalRelations;j++)
			{
				joinConditions[i][j]="";
			}
		}

		
		for(int i=0;i<predicates.size();i++)
		{
			String currentPredicate = predicates.get(i);
			boolean parseSuccess = false;
			sToken = new StringTokenizer(currentPredicate, "<>!=");
			String lValue = null;
			String rValue = null;
			if(sToken.hasMoreTokens()) {
				lValue = sToken.nextToken().trim();
				if(lValue.contains("."))   // to handle aliases
					lValue = lValue.substring(lValue.indexOf('.') + 1, lValue.length());
			}
			if(sToken.hasMoreTokens()) {
				rValue = sToken.nextToken().trim();
				if(rValue.contains("."))   // to handle aliases
					rValue = rValue.substring(rValue.indexOf('.') + 1, rValue.length());
			}
			if(rValue != null)
			{
				/*
				 * Following condition is true when attribute is compared with a string/date or number.
				 * It is a base condition.
				 */
				if(rValue.indexOf('\'') != -1 || rValue.matches("-?\\d+"))
				{
					int j;
					for(j=0;j<totalRelations;j++)
					{
						if(attributeNames[j].contains(lValue))
						{
							break;
						}
					}
					if(j<totalRelations)
					{
						baseConditions[j] += currentPredicate+", ";
					}
					else
					{
						/*if lvalue contains more than an attribute name -- e.g. substring(c_name from 1 for 4) -- many things other than c_name are also present 
						handling for substring specifically*/
						if(lValue.contains("substring") || lValue.contains("(")) 
						{
							StringTokenizer tokenSubString = new StringTokenizer(lValue," ()");

							String ctoken = tokenSubString.nextToken().trim();   /* first token should be 'substring'  */
							if(ctoken.equalsIgnoreCase("substring")) 
							{
								ctoken = tokenSubString.nextToken();      		 /* second token should be an attribute name -- check this */
								for(j=0;j<totalRelations;j++)
								{
									if(attributeNames[j].contains(ctoken))
									{
										break;
									}
								}
								if(j<totalRelations)                             /* it was found to be an attribute name */
								{
									ctoken = tokenSubString.nextToken().trim();
									if(ctoken.equalsIgnoreCase("from"))
									{
										ctoken = tokenSubString.nextToken().trim(); /* intentional -- this one should an integer */
										ctoken = tokenSubString.nextToken().trim();
										if(ctoken.equalsIgnoreCase("for"))
										{
											ctoken = tokenSubString.nextToken().trim();       /* this also should an integer */
											if(!tokenSubString.hasMoreTokens()) 
											{
												baseConditions[j] += currentPredicate+", ";
												parseSuccess = true;
											}
										}
									}
								}
							}	
							else  // for the case (r_name
							{
								for(j=0;j<totalRelations;j++)
								{
									if(attributeNames[j].contains(ctoken))
									{
										break;
									}
								}
								if(j<totalRelations)                             /* it was found to be an attribute name */
								{
									baseConditions[j] += currentPredicate+", ";
									parseSuccess = true;
								}
							}
						}

						
						if(!parseSuccess)
						{
//							//attribute is not found
							JOptionPane.showMessageDialog(new JFrame(),
									"\""+lValue+"\" invalid function on attribute",
								    "Warning",
								    JOptionPane.WARNING_MESSAGE);
							return(false);
							
						}	
					}

				}
				else
				{
					int j;
					for(j=0;j<totalRelations;j++)
					{
						if(attributeNames[j].contains(lValue))
						{
							break;
						}
					}
					int firstRelation = 0;
					if(j<totalRelations)
						firstRelation = j;
					else
					{
						//Attribute is not found
						JOptionPane.showMessageDialog(new JFrame(),
								"\""+lValue+"\" attribute does not exists",
							    "Warning",
							    JOptionPane.WARNING_MESSAGE);
						return(false);
					}
					for(j=0;j<totalRelations;j++)
					{
						if(attributeNames[j].contains(rValue))
						{
							break;
						}
					}
					int secondRelation = 0;
					if(j<totalRelations)
						secondRelation = j;
					else
					{
						//attribute is not found
						JOptionPane.showMessageDialog(new JFrame(),
								"\""+rValue+"\" attribute does not exists",
							    "Warning",
							    JOptionPane.WARNING_MESSAGE);
						return(false);
					}
					/*
					 * following condition is true for predicates like
					 * attribute1 = attribute2 and both attributes are of same relation. 
					 */
					if(firstRelation == secondRelation)
						baseConditions[firstRelation] += currentPredicate+", ";
					/*
					 * otherwise predicate is join predicate.
					 */
					else if(firstRelation<secondRelation)
					{
						joinConditions[firstRelation][secondRelation] += currentPredicate+", ";
					}
					else
					{
						joinConditions[secondRelation][firstRelation] += currentPredicate+", ";
					}
				}
			}
			/*
			 * 'like' or 'not like' case.
			 */
			else
			{
				int indexSpace = currentPredicate.indexOf(" ");
				lValue = currentPredicate.substring(0, indexSpace).trim();
				int j;
				for(j=0;j<totalRelations;j++)
				{
					if(attributeNames[j].contains(lValue))
					{
						break;
					}
				}
				if(j<totalRelations)
					baseConditions[j] += currentPredicate+", ";
				else
				{
					JOptionPane.showMessageDialog(new JFrame(),
							"\""+lValue+"\" attribute does not exists",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
					return(false);
				}
			}
		}
		if(query_no == 1)
		{
			baseConditions1 = baseConditions;
			baseRelationNames1 = relations;
		}
		else
		{
			baseConditions2 = baseConditions;
			baseRelationNames2 = relations;
		}
		return(true);
	}
	
}
