package quest;
import java.sql.*;

import javax.swing.JOptionPane;

public class ConnectDB 
{
//	String databaseURL = "jdbc:postgresql://localhost:5431/tpch_allidx";
	String databaseURL = "jdbc:postgresql://localhost:5432/tpcds";
	String databaseUser = "dsladmin";
	String databasePassword = "";
	public Connection connection;
	public ConnectDB(AllObjects allObjects)
	{
		allObjects.setConnectDBObj(this);
	}
	public ConnectDB()
	{
		
	}
	void connectDB()
	{
		try
		{
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
		}
		catch(Exception e)
		{
			System.out.println("Execption in ConnectDB: "+e);
			JOptionPane.showMessageDialog(null,"Could not connect to PostgreSQL Server","Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	void disconnectDB()
	{
		try
		{
			connection.close();
		}
		catch(Exception e)
		{
			System.out.println("Execption in ConnectDB: "+e);
			e.printStackTrace();
		}
	}
}
