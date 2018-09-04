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
	public String t2d_path;
		
	//This file assigns one dbpedia class type to each table (i.e., a type to "primary" key column)
	public String type_gs_file;
	
	//Extended types for non "primary" key columns
	public String extended_type_gs_file;
	
	//with superclasses
	public String extended_type_sc_gs_file;
	
	
	//partial reference for testing
	public String partial_reference_file;
	
	
	
	//This folder includes the annotations for some of the columns of each table
	//Columns are annotated with dbpedia data and object properties
	public String columns_annotations_folder;
	
	//Folder containing links to dbpedia 
	public String instance_annotations_folder;
	
	
	
	//Dataset of tables
	public String tables_folder;
	
	
	
	public T2DConfiguration(){
		super("t2d");
	}
	
	
	public void readProperties(Properties properties){
		
		//read parameters
		t2d_path = properties.getProperty("path");
		type_gs_file = properties.getProperty("gs_types_file");
		extended_type_gs_file = properties.getProperty("gs_types_extended_file");
		extended_type_sc_gs_file = properties.getProperty("gs_types_extended_file_sc");
		partial_reference_file = properties.getProperty("partial_reference_file");
		columns_annotations_folder = properties.getProperty("columns_ann_folder");			
		instance_annotations_folder = properties.getProperty("instance_ann_folder");				
		tables_folder = properties.getProperty("tables_folder");

	}
	
	
	
}
