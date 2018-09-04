/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata.limaye;

import java.util.Properties;
import uk.turing.aida.Configuration;

/**
 *
 * @author ernesto
 * Created on 30 Jul 2018
 *
 */
public class LimayeConfiguration extends Configuration{

	//Path to T2D dataset
	public String limaye_path;
		
	//Types
	public String type_gs_file;
	
	//with superclasses
	public String type_sc_gs_file;
	
	
	//partial reference for testing
	public String partial_reference_file;
	
	
	//Folder containing links to dbpedia 
	public String instance_annotations_folder;
	
	//Dataset of tables
	public String tables_folder;
	
	
	
	public LimayeConfiguration(){
		super("limaye");
	}
	
	
	public void readProperties(Properties properties){
		
		//read parameters
		limaye_path = properties.getProperty("path");
		type_gs_file = properties.getProperty("gs_types_file");
		type_sc_gs_file = properties.getProperty("gs_types_file_sc");
		partial_reference_file = properties.getProperty("partial_reference_file");			
		instance_annotations_folder = properties.getProperty("instance_ann_folder");				
		tables_folder = properties.getProperty("tables_folder");

	}
	
	
	
}
