/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata.t2d;

import java.util.Properties;
import uk.turing.aida.Configuration;

/**
 *
 * @author ernesto
 * Created on 30 Jul 2018
 *
 */
public class T2DConfiguration extends Configuration{

	//Path to T2D dataset
	String t2d_path;
		
	//This file assigns one dbpedia class type to each table (i.e., a type to "primary" key column)
	String type_gs_file;
	
	//This folder includes the annotations for some of the columns of each table
	//Columns are annotated with dbpedia data and object properties
	String columns_annotations_folder;
	
	//Folder containing links to dbpedia 
	String instance_annotations_folder;
	
	
	//Dataset of tables
	String tables_folder;
	
	
	
	public T2DConfiguration(){
		super("t2d");
	}
	
	
	public void readProperties(Properties properties){
		
		//read parameters
		t2d_path = properties.getProperty("path");
		type_gs_file = properties.getProperty("gs_types_file");
		columns_annotations_folder = properties.getProperty("columns_ann_folder");			
		instance_annotations_folder = properties.getProperty("instance_ann_folder");				
		tables_folder = properties.getProperty("tables_folder");

	}
	
	
	
}
