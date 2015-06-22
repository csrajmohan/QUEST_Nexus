package quest;

import iisc.dsl.picasso.common.PicassoConstants;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ShapeUtilities;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.Ellipse2D;
public class DrawContours
{
	boolean logScale = true;
	XYSeries cont[][];
	XYSeries selPath;
	XYSeries selPoint;
	XYPlot plot;
	XYAreaRenderer areaRend;
	BouquetDriver bouquetDriverObj;
	public DrawContours(AllObjects allObjects)
	{
		allObjects.setDrawContoursObj(this);
	}

	JPanel drawContour(AllObjects allObjects, int width, int height)
	{
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		bouquetDriverObj = allObjects.getBouquetDriverObj();
		
		int totalPlans = bouquetDataObj.getTotalPlans();
		int totalContours = bouquetDataObj.getTotalContours();
		int contourPlansCount[] = bouquetDataObj.getContourPlansCount();
		int finalPlansSet[] = bouquetDataObj.getFinalPlanSet();
		int dimension = bouquetDataObj.getDimension();
		int resolution = bouquetDataObj.getResolution();
		
		String errorProneRelationNames[] = bouquetDataObj.getErrorProneBaseRelationNames();
		
		JPanel plotPanel = new JPanel();
		
		cont = new XYSeries[totalContours+1][totalPlans];
		

		int contourLocation[] = bouquetDataObj.getContourLocationInESS();
		
		for(int i=0;i<=totalContours;i++)
		{
			for(int j=0;j<totalPlans;j++)
			{
				if(i==totalContours)
					cont[i][j]=new XYSeries("P"+(j+1));
				else
					cont[i][j]=new XYSeries("Plan"+(i+1)+""+(j+1));
			}
		}
	
		selPath = new XYSeries("Selectivity Path");
		selPoint = new XYSeries("selectivity Point");
	
		int spaceSize[]=new int [dimension];
		for(int i=0;i<dimension;i++)
		{
			spaceSize[i]=resolution;
		}
		int totalPoints = (int)Math.pow(resolution, dimension);
		for(int i=totalPoints-1;i>=0;i--)
		{			
			if(contourLocation[i]!=0)
			{
				int spaceLocation[] = indexCalc(spaceSize, i, dimension);
				int planIndex = bouquetDriverObj.getOptimalPlan(i, finalPlansSet);

				int contour = contourLocation[i];
				if(logScale)
					cont[contour-1][planIndex].add(bouquetDriverObj.picsel[spaceLocation[0]], bouquetDriverObj.picsel[spaceLocation[1]]);
				else
					cont[contour-1][planIndex].add(spaceLocation[0], spaceLocation[1]);


				}
		}
		double minLolations[][][] = new double[totalContours][totalPlans][dimension];
		double maxLocations[][][] = new double[totalContours][totalPlans][dimension];

		int contourPlans[][] = bouquetDataObj.getContourPlans();

		for(int i=0;i<totalContours;i++)
		{
			for(int j=0;j<contourPlansCount[i];j++)
			{
				int plan = contourPlans[i][j];
				int index = Arrays.binarySearch(finalPlansSet, plan);
				double minX = cont[i][index].getMinX();
				double minY = cont[i][index].getMinY();
				
				double maxX = cont[i][index].getMaxX();
				double maxY = cont[i][index].getMaxY();
				
				minLolations[i][index][0] = minX;
				minLolations[i][index][1] = maxY;
				
				maxLocations[i][index][0] = maxX;
				maxLocations[i][index][1] = minY;
			}
		}
		
		bouquetDataObj.setPlanLocationMinCoordinate(minLolations);
		bouquetDataObj.setPlanLocationMaxCoordinate(maxLocations);
		
		XYSeriesCollection xySerCol=  new XYSeriesCollection();
		
		
		
		for(int i=0;i<=totalContours;i++)
		{
			for(int j=0;j<totalPlans;j++)
			{
				xySerCol.addSeries(cont[i][j]);
			}
		}

		xySerCol.addSeries(selPath);
		xySerCol.addSeries(selPoint);
	
		XYDataset xyDataset = xySerCol; 

		JFreeChart chart = ChartFactory.createXYLineChart
				("Isocost Contours in ESS (log-log scale)",errorProneRelationNames[0], errorProneRelationNames[1],
						xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if(allObjects.demo_mode)
			chart.getTitle().setFont(new Font("Dialog", Font.BOLD, 12));

		plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.WHITE);
		
		LegendTitle legend = chart.getLegend();
		Font labelFont;
		if(allObjects.demo_mode)
			labelFont = new Font("SansSerif", Font.BOLD, 12);
		else
			labelFont = new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE);			//For demo

		legend.setItemFont(labelFont);
		legend.setPosition(RectangleEdge.RIGHT);
		
//		plot.setDomainGridlinePaint(Color.black);
//		plot.setRangeGridlinePaint(Color.black);
		  
//		plot.getRenderer().setSeriesVisibleInLegend(0, false);
//		plot.getRenderer().setSeriesPaint(totalContours, Color.BLACK);
		
		
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
//      renderer.setSeriesShapesVisible((totalContours+1)*BouquetDriver.planSet.length+1, true);
//      renderer.setSeriesLinesVisible((totalContours+1)*BouquetDriver.planSet.length+1, false);
//        
//      renderer.setSeriesShapesVisible((totalContours+1)*BouquetDriver.planSet.length+2, true);
//      renderer.setSeriesLinesVisible((totalContours+1)*BouquetDriver.planSet.length+2, false);
		for(int i=0;i<=totalContours;i++)
		{
			for(int j=0;j<totalPlans;j++)
			{
				renderer.setSeriesStroke(i*totalPlans + j, new BasicStroke(1.5f));
//				plot.getRenderer().setSeriesPaint(i*totalPlans + j, new Color(PicassoConstants.color[j]));
				plot.getRenderer().setSeriesPaint(i*totalPlans + j, new Color(PicassoConstants.color[finalPlansSet[j] % PicassoConstants.color.length]));

				if(i!=totalContours)
				{
					plot.getRenderer().setSeriesVisibleInLegend(i * totalPlans + j, false);
				}
				if(i == totalContours-1)
				{
					renderer.setSeriesShapesVisible(i*totalPlans + j, true);
					Shape s = ShapeUtilities.createDiamond(2.0f);
					renderer.setSeriesShape(i*totalPlans + j, s);
				}
			}
		}
		
		/*
		 * For simulated selectivity point.
		 */
		Shape s = new Ellipse2D.Double(0, 0, 5, 5);
		renderer.setSeriesVisibleInLegend((totalContours+1)*totalPlans+1, false);
		renderer.setSeriesShapesVisible((totalContours+1)*totalPlans+1, true);
		renderer.setSeriesShape((totalContours+1)*totalPlans+1, s);
		
		
		/*
		 * For selectivity Path
		 */
		plot.getRenderer().setSeriesPaint((totalContours+1)*totalPlans, Color.BLACK);
		plot.getRenderer().setSeriesVisibleInLegend((totalContours+1)*totalPlans, false);

		
		renderer.setSeriesShapesVisible((totalContours+1)*totalPlans, true);
        renderer.setSeriesStroke(
        		(totalContours+1)*totalPlans, new BasicStroke(
                        1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1.0f, new float[] {5.0f, 6.0f}, 0.0f
                    )
            );
		  if(logScale)
		  {
			  LogAxis x = new LogAxis(errorProneRelationNames[0]+" selectivity");
			  LogAxis y = new LogAxis(errorProneRelationNames[1]+" selectivity");

			  x.setBase(2);
			  y.setBase(2);

			  x.setTickUnit(new NumberTickUnit(2));
			  y.setTickUnit(new NumberTickUnit(2));

			  plot.setDomainAxis(x);
			  plot.setRangeAxis(y);

			  if(allObjects.demo_mode)
			  {
				  x.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
				  x.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
					
				  y.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
				  y.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
				  x.setMinorTickMarksVisible(false);
				  y.setMinorTickMarksVisible(false);

			  	  x.setStandardTickUnits(NumberAxis.createStandardTickUnits());
				  y.setStandardTickUnits(NumberAxis.createStandardTickUnits());
			  
				  y.setAutoRange(false);
				  x.setAutoRange(false);

			  }
			  else
			  {
				  x.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.AXIS_LABEL_FONT_SIZE));			//For demo
				  x.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE));
				  y.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.AXIS_TICK_FONT_SIZE));
				  y.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE));
			  }			  
				
//			  x.setNumberFormatOverride(currencyFormat);
//			  y.setNumberFormatOverride(currencyFormat);
		  
//			  x.setStandardTickUnits(NumberAxis.createStandardTickUnits());
//			  y.setStandardTickUnits(NumberAxis.createStandardTickUnits());
//		  
//			  y.setAutoRange(false);
//			  x.setAutoRange(false);
		  
			  x.setLowerBound(0.031);
			  y.setLowerBound(0.031);
				  
//			  x.setRange(bouquetDriverObj.picsel[0]/2, 100);
//			  y.setRange(bouquetDriverObj.picsel[0]/2, 100);
		  }
		  else
		  {
				
				NumberAxis domain = (NumberAxis)plot.getDomainAxis();
				NumberAxis range = (NumberAxis)plot.getRangeAxis();
//				range.setTickUnit(new NumberTickUnit(10));
			  
				domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.AXIS_TICK_FONT_SIZE));
				domain.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE));
				
				range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, QUESTConstants.AXIS_TICK_FONT_SIZE));
				range.setLabelFont(new Font("SansSerif", Font.BOLD, QUESTConstants.AXIS_LABEL_FONT_SIZE));
		  }
		
		  XYDataset areaSeriesDataset = buildAreaSeriesCollection(bouquetDataObj);
		  plot.setDataset(1, areaSeriesDataset);
		  areaRend = new XYAreaRenderer();
		  plot.setRenderer(1, areaRend);

		  /* this code applies auto-range on domain and range axis
		   * added on Aug 20, 2014 */
		  if(!allObjects.demo_mode)
		  {
			plot.getRangeAxis().setAutoRange(true);
			plot.getDomainAxis().setAutoRange(true);
			plot.configureRangeAxes();
			plot.configureDomainAxes();
		  }
		  for(int i=0;i<totalContours;i++)
		  {
			  for(int j=0;j<totalPlans;j++)
			  {
				  areaRend.setSeriesPaint(i*totalPlans + j, new Color(0,0,255,0));
//				  areaRend.setSeriesPaint(i*totalPlans + j, new Color(PicassoConstants.AreaColor[j], true));
				  areaRend.setSeriesVisibleInLegend(i*totalPlans + j, false);
			  }
		  }
		  
		ChartPanel cPanel = new ChartPanel(chart);

//		cPanel.setPreferredSize(new Dimension(500, 400));				//uncomment
//		cPanel.setPreferredSize(new Dimension(500, 380));				//For demo
//		cPanel.setPreferredSize(new Dimension(350, 250));
		cPanel.setPreferredSize(new Dimension(width, height));	

		plotPanel.add(cPanel);
		plotPanel.setBackground(Color.WHITE);

		return(plotPanel);
	}
	XYSeriesCollection buildAreaSeriesCollection(BouquetData bouquetDataObj)
	{
		float picsel[] = bouquetDriverObj.picsel;
		XYSeriesCollection areaSeriesColl = new XYSeriesCollection();
		int contour = bouquetDataObj.getTotalContours();
		int totalPlans = bouquetDataObj.getTotalPlans();
		
		for(int i=0;i<contour;i++)
		{
			for(int j=0;j<totalPlans;j++)
			{
				if(cont[i][j].getItemCount()!=0)
				{
					double minX= cont[i][j].getMinX();
					if(minX>0.005)
//					if(minX>0)
					{
						XYSeries s = new XYSeries("Plan"+(i+1)+","+(j+1));
						if(logScale)
							s.add(picsel[0], cont[i][j].getMaxY());
						else
							s.add(0, cont[i][j].getMaxY());
						for(int k=0;k<cont[i][j].getItemCount();k++)
						{
							XYDataItem datapoint= cont[i][j].getDataItem(k);
							s.add(datapoint);
						}
						areaSeriesColl.addSeries(s);
					}
					else
					{
						areaSeriesColl.addSeries(cont[i][j]);
					}
				}
				else
				{
					areaSeriesColl.addSeries(cont[i][j]);
				}
			}
		}
		return(areaSeriesColl);
	}
    //Column major
	int[] indexCalc(int size[], int address, int dim)
	{
		int maxAdd=1;
		for(int i=0;i<dim;i++)
		{
			maxAdd *= size[i];
		}
		if(address >= maxAdd)
		{
			System.err.println("Error: Address in invalid");
			System.exit(0);
		}
		int index[] = new int [dim];
		for(int i=0;i<dim;i++)
		{
			int n;
			n = address % size[i];
			address = address / size[i];
			index[i] = n;
		}
		return(index);
	}
	void changeContourColor(int contour,int plan, int totalPlans, int finalPlanSet[])
	{
//		plot.getRenderer().setSeriesPaint(contour * totalPlans + plan, Color.BLACK);
//		areaRend.setSeriesPaint(contour * totalPlans + plan, new Color(PicassoConstants.AreaColor[plan], true));
		
		plot.getRenderer().setSeriesPaint(contour * totalPlans + plan, Color.BLACK);
		areaRend.setSeriesPaint(contour * totalPlans + plan, new Color(PicassoConstants.AreaColor[finalPlanSet[plan] % PicassoConstants.AreaColor.length], true));	
	}
	void path(int x,int y,int steps)
	{
		if(logScale)
			selPath.add(bouquetDriverObj.picsel[x], bouquetDriverObj.picsel[y]);
		else	
			selPath.add(x, y);
	}
	void resetContoursPlot(AllObjects allObjects)
	{
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		int totalContours = bouquetDataObj.getTotalContours();
		int totalPlans = bouquetDataObj.getTotalPlans();
		int finalPlanSet[] = bouquetDataObj.finalPlanSet;
		for(int i=0;i<=totalContours;i++)
		{
			for(int j=0;j<totalPlans;j++)
			{
				plot.getRenderer().setSeriesPaint(i*totalPlans + j, new Color(PicassoConstants.color[finalPlanSet[j] % PicassoConstants.color.length]));
				areaRend.setSeriesPaint(i*totalPlans + j, new Color(0,0,255,0));
			}
		}
		selPath.clear();
	}
	void addSelectivityPoint(double sel[])
	{
		selPoint.clear();
		selPoint.add(sel[0], sel[1]);
	}
	void clearSelectivityPoint()
	{
		selPoint.clear();
	}
}