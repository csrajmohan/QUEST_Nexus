package quest;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import visad.AxisScale;
import visad.ColorAlphaControl;
import visad.ColorControl;
import visad.ConstantMap;
import visad.DataReferenceImpl;
import visad.Display;
import visad.DisplayImpl;
import visad.DisplayRenderer;
import visad.FlatField;
import visad.FunctionType;
import visad.GraphicsModeControl;
import visad.Gridded2DSet;
import visad.Linear2DSet;
import visad.ProjectionControl;
import visad.RealTupleType;
import visad.RealType;
import visad.ScalarMap;
import visad.Set;
import visad.SetException;
import visad.VisADException;
import visad.java3d.DisplayImplJ3D;
import visad.util.RGBAMap;
//import iisc.dsl.picasso.client.network.MessageUtil;
//import iisc.dsl.picasso.client.panel.MainPanel;
//import iisc.dsl.picasso.client.panel.PicassoPanel;
//import iisc.dsl.picasso.client.util.DiagramUtil;
//import iisc.dsl.picasso.client.util.Draw2DDiagram;
//import iisc.dsl.picasso.client.util.PicassoUtil;
import iisc.dsl.picasso.common.PicassoConstants;
import iisc.dsl.picasso.common.ds.DataValues;
import iisc.dsl.picasso.common.ds.DiagramPacket;

import javax.swing.*;


public class DrawCostDiagram 
{
	AllObjects allObjects;
//	void drawCostDiagram(DiagramPacket gdp)
	Component drawCostDiagram(DiagramPacket gdp, int totalHorizontalPlan, boolean showPlanes, AllObjects allObject)
	{
		ScalarMap maps[] = new ScalarMap[7];
		this.allObjects = allObject;
		
		for (int i=0; i < 4; i++)
			maps[i] = null;
		DisplayImpl display = draw(gdp, maps, totalHorizontalPlan, showPlanes);
		Component c = display.getComponent();
		c.setPreferredSize(new Dimension(900, 600));
		
//		JFrame f = new JFrame("Cost Diagram");
//		JPanel p = new JPanel();
//		p.setLayout(new BorderLayout());
//		p.add(BorderLayout.CENTER, c);
//		f.add(p);
//		f.setVisible(true);
//		f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		return(c);
	}
	
	public DisplayImpl draw(/*DisplayImpl display, MainPanel panel,*/ DiagramPacket gdp,/* int type,*/ ScalarMap[] maps, int totalHorizontalPlan, boolean showPlanes) 
	{
		DisplayImpl display1=null;
		ScalarMap	latitudeMap, longitudeMap, altitudeMap, planRGBMap;

		//System.out.println(" In 3d Diagram " + panel.getCurrentTab());
		try {
//			if ( display != null ) {
//				display.removeAllReferences();
//				for (int i=0; i < maps.length; i++) {
//					if ( maps[i] != null )
//						display.removeMap(maps[i]);
//				}
//				display1 = display;
//			} else
				display1 = new DisplayImplJ3D("display1");
			String[] relations = gdp.getRelationNames();
			String[] attributes = gdp.getAttributeNames();
			String[] relNames = new String[2];

			relNames[0] = relations[0];

			if ( gdp.getDimension() == 1 )
				relNames[1] = "";
			else
				relNames[1] = relations[1];

			// Draw the 3D Graph
			RealType latitude = RealType.getRealType(relNames[0]);
			RealType longitude = RealType.getRealType(relNames[1]);
			RealType altitude = null;
			String altName = "";
//			if ( type == PicassoPanel.PLAN_CARD_DIAGRAM) {
//				altitude = RealType.getRealType("CompiledCard");
//				altName = "Compiled Card (N)";
//			} else if ( type == PicassoPanel.PLAN_COST_DIAGRAM ) {
				altitude = RealType.getRealType("CompiledCost");
				altName = "Compiled Cost (N)";
//			} else if ( type == PicassoPanel.EXEC_PLAN_CARD_DIAGRAM ) {
//				altitude = RealType.getRealType("ExecCard");
//				altName = "Exec Card (N)";
//			} else if (type == PicassoPanel.EXEC_PLAN_COST_DIAGRAM) {
//				altitude = RealType.getRealType("ExecCost");
//				altName = "Exec Time (N)";
//			}
			RealType planNumber = RealType.getRealType("plan");

			RealTupleType domainTuple = new RealTupleType(latitude, longitude);
			RealTupleType rangeTuple = new RealTupleType(altitude, planNumber);


			// Create a FunctionType (domain_tuple -> plan_tuple)
			// Use FunctionType(MathType domain, MathType range)
			FunctionType funcType = null;
			funcType = new FunctionType(domainTuple, rangeTuple);

			double[][] flatSamples = null;
//			if ( type == PicassoPanel.PLAN_COST_DIAGRAM ) {
				
			int sortedPlanCount[][] = gdp.getSortedPlanArray();
			flatSamples = getSamplesForCost(sortedPlanCount, gdp);
				
			
//			double[][] flatSamples1 = getSamplesForCostTemp(sortedPlanCount, gdp,1.0);
				
				
//			} else if ( type == PicassoPanel.EXEC_PLAN_COST_DIAGRAM ) {
//				flatSamples = DiagramUtil.getSamplesForCost(panel, panel.getExecSortedPlan(), gdp);
//			} else if ( type == PicassoPanel.EXEC_PLAN_CARD_DIAGRAM ) {
//				flatSamples = DiagramUtil.getSamplesForCard(panel, panel.getExecSortedPlan(), gdp);
//			} else if ( type == PicassoPanel.PLAN_CARD_DIAGRAM ) {
//				flatSamples = DiagramUtil.getSamplesForCard(panel, panel.getSortedPlan(), gdp);
//			}

			// Create a flat array for plans

			latitudeMap = new ScalarMap( latitude, Display.XAxis );
			longitudeMap = new ScalarMap( longitude, Display.YAxis );
			altitudeMap = new ScalarMap( altitude, Display.ZAxis );
			planRGBMap = new ScalarMap( planNumber,  Display.RGBA );
			
			maps[0] = latitudeMap;
			maps[1] = longitudeMap;
			maps[2] = planRGBMap;
			maps[3] = altitudeMap;
			
//			int selectivity = panel.getDBSettingsPanel().getSelecType();
			boolean addExtra = false;
			//apexp
			if ( gdp.getQueryPacket().getDistribution().equals(PicassoConstants.UNIFORM_DISTRIBUTION) && /*selectivity == PicassoConstants.PICASSO_SELECTIVITY && */gdp.getMaxResolution() < 100 ) {//rss
				// flatSamples = DiagramUtil.getExtra3DSamples(gdp, flatSamples);
				addExtra = false;//true;
				
			}
			//end apexp
			
			
			//apexp
			//flatSamples are plan numbers.
			//change flatSamples from 100 to 121 (or something else, dep. on res)
			if(gdp.getQueryPacket().getDistribution().startsWith(PicassoConstants.EXPONENTIAL_DISTRIBUTION))
			{
				double[][] nflatSamples = new double[2][(gdp.getResolution(PicassoConstants.a[0])+1)*(gdp.getResolution(PicassoConstants.a[1])+1)];
				int k=0,l=0;
				for(int i=0;i<gdp.getResolution(PicassoConstants.a[1]);i++)
				{ 	for(int j=0;j<gdp.getResolution(PicassoConstants.a[0]);j++)
					{
						nflatSamples[0][l++]=flatSamples[0][k++];
						nflatSamples[1][l-1]=flatSamples[1][k-1];
					}
					nflatSamples[0][l++]=flatSamples[0][k-1];
					nflatSamples[1][l-1]=flatSamples[1][k-1];
				}
				//copy uppermost row
				k-=gdp.getResolution(PicassoConstants.a[0]);
				for(int j=0;j<gdp.getResolution(PicassoConstants.a[0]);j++)
				{
					nflatSamples[0][l++]=flatSamples[0][k++];
					nflatSamples[1][l-1]=flatSamples[1][k-1];
				}
				nflatSamples[0][l++]=flatSamples[0][k-1];
				nflatSamples[1][l-1]=flatSamples[1][k-1];
				
				flatSamples=nflatSamples;
			}
			
			
			
//			if(gdp.getQueryPacket().getDistribution().startsWith(PicassoConstants.EXPONENTIAL_DISTRIBUTION))
//			{
//				double[][] nflatSamples = new double[2][(gdp.getResolution(PicassoConstants.a[0])+1)*(gdp.getResolution(PicassoConstants.a[1])+1)];
//				int k=0,l=0;
//				for(int i=0;i<gdp.getResolution(PicassoConstants.a[1]);i++)
//				{ 	for(int j=0;j<gdp.getResolution(PicassoConstants.a[0]);j++)
//					{
//						nflatSamples[0][l++]=flatSamples1[0][k++];
//						nflatSamples[1][l-1]=flatSamples1[1][k-1];
//					}
//					nflatSamples[0][l++]=flatSamples1[0][k-1];
//					nflatSamples[1][l-1]=flatSamples1[1][k-1];
//				}
//				//copy uppermost row
//				k-=gdp.getResolution(PicassoConstants.a[0]);
//				for(int j=0;j<gdp.getResolution(PicassoConstants.a[0]);j++)
//				{
//					nflatSamples[0][l++]=flatSamples1[0][k++];
//					nflatSamples[1][l-1]=flatSamples1[1][k-1];
//				}
//				nflatSamples[0][l++]=flatSamples1[0][k-1];
//				nflatSamples[1][l-1]=flatSamples1[1][k-1];
//				
//				flatSamples1=nflatSamples;
//			}
			
			
			
			
			//paste from 2d diag
			//int fac=5;
			//flatsamples are plan numbers
			//get indices
			//end apexp
			
			FlatField flatFieldValues = getFlatFieldValues(domainTuple, funcType, gdp,/* selectivity,*/ addExtra,true/*,panel*/);

//			FlatField flatFieldValues1 = getFlatFieldValues(domainTuple, funcType, gdp,/* selectivity,*/ addExtra,true/*,panel*/);

			//	False being array won't be copied
			//FlatField flatFieldValues = new FlatField(funcType, domainSet);
			flatFieldValues.setSamples( flatSamples , false );
			
//			flatFieldValues1.setSamples( flatSamples1 , false );
			

			//		 Add maps to display
			display1.addMap( latitudeMap );
			display1.addMap( longitudeMap );
			display1.addMap( altitudeMap );
			display1.addMap( planRGBMap );

			// Set maps ranges
			latitudeMap.setRange(0.0f, 100.0f);
			longitudeMap.setRange(0.0f, 100.0f);
			altitudeMap.setRange(0.0f, 1.0f);

			int maxPlanNumber = gdp.getMaxPlanNumber();
			planRGBMap.setRange(0, maxPlanNumber);

			//float[] colorMap =
			setColorMap(maxPlanNumber, planRGBMap);
//			setColorMap(84, planRGBMap);

			latitudeMap.setScalarName(relNames[0]+" selectivity (log scale)");
			longitudeMap.setScalarName(relNames[1]+" selectivity (log scale)");

			//HersheyFont hf = new HersheyFont("cursive");
			Font hf = new Font(PicassoConstants.SCALE_FONT, Font.PLAIN, 10);

			AxisScale scale = latitudeMap.getAxisScale();
			//apexp
			
			Hashtable label = new Hashtable();
			label.put(new Double(14), "0.062");
			label.put(new Double(28), "0.25");
			label.put(new Double(42), "1");
			label.put(new Double(56), "4");
			label.put(new Double(70), "16");
			label.put(new Double(84), "64");
			
			/* if ( gdp.getQueryPacket().getDistribution().equals(PicassoConstants.UNIFORM_DISTRIBUTION)) { */
				scale.setSide(AxisScale.PRIMARY);
				scale.setLabelTable(label);
//				scale.createStandardLabels(100.0, 0.0, 0.0, 20.0);
			//apexp
			/* } else
				DiagramUtil.setScaleTickValues(scale, gdp, 0); */

			scale.setFont( hf );
//			scale.setTitle(relations[0] + "." + attributes[0]+" ["+Double.toString((int) (gdp.getQueryPacket().getStartPoint(PicassoConstants.a[0])*100))+","+Double.toString((int) (gdp.getQueryPacket().getEndPoint(PicassoConstants.a[0])*100))+"]@ "+Integer.toString((int)gdp.getResolution(PicassoConstants.a[0])));
			scale.setSnapToBox(true);
			scale.setColor(PicassoConstants.IMAGE_TEXT_COLOR);

			scale = longitudeMap.getAxisScale();
			scale.setSide(AxisScale.SECONDARY);
			//apexp
			/* if ( gdp.getQueryPacket().getDistribution().equals(PicassoConstants.UNIFORM_DISTRIBUTION)) { */
			//end apexp
			scale.setLabelTable(label);
//				scale.createStandardLabels(100.0, 0.0, 0.0, 20.0);
//				apexp
				/*
				}
			else
				DiagramUtil.setScaleTickValues(scale, gdp, 1);
				*/ 
			scale.setFont( hf );
			scale.setSnapToBox(true);
//			scale.setTitle(relations[1] + "." + attributes[1]+" ["+Double.toString((int) (gdp.getQueryPacket().getStartPoint(PicassoConstants.a[1])*100))+","+Double.toString((int) (gdp.getQueryPacket().getEndPoint(PicassoConstants.a[1])*100))+"]@ "+Integer.toString((int)gdp.getResolution(PicassoConstants.a[1])));
			
			scale.setColor(PicassoConstants.IMAGE_TEXT_COLOR);

			scale = altitudeMap.getAxisScale();
			altitudeMap.setScalarName(altName);
			Hashtable labelTable = new Hashtable();
			labelTable.put(new Double(0.0), "0.0");
			labelTable.put(new Double(0.2), "0.2");
			labelTable.put(new Double(0.4), "0.4");
			labelTable.put(new Double(0.6), "0.6");
			labelTable.put(new Double(0.8), "0.8");
			labelTable.put(new Double(1.0), "1.0");
			
//			labelTable.put(new Double(0.0), "0.0");
//			labelTable.put(new Double(0.06), "0.06");
//			labelTable.put(new Double(0.12), "0.12");
//			labelTable.put(new Double(0.25), "0.25");
//			labelTable.put(new Double(0.5), "0.5");
//			labelTable.put(new Double(1.0), "1.0");
			
			scale.setLabelTable(labelTable);
			scale.setFont( hf );
			scale.setSnapToBox(true);
			scale.setColor(PicassoConstants.IMAGE_TEXT_COLOR);

			ProjectionControl projCont = display1.getProjectionControl();
			DisplayRenderer dRenderer = display1.getDisplayRenderer();
			dRenderer.setBoxOn( false );
			dRenderer.setBackgroundColor(PicassoConstants.IMAGE_BACKGROUND);

			//		 Get display's graphics mode control and draw scales
			GraphicsModeControl dispGMC1 = (GraphicsModeControl)display1.getGraphicsModeControl();

			dispGMC1.setProjectionPolicy(DisplayImplJ3D.PARALLEL_PROJECTION);
			dispGMC1.setScaleEnable(true);

			
			DataReferenceImpl dataRef = new DataReferenceImpl("dataReference");
			dataRef.setData(flatFieldValues);
			
//			DataReferenceImpl dataRef1 = new DataReferenceImpl("dataReference1");
//			dataRef1.setData(flatFieldValues1);
			
			
			display1.addReference(dataRef);
//			display1.addReference(dataRef1);
			
			
			if(showPlanes)
			{
				double[][] flatSamples1;
				FlatField flatFieldValues1;
				DataReferenceImpl dataRef1;
			
				BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
				
				for(int p = 0;p<totalHorizontalPlan;p++)
				{
					
					double level = 1.0 / Math.pow(bouquetDataObj.getCommonRatio(), p);
					flatSamples1 = getSamplesForCostTemp(sortedPlanCount, gdp, level);

					if(gdp.getQueryPacket().getDistribution().startsWith(PicassoConstants.EXPONENTIAL_DISTRIBUTION))
					{
						double[][] nflatSamples = new double[2][(gdp.getResolution(PicassoConstants.a[0])+1)*(gdp.getResolution(PicassoConstants.a[1])+1)];
						int k=0,l=0;
						for(int i=0;i<gdp.getResolution(PicassoConstants.a[1]);i++)
						{ 	for(int j=0;j<gdp.getResolution(PicassoConstants.a[0]);j++)
						{
							nflatSamples[0][l++]=flatSamples1[0][k++];
							nflatSamples[1][l-1]=flatSamples1[1][k-1];
						}
						nflatSamples[0][l++]=flatSamples1[0][k-1];
						nflatSamples[1][l-1]=flatSamples1[1][k-1];
						}
						//copy uppermost row
						k-=gdp.getResolution(PicassoConstants.a[0]);
						for(int j=0;j<gdp.getResolution(PicassoConstants.a[0]);j++)
						{
							nflatSamples[0][l++]=flatSamples1[0][k++];
							nflatSamples[1][l-1]=flatSamples1[1][k-1];
						}
						nflatSamples[0][l++]=flatSamples1[0][k-1];
						nflatSamples[1][l-1]=flatSamples1[1][k-1];

						flatSamples1=nflatSamples;
					}
					flatFieldValues1 = getFlatFieldValues(domainTuple, funcType, gdp,/* selectivity,*/ addExtra,true/*,panel*/);
					flatFieldValues1.setSamples( flatSamples1 , false );
					dataRef1 = new DataReferenceImpl("dataReference1");
					dataRef1.setData(flatFieldValues1);
					display1.addReference(dataRef1);
				}
			}
			
			
			
			

			double[] aspect;

			aspect = new double[]{PicassoConstants.ASPECT_X, PicassoConstants.ASPECT_Y, PicassoConstants.ASPECT_Z};
//			aspect = new double[]{2.0,2.0,2.0};
			drawBackground(display1, PicassoConstants.THREE_D, maps);

//			 double[] 	make_matrix(double rotx, double roty, double rotz, double scale, double transx, double transy, double transz) 
			projCont.setMatrix(display1.make_matrix(48.75,52.5,60.0,0.65,0.0,0.0,0.0)); //0.5 normal

			projCont.setAspectCartesian( aspect );
		} catch (OutOfMemoryError bounded) {
//			JOptionPane.showMessageDialog(panel, "Out Of Memory Error, Please Restart PicassoClient","Error",JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(new JFrame(), "Out Of Memory Error, Please Restart PicassoClient","Error",JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		display1.setAlwaysAutoScale(true);
		return display1;
    }
	
	   public double[][] getSamplesForCost(int[][] sortedPlan, DiagramPacket gdp) {
//	   	 Create a flat array for plans
			double[][] flatSamples = null;
			
			int NROWS=1;
			if(gdp.getDimension()!=1)
				NROWS = gdp.getResolution(PicassoConstants.a[1]);//rss
			int NCOLS = gdp.getResolution(PicassoConstants.a[0]);//rss
			DataValues[] data = gdp.getData();
			
			double maxCost = gdp.getMaxCost();
			
			int index = 0;
			//int[][] sortedPlan = panel.getSortedPlan();
			int maxConditions = gdp.getDimension();
			if(gdp.getQueryPacket().getDistribution().startsWith(PicassoConstants.UNIFORM_DISTRIBUTION))
			{
				if ( maxConditions == 1 ) 
				{
					flatSamples = new double[2][NCOLS*2 + 4];
					
					for (int i=0; i < 2; i++)
					{
						for (int j=0; j < NCOLS + 2; j++) 
						{
							if(j == 0)
							{
								if(i == 0)
									flatSamples[0][index] = data[j].getCost()/maxCost;
								else
									flatSamples[0][index] = 0.05;
								flatSamples[1][index] = sortedPlan[0][data[j].getPlanNumber()];
							}
							else if(j == NCOLS + 1)
							{
								if(i == 0)
									flatSamples[0][index] = data[j-2].getCost()/maxCost;
								else
									flatSamples[0][index] = 0.05;
								flatSamples[1][index] = sortedPlan[0][data[j - 2].getPlanNumber()];
							}
							else
							{
								if(i == 0)
									flatSamples[0][index] = data[j-1].getCost()/maxCost;
								else
									flatSamples[0][index] = 0.05;
							
								flatSamples[1][index] = sortedPlan[0][data[j - 1].getPlanNumber()];
							}
							index++;
						}
					}
				}
				else
				{
					flatSamples = new double[2][(NCOLS + 2) * (NROWS + 2)];
					for (int i=0; i < NROWS + 2; i++)
					{
						for (int j=0; j < NCOLS + 2; j++) 
						{
							if(j > 0 && j <= NCOLS && i > 0 && i <= NROWS)
							{
								flatSamples[0][index] = data[(i-1)*NCOLS+j-1].getCost()/maxCost;
								flatSamples[1][index] = sortedPlan[0][data[(i-1)*NCOLS+j-1].getPlanNumber()];
							}
							else if(j == 0)
							{
								if(i == 0)
								{
									flatSamples[0][index] = data[0].getCost()/maxCost;
									flatSamples[1][index] = sortedPlan[0][data[0].getPlanNumber()];
								}
								else if(i == NROWS + 1)
								{
									flatSamples[0][index] = data[(i-2)*NCOLS].getCost()/maxCost;
									flatSamples[1][index] = sortedPlan[0][data[(i-2)*NCOLS].getPlanNumber()];
								}
								else
								{
									flatSamples[0][index] = data[(i-1)*NCOLS].getCost()/maxCost;
									flatSamples[1][index] = sortedPlan[0][data[(i-1)*NCOLS].getPlanNumber()];
								}
							}
							else if(j == NCOLS + 1)
							{
								if(i == 0)
								{
									flatSamples[0][index] = data[j - 2].getCost()/maxCost;
									flatSamples[1][index] = sortedPlan[0][data[j - 2].getPlanNumber()];
								}
								else if(i == NROWS + 1)
								{
									flatSamples[0][index] = data[(i-2)*NCOLS + j - 2].getCost()/maxCost;
									flatSamples[1][index] = sortedPlan[0][data[(i-2)*NCOLS + j - 2].getPlanNumber()];
								}
								else
								{
									flatSamples[0][index] = data[(i-1)*NCOLS + j - 2].getCost()/maxCost;
									flatSamples[1][index] = sortedPlan[0][data[(i-1)*NCOLS + j - 2].getPlanNumber()];
								}
							}
							else if(i == 0)
							{
								flatSamples[0][index] = data[j-1].getCost()/maxCost;
								flatSamples[1][index] = sortedPlan[0][data[j-1].getPlanNumber()];
							}
							else if(i == NROWS + 1)
							{
								flatSamples[0][index] = data[(i-2)*NCOLS+j-1].getCost()/maxCost;
								flatSamples[1][index] = sortedPlan[0][data[(i-2)*NCOLS+j-1].getPlanNumber()];
							}
							index++;
						}
					}
				}
			}
			else
			{
				// EXPO
				if ( maxConditions == 1 ) 
				{
					flatSamples = new double[2][NCOLS*2];
					
					for (int j=0; j < NCOLS; j++) {
						flatSamples[0][index] = data[j].getCost()/maxCost;
						flatSamples[1][index] = sortedPlan[0][data[j].getPlanNumber()];
						index++;
					}
					
					for (int j=0; j < NCOLS; j++) {
						flatSamples[0][index] = 0.05;
						flatSamples[1][index] = sortedPlan[0][data[j].getPlanNumber()];
						index++;
					}
					
					return flatSamples;
				}
					
				flatSamples = new double[2][NCOLS * NROWS];
				for (int i=0; i < NROWS; i++)
				{
					for (int j=0; j < NCOLS; j++) 
					{
			            if(maxCost==0.0)
			                flatSamples[0][index] = 1.0;
			            else
			                flatSamples[0][index] = data[i*NCOLS+j].getCost()/maxCost;
						flatSamples[1][index] = sortedPlan[0][data[i*NCOLS+j].getPlanNumber()] + 1;			//SN Modified +1 for white color in the starting of costDiagramColor array
						if ( flatSamples[0][index] > 1.0 ) {
//							CPrintErrToConsole(">>>>>> 1.0 Val : " + index + " " + flatSamples[0][index]);
							System.out.println(">>>>>> 1.0 Val : " + index + " " + flatSamples[0][index]);
						}
						index++;
					}
				}
			}
			return flatSamples;
	   }
	   public float[] setColorMap(int maxNumber, ScalarMap RGBMap) throws RemoteException, VisADException 
	   {
		   float[][] myColorTable = new float[4][maxNumber+1];

		   for(int r = 0; r < maxNumber+1; r++)
		   {	
			   float[] rgb = PicassoUtil.colorToFloats(new Color(PicassoConstants.costDiagramColor[(r%PicassoConstants.costDiagramColor.length)], true)); //true for getting alpha value
			   for(int c = 0 ; c < 4; c++)
				   myColorTable[c][r] = rgb[c];
		   }

		   /*float[] rgb = PicassoUtil.colorToFloats(Color.BLACK);
for(int c = 0 ; c < 3; c++)
myColorTable[c][maxNumber+1] = rgb[c];*/

		   // Get the ColorControl from the altitude RGB map
//		   ColorControl colCont1 = (ColorControl)RGBMap.getControl();
		   
		   ColorAlphaControl colCont1 = (ColorAlphaControl)RGBMap.getControl();

		   // Set the table
		   colCont1.setTable(myColorTable);


		   float[] cr = PicassoUtil.colorToFloats(Color.black);
		   return(cr);
	   }

	   public void drawBackground(DisplayImpl display, int dimension, ScalarMap[] maps) throws RemoteException, VisADException 
	   {
		   RealType longitude, latitude, altitude;
		   RealTupleType zdomain_tuple, zrange_tuple;
		   RealTupleType xdomain_tuple, xrange_tuple;
		   RealTupleType ydomain_tuple, yrange_tuple;
		   FunctionType func_zdomain_range;
		   FunctionType func_xdomain_range;
		   FunctionType func_ydomain_range;
		   Set zdomain_set, xdomain_set, ydomain_set;
		   FlatField zvals_ff, xvals_ff, yvals_ff;
		   DataReferenceImpl zdata_ref, xdata_ref, ydata_ref;
		   ScalarMap latMap, lonMap, altMap;

		   latitude = RealType.getRealType("latitude",null,null);
		   longitude = RealType.getRealType("longitude",null,null);
		   altitude = RealType.getRealType("altitude",null,null);

		   zdomain_tuple = new RealTupleType(latitude, longitude);
		   zrange_tuple = new RealTupleType( altitude );
		   xdomain_tuple = new RealTupleType(longitude,altitude);
		   xrange_tuple = new RealTupleType( latitude);
		   ydomain_tuple = new RealTupleType(altitude, latitude);
		   yrange_tuple = new RealTupleType(longitude);

		   func_zdomain_range = new FunctionType( zdomain_tuple, zrange_tuple);
		   func_xdomain_range = new FunctionType( xdomain_tuple, xrange_tuple);
		   func_ydomain_range = new FunctionType( ydomain_tuple, yrange_tuple);

		   zdomain_set = new Linear2DSet(zdomain_tuple, 0.0, 1.0, 2, 0.0, 1.0, 2);
		   xdomain_set = new Linear2DSet(xdomain_tuple, 0.0, 1.0, 2, 0.0, 1.0, 2);
		   ydomain_set = new Linear2DSet(ydomain_tuple, 0.0, 1.0, 2, 0.0, 1.0, 2);

		   float[][] zflat_samples = new float[1][4];
		   float[][] xflat_samples = new float[1][4];
		   float[][] yflat_samples = new float[1][4];
		   for(int i=0;i<4;i++){
			   zflat_samples[0][i] = 0.0f;
			   xflat_samples[0][i] = 0.0f;
			   yflat_samples[0][i] = 1.0f;
		   }
		   zvals_ff = new FlatField( func_zdomain_range, zdomain_set);
		   zvals_ff.setSamples( zflat_samples , false );
		   xvals_ff = new FlatField( func_xdomain_range, xdomain_set);
		   xvals_ff.setSamples( xflat_samples , false );
		   yvals_ff = new FlatField( func_ydomain_range, ydomain_set);
		   yvals_ff.setSamples( yflat_samples , false );

		   latMap = new ScalarMap( latitude,    Display.XAxis );
		   latMap.setRange(0.0,1.0);
		   latMap.setScaleEnable(false);
		   lonMap = new ScalarMap( longitude, Display.YAxis );
		   lonMap.setRange(0.0,1.0);
		   lonMap.setScaleEnable(false);
		   altMap = new ScalarMap( altitude,  Display.ZAxis );
		   altMap.setRange(0.0,1.0);
		   altMap.setScaleEnable(false);
		   display.addMap( latMap );
		   maps[4] = latMap;
		   maps[5] = lonMap;
		   maps[6] = altMap;

		   if ( dimension != PicassoConstants.ONE_D )
			   display.addMap( lonMap );

		   if ( dimension == PicassoConstants.THREE_D)
			   display.addMap( altMap );

		   zdata_ref = new DataReferenceImpl("zdata_ref");
		   zdata_ref.setData( zvals_ff );
		   xdata_ref = new DataReferenceImpl("xdata_ref");
		   xdata_ref.setData( xvals_ff );
		   ydata_ref = new DataReferenceImpl("ydata_ref");
		   ydata_ref.setData( yvals_ff );


		   ConstantMap[] zconstMap = {new ConstantMap(0.5f, Display.Red ),
				   new ConstantMap(0.5f, Display.Green ),
				   new ConstantMap(0.0f, Display.Blue)};
		   ConstantMap[] xconstMap = {new ConstantMap(0.1f, Display.Red ),
				   new ConstantMap(0.5f, Display.Green ),
				   new ConstantMap(0.4f, Display.Blue)};
		   ConstantMap[] yconstMap = {new ConstantMap(0.0f, Display.Red ),
				   new ConstantMap(0.4f, Display.Green ),
				   new ConstantMap(0.0f, Display.Blue)};

		   if ( dimension == PicassoConstants.THREE_D)
			   display.addReference( zdata_ref, zconstMap );

		   display.addReference( xdata_ref, xconstMap );

		   if ( dimension != PicassoConstants.ONE_D )
			   display.addReference( ydata_ref, yconstMap );	
	   }
	   
		public FlatField getFlatFieldValues(RealTupleType domainTuple, FunctionType funcType, DiagramPacket gdp, /*int selecType,*/ boolean addExtra, boolean is3d/*, MainPanel panel*/) throws Exception 
		{
			// float[] sValue = gdp.getPicassoSelectivity();
			float[] selecValues = gdp.getPicassoSelectivity();// = new
																// float[sValue.length];

			int NROWS=1;
			//int a[] = panel.getPicassoPanel().getDisplayedDimensions();
			if(gdp.getDimension()!=1)
				NROWS = gdp.getResolution(PicassoConstants.a[1]);//rss
			int NCOLS = gdp.getResolution(PicassoConstants.a[0]);//rss

			
			/*
			 * Currently Cost Diagram is generated for Picasso Selectivity.
			 */
			// MessageUtil.CPrintToConsole("Selec :: " + selecType);
//			if (selecType == PicassoConstants.PICASSO_SELECTIVITY) {
				selecValues = gdp.getPicassoSelectivity();
//			} else if (selecType == PicassoConstants.PREDICATE_SELECTIVITY)
//				selecValues = gdp.getPredicateSelectivity();
//			else if (selecType == PicassoConstants.PLAN_SELECTIVITY)
//				selecValues = gdp.getPlanSelectivity();
//			else if (selecType == PicassoConstants.DATA_SELECTIVITY)
//				selecValues = gdp.getDataSelectivity();

			// This and getExtra samples for data go together..
			// This is done so that the data is shown properly in resolutions of 10
			// and 30
			// Here the two extra bands are copied (the 0th row and the 0th column)
			if (addExtra) {
				int multipleFactor = (int) (100.0 / NROWS);
				double addValue = 100.0 / (multipleFactor * NROWS);
				int length = multipleFactor * NROWS;
				float[] sValues = new float[length * 2];
				int index = 0;
				for (int k = 0; k < 2; k++) {
					double pointValue = 0;
					for (int i = 0; i < multipleFactor; i++) {
						for (int j = 0; j < NROWS; j++) {
							sValues[index++] = new Float(pointValue).floatValue();// selecValues[k*NROWS+j];
							pointValue += addValue;
						}
					}
				}
				sValues[length - 1] = 100.0f;
				sValues[(length * 2) - 1] = 100.0f;
				selecValues = sValues;
				NCOLS = multipleFactor * NROWS;
				NROWS = NCOLS;
			}

			// Set the boundary conditions properly
			// apexp
			if (gdp.getQueryPacket().getDistribution().equals(
					PicassoConstants.UNIFORM_DISTRIBUTION)
					/*&& panel.getDBSettingsPanel().getSelecType() == PicassoConstants.PICASSO_SELECTIVITY*/) {
				// validateSelecValues(NROWS, NCOLS, selecValues, selecType);
				// apexp
			}
			// end apexp

			// commented by apexp//float[][] sampleSet = new
			// float[2][(NROWS)*(NCOLS)];
			// apexp
			float[][] osampleSet = null;
			float[][] sampleSet;
			int index = 0;
			int fac;
			if (gdp.getMaxResolution() < 100)
				fac = 10;
			else
				fac = 5;
			
			int ressum[] = new int[gdp.getDimension()];
			for(int i = 1; i < ressum.length; i++)
				ressum[i] = ressum[i-1] + gdp.getResolution(i-1);

			if (gdp.getQueryPacket().getDistribution().equals(
					PicassoConstants.UNIFORM_DISTRIBUTION)) {
				sampleSet = new float[2][(NROWS + 2) * (NCOLS + 2)];
				// end apexp
				for (int i = 0; i < NROWS + 2; i++) {
					// System.out.println(i + " :: " + selecValues[NROWS+i] + " ::
					// ");
					for (int j = 0; j < NCOLS + 2; j++) {
						
						if(j == 0)
						{
							sampleSet[0][index] = new Double(gdp.getQueryPacket().getStartPoint(PicassoConstants.a[0])).floatValue()*100;
						}
						else if(j == NCOLS + 1)
							sampleSet[0][index] = new Double(gdp.getQueryPacket().getEndPoint(PicassoConstants.a[0])).floatValue()*100;
						else
							sampleSet[0][index] = new Double(selecValues[ressum[PicassoConstants.a[0]]+j - 1]).floatValue();

						if (selecValues.length == NCOLS) // a complex way write 1-D.
							sampleSet[1][index] = new Double(selecValues[i]).floatValue();
						else if(i == 0)
							sampleSet[1][index] = new Double(gdp.getQueryPacket().getStartPoint(PicassoConstants.a[1])).floatValue()*100;
						else if (i == NROWS + 1)
							sampleSet[1][index] = new Double(gdp.getQueryPacket().getEndPoint(PicassoConstants.a[1])).floatValue()*100;
						else
							sampleSet[1][index] = new Double(selecValues[ressum[PicassoConstants.a[1]]+ i - 1]).floatValue();
						// System.out.print("(" + sampleSet[0][index] + "," +
						// sampleSet[1][index] + ")");
						index++;
					}
					int z = 0;
				}
				// apexp
			}

			else // exponential distri
			{

				osampleSet = new float[2][(NROWS + 1) * (NCOLS + 1)];

				/*
				 * 
				 * set the points directly, not lowers
				 */
				if (is3d) {
					for (int i = 0; i <= NROWS; i++) {
						for (int j = 0; j <= NCOLS; j++) {

							int k;

							/*if(i==0)
							{
								osampleSet[1][index] = new Double(gdp.getQueryPacket().getStartPoint(PicassoConstants.a[1])).floatValue() * 100;
							}*/
							if (i == NROWS) {
								osampleSet[1][index] = new Double(gdp.getQueryPacket().getEndPoint(PicassoConstants.a[1])).floatValue() * 100;
							} else {
								osampleSet[1][index] = i; /* exponential vs uniform */ /*ease of use for rajmohan*/
//								osampleSet[1][index] = selecValues[ressum[PicassoConstants.a[1]] + i];
							}

							/*if(j==0)
							{
								osampleSet[0][index] = new Double(gdp.getQueryPacket().getStartPoint(PicassoConstants.a[0])).floatValue() * 100;
							}*/
							if (j == NCOLS) {
								osampleSet[0][index] = new Double(gdp.getQueryPacket().getEndPoint(PicassoConstants.a[0])).floatValue() * 100;
							} else {
								osampleSet[0][index] = j;
//								osampleSet[0][index] = selecValues[ressum[PicassoConstants.a[0]]+j];
							}

							index++;
						} // end for j
					} // end for i

					sampleSet = osampleSet;
				}

				else // 2d
				{
					// set the lowers
					for (int i = 0; i <= NROWS; i++) {
						for (int j = 0; j <= NCOLS; j++) {

							int k;

							if (i == NROWS) {
								osampleSet[1][index] = new Double(gdp.getQueryPacket().getEndPoint(PicassoConstants.a[1])).floatValue() * 100; // 100.0f;
							} else {
								if (i != 0)
									osampleSet[1][index] = selecValues[ressum[PicassoConstants.a[1]] + i] - (selecValues[ressum[PicassoConstants.a[1]] + i] - selecValues[ressum[PicassoConstants.a[1]]+ i - 1]) / 2;
								else
									osampleSet[1][index] = selecValues[ressum[PicassoConstants.a[1]]];
							}

							if (j == NCOLS) {
								osampleSet[0][index] = new Double(gdp.getQueryPacket().getEndPoint(PicassoConstants.a[0])).floatValue() * 100;
							} else {
								if (j != 0)
									osampleSet[0][index] = selecValues[ressum[PicassoConstants.a[0]]+j]
											- (selecValues[ressum[PicassoConstants.a[0]]+j] - selecValues[ressum[PicassoConstants.a[0]]+j - 1])
											/ 2;
								else
									osampleSet[0][index] = selecValues[ressum[PicassoConstants.a[0]]];
							}

							index++;
						} // end for j
					} // end for i

					// multiple duplication interpolation code
					if (gdp.getMaxResolution() <= 100 && !is3d) {//rss
						sampleSet = new float[2][(NROWS + 1 + fac * NROWS)
								* (NCOLS + 1 + fac * NCOLS)];
						int k;
						index = 0;
						int oi = 0;

						for (int i = 0; i < NROWS; i++) {

							for (int j = 0; j < NCOLS; j++) {
								for (k = 0; k <= fac; k++) {
									sampleSet[0][index] = osampleSet[0][oi]
											+ (osampleSet[0][oi + 1] - osampleSet[0][oi])
											* k / (fac + 1);
									sampleSet[1][index] = osampleSet[1][oi];
									index++;
								}
								oi++;
							}

							sampleSet[0][index] = osampleSet[0][oi];
							sampleSet[1][index++] = osampleSet[1][oi++];

							// vertical
							for (int k2 = 1; k2 <= fac; k2++) {
								oi -= (NCOLS + 1);
								for (int j = 0; j < NCOLS; j++) {
									for (k = 0; k <= fac; k++) {
										sampleSet[0][index] = osampleSet[0][oi]
												+ (osampleSet[0][oi + 1] - osampleSet[0][oi])
												* k / (fac + 1);
										;
										sampleSet[1][index] = osampleSet[1][oi]
												+ (osampleSet[1][oi + NCOLS + 1] - osampleSet[1][oi])
												* k2 / (fac + 1);
										index++;
									}
									oi++;
								}
								sampleSet[0][index] = osampleSet[0][oi];
								sampleSet[1][index] = osampleSet[1][oi]
										+ (osampleSet[1][oi + NCOLS + 1] - osampleSet[1][oi])
										* k2 / (fac + 1);
								index++;
								oi++;
							}

						} // end for i

						// TOP ROW
						for (int j = 0; j < NCOLS; j++) {
							for (k = 0; k <= fac; k++) {
								sampleSet[0][index] = osampleSet[0][oi]
										+ (osampleSet[0][oi + 1] - osampleSet[0][oi])
										* k / (fac + 1);
								sampleSet[1][index] = osampleSet[1][oi];
								index++;
							}
							oi++;
						}
						sampleSet[0][index] = osampleSet[0][oi];
						sampleSet[1][index++] = osampleSet[1][oi++];
					} else {
						sampleSet = osampleSet;
					}
				} // end of else (2d)
			} // end of else (expo)
			// end apexp
			try {
				// apexp
				Gridded2DSet domainSet;
				if (gdp.getQueryPacket().getDistribution().equals(
						PicassoConstants.UNIFORM_DISTRIBUTION)) {
					domainSet = new Gridded2DSet(domainTuple, sampleSet, NCOLS + 2,
							NROWS + 2);
					// domainSet = new Gridded2DSet(domainTuple, sampleSet, (NCOLS+1)*(100/NCOLS), (100/NROWS) * (NROWS+1));
				} 
				else 
				{ //EXPONENTIAL
					if (gdp.getMaxResolution() <= 100 && !is3d)//rss
						sampleSet = validateSelecValues2(sampleSet, NROWS + 1 + fac	* NROWS, NCOLS + 1 + fac * NCOLS);
					else
						sampleSet = validateSelecValues2(sampleSet, NROWS + 1, NCOLS + 1);
					//sampleSet = validateSelecValues2(sampleSet, NROWS + 1 + fac*NROWS, NCOLS + 1+fac*NCOLS); //-ma
					// normal
					// domainSet = new Gridded2DSet(domainTuple, sampleSet, NROWS+1,
					// NCOLS+1);
					// multiple duplication
					if (gdp.getMaxResolution() <= 100 && !is3d)//rss
						domainSet = new Gridded2DSet(domainTuple, sampleSet, NCOLS
								+ 1 + fac * NCOLS, NROWS + 1 + fac * NROWS);
					else
						domainSet = new Gridded2DSet(domainTuple, sampleSet,
								NCOLS + 1, NROWS + 1);
					
					//the osampleset is of size (ncols+1) * (nrows+1)
					//domainSet = new Gridded2DSet(domainTuple, osampleSet, NCOLS + 1, NROWS + 1); //use for originial expo diagram.
					// end duplication
					// domainSet = new Gridded2DSet(domainTuple, sampleSet, NROWS*2,
					// NCOLS*2);
					// end and multiple duplication
					// domainSet = new Gridded2DSet(domainTuple, sampleSet,
					// NROWS*(2+fac), NCOLS*(2+fac));
				}
				// end apexp
				FlatField flatFieldValues = new FlatField(funcType, domainSet);
				return flatFieldValues;
			} catch (SetException se) {

				/*index = 0;
				for (int i = 0; i < NROWS; i++) {
					System.out.println(i + " :: " + selecValues[i] + " :: ");
					System.out.println("");
				}
				for (int j = 0; j < NCOLS; j++) {
					System.out.println(j + " :: " + selecValues[NROWS + j] + ")");
					// index++;
				}*/
				se.printStackTrace();
				throw new Exception("Gridded2DSet exception");
			} catch (Exception e) {
//				CPrintErrToConsole("Exception in Gridded2DSet");
				System.out.println("Exception in Gridded2DSet");
				e.printStackTrace();
				throw new Exception("Gridded2DSet exception");
			}
			// return null;
		}
		
		private float[][] validateSelecValues2(float[][] s, int rows,
				int cols) {
			int i, j, k;
			float c, d = 1;
			double maxval, diff, theval;
			int cnt;

			for (i = 0; i < rows; i++) {
				c = 1;
				for (j = 0; j < cols; j++) {
					if (s[0][i * cols + j] < 1e-6f
							|| (j != 0 && s[0][i * cols + j] < s[0][i * cols + j
									- 1])) {
						s[0][i * cols + j] = c * 1e-6f;
						c++;
					}
					if (s[1][i * cols + j] < 1e-6f
							|| (i != 0 && s[1][i * cols + j] < s[1][(i - 1) * cols
									+ j])) {
						s[1][i * cols + j] = d * 1e-6f;
					}
				}
				d++;
			}

			// This part tries to create a small epsilon such that suppose your
			// selvalues were
			// 0.0005 0.0005 0.0005 0.00500001, your epsilon couldn't be such that
			// 0.0005 + epsilon >(=) 0.00500001
			for (i = 0; i < rows; i++) {
				for (j = 0; j < cols; j++) {
					cnt = 1;
					k = j;
					while (k != 0 && k != cols
							&& s[0][i * cols + k] == s[0][i * cols + k - 1]) {
						cnt++;
						k++;
					}
					if (cnt != 1) {
						if (k == cols) {
							maxval = 0.999999999999999;
						} else {
							maxval = s[0][i * cols + k]; // the greater one after
															// the equal set
						}
						diff = maxval - s[0][i * cols + j]; // the difference
															// between the bigger
															// value and the equal
															// set

						diff /= 2;
						diff /= cnt;
						// now diff (epsilon) is such that after adding it to all
						// these same valued points, the final point will
						// still be before the 1st point with a different
						// selectivity value.
						if (diff > 1e-6)
							diff = 1e-6;

						theval = s[0][i * cols + j];
						k = j;
						while (k != 0 && k != cols && s[0][i * cols + k] == theval) {
							s[0][i * cols + k] = s[0][i * cols + k - 1]
									+ (float) diff; // add our epsilon to it
							k++;
						}
					} // end if(cnt!=1)
				} // end for j
			} // end for i

			for (i = 1; i < rows; i++) {
				cnt = 1;
				k = i;
				// check 0th column only (rest will be same w.r.t. y co-ord)
				while (s[1][k * cols + 0] == s[1][(k - 1) * cols + 0]) {
					cnt++;
					k++;
				}
				if (cnt != 1) {
					if (k == rows) {
						maxval = 0.999999999999999;
					} else {
						maxval = s[1][k * cols + 0]; // the greater one after the
														// equal set
					}
					diff = maxval - s[1][i * cols + 0]; // the difference between
														// the bigger value and the
														// equal set

					diff /= 2;
					diff /= cnt;
					if (diff > 1e-6)
						diff = 1e-6;
					theval = s[1][i * cols + 0];
					k = i;
					while (k != 0 && k != rows && s[1][k * cols + 0] == theval) {

						for (j = 0; j < cols; j++)
							s[1][k * cols + j] = s[1][(k - 1) * cols + j]
									+ (float) diff; // add our epsilon to it
						k++;
					}
				}
			}
			return s;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		   public double[][] getSamplesForCostTemp(int[][] sortedPlan, DiagramPacket gdp, double level) {
//			   	 Create a flat array for plans
					double[][] flatSamples = null;
					
					int NROWS=1;
					if(gdp.getDimension()!=1)
						NROWS = gdp.getResolution(PicassoConstants.a[1]);//rss
					int NCOLS = gdp.getResolution(PicassoConstants.a[0]);//rss
					DataValues[] data = gdp.getData();
					
					double maxCost = gdp.getMaxCost();
					
					int index = 0;
					//int[][] sortedPlan = panel.getSortedPlan();
					int maxConditions = gdp.getDimension();
					if(gdp.getQueryPacket().getDistribution().startsWith(PicassoConstants.UNIFORM_DISTRIBUTION))
					{
						if ( maxConditions == 1 ) 
						{
							flatSamples = new double[2][NCOLS*2 + 4];
							
							for (int i=0; i < 2; i++)
							{
								for (int j=0; j < NCOLS + 2; j++) 
								{
									if(j == 0)
									{
										if(i == 0)
											flatSamples[0][index] = data[j].getCost()/maxCost;
										else
											flatSamples[0][index] = 0.05;
										flatSamples[1][index] = sortedPlan[0][data[j].getPlanNumber()];
									}
									else if(j == NCOLS + 1)
									{
										if(i == 0)
											flatSamples[0][index] = data[j-2].getCost()/maxCost;
										else
											flatSamples[0][index] = 0.05;
										flatSamples[1][index] = sortedPlan[0][data[j - 2].getPlanNumber()];
									}
									else
									{
										if(i == 0)
											flatSamples[0][index] = data[j-1].getCost()/maxCost;
										else
											flatSamples[0][index] = 0.05;
									
										flatSamples[1][index] = sortedPlan[0][data[j - 1].getPlanNumber()];
									}
									index++;
								}
							}
						}
						else
						{
							flatSamples = new double[2][(NCOLS + 2) * (NROWS + 2)];
							for (int i=0; i < NROWS + 2; i++)
							{
								for (int j=0; j < NCOLS + 2; j++) 
								{
									if(j > 0 && j <= NCOLS && i > 0 && i <= NROWS)
									{
										flatSamples[0][index] = data[(i-1)*NCOLS+j-1].getCost()/maxCost;
										flatSamples[1][index] = sortedPlan[0][data[(i-1)*NCOLS+j-1].getPlanNumber()];
									}
									else if(j == 0)
									{
										if(i == 0)
										{
											flatSamples[0][index] = data[0].getCost()/maxCost;
											flatSamples[1][index] = sortedPlan[0][data[0].getPlanNumber()];
										}
										else if(i == NROWS + 1)
										{
											flatSamples[0][index] = data[(i-2)*NCOLS].getCost()/maxCost;
											flatSamples[1][index] = sortedPlan[0][data[(i-2)*NCOLS].getPlanNumber()];
										}
										else
										{
											flatSamples[0][index] = data[(i-1)*NCOLS].getCost()/maxCost;
											flatSamples[1][index] = sortedPlan[0][data[(i-1)*NCOLS].getPlanNumber()];
										}
									}
									else if(j == NCOLS + 1)
									{
										if(i == 0)
										{
											flatSamples[0][index] = data[j - 2].getCost()/maxCost;
											flatSamples[1][index] = sortedPlan[0][data[j - 2].getPlanNumber()];
										}
										else if(i == NROWS + 1)
										{
											flatSamples[0][index] = data[(i-2)*NCOLS + j - 2].getCost()/maxCost;
											flatSamples[1][index] = sortedPlan[0][data[(i-2)*NCOLS + j - 2].getPlanNumber()];
										}
										else
										{
											flatSamples[0][index] = data[(i-1)*NCOLS + j - 2].getCost()/maxCost;
											flatSamples[1][index] = sortedPlan[0][data[(i-1)*NCOLS + j - 2].getPlanNumber()];
										}
									}
									else if(i == 0)
									{
										flatSamples[0][index] = data[j-1].getCost()/maxCost;
										flatSamples[1][index] = sortedPlan[0][data[j-1].getPlanNumber()];
									}
									else if(i == NROWS + 1)
									{
										flatSamples[0][index] = data[(i-2)*NCOLS+j-1].getCost()/maxCost;
										flatSamples[1][index] = sortedPlan[0][data[(i-2)*NCOLS+j-1].getPlanNumber()];
									}
									index++;
								}
							}
						}
					}
					else
					{
						// EXPO
						if ( maxConditions == 1 ) 
						{
							flatSamples = new double[2][NCOLS*2];
							
							for (int j=0; j < NCOLS; j++) {
								flatSamples[0][index] = data[j].getCost()/maxCost;
								flatSamples[1][index] = sortedPlan[0][data[j].getPlanNumber()];
								index++;
							}
							
							for (int j=0; j < NCOLS; j++) {
								flatSamples[0][index] = 0.05;
								flatSamples[1][index] = sortedPlan[0][data[j].getPlanNumber()];
								index++;
							}
							
							return flatSamples;
						}
							
						flatSamples = new double[2][NCOLS * NROWS];
						for (int i=0; i < NROWS; i++)
						{
							for (int j=0; j < NCOLS; j++) 
							{
					            if(maxCost==0.0)
					                flatSamples[0][index] = 1.0;
					            else
//					                flatSamples[0][index] = data[i*NCOLS+j].getCost()/maxCost;
					            	flatSamples[0][index] = level;
					            flatSamples[1][index] = 0;					// 0 shows white color
//								flatSamples[1][index] = sortedPlan[0][data[i*NCOLS+j].getPlanNumber()];
								if ( flatSamples[0][index] > 1.0 ) {
//									CPrintErrToConsole(">>>>>> 1.0 Val : " + index + " " + flatSamples[0][index]);
									System.out.println(">>>>>> 1.0 Val : " + index + " " + flatSamples[0][index]);
								}
								index++;
							}
						}
					}
					return flatSamples;
			   }
		
		
		
		
		
		
		
		
		
		
		
		
		
//		public static final void CPrintErrToConsole(String str) {
//			if ( !MainPanel.IS_APPLET ) {
//			try
//			{
//				FileWriter fis = new FileWriter (MainPanel.FileName, true);
//				fis.write(str + "\n");
//				fis.flush();
//				fis.close();
//			}
//			catch(FileNotFoundException fnfe)
//			{
//				System.out.println("File not found: "+fnfe);
//			}
//			catch(IOException ioe)
//			{
//				System.out.println("IOExceptio: "+ioe);
//			}
//			}
//			System.out.println("CLIENT ERROR :: " + str);
//		}
}
