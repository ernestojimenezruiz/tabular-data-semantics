/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata.t2d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author ernesto
 * Created on 30 Jul 2018
 *
 */
public class T2DConfiguration {

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
	
		
	
	public void loadConfiguration() throws IOException{
		
		String config_path = System.getProperty("user.dir") + "/configuration/t2d/";
		
		//System.out.println(config_path);
		
		
		//Keep only files ending in .properties
		File files = new File(config_path);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".properties")) {
					return true;
				} else {
					return false;
				}
			}
		};		
		String[] tool_files = files.list(filter);
		
		
		if (tool_files.length==1){
			
			//We expect only one
			FileInputStream fileInput = new FileInputStream(new File(config_path + tool_files[0]));
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
			
			//read parameters
			t2d_path = properties.getProperty("path");
			type_gs_file = properties.getProperty("gs_types_file");
			columns_annotations_folder = properties.getProperty("columns_ann_folder");			
			instance_annotations_folder = properties.getProperty("instance_ann_folder");				
			tables_folder = properties.getProperty("tables_folder");
			
		}
		else if (tool_files.length>1){
			System.err.println("There are more than one configuration file.");
		}
		else{
			System.err.println("No configuration file available.");
		}
		
		
	}
	
	
	
}
