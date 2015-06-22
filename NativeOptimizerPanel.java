package quest;

/*
 * mainPanel
 * ------------------------------
 * |		inputPanel			|
 * |----------------------------|
 * |							|
 * |							|
 * |							|
 * |							|
 * |							|
 * |							|
 * |							|
 * ------------------------------
 */
import iisc.dsl.picasso.common.ds.TreeNode;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ShapeUtilities;
import org.postgresql.util.PSQLException;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Vector;
public class NativeOptimizerPanel implements ActionListener 
{
	AllObjects allObjects;
	
	JPanel mainPanel;				//top panel of this tab
	
	JPanel centerPanel;				//shows native plan tree, xy scatter chart, optimal plan, sub-optimality bar chart
	JPanel nativeExecutionPanel;	//shows native plan tree, xy scatter chart
	JPanel optExecutionPanel;		//shows optimal plan tree, sub-optimality bar chart
	JPanel barPanel;				//sub-optimality bar chart
	
	/*
	 * buttons for executing and showing query with native and optimal plan
	 */
	JButton btnExecNativePlan;		
	JButton btnGetNativePlan;
	
	JButton btnGetOptPlan;
	JButton btnExecOptPlan;
	JButton btnCancelQueryExec;
	JButton btnMore;
	
	JCheckBox chkClearCache;
	
	/*
	 * Labels for status panel.
	 */
	JLabel timeLabel;
	JLabel executionStatusLabel;
	
	Connection conPgSql;
	PreparedStatement pstmtPgSql;
	
	/*
	 * used in time clock shown in status panel
	 */
	boolean executionFlag = true;
	
	String baseRelationQuery[];
	
	/*
	 * estimated, actual and maximum tuples for error-prone base relation.
	 */
	int estimatedTuples[];
	int actualTuples[];
	int maxTuples[];
	
	String optPlanQueryString;
	
	/*
	 * data structure for xy scatter plot chart
	 */
	XYSeries estimatedSelectivityPoint;
	XYSeries actualSelectivityPoint;
	XYItemRenderer ESSPlotRenderer;
	DefaultCategoryDataset nativeSubOptData;
	
	public NativeOptimizerPanel(AllObjects allObjects)
	{
		allObjects.setNativeOptimizerPanelObj(this);
		this.allObjects = allObjects;
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
	}
	void addComponentsToPanel(AllObjects allObjects)
	{
		mainPanel.removeAll();
		
		
		/*
		 * Initialize buttons for input panel
		 */
		btnGetNativePlan = QUESTUtility.createButton("Get Native Plan", QUESTConstants.BUTTON_FONT_SIZE);
		btnGetNativePlan.addActionListener(this);
		
		btnExecNativePlan = QUESTUtility.createButton("Execute with Native Plan", QUESTConstants.BUTTON_FONT_SIZE);
		btnExecNativePlan.addActionListener(this);
		btnExecNativePlan.setEnabled(false);
		
		btnGetOptPlan = QUESTUtility.createButton("Show Optimal Plan", QUESTConstants.BUTTON_FONT_SIZE);
		btnGetOptPlan.addActionListener(this);
		btnGetOptPlan.setEnabled(false);
		
		btnExecOptPlan = QUESTUtility.createButton("Execute with Optimal Plan", QUESTConstants.BUTTON_FONT_SIZE);
		btnExecOptPlan.addActionListener(this);
		btnExecOptPlan.setEnabled(false);
		
		btnMore = QUESTUtility.createButton("More", QUESTConstants.BUTTON_FONT_SIZE);
		btnMore.addActionListener(this);
		btnMore.setEnabled(true);
		
		/* added by rajmohan in july 2014 */
		chkClearCache = new JCheckBox("Clear Cache");
		chkClearCache.setFont(new Font("Times New Roman", Font.BOLD, 10));
		chkClearCache.setSelected(true);
		
		
		btnCancelQueryExec = QUESTUtility.createButton("Cancel", QUESTConstants.BUTTON_FONT_SIZE);
		btnCancelQueryExec.addActionListener(this);
		btnCancelQueryExec.setVisible(false);
		
		/* Initialize input panel and add buttons to it */
		JPanel inputPanel = new JPanel();
		
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		inputPanel.setBackground(new Color(QUESTConstants.backgroundColor2));
		inputPanel.setPreferredSize(new Dimension(200, QUESTConstants.INPUT_PANEL_HEIGHT));
		inputPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		inputPanel.add(Box.createGlue());
		inputPanel.add(btnGetNativePlan);
		inputPanel.add(btnExecNativePlan);
		inputPanel.add(Box.createGlue());
		inputPanel.add(btnGetOptPlan);
		inputPanel.add(btnExecOptPlan);
		inputPanel.add(Box.createGlue());
		
		inputPanel.add(btnMore);
		inputPanel.add(btnCancelQueryExec);
		inputPanel.add(chkClearCache);
		
		/*
		 * Initialize nativeExeuctionPanel that holds native optimizer plan and xy scatter plot chart.
		 */
		nativeExecutionPanel = new JPanel();
		nativeExecutionPanel.setBackground(Color.WHITE);
		nativeExecutionPanel.setLayout(new BoxLayout(nativeExecutionPanel, BoxLayout.Y_AXIS));

		/*
		 * Initialize optExecutionPanel that holds optimal plan and sub-optimality bar chart.
		 */
		optExecutionPanel = new JPanel();
		optExecutionPanel.setLayout(new BoxLayout(optExecutionPanel, BoxLayout.Y_AXIS));
		optExecutionPanel.setBackground(Color.WHITE);
		
		/*
		 * centerPanel holds nativeExecutionPanel and optExecutionPanel.
		 */
		centerPanel = new JPanel();
		centerPanel.setBackground(Color.WHITE);
		centerPanel.setLayout(new GridLayout(1, 2));
		
		centerPanel.add(nativeExecutionPanel);
		centerPanel.add(optExecutionPanel);

		Font f = new Font(QUESTConstants.TEXT_FONT, Font.PLAIN, QUESTConstants.STATUS_PANEL_FONT_SIZE);
		
		/*
		 * Initialize status panel and add labels to it.
		 */
		executionStatusLabel = new JLabel("");
		executionStatusLabel.setFont(f);
		executionStatusLabel.setForeground(Color.WHITE);
		
		timeLabel = new JLabel("");
		timeLabel.setFont(f);
		timeLabel.setForeground(Color.WHITE);
		
		JPanel statusPanel = new JPanel();
		statusPanel.setBackground(new Color(QUESTConstants.STATUS_PANEL_COLOR));
		statusPanel.add(executionStatusLabel);
		statusPanel.add(timeLabel);
		
		/*
		 * add all components to mainPanel
		 */
		mainPanel.add(BorderLayout.NORTH, inputPanel);
		mainPanel.add(BorderLayout.CENTER, centerPanel);
		mainPanel.add(BorderLayout.SOUTH, statusPanel);
	
		mainPanel.revalidate();
		mainPanel.repaint();
	}
	public void actionPerformed(ActionEvent e)
	{
		final BouquetData bouquetDataObj = allObjects.getBouquetDataObj();

		String errorProneBaseRelation[] = bouquetDataObj.getErrorProneBaseRelationNames();	
		String baseRelationNames [] = bouquetDataObj.getBaseRelationNames();
		
		if(bouquetDataObj.bouquetLocation.endsWith("dummy")){                                     /*Added by Anshuman (Aug-22, 2014):this is to handle only those queries for which we want to show only that estimated sel is very bad */ 
			for(int i=0; i<bouquetDataObj.dimesion;i++)
				errorProneBaseRelation[i] = baseRelationNames[i];                                 /* assuming that in this case first two relations are the ones which have error prone predicate list */
				bouquetDataObj.setErrorProneBaseRelationNames(errorProneBaseRelation);
		}
		/*
		 * action handler for showing native optimizer plan.
		 */
		if(e.getSource() == btnMore)
		{
			NativeLimitations obj = new NativeLimitations(allObjects);
			obj.setTitle("Native Optimizer Limitations");
			obj.setVisible(true);
		}
		else if(e.getSource() == btnGetNativePlan)
		{	
			nativeExecutionPanel.removeAll();
			
			
			String query = bouquetDataObj.getQuery();
			/*
			 * Getting TreeNode data structure of plan tree. 
			 * It will visually show plan in tree form. 
			 */
			TreeNode root = getPlanTree("explain "+query);
			DrawGraph drawGraphObj = new DrawGraph();
			double zoomLevel = 1.0;
			
			/*
			 * this function draws plan tree visually
			 */
			drawGraphObj.drawGraph(root, 0, zoomLevel);
			
//			JLabel l = new JLabel("Native Optimizer Plan");
			JLabel l = new JLabel("PostgreSQL Optimizer Plan");
//			l.setFont(new Font(QUESTConstants.TEXT_FONT, Font.BOLD, QUESTConstants.HEADING_FONT_SIZE));
			l.setFont(new Font("Dialog", Font.BOLD, 16));	
//			l.setFont(new Font("Dialog", Font.BOLD, 24));				//For demo
			
			JPanel p1  = new JPanel();
			p1.setBackground(Color.WHITE);
			p1.add(l);
			nativeExecutionPanel.add(p1);
			nativeExecutionPanel.add(drawGraphObj.centerPanel);
			
			/*
			 * Getting xy scatter plot chart for showing estimated and actual selectivity
			 */
			JPanel chartPanel = getXYChart(bouquetDataObj);
			nativeExecutionPanel.add(chartPanel);
			
			getEstimatedSelectivity();
			
			btnExecNativePlan.setEnabled(true);
			
			centerPanel.revalidate();
			centerPanel.repaint();
		}
		/* Run Query on the Native Optimizer chosen plan */
		else if(e.getSource() == btnExecNativePlan)
		{
			barPanel = getBarChart();
			
			/* It removes entry for actual selectivity in xy scatter plot legend */
			ESSPlotRenderer.setSeriesVisibleInLegend(1, false);
			
			setStatusStartExecution();
			executionStatusLabel.setText("Status: Executing Query with PostgreSQL Optimizer Plan    ");
			
			/* Dedicated thread executes query on the server */
			new Thread(new Runnable() 
			{
				public void run() 
				{
					if(chkClearCache.isSelected())
						QUESTUtility.clearCache();
					String query = bouquetDataObj.getQuery();
					executeQuery(query);
		
					/* It adds actual selectivity entry in xy scatter plot legend  */
					ESSPlotRenderer.setSeriesVisibleInLegend(1, true);
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
					
						timeLabel.setText("Execution Time ="+ String.format("%02d",hours)+":"+ String.format("%02d", min)+":"+ String.format("%02d",sec));
					}
					/* I hate Message boxes :-(. See the status bar to know result of execution ! */
//					JOptionPane.showMessageDialog(new JFrame(),
//						    "Execution Completed",
//						    "Message",
//						    JOptionPane.PLAIN_MESSAGE);
				}
			}).start();
		}
		/*
		 * action handler for showing optimal plan
		 */
		else if(e.getSource() == btnGetOptPlan)
		{
			optExecutionPanel.removeAll();
			BouquetDriver bouquetDriverObj = allObjects.getBouquetDriverObj();
			double actualSelectivity[] = bouquetDataObj.getActualSelectivity();
			
			int resolution = bouquetDataObj.getResolution();
			int dimension = bouquetDataObj.getDimension();
			int totalPOSPPlans = bouquetDataObj.getDiagramPacket().getMaxPlanNumber();
			String bouquetPath = bouquetDataObj.getBouquetLocation();
			int allPlans[] = new int[totalPOSPPlans];
			for(int i=0;i<totalPOSPPlans;i++)
			{
				allPlans[i] = i;
			}
			
			int location[] = getESSCoordinate(actualSelectivity, bouquetDriverObj, resolution, dimension);
			int optPlan = bouquetDriverObj.getOptimalPlan(addressCalc(resolution, location, dimension), allPlans);
			
			double queryValues[]= bouquetDataObj.getQueryValues();			
			bouquetDataObj.setQueryValues(queryValues);
			
			String explainQueryString = "";
			String explainAnalyseQueryString="";
			String fpc_file_name;
			
			/* find out the optimal plan at the actual selectivity location 
			 * Actual selectivity is know when we executed explain analyse of native plan 
			 * Then force that plan on input query using fpc which will give optimal execution */
			String pServerLog = bouquetPath + "/PServerLog_XML";
			try
			{
				FileReader f = new FileReader(pServerLog);
				BufferedReader br = new BufferedReader (f);
				while (true)
				{
					String str =  br.readLine();
					int hash_index = str.indexOf('#');
					while (hash_index==-1)
					{
						str = br.readLine();
						hash_index = str.indexOf('#');
					}
					int plan_num = Integer.parseInt(str.substring(hash_index+1));
					if(plan_num == optPlan)
					{
						str = br.readLine();
						
						int lastbrace = str.lastIndexOf(')');
						fpc_file_name = str.substring(0,lastbrace-1);
						
						String remainingPart = str.substring(lastbrace+1);
						str = "explain analyse ";
						
						String planStr = "";
						String line = remainingPart;
						while(line.length()!=0)
						{
							planStr += line + " ";
							line = br.readLine();
						}						

						//explainAnalyseQueryString = str + " " + planStr + fpc_file_name + ";";
						explainAnalyseQueryString = "explain analyze " + bouquetDataObj.getQuery() + " " + fpc_file_name + ";";     // Anshuman thought on Aug-23-2014 that optimal should use the original query
						System.out.println("Fpc file used = " + fpc_file_name);
						explainQueryString="explain "+planStr + fpc_file_name + ";"; 
						break;
					}
				}
				br.close();
			}
			catch(Exception ex)
			{
				System.out.println("Error occured in plan file reading:"+ e);
			}
			
			optPlanQueryString = explainAnalyseQueryString;
			double zoomLevel = 1.0;
			TreeNode root = getPlanTree(explainQueryString);
			DrawGraph drawGraphObj = new DrawGraph();
			drawGraphObj.drawGraph(root, 0, zoomLevel);
			JLabel l = new JLabel("Optimal Plan");
//			l.setFont(new Font("Dialog", Font.BOLD, 24));			//For demo
			l.setFont(new Font("Dialog", Font.BOLD, 16));
			JPanel p1 = new JPanel();
			p1.setBackground(Color.WHITE);
			p1.add(l);
			optExecutionPanel.add(p1);
			optExecutionPanel.add(Box.createGlue());
			optExecutionPanel.add(drawGraphObj.centerPanel);
			optExecutionPanel.add(Box.createGlue());
//			optExecutionPanel.add(barPanel);
			
			btnExecOptPlan.setEnabled(true);
			centerPanel.revalidate();
		}
		/*
		 * action handler for running query with optimal plan
		 */
		else if(e.getSource() == btnExecOptPlan)
		{
			setStatusStartExecution();
			executionStatusLabel.setText("Status: Executing Query with Optimal Plan    ");
			new Thread(new Runnable() 
			{
				public void run() 
				{
					if(chkClearCache.isSelected())
						QUESTUtility.clearCache();
					executeQueryWithOptimalPlan(optPlanQueryString);
					
					optExecutionPanel.add(barPanel);
				}
			}).start();
			
			new Thread(new Runnable() 
			{
				public void run() 
				{
					final long start_time = System.currentTimeMillis();
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
					
						timeLabel.setText("Execution Time ="+ String.format("%02d",hours)+":"+ String.format("%02d", min)+":"+ String.format("%02d",sec));
					}
					/* I hate Message boxes :-(. See the status bar to know result of execution ! */
//					JOptionPane.showMessageDialog(new JFrame(),
//						    "Execution Completed",
//						    "Message",
//						    JOptionPane.PLAIN_MESSAGE);
				}
			}).start();
		}
		else if(e.getSource() == btnCancelQueryExec)
		{
			btnCancelQueryExec.setVisible(false);
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
	
	/* This function calculates selectivity estimated by optimizer */
	void getEstimatedSelectivity()
	{
		
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		int dimension = bouquetDataObj.getDimension();
		String errorProneBaseRelation[] = bouquetDataObj.getErrorProneBaseRelationNames();
		String baseConditions [] = bouquetDataObj.getBaseConditions();			
		String baseRelationNames [] = bouquetDataObj.getBaseRelationNames();
		
		estimatedTuples = new int[dimension];
		baseRelationQuery = new String[dimension];
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

				baseRelationQuery[i] = "select * from "+errorProneBaseRelation[i]+" where "+andPredicate;

				String q = "explain select * from "+errorProneBaseRelation[i]+" where "+andPredicate;

				ResultSet rs = st.executeQuery(q);
				if(rs.next())
				{
					String PlanString = rs.getString(1);
					int startIndex = PlanString.indexOf("rows="); 					//getting index of "rows=" string. startIndex will point to 'r'.
					int lastIndex = PlanString.indexOf(32, startIndex);				//Searching for space in string after "rows=" string.
					String tuples = PlanString.substring(startIndex+5, lastIndex);	//getting number of tuples in string format
					estimatedTuples[i] = Integer.parseInt(tuples);

				}
				rs.close();
			}
			double estimatedSel[] = new double[dimension];
			maxTuples = new int[dimension];
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
				maxTuples[i] = Integer.parseInt(tuples);

				/*
				 * optimizer estimated selectivity is calculated below.
				 */
				estimatedSel[i] = (((double)estimatedTuples[i]/maxTuples[i]) * 100.0);
				if(estimatedSel[i] > 100)
					estimatedSel[i] = 1.0;
				rs.close();
			}
			estimatedSelectivityPoint.add(estimatedSel[0], estimatedSel[1]);
			bouquetDataObj.setMaxTuplesForErrorProneBaseRelation(maxTuples);
			st.close();
		}
		catch(Exception e)
		{
			System.out.println("Execption is Native opt query execution: "+e);
			e.printStackTrace();
		}
	}
	
	/* Executes query and calculates actual selectivity for error-prone base relation */
	void executeQuery(String query)
	{
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		String errorProneBaseRelation[] = bouquetDataObj.getErrorProneBaseRelationNames();
		int dimension = bouquetDataObj.getDimension();
		
		actualTuples = new int[dimension];
		
		actualSelectivityPoint.clear();
		Arrays.fill(actualTuples, -1);
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
								actualTuples[i] = tuples;
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
				if(actualTuples[i] == -1)
				{
					String q = "explain analyse "+ baseRelationQuery[i];
					pstmtPgSql = conPgSql.prepareStatement(q);
					
//					rs = st.executeQuery(q);
					rs = pstmtPgSql.executeQuery();
					if(rs.next())
					{
						String str = rs.getString(1);
						int startIndex = str.lastIndexOf("rows=");
						int lastIndex = str.indexOf(32, startIndex);
						String tuples = str.substring(startIndex+5, lastIndex);
						actualTuples[i] = Integer.parseInt(tuples);
					}
				}
			}
			
			/*
			 * Actual selectivity is calculated here.
			 */
			double actualSel[] = new double[dimension];
			for(int i=0;i<dimension;i++)
			{
				if(actualTuples[i] == 0)
					actualTuples[i] = maxTuples[i]/10000;

				actualSel[i] = ((double)actualTuples[i]/maxTuples[i]) * 100.0;
			}
			bouquetDataObj.setActualSelectivity(actualSel);
			actualSelectivityPoint.add(actualSel[0], actualSel[1]);
			rs.close();
//			st.close();
			pstmtPgSql.close();
			
			/*
			 * This execution time is saved for showing performance comparison in last tab (Performance Comparison).
			 */
			PerformanceComparisionPanel performanceComparisonPanelObj = allObjects.getExecutionResultPanelObj();
			performanceComparisonPanelObj.setNativeOptimizerExecTime((endTime - startTime)/1000.0, allObjects);
			
			setStatusEndNativeExecution();
			
			if(bouquetDataObj.bouquetLocation.endsWith("dummy"))
				btnGetOptPlan.setEnabled(false);
				
		}
		catch(PSQLException psql)
		{
			executionFlag = false;
			btnGetNativePlan.setEnabled(true);
			btnExecNativePlan.setEnabled(true);
			btnMore.setVisible(true);
			btnCancelQueryExec.setVisible(false);
			executionStatusLabel.setText("Status: Execution Cancelled    ");
		}
		catch(Exception e)
		{
			System.out.println("Execption is Native opt query execution: "+e);
			e.printStackTrace();
		}
	}
	
	/*
	 * This function executes query with optimal plan
	 */
	void executeQueryWithOptimalPlan(String query)
	{
		/* hard-coded optimal plan for quest query */
//		query = "select n_name, sum(l_extendedprice * (1 - l_discount)) as revenue from customer, orders, lineitem, supplier, nation, region where c_custkey = o_custkey and l_orderkey = o_orderkey and l_suppkey = s_suppkey and c_nationkey = s_nationkey and s_nationkey = n_nationkey and n_regionkey = r_regionkey and (r_name='ASIA' or r_name='AFRICA') and o_orderdate >= date '1994-01-01' and o_orderdate < date '1994-01-01' + interval '1 year' and l_extendedprice <= 20000 and c_acctbal <=2400 group by n_name order by revenue desc";
		
			
		try
		{
			ConnectDB connectDBObj = allObjects.getConnectDBObj();
//			Connection con = connectDBObj.connection;
//			Statement st = con.createStatement();
			System.out.println("OPTIMAL EXEC: "+query);
			conPgSql = connectDBObj.connection;
			pstmtPgSql = conPgSql.prepareStatement(query);
			
			
			long startTime = System.currentTimeMillis();
			pstmtPgSql.execute();
//			st.executeQuery(query);
			long endTime = System.currentTimeMillis();
			
			PerformanceComparisionPanel performanceComparisonPanelObj = allObjects.getExecutionResultPanelObj();
			performanceComparisonPanelObj.setOptimalPlanExecutionTime((endTime - startTime)/1000.0, allObjects);
			
			double nativeExecTime = performanceComparisonPanelObj.executionTime[0];
			double optExecTime = (endTime - startTime)/1000.0;
			nativeSubOptData.setValue(nativeExecTime/optExecTime, "SubOpt", "Native Optimizer");
			pstmtPgSql.close();
			System.out.println("Time taken = " + nativeExecTime + " (Native) " + optExecTime + " (optimal)");
			setStatusEndOptExecution();
//			nativeSubOptData.setValue((endTime - startTime)/1000.0, "Time", "Optimal Plan");
		}
		catch(PSQLException psql)
		{
			executionFlag = false;
			btnGetNativePlan.setEnabled(true);
			btnMore.setVisible(true);
			btnExecNativePlan.setEnabled(true);
			btnGetOptPlan.setEnabled(true);
			btnExecOptPlan.setEnabled(true);
			
			btnCancelQueryExec.setVisible(false);
			executionStatusLabel.setText("Status: Execution Cancelled    ");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/* This function create a xy scatter plot chart.
	   Through this chart, estimated and actual selectivities are shown */
	JPanel getXYChart(BouquetData bouquetDataObj)
	{
		String errorProneBaseRelationNames[] = bouquetDataObj.getErrorProneBaseRelationNames();
		
		int height = (int)(QUESTConstants.SCREEN_HEIGHT / 4);
		
		XYSeriesCollection seriesCollection = new XYSeriesCollection();
		
		/* estimatedPoint and actualPoint are initialized but values are not put here */
		estimatedSelectivityPoint = new XYSeries("Estimated Selectivity");
		actualSelectivityPoint = new XYSeries("Actual Selectivity");

		seriesCollection.addSeries(estimatedSelectivityPoint);
		seriesCollection.addSeries(actualSelectivityPoint);

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
		
		legend.setPosition(RectangleEdge.RIGHT);
		
		XYPlot plot = chart.getXYPlot();
		
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRangeGridlinePaint(Color.BLACK);

		ESSPlotRenderer = plot.getRenderer();
		Shape s1 = new Ellipse2D.Double(0, 0, 10, 10);
//		Shape s1 = ShapeUtilities.createDiagonalCross(4.0f, 0.6f);
		ESSPlotRenderer.setSeriesShape(0, s1);
		
		Shape s2 = ShapeUtilities.createDiamond(7.0f);
		ESSPlotRenderer.setSeriesShape(1, s2);
		
		ESSPlotRenderer.setSeriesVisibleInLegend(1, false);					//1 is for actual selectivity
		
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
	
	/* This functions returns sub-optimality bar chart panel for native optimizer */
	JPanel getBarChart()
	{
		int height = (int)(QUESTConstants.SCREEN_HEIGHT / 4);
		
		nativeSubOptData = new DefaultCategoryDataset();
		
		nativeSubOptData.setValue(0, "SubOpt", "Native Optimizer");
//		planExecutionTimeData.setValue(0, "ExecutionTime", "Optimal Plan");
//		nativeSubOptData.setValue(0, "Time", "Optimal Plan");

		JFreeChart chart = ChartFactory.createBarChart
				("Native Sub-optimality (log scale)",null, "Sub-Optimality", nativeSubOptData, 
						PlotOrientation.VERTICAL, false,true, false);

		
//		chart.getTitle().setFont(new Font("Dialog", Font.BOLD, QUESTConstants.LARGE_FONT_SIZE));		//for demo
		chart.getTitle().setFont(new Font("Dialog", Font.BOLD, 12));
		CategoryPlot p = chart.getCategoryPlot(); 
//		p.setRangeGridlinePaint(Color.WHITE);
		p.setBackgroundPaint(Color.WHITE);
		p.getRenderer().setSeriesPaint(0, new Color(QUESTConstants.backgroundColor1));

		BarRenderer barRenderer = (BarRenderer)p.getRenderer();
		barRenderer.setBarPainter(new StandardBarPainter());
		
		BarRenderer renderer = (BarRenderer) p.getRenderer();
		renderer.setMaximumBarWidth(0.1);
		
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setBase(0.001);
		
//		NumberAxis range = (NumberAxis)p.getRangeAxis();
//		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.LARGE_FONT_SIZE));		
//		range.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.LARGE_FONT_SIZE));
		CategoryAxis domain = p.getDomainAxis();
//		domain.setTickLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.LARGE_FONT_SIZE));			//For demo
//		domain.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.LARGE_FONT_SIZE));
		
//		domain.setTickLabelFont(new Font("SansSerif", Font.BOLD,12));
//		domain.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
		
//		range.setTickUnit(new NumberTickUnit(20));
		
		LogAxis range = new LogAxis("Sub-optimality");
		range.setBase(2);
		range.setTickUnit(new NumberTickUnit(1));
//		range.setLowerBound(1);
		range.setRange(1, 32);
		range.setAutoRange(false);
//		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.AXIS_TICK_FONT_SIZE));		//for demo
//		range.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE));
		
		
//		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
//		range.setLabelFont(new Font("SansSerif", Font.BOLD, 12));				
		p.setRangeAxis(range);
		
		
		
		ChartPanel cPanel = new ChartPanel(chart);
		int width = (int)(height*1.2);
		cPanel.setPreferredSize(new Dimension(width, height));		//for demo
		
//		cPanel.setPreferredSize(new Dimension(450, 200));
		cPanel.setMaximumSize(cPanel.getPreferredSize());
		return(cPanel);
	}
	/*
	 * this function is called before starting of query execution.
	 * It disables query execution buttons.
	 */
	void setStatusStartExecution()
	{
		timeLabel.setText("");
		
		btnGetNativePlan.setEnabled(false);
		btnExecNativePlan.setEnabled(false);
		btnGetOptPlan.setEnabled(false);
		btnExecOptPlan.setEnabled(false);
		btnMore.setVisible(false);
		btnCancelQueryExec.setVisible(true);
		
		executionFlag = true;
	}
	/*
	 * This function is called after query execution completion.
	 * It enables query execution buttons.
	 */
	void setStatusEndNativeExecution()
	{
		executionFlag = false;
		
		btnGetNativePlan.setEnabled(true);
		btnExecNativePlan.setEnabled(true);
		btnMore.setVisible(true);
		btnGetOptPlan.setEnabled(true);
		
		btnExecOptPlan.setEnabled(false);		/* commented by rajmohan since opt-plan is hard-coded */
		
		btnCancelQueryExec.setVisible(false);
		
		executionStatusLabel.setText("Status: Execution Completed    ");
	}
	
	void setStatusEndOptExecution()
	{
		executionFlag = false;
		
		btnGetNativePlan.setEnabled(true);
		btnExecNativePlan.setEnabled(true);
		btnMore.setVisible(true);
		btnGetOptPlan.setEnabled(true);
		
		btnExecOptPlan.setEnabled(true);		/* commented by rajmohan since opt-plan is hard-coded */
		
		btnCancelQueryExec.setVisible(false);
		
		executionStatusLabel.setText("Status: Execution Completed    ");
	}
	
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
	int addressCalc(int resolution,int index[], int d)
	{
		for(int i=0;i<d;i++)
		{
			if(index[i]>=resolution)
			{
				System.err.println("Error: Indices are out of range");
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
}