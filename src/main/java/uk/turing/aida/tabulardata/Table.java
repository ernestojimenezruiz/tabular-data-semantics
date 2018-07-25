/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This class will manage the tabular data structure as a list of String vectors.
 * Optionally the column names and table names may be known.
 * 
 * @author ernesto
 * Created on 24 Jul 2018
 *
 */
public class Table {
	
	//Optional
	String table_name="";
	//Optional
	String[] column_names;
	
	List<String[]> table_data;
	
	public Table(){
		setEmptyTable();
	}
	
	
	public void setEmptyTable(){
		table_data = new ArrayList<String[]>();
	}
	
	
	public void setTableName(String name){
		table_name = name;
	}
	
	public String getTableName(){
		return table_name ;
	}
	
	
	public void addColumnNames(String[] names){
		column_names=names.clone();
	}
	
	
	public String[] getColumnNames(){
		return column_names;
	}
	
	public String getColumnName(int cid){
		return column_names[cid];
	}
	
	public void addRow(String[] row){
		table_data.add(row);
	}
	
	
	public void addRow(String row_str, String split_char){
		addRow(row_str.split(split_char));
	}
	
	
	public List<String[]> getTableData(){
		return table_data;
	}
	
	
	public String[] getRow(int rid){
		return table_data.get(rid);
	}
	
	
	
	
	public String getCell(int rid, int cid){
		return getRow(rid)[cid];
	}
	
	
	public Column getColumnValues(int cid){
		
		Column c =  new Column();		
		
		for (String[] row : table_data){
			c.addColumnnValue(row[cid]);
		}
		
		return c;
	}
	
	
	
	

}
