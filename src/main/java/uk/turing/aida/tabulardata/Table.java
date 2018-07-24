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
 * Created on 24 Jul 2018
 *
 */
public class Table {
	
	List<String[]> table_data;
	
	public Table(){
		setEmptyList();
	}
	
	
	public void setEmptyList(){
		table_data = new ArrayList<String[]>();
	}
	
	
	
	public void addRow(String[] row){
		table_data.add(row);
	}
	
	
	public void addRow(String row_str, String split_char){
		addRow(row_str.split(split_char));
	}
	

}
