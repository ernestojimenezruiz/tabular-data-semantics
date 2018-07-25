/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ernesto
 * Created on 25 Jul 2018
 *
 */
public class Column {

	String column_name="";
	List<String> column_data;
	
	
	public Column(){
		setEmptyColumn();
	}
	
	
	/**
	 * 
	 */
	private void setEmptyColumn() {
		column_data = new ArrayList<String>();
		
	}
	
	
	public void setColumnName(String name){
		column_name = name;
	}
	
	public String getColumnName(){
		return column_name;
	}


	public String getElement(int rid){
		return column_data.get(rid);
	}
	

	public void addColumnnValue(String value){
		column_data.add(value);
	}
	
	
	
}
