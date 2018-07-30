/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata.t2d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.turing.aida.tabulardata.reader.CVSReader;
import uk.turing.aida.tabulardata.utils.Utils;

/**
 *
 * Extension of the T2D dataset http://webdatacommons.org/webtables/goldstandardV2.html
 * to provide the types of other columns apart from primary keys.
 * For example in the table "Country", we have the column "capital", "government", "languages", "date"
 * for which dbpedia can also give us the type
 * @author ernesto
 * Created on 30 Jul 2018
 *
 */
public class ExtendedColumnTypeDataset {
	
	T2DConfiguration config = new T2DConfiguration();
	
	List<String> files = new ArrayList<String>();
	List<String> types = new ArrayList<String>();
	
	public ExtendedColumnTypeDataset() throws IOException{
		
		config.loadConfiguration();
		
		readTableFilesWithType();
				
	}
	
	//1. Read GS_types file, for each file name in col1 then search file in "property folder"
	private void readTableFilesWithType() throws FileNotFoundException{
		
		
		//Format 
		//"68779923_2_1000046510804975562.tar.gz","Country","http://dbpedia.org/ontology/Country"

		CVSReader cvs_reader = new CVSReader(config.t2d_path + config.type_gs_file);
		
		for (int i=0; i<cvs_reader.getTable().getSize(); i++){
			
			files.add(Utils.removeQuotes(cvs_reader.getTable().getRow(i)[0]).replaceAll(".tar.gz", ""));
			types.add(Utils.removeQuotes(cvs_reader.getTable().getRow(i)[2]));
		}		
		
		//System.out.println(Utils.removeQuotes(cvs_reader.getTable().getRow(0)[0]).replaceAll(".tar.gz", ""));
		//System.out.println(Utils.removeQuotes(cvs_reader.getTable().getRow(0)[2]).replaceAll(".tar.gz", ""));
		
	}
	
	
	//2. Get column type from dbpedia ontology. Type of primary key given by gold standard (list with Types)
	private void extractColumnTypes(){
		
		String column_annotation_file;
		
		for (String file_name : files){
			
			column_annotation_file = config.t2d_path + config.columns_annotations_folder + file_name + ".csv";
			
			System.out.println(column_annotation_file);
			
		}
		
	}
	
	
	
	private void loadDBPediaOntology(){
		
	}
	
	
	public static void main(String[] args) {
		try {
			new ExtendedColumnTypeDataset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	

}
