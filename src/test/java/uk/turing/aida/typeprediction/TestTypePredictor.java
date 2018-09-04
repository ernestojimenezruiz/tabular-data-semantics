/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.typeprediction;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.semanticweb.owlapi.model.OWLClass;

import uk.turing.aida.kb.dbpedia.DBpediaOntology;
import uk.turing.aida.tabulardata.Table;
import uk.turing.aida.tabulardata.reader.CVSReader;
import uk.turing.aida.tabulardata.t2d.T2DConfiguration;
import uk.turing.aida.tabulardata.utils.WriteFile;
import uk.turing.aida.typeprediction.ColumnClassTypePredictor;

/**
 *
 * Template to test the type predictors
 * 
 * @author ernesto
 * Created on 7 Aug 2018
 *
 */
public abstract class TestTypePredictor {

	T2DConfiguration config = new T2DConfiguration();
	
	ColumnClassTypePredictor type_predictor;
	
	DBpediaOntology dbpo;	
	
	String dbpedia_uri = "http://dbpedia.org/ontology/";
	
	
	private double precision = 0.0, recall = 0.0, fmeasure = 0.0;
	
	
	//String output_file_name;
	
	
	boolean only_primary_columns;
	
	Map<String, Map<Integer, Set<String>>> gold_standard_types = new HashMap<String, Map<Integer, Set<String>>>();
	
	
	WriteFile entities_writer;
	WriteFile prediction_writer;
		
	
	public TestTypePredictor(boolean only_primary_columns) throws Exception{
		
		this.only_primary_columns=only_primary_columns;
		
		config.loadConfiguration();
		
	}
	
	
	
	public void performTest(int starting_row) throws Exception{
		readGroundTruthaAndComputePrediction(starting_row);
	}
		
	
	
	
	
	
	protected void readGroundTruthaAndComputePrediction(int starting_row) throws Exception{
		
		//Read GS which will lead the evaluation
		CVSReader gs_reader = new CVSReader(config.t2d_path + config.extended_type_gs_file);
		//CVSReader gs_reader = new CVSReader(config.t2d_path + config.partial_reference_file);
		
		if (gs_reader.getTable().isEmpty()){
			System.err.println("File '" + config.t2d_path + config.extended_type_gs_file + "' is empty.");
			return;
		}		
		
		//We init with first table id
		String table_id=gs_reader.getTable().getRow(starting_row)[0];
		List<Integer> column_ids= new ArrayList<Integer>();
		Map<Integer, Set<String>> reference_map= new HashMap<Integer, Set<String>>();
		
		boolean first_table=(starting_row==0);
		
		int previous_rid=starting_row;
		
		for (int rid=starting_row; rid<gs_reader.getTable().getSize(); rid++){
			
			String[] row = gs_reader.getTable().getRow(rid);
			
			//System.out.println(row[0] + " " + row[1] + "  " +  row[2] + " " + row[3]);
			File file = new File(config.t2d_path + config.tables_folder + row[0] + ".csv");
			if (!file.exists()){
				System.err.println("The file '" + config.t2d_path + config.tables_folder + row[0] + ".csv' does not exixt.");
				continue;
			}
						
			
			//new table row
			if (!table_id.equals(row[0])){
				
				
				System.out.println("Computing predictions for table '" + table_id + "' row-id: " + previous_rid);
				
				//call predictor with previous values
				getPrediction(table_id, column_ids, first_table);
				//store reference values
				gold_standard_types.put(table_id, new HashMap<Integer, Set<String>>());
				gold_standard_types.get(table_id).putAll(reference_map);
				
				//Set new table and clear values
				table_id=row[0];
				previous_rid = rid; //Starting point of a table (useful to continue computing results)
				column_ids.clear();
				reference_map.clear();
				first_table=false;		
				
			}			
			
			//Populate elements for working table
			if (!only_primary_columns || Boolean.valueOf(row[2])){
				column_ids.add(Integer.valueOf(row[1]));
				reference_map.put(Integer.valueOf(row[1]), new HashSet<String>());
				
				//There may be more than one type
				for (int i=3; i<row.length; i++)
					reference_map.get(Integer.valueOf(row[1])).add(dbpedia_uri+row[i]);
			}			
			
		}//end-for gs file
		
		System.out.println("Computing predictions for table '" + table_id + "' row-id: " + previous_rid);
		//call predictor for last table values
		getPrediction(table_id, column_ids, first_table);
		//store reference types
		gold_standard_types.put(table_id, new HashMap<Integer, Set<String>>());
		gold_standard_types.get(table_id).putAll(reference_map);
		
		
		
		///
		
				
	}
	
	
	

	
	protected void printEntityHitsIntoFile(boolean resetFile){
				
		entities_writer = new WriteFile(config.t2d_path + "output_results/" + getOutputEntitiesFile(), !resetFile); //we append to previous table results
		
		String line="";
		
		for (String entity : type_predictor.getEntityHits().keySet()){
			
			line="\""+entity.replace("http://dbpedia.org/resource/", "") + "\"";
			
			for (String type : type_predictor.getEntityHits().get(entity)){
				line+=",\"" + type.replace("http://dbpedia.org/ontology/", "") + "\"";				
			}
			
			entities_writer.writeLine(line);
			
		}
		
		entities_writer.closeBuffer();
	}
	
	
	
	protected void printPredictionIntoFile(boolean resetFile, String table_name, Map<Integer, TreeMap<String, Double>> predicted_types_map){
		prediction_writer = new WriteFile(config.t2d_path + "output_results/" + getOutputTypesFile(), !resetFile);
		
		
		for (Integer col_id : predicted_types_map.keySet()){
				
			//String line = "\""+ table_name + "\",\"" + col_id + "\"";
								
			for (String type: predicted_types_map.get(col_id).descendingKeySet()){
				
				String line = "\""+ table_name + "\",\"" + col_id + "\",\"" + 
						type.replaceAll(dbpedia_uri, "") + "\",\"" + predicted_types_map.get(col_id).get(type)  +  "\"";
				
				prediction_writer.writeLine(line);
				
			}
				
		}
		
		prediction_writer.closeBuffer();
	}
	
	
	
	protected void getPrediction(String table_name, List<Integer> column_ids, boolean resetFile) throws Exception{
		
		CVSReader table_reader = new CVSReader(config.t2d_path + config.tables_folder + table_name + ".csv");
		
		Map<Integer, TreeMap<String, Double>> predicted_types_map = getPrediction(table_reader.getTable(), column_ids);
		
		
		
		//OUTPUT PREDICTIONS
		
		//Table by table
		printPredictionIntoFile(resetFile, table_name, predicted_types_map);
		
		//output hit entities + types after perfomed prediction over table
		printEntityHitsIntoFile(resetFile);
		
		
	}


	protected Map<Integer, TreeMap<String, Double>> getPrediction(Table table, List<Integer> column_ids) throws Exception{
		
		createPredictor();//initializa predictor	
		return type_predictor.getClassTypesForTable(table, column_ids);
		
	}
	
	
	
	
	

	protected abstract void createPredictor();

	
	protected abstract String getOutputTypesFile();
	
	protected abstract String getOutputEntailedTypesFile();
	
	protected abstract String getOutputEntitiesFile();
	
	
}
