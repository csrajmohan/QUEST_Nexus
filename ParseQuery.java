package quest;
/* 
 * This file contains function to parse query and find predicates involved in query.
 * It also creates JList for those predicates.
 */

import java.util.StringTokenizer;
import java.util.Vector;
import java.sql.*;

import javax.swing.*;
public class ParseQuery 
{
	JScrollPane listScrollPanel;
	
	/*
	 * This function takes query string as input.
	 * It parses query and finds base and join predicates.
	 * 
	 * Call By: MainFrame.java
	 */
	boolean parseQuery(String queryInput, AllObjects allObjects)
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
		BouquetData bouquetDataObj = allObjects.getBouquetDataObj();
		bouquetDataObj.setBaseConditions(baseConditions);
		bouquetDataObj.setBaseRelationNames(relations);
	
		listScrollPanel = getList(relations, baseConditions, joinConditions, totalRelations, predicates.size());
		return(true);
	}
	
	/* 
	 * This function creates JList of all predicates
	 */
	JScrollPane getList(String relations[], String baseConditions[], String joinConditions[][], int totalRelations, int totalPreidicates)
	{
		JScrollPane listScrollPane;
		JList<String> errorPronePredicateList;
		String str[] = new String[totalPreidicates];
		int count = 0;
		for(int i=0;i<totalRelations;i++)
		{
			if(baseConditions[i]!="")
			{
				str[count] = relations[i] +" (base conditions): "+baseConditions[i];
				count++;
			}
		}
		for(int i=0;i<totalRelations;i++)
		{
			for(int j=0;j<totalRelations;j++)
			{
				if(joinConditions[i][j]!="")
				{
					str[count] = relations[i] +", "+relations[j]+" (join condition): "+joinConditions[i][j];
					count++;
				}
			}
		}
		String listValues[] = new String[count];
		for(int i=0;i<count;i++)
		{
			listValues[i] = str[i];
		}
		errorPronePredicateList = new JList<String>(listValues);
		listScrollPane = new JScrollPane(errorPronePredicateList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		listScrollPane.setPreferredSize(new Dimension(400,400));
		return(listScrollPane);
	}
}
