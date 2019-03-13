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

	
	
	public LimayeConfiguration(){
		super("limaye");
	}
	
	
	public void readProperties(Properties properties){
		
		//read parameters
		path = properties.getProperty("path");
		type_gs_file = properties.getProperty("gs_types_file");
		type_sc_gs_file = properties.getProperty("gs_types_file_sc");
		partial_reference_file = properties.getProperty("partial_reference_file");			
		instance_annotations_folder = properties.getProperty("instance_ann_folder");				
		tables_folder = properties.getProperty("tables_folder");

	}
	
	
	
}
