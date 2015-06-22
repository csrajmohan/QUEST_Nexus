package quest;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

import iisc.dsl.picasso.common.ds.TreeNode;
public class ExecutionInformationPanel implements ActionListener 
{
	private JPanel mainPanel;
	private JPanel plansExecPanel;
//	private JPanel statusPanel;
	private JScrollPane plansExecScrollPane;
	JPanel planExecInfoPanel;
//	JLabel timeLabel;
//	JLabel planContourLabel;
//	JLabel executionStatusLabel;
	JButton neverExecTreeNodeButton[][];
	TreeNodeState neverExecNodeTree[][];
	AllObjects allObjects;
	GridBagConstraints c;
	int planExecutionCount;
	int totalContours;
	int totalPlans;
	boolean executionFlag;
	TreeNode planTreeRootNodes[];
	DrawGraph drawGraphObj;
	public ExecutionInformationPanel(AllObjects allObjects)
	{
		Font f = new Font(QUESTConstants.TEXT_FONT,Font.PLAIN,QUESTConstants.STATUS_PANEL_FONT_SIZE);
		this.allObjects = allObjects;
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		totalContours = bouquetDataObj.getTotalContours();
		totalPlans = bouquetDataObj.getTotalPlans();
		
		planTreeRootNodes = bouquetDataObj.getPlanTreeRootNodes();
		
		allObjects.setExecutionInformationPanelObj(this);
		mainPanel = new JPanel();
//		mainPanel.setBackground(Color.WHITE);
		plansExecPanel = new JPanel();
		plansExecPanel.setBackground(new Color(QUESTConstants.backgroundColor2));
//		plansExecPanel.setBackground(Color.WHITE);
		plansExecPanel.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
//		statusPanel = new JPanel();
//		statusPanel.setBackground(new Color(QUESTConstants.STATUS_PANEL_COLOR));
//		executionStatusLabel = new JLabel("");
//		executionStatusLabel.setFont(f);
//		statusPanel.add(executionStatusLabel);
		
//		planContourLabel = new JLabel("");
//		planContourLabel.setFont(f);
//		statusPanel.add(planContourLabel);
//		timeLabel = new JLabel("");
//		timeLabel.setFont(f);
//		statusPanel.add(timeLabel);
		neverExecTreeNodeButton = new JButton[totalContours][totalPlans];
		neverExecNodeTree = new TreeNodeState[totalContours][totalPlans];
		plansExecScrollPane = new JScrollPane(plansExecPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		plansExecScrollPane.setViewportBorder(null);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(BorderLayout.CENTER, plansExecScrollPane);
//		mainPanel.add(BorderLayout.SOUTH, statusPanel);
	}
	JPanel getPanel()
	{
		return(mainPanel);
	}
	void startExecution()
	{
		executionFlag = true;
//		executionStatusLabel.setText("Status: Query is Executing, ");
		planExecutionCount = 0;
		clearExecutionInformationPanel();
	}
	void clearExecutionInformationPanel()
	{
		plansExecPanel.removeAll();
		plansExecPanel.revalidate();
		plansExecPanel.repaint();
	}
	void addPlanTree(int planNum, int contour, double contourCost, boolean contourChanged)
	{
		if(contourChanged)
		{
//			Font f = new Font(QUESTConstants.TEXT_FONT, Font.PLAIN, QUESTConstants.LARGE_FONT_SIZE);		//for demo
			Font f = new Font(QUESTConstants.TEXT_FONT, Font.PLAIN, 16);
//			JSeparator sep = new JSeparator(JSeparator.VERTICAL);
//			sep.setPreferredSize(new Dimension(5,1));
			c.gridx = planExecutionCount;
			c.gridy = 0;
			c.gridheight = 2;
			c.fill = GridBagConstraints.VERTICAL;
			c.anchor = GridBagConstraints.LINE_START;
			c.weighty = 1;
//			plansExecPanel.add(sep,c);
			
			
//			planExecutionCount++;
			JPanel smallPanel = new JPanel();
			smallPanel.setBackground(new Color(QUESTConstants.backgroundColor2));
			smallPanel.setLayout(new BoxLayout(smallPanel, BoxLayout.Y_AXIS));
			
//			Box smallPanel = Box.createVerticalBox();
			BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
			
			JLabel contourLabel = new JLabel("Contour ID = "+(contour+1));
			contourLabel.setFont(f);
			double multi = Math.pow(1.0/(bouquetDataObj.getCommonRatio()), totalContours-1-contour);
			JLabel costLabel = new JLabel("Cost Budget= ");
			costLabel.setFont(f);
			
			JLabel costValueLabel = new JLabel(String.format("%3.2f", multi)+" * MaxCost");
			costValueLabel.setFont(f);
			
			smallPanel.add(Box.createGlue());
			smallPanel.add(contourLabel);
			smallPanel.add(Box.createGlue());
			smallPanel.add(costLabel);
			smallPanel.add(costValueLabel);
			smallPanel.add(Box.createGlue());
		
			c.fill = GridBagConstraints.VERTICAL;
			c.gridx = planExecutionCount;
			plansExecPanel.add(smallPanel, c);
			
			
			planExecutionCount++;
//			sep = new JSeparator(JSeparator.VERTICAL);
//			sep.setPreferredSize(new Dimension(5,1));
			c.gridx = planExecutionCount;
			c.fill = GridBagConstraints.VERTICAL;
			c.weighty = 1;
//			plansExecPanel.add(sep,c);
		}
		
		
		BouquetPanel bouquetPanel = allObjects.getBouquetPanelObj();
		bouquetPanel.executionStatusLabel.setText("Status: Executing Plan P"+(planNum+1)+" of Contour "+(contour+1)+"   ");
		double zoomLevel = 0.5;
		drawGraphObj = new DrawGraph();
		TreeNode rootNode = planTreeRootNodes[planNum];
		drawGraphObj.drawGraph(rootNode, planNum, zoomLevel);
		
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.weightx = 1;
//		c.weighty = 1;
//		
//		c.gridx = 0;
//		c.gridy = planExecutionCount;
//		c.gridheight = 1;
//		c.anchor = GridBagConstraints.FIRST_LINE_START;
		
//		planExecutionCount++;
		
		c.fill = GridBagConstraints.NONE;
//		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 1;
		
		c.gridx = planExecutionCount;
		c.gridy = 0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		
		drawGraphObj.myGraphs[0].graph.setBackground(new Color(0xFFFFDF));
		plansExecPanel.add(drawGraphObj.centerPanel,c);
		
	
		planExecInfoPanel = new JPanel();
		planExecInfoPanel.setBackground(new Color(QUESTConstants.backgroundColor2));
		planExecInfoPanel.setLayout(new GridLayout(1,2,10,0));
//		planExecInfoPanel.setBackground(Color.WHITE);
		
//		c.fill = GridBagConstraints.NONE;
//		c.gridx = 1;
//		c.gridy = planExecutionCount;
//		c.gridheight = 1;
//		c.anchor = GridBagConstraints.FIRST_LINE_START;
//		c.insets = new Insets(0,50,0,0);  // this statement added.  
//		
		JLabel contourLabel = new JLabel("Contour:");
		contourLabel.setFont(new Font(QUESTConstants.TEXT_FONT,Font.BOLD,15));
//		plansExecPanel.add(contourLabel,c);
//		
//		
//		c.fill = GridBagConstraints.NONE;
//		c.gridx = 2;
//		c.gridy = planExecutionCount;
//		c.gridheight = 1;
//		c.anchor = GridBagConstraints.FIRST_LINE_START;
////		c.insets = new Insets(0,50,0,0);  // this statement added.  
//		
		JLabel contourValueLabel = new JLabel(""+(contour+1));
		contourValueLabel.setFont(new Font(QUESTConstants.TEXT_FONT,Font.BOLD,15));
//		plansExecPanel.add(contourValueLabel,c);
//		
//		
//		c.fill = GridBagConstraints.NONE;
//		c.gridx = 1;
//		c.gridy = planExecutionCount+1;
//		c.gridheight = 1;
//		c.anchor = GridBagConstraints.FIRST_LINE_START;
//		c.insets = new Insets(0,50,0,0);  // this statement added.  
//		
//		JLabel planLabel = new JLabel("Plan Number:");
//		planLabel.setFont(new Font(QUESTConstants.TEXT_FONT,Font.BOLD,15));
		JLabel planLabel = QUESTUtility.createInfoLable("Plan:");
//		plansExecPanel.add(planLabel,c);
//		
//		
//		c.fill = GridBagConstraints.NONE;
//		c.gridx = 2;
//		c.gridy = planExecutionCount+1;
//		c.gridheight = 1;
//		c.anchor = GridBagConstraints.FIRST_LINE_START;
////		c.insets = new Insets(0,50,0,0);  // this statement added.  
//		
//		JLabel planValueLabel = new JLabel("P"+(planNum+1));
		JLabel planValueLabel = QUESTUtility.createInfoLable("P"+(planNum+1));
//		planValueLabel.setFont(new Font(QUESTConstants.TEXT_FONT,Font.BOLD,15));
//		plansExecPanel.add(planValueLabel,c);
//		
//		
//		
//		c.fill = GridBagConstraints.NONE;
//		c.gridx = 1;
//		c.gridy = planExecutionCount+2;
//		c.gridheight = 1;
//		c.anchor = GridBagConstraints.FIRST_LINE_START;
//		c.insets = new Insets(0,50,0,0);  // this statement added.  
//		
		JLabel costLabel = new JLabel("Cost:");
		costLabel.setFont(new Font(QUESTConstants.TEXT_FONT,Font.PLAIN,15));
//		plansExecPanel.add(costLabel,c);
//		
//		
//		c.fill = GridBagConstraints.NONE;
//		c.gridx = 2;
//		c.gridy = planExecutionCount+2;
//		c.gridheight = 1;
//		c.anchor = GridBagConstraints.FIRST_LINE_START;
////		c.insets = new Insets(0,50,0,0);  // this statement added.  
//		
		JLabel costValueLabel = new JLabel(""+(int)contourCost);
		costValueLabel.setFont(new Font(QUESTConstants.TEXT_FONT,Font.PLAIN,15));
//		plansExecPanel.add(costValueLabel,c);
//		
//	
////		neverExecTreeNodeButton[contour][planNum] = new JButton("Show Execution Details");
////		neverExecTreeNodeButton[contour][planNum].addActionListener(this);
////		c.fill = GridBagConstraints.NONE;
////		c.gridx = 1;
////		c.gridy = planExecutionCount+3;
//////		c.anchor = GridBagConstraints.NORTH;
////		plansExecPanel.add(neverExecTreeNodeButton[contour][planNum],c);
//		
////		c.insets = new Insets(0,0,0,0);
//		plansExecPanel.revalidate();
//		planExecutionCount += 3;
//		planAdded = true;
		
//		
//		planExecInfoPanel.add(contourLabel);
//		planExecInfoPanel.add(contourValueLabel);
//		planExecInfoPanel.add(planLabel);
//		planExecInfoPanel.add(planValueLabel);
//		planExecInfoPanel.add(costLabel);
//		planExecInfoPanel.add(costValueLabel);
//		
		JLabel planContourLabel = QUESTUtility.createInfoLable("IC"+(contour+1)+"_P"+(planNum+1));
		planExecInfoPanel.add(planContourLabel);
//		c.fill = GridBagConstraints.BOTH;
		c.gridx = planExecutionCount;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
//		c.insets = new Insets(0,50,0,0);  // this statement added.  
		plansExecPanel.add(planExecInfoPanel,c);
		
		planExecutionCount++;
		
//		planContourLabel.setText(" Contour="+(contour+1)+", Plan="+(planNum+1)+", ");
		
		int width = (int)plansExecPanel.getPreferredSize().getWidth();
		Rectangle rect = new Rectangle(width,0,10,10);
        plansExecPanel.scrollRectToVisible(rect);
	}
	void addPostExecutionInfo(int planNum, int contour, double execTime, double[] selectivityLocation, boolean isSimulation)
	{
		drawGraphObj.myGraphs[0].graph.setBackground(Color.WHITE);
//		c.fill = GridBagConstraints.NONE;
//		c.gridx = 1;
//		c.gridy = planExecutionCount;
//		c.gridheight = 1;
//		c.anchor = GridBagConstraints.FIRST_LINE_START;
//		c.insets = new Insets(0,50,0,0);  // this statement added.  
		
//		JLabel execTimeLabel = new JLabel("Execution Time:");
		JLabel execTimeLabel = QUESTUtility.createInfoLable("Execution Time:");
//		execTimeLabel.setFont(new Font(QUESTConstants.TEXT_FONT,Font.PLAIN,15));
//		plansExecPanel.add(execTimeLabel,c);
		
		
//		c.fill = GridBagConstraints.NONE;
//		c.gridx = 2;
//		c.gridy = planExecutionCount;
//		c.gridheight = 1;
//		c.anchor = GridBagConstraints.FIRST_LINE_START;
//		c.insets = new Insets(0,50,0,0);  // this statement added.  
		
//		JLabel execTimeValueLabel = new JLabel(""+execTime+" sec.");
		JLabel execTimeValueLabel = QUESTUtility.createInfoLable(""+execTime+" sec.");
//		execTimeValueLabel.setFont(new Font(QUESTConstants.TEXT_FONT,Font.PLAIN,15));
//		plansExecPanel.add(execTimeValueLabel,c);
		
		
//		planExecInfoPanel.add(execTimeLabel);
//		planExecInfoPanel.add(execTimeValueLabel);
//		
//		neverExecTreeNodeButton[contour][planNum] = new JButton("Execution Details");
//		neverExecTreeNodeButton[contour][planNum].addActionListener(this);
//		c.fill = GridBagConstraints.NONE;
//		c.gridx = 1;
//		c.gridy = planExecutionCount+1;
//		c.gridwidth = 1;
//		c.gridheight = 1;
//		c.anchor = GridBagConstraints.LAST_LINE_START;
//		c.insets = new Insets(0,50,0,0);
//		plansExecPanel.add(neverExecTreeNodeButton[contour][planNum],c);
		
	
//		planExecInfoPanel.add(neverExecTreeNodeButton[contour][planNum]);
		
//		c.gridwidth = 1;
//		planExecutionCount += 2;
//		c.insets = new Insets(0,0,0,0);
		plansExecPanel.revalidate();
		
		
		
		BouquetPanel bouquetPanelObj = allObjects.getBouquetPanelObj();
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
	
		double timeLimit[] = bouquetDataObj.getTimeLimit(); 
		if(isSimulation)
		{
			bouquetPanelObj.simulationDataset.setValue(0.001, "initialValue", "IC"+(contour+1)+"_P"+(planNum+1));
			bouquetPanelObj.simulationDataset.setValue(execTime, "runningValue", "IC"+(contour+1)+"_P"+(planNum+1));
		}
		else
		{
//			bouquetPanelObj.dataset.setValue(0.001, "initialValue", "C"+(contour+1)+"_P"+(planNum+1));
//			if(contour==4 && execTime <=8)
//			{
//				execTime += 4;
//				try
//				{
//					Thread.sleep(4000);
//				}
//				catch(Exception e)
//				{
//					
//				}
//			}
			double a = Math.max(timeLimit[contour]/1000.0 - execTime, 0);
			bouquetPanelObj.dataset.setValue(a, "initialValue", "IC"+(contour+1)+"_P"+(planNum+1));
//			bouquetPanelObj.dataset.setValue(timeLimit[contour]/1000.0 - execTime, "initialValue", "C"+(contour+1)+"_P"+(planNum+1));
			bouquetPanelObj.dataset.setValue(execTime, "runningValue", "IC"+(contour+1)+"_P"+(planNum+1));
		}
	}
	void addBouquetExecutionFinishedInformation(double totalExecTime, int totalPlansExecution)
	{
//		Font f = new Font(QUESTConstants.TEXT_FONT, Font.PLAIN, QUESTConstants.LARGE_FONT_SIZE);		//For demo
		Font f = new Font(QUESTConstants.TEXT_FONT, Font.PLAIN, 16);

		c.gridx = planExecutionCount;
		c.gridy = 0;
		c.gridheight = 2;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.weighty = 1;

		JPanel smallPanel = new JPanel();
		smallPanel.setBackground(new Color(0xFFFFDF));
		smallPanel.setLayout(new BoxLayout(smallPanel, BoxLayout.Y_AXIS));
		
		JLabel bouLabel = new JLabel("Bouquet Execution Finished");
		bouLabel.setFont(f);
		
		JLabel totalPlansLabel = new JLabel("Total Plans Execution = "+totalPlansExecution);
		totalPlansLabel.setFont(f);

		JLabel totalExecTimeLabel = new JLabel("Total Execution Time = "+String.format("%4.1f", totalExecTime)+" sec.");
		totalExecTimeLabel.setFont(f);

		smallPanel.add(Box.createGlue());
		smallPanel.add(bouLabel);
		smallPanel.add(Box.createGlue());
		smallPanel.add(totalPlansLabel);
//		smallPanel.add(Box.createGlue());
		smallPanel.add(totalExecTimeLabel);
		smallPanel.add(Box.createGlue());

		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = planExecutionCount;
		plansExecPanel.add(smallPanel, c);
		
		int width = (int)plansExecPanel.getPreferredSize().getWidth();
		Rectangle rect = new Rectangle(width,0,10,10);
        plansExecPanel.scrollRectToVisible(rect);
	}
	public void actionPerformed(ActionEvent e)
	{
		boolean buttonFound = false;
		int planNum = 0;
		boolean showNeverExecutedNodes = true;
		int i,j=0;
		for(i=0;i<totalContours;i++)
		{
			for(j=0;j<totalPlans;j++)
			{
				if(e.getSource() == neverExecTreeNodeButton[i][j])
				{
					buttonFound = true;
					planNum = j;
					break;
				}
			}
			if(buttonFound == true)
				break;
		}
		if(buttonFound == true)
			PicassoUtil.displayTree(planTreeRootNodes[planNum],  neverExecNodeTree[i][j], showNeverExecutedNodes);
	}
	void startTimerClock()
	{
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
				
//					timeLabel.setText("Execution Time ="+ String.format("%02d",hours)+":"+ String.format("%02d", min)+":"+ String.format("%02d",sec));
				}
				JOptionPane.showMessageDialog(new JFrame(),
					    "Execution is completed",
					    "Message",
					    JOptionPane.PLAIN_MESSAGE);
			}
		}).start();
	}
	void stopTimerClock()
	{
		executionFlag = false;
//		executionStatusLabel.setText("Status: Execution is completed, ");
	}
}
