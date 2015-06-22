package quest;
/*
 * this file is taken from Picasso code.
 */
import iisc.dsl.picasso.common.ds.TreeNode;
import iisc.dsl.picasso.server.plan.Node;
import iisc.dsl.picasso.server.plan.Plan;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;


public class CreatePlanTree 
{
	private int leafid=0;
	public Plan otherPlans[] = new Plan[QUESTConstants.numExtraPlans - 1];

	
	void createAllPlanTreeStructure(String planWithExplain[], int totalPlans, AllObjects allObjects)
	{
		TreeNode planTreeRootNodes[] = new TreeNode[totalPlans];
		Vector<String> textualPlan = new Vector<String>();
		try
		{
			ResultSet rs=null;

			ConnectDB connectDBObj = allObjects.getConnectDBObj();
			Connection con = connectDBObj.connection;
			Statement st = con.createStatement();
			
			CreatePlanTree createPlanTree = new CreatePlanTree();
			
			for(int i=0;i<totalPlans;i++)
			{
//				Plan plan = new Plan();
				textualPlan.clear();
				rs = st.executeQuery(planWithExplain[i]);
				while (rs.next())
					textualPlan.add(rs.getString(1));
				
				textualPlan.remove(textualPlan.size()-1);			//removing completed/Not Completed Line
				planTreeRootNodes[i] = createPlanTree.getPlanStructure(textualPlan);
			}
			
			BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
			bouquetDataObj.setPlanTreeRootNodes(planTreeRootNodes);
			
			rs.close();
			st.close();
		}
		catch(Exception e)
		{
			System.out.println("Execption in DrawPlan Class:"+e);
			e.printStackTrace();
		}
	}
	
	TreeNode getPlanStructure(Vector<String> textualPlan)
	{
		Plan plan = new Plan();
		String str = (String)textualPlan.remove(0);
		CreateNode(plan, str, 0, -1);
		plan.isOptimal = true;
		FindChilds(plan, 0, 1, textualPlan, 2);
		SwapSORTChilds(plan);
		TreeNode root = plan.createPlanTree();
		return(root);
	}
	int CreateNode(Plan plan, String str, int id, int parentid) 
	{

		if(id==1)
			leafid=-1;
		Node node = new Node();

		if(str.indexOf("->")>=0)
			str=str.substring(str.indexOf("->")+2).trim();
		String cost = str.substring(str.indexOf("..") + 2, str.indexOf("rows") - 1);
		String card = str.substring(str.indexOf("rows") + 5, str.indexOf("width")-1);
		if(str.indexOf(" on ") != -1 ||str.startsWith("Subquery Scan")) {
			node.setId(id++);
			node.setParentId(parentid);
			node.setCost(Double.parseDouble(cost));
			node.setCard(Double.parseDouble(card));
			if(str.startsWith("Index Scan"))
				node.setName("Index Scan");
			else if(str.startsWith("Subquery Scan"))
				node.setName("Subquery Scan");
			else
				node.setName(str.substring(0,str.indexOf(" on ")).trim());
			plan.setNode(node,plan.getSize());
			node = new Node();
			node.setId(leafid--);
			node.setParentId(id-1);
			node.setCost(0.0);
			node.setCard(0.0);
			if(str.startsWith("Subquery Scan"))
				node.setName(str.trim().substring("Subquery Scan".length(),str.indexOf("(")).trim());
			else
				node.setName(str.substring(str.indexOf(" on ")+3,str.indexOf("(")).trim());
			plan.setNode(node,plan.getSize());
		} else {
			node.setId(id++);
			node.setParentId(parentid);
			node.setCost(Double.parseDouble(cost));
			node.setCard(Double.parseDouble(card));
			node.setName(str.substring(0,str.indexOf("(")).trim());

			plan.setNode(node,plan.getSize());
		}
		return id;
	}

	boolean optFlag;
	int FindChilds(Plan plan, int parentid, int id, Vector text, int childindex) 
	{
		String str ="";
		int oldchildindex=-5;
		while(text.size()>0) {
			int stindex;            
			str = (String)text.remove(0);

			if(QUESTConstants.TOP_K == true){


				if(str.trim().startsWith("NEXT PLAN")){

					if(QUESTConstants.currentExtraPlanNum > 0  && QUESTConstants.lookingExtraPlans == false){
						text.addElement(( Object )str);
						QUESTConstants.lookingExtraPlans = true;
						return 0;
					}

					if(QUESTConstants.saveExtraPlans == false){
						if(QUESTConstants.currentExtraPlanNum == 0) //8
						QUESTConstants.lookingExtraPlans = true;

						str = (String)text.remove(0); //7
						int temp = QUESTConstants.currentExtraPlanNum + 2; //6

						System.out.println("Plan "+ temp +" cost = " +str.substring(str.indexOf("..") + 2, str.indexOf("rows") - 1)); //5
						QUESTConstants.otherPlanCosts[QUESTConstants.currentExtraPlanNum] = Double.parseDouble( str.substring(str.indexOf("..") + 2, str.indexOf("rows") - 1)); //4

						//	return id;

						QUESTConstants.currentExtraPlanNum++; //3



						if(QUESTConstants.lookingExtraPlans == true){//2
							//	            				System.out.println(str);
							continue;		
						}  //1
					}
					else if(QUESTConstants.lookingExtraPlans == false){
						//text.addElement(( Object )str);
						QUESTConstants.lookingExtraPlans = true;
						continue;
					}
				}
				else if(QUESTConstants.lookingExtraPlans == true) {

					if(QUESTConstants.saveExtraPlans == true){
						if(QUESTConstants.currentExtraPlanNum == 0)
							SwapSORTChilds(plan);

						otherPlans[QUESTConstants.currentExtraPlanNum] = new Plan();

						//	            			System.out.println("Create node works on: \n" + str);
						CreateNode(otherPlans[QUESTConstants.currentExtraPlanNum], str, 0, -1);
						QUESTConstants.lookingExtraPlans = false;
						FindChilds(otherPlans[QUESTConstants.currentExtraPlanNum++], 0, 1, text, 2);
						QUESTConstants.currentExtraPlanNum--;
						SwapSORTChilds(otherPlans[QUESTConstants.currentExtraPlanNum]);

						QUESTConstants.otherPlanCosts[QUESTConstants.currentExtraPlanNum] = otherPlans[QUESTConstants.currentExtraPlanNum].getCost();
						//int temp = QUESTConstants.currentExtraPlanNum + 2;
						//System.out.println("Plan "+ temp +"Cost from plan = " + otherPlans[QUESTConstants.currentExtraPlanNum].getCost());


						//System.out.println(str);
					}
					continue;		            		
				}
			}


			//  System.out.println("findling_childs");




			if (str.indexOf("Plan Type: STABLE")>=0)
				optFlag = false;
			if(str.trim().startsWith("InitPlan"))
				stindex=str.indexOf("InitPlan");
			else if(str.trim().startsWith("SubPlan"))
				stindex=str.indexOf("SubPlan");
			else
				stindex=str.indexOf("->");







			if(stindex==-1)
				continue;
			if(stindex==oldchildindex) {
				childindex=oldchildindex;
				oldchildindex=-5;
			}
			if(stindex < childindex) {
				text.add(0,str);
				break;
			}


			if(stindex>childindex) {
				if(str.trim().startsWith("InitPlan")||str.trim().startsWith("SubPlan")) {
					str = (String)text.remove(0);
					stindex=str.indexOf("->");
					oldchildindex=childindex;
					childindex=str.indexOf("->");
				}
				text.add(0,str);
				id = FindChilds(plan, id-1, id, text, stindex);
				continue;
			}

			if(str.trim().startsWith("InitPlan")||str.trim().startsWith("SubPlan")) {
				str = (String)text.remove(0);
				stindex=str.indexOf("->");
				oldchildindex=childindex;
				childindex=str.indexOf("->");
			}



			if(stindex==childindex)
				id = CreateNode(plan,str, id, parentid);
		}
		return id;
	}

	void SwapSORTChilds(Plan plan) 
	{
		for(int i =0;i<plan.getSize();i++) {
			Node node = plan.getNode(i);
			if(node.getName().equals("Sort")) {
				int k=0;
				Node[] chnodes = new Node[2];
				for(int j=0;j<plan.getSize();j++) {
					if(plan.getNode(j).getParentId() == node.getId()) {
						if(k==0)chnodes[0]=plan.getNode(j);
						else chnodes[1]=plan.getNode(j);
						k++;
					}
				}
				if(k>=2) {
					k=chnodes[0].getId();
					chnodes[0].setId(chnodes[1].getId());
					chnodes[1].setId(k);

					for(int j=0;j<plan.getSize();j++) {
						if(plan.getNode(j).getParentId() == chnodes[0].getId())
							plan.getNode(j).setParentId(chnodes[1].getId());
						else if(plan.getNode(j).getParentId() == chnodes[1].getId())
							plan.getNode(j).setParentId(chnodes[0].getId());
					}
				}
			}
		}
	}
}
