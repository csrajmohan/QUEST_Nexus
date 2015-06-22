package quest;
import java.util.Vector;


public class TreeNodeState 
{
	boolean isNotExecuted;
	private Vector<TreeNodeState>	children;
	String name;
	
	public boolean getExecFlag()
	{
		return(isNotExecuted);
	}
	public void setExecFlag(boolean flag)
	{
		isNotExecuted = flag;
	}
	public Vector<TreeNodeState> getChildren() {
		return(children);
	}
	
	public void setChildren(Vector<TreeNodeState> c) {
		children = c;
	}
	
	public void setNodeValues(String name, boolean isNotExecuted) 
	{
		this.name = name;
		this.isNotExecuted = isNotExecuted;
	}
}
