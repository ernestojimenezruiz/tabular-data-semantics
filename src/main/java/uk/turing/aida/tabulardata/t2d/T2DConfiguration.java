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

	
	
	
	
	public T2DConfiguration(){
		super("t2d");
	}
	
	
	public void readProperties(Properties properties){
		
		//read parameters
		path = properties.getProperty("path");
		//type_gs_file = properties.getProperty("gs_types_file");
		type_gs_file = properties.getProperty("gs_types_extended_file");
		type_sc_gs_file = properties.getProperty("gs_types_extended_file_sc");
		partial_reference_file = properties.getProperty("partial_reference_file");
		columns_annotations_folder = properties.getProperty("columns_ann_folder");			
		instance_annotations_folder = properties.getProperty("instance_ann_folder");				
		tables_folder = properties.getProperty("tables_folder");

	}
	
	
	
}
