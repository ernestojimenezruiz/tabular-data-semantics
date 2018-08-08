/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.t2d.typeprediction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.turing.aida.tabulardata.Table;
import uk.turing.aida.tabulardata.reader.CVSReader;
import uk.turing.aida.tabulardata.t2d.T2DConfiguration;
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
	
	
	boolean only_primary_columns;
	
	Map<String, Map<Integer, Set<String>>> gold_standard_types = new HashMap<String, Map<Integer, Set<String>>>();
	
	Map<String, Map<Integer, Set<String>>> predicted_types = new HashMap<String, Map<Integer, Set<String>>>();
	
	
	public TestTypePredictor(boolean only_primary_columns) throws Exception{
		
		this.only_primary_columns=only_primary_columns;
		
		config.loadConfiguration();
		
		
		//Read GS which will lead the evaluation
		CVSReader gs_reader = new CVSReader(config.t2d_path + config.extended_type_gs_file);
		
		if (gs_reader.getTable().isEmpty()){
			System.err.println("File '" + config.t2d_path + config.extended_type_gs_file + "' is empty.");
			return;
		}		
		
		//We init with first table id
		String table_id=gs_reader.getTable().getRow(0)[0];
		List<Integer> cololunn_ids= new ArrayList<Integer>();
		Map<Integer, Set<String>> reference_map= new HashMap<Integer, Set<String>>();
		
		for (int rid=0; rid<gs_reader.getTable().getSize(); rid++){
			String[] row = gs_reader.getTable().getRow(rid);
			
			//Equals than "working table"
			if (table_id.equals(row[0])){
				
				if (!only_primary_columns || Boolean.valueOf(row[2])){
					cololunn_ids.add(Integer.valueOf(row[1]));
					reference_map.put(Integer.valueOf(row[1]), Collections.<String>emptySet());
					reference_map.get(Integer.valueOf(row[1])).add(row[3]);
				}
				
			}
			else{ //new table row
				
				//call predictor with previous values
				getPrediction(table_id, cololunn_ids);
				//store reference values
				gold_standard_types.put(table_id, Collections.<Integer, Set<String>>emptyMap());
				gold_standard_types.get(table_id).putAll(reference_map);
				
				//Set new table anc clear values
				table_id=row[0];
				cololunn_ids.clear();
				reference_map.clear();
			}
		}
		//call predictor for last table values
		getPrediction(table_id, cololunn_ids);
		//store reference types
		gold_standard_types.put(table_id, Collections.<Integer, Set<String>>emptyMap());
		gold_standard_types.get(table_id).putAll(reference_map);
		
		
		
		
		//Get micro P and R or macro P & R?
		
		
		//Id column to 
		// Map<Integer, Set<String>>
		
		//The predicor is expecting table and column ids, or one column 
		
		
		
		
		//read gold standard
		
		
	}
	
	

	
	
	public void getPrediction(String table_name, List<Integer> column_ids) throws Exception{
		
		CVSReader table_reader = new CVSReader(config.t2d_path + config.tables_folder + table_name + ".csv");
		
		predicted_types.put(table_name, getPrediction(table_reader.getTable(), column_ids));
		
	}


	public Map<Integer, Set<String>> getPrediction(Table table, List<Integer> column_ids) throws Exception{
		
		return type_predictor.getClassTypesForTable(table, column_ids);
		
	}
	
	
	
	/**
	 * Computes micro/macro precision and recall
	 */
	public void getStandardMeaures(){
		
		//1. Extend types with superclasses using DBpedia ontology: filter by 
		
		
		//2. Micro measures
		
		
		//3. Macro measures
	}
	
	
	
	public abstract void createPredictor();

	
}
