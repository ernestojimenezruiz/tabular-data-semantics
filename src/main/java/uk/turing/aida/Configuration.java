/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida;

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
public abstract class Configuration {
	
	protected String config_name;
	
	
	public Configuration(String c_name){
		config_name = c_name;
	}
	
	
	public String getConfigName(){
		return config_name;
	}
	
	
	protected abstract void readProperties(Properties properties);
	
	
	public void loadConfiguration() throws IOException {
		String config_path = getFullConfigPath();
		
		//System.out.println(config_path);
		
		
		String[] tool_files = getConfigurationFiles(config_path);
		

		//We expect only one properties file
		if (tool_files.length==1){
			
			readProperties(getPropertiesFile(config_path + tool_files[0]));
			
					
		}
		else if (tool_files.length>1){
			System.err.println("There are more than one configuration file.");
		}
		else{
			System.err.println("No configuration file available.");
		}
	}
	
	
	
	
	protected String getFullConfigPath(){
		return System.getProperty("user.dir") + "/configuration/" + getConfigName() + "/";

	}
	
	
	protected String[] getConfigurationFiles(String config_path){
		
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
		return files.list(filter);
	}
	
	
	protected Properties getPropertiesFile(String file) throws IOException{
		FileInputStream fileInput = new FileInputStream(new File(file));
		Properties properties = new Properties();
		properties.load(fileInput);
		fileInput.close();
		
		return properties;
	}
	
	
	
	
	

}
