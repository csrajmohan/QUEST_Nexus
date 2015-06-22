package quest;
import iisc.dsl.picasso.common.ds.TreeNode;


public class NodeState 
{
	private String name;
	private int id, parentId;
	private boolean isNotExecuted;
	
	public NodeState()
	{
		
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		if(name != null)
			this.name = name.trim();
		else
			this.name = "";
	}
	public int getId()
	{
		return(id);
	}
	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getParentId()
	{
		return(parentId);
	}
	public void setParentId(int id)
	{
		this.parentId = id;
	}
	public boolean getNotExecFlag()
	{
		return(isNotExecuted);
	}
	public void setNotExecFlag(boolean flag)
	{
		isNotExecuted = flag;
	}
	public void populateTreeNode(TreeNodeState node)
	{
		node.setNodeValues(name, isNotExecuted);
	}
}
