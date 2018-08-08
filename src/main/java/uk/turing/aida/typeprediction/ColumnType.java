/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.typeprediction;

import java.util.HashSet;
import java.util.Set;

/**
 * To store the type (predicted or ground truth) of a table column 
 * 
 * @author ernesto
 * Created on 8 Aug 2018
 *
 */
public class ColumnType {
	
	private String table_id;
	
	private int column_id;
	
	private boolean is_primary_column;
	
	private Set<String> types;
	
	
	public ColumnType(String table_id, int column_id){
		this.table_id=table_id;
		this.column_id=column_id;
		is_primary_column = false;
		types = new HashSet<String>();
	}


	/**
	 * @return the table_id
	 */
	public String getTableId() {
		return table_id;
	}


	/**
	 * @return the column_id
	 */
	public int getColumnId() {
		return column_id;
	}


	/**
	 * @return the is_primary_column
	 */
	public boolean isPrimaryColumn() {
		return is_primary_column;
	}


	/**
	 * @param is_primary_column the is_primary_column to set
	 */
	public void setPrimaryColumn(boolean is_primary_column) {
		this.is_primary_column = is_primary_column;
	}


	/**
	 * @return the types
	 */
	public Set<String> getTypes() {
		return types;
	}


	/**
	 * @param types the types to set
	 */
	public void setTypes(Set<String> types) {
		this.types = types;
	}
	
	
	/**
	 * @param types the types to add
	 */
	public void addTypes(Set<String> types) {
		this.types.addAll(types);
	}

	
	/**
	 * @param type the type to add
	 */
	public void addType(String type) {
		this.types.add(type);
	}
	
	
	
	
	public String toStringCSV(){
		
		String output = "\""+ table_id + "\",\"" + column_id + "\",\"";
		
		if (isPrimaryColumn())
			output+="True";
		else
			output+="False";
		
		output+="\"";
		
		for (String type: types){
			output+= ",\"" + type + "\""; 
		}
		
		
		return output;
		
		
	}


	
	
	
	
	

}
