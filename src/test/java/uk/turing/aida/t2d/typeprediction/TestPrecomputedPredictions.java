/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.t2d.typeprediction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLClass;
import uk.turing.aida.kb.dbpedia.DBpediaOntology;
import uk.turing.aida.tabulardata.reader.CVSReader;
import uk.turing.aida.tabulardata.t2d.T2DConfiguration;
import uk.turing.aida.tabulardata.utils.WriteFile;


/**
 * Computes Precision and Recall for already computed predictions
 * @author ernesto
 * Created on 8 Aug 2018
 *
 */
public class TestPrecomputedPredictions {

	
	T2DConfiguration config = new T2DConfiguration();
	
	DBpediaOntology dbpo;	
	
	String dbpedia_uri = "http://dbpedia.org/ontology/";
	
	
	private double precision = 0.0, recall = 0.0, fmeasure = 0.0;
	
	
	boolean only_primary_columns;
	
	//TODO Use ColumnType?
	Map<String, Map<Integer, Set<String>>> gold_standard_types = new HashMap<String, Map<Integer, Set<String>>>();
	
	Map<String, Map<Integer, Set<String>>> predicted_types = new HashMap<String, Map<Integer, Set<String>>>();
	
	String predicted_types_file; 
	
	
	public TestPrecomputedPredictions(boolean only_primary_columns, String predicted_types_file) throws Exception{
		
		this.only_primary_columns=only_primary_columns;
		
		this.predicted_types_file = predicted_types_file;
		
		config.loadConfiguration();	
		
	}
	
	
	public void performTest() throws Exception{
		readGroundTruthaAndPrediction();
							
		computeStandardMeaures();		
		
	}
	
	
	
	
	
	protected void readGroundTruthaAndPrediction() throws Exception{
		
		//Read GS which will lead the evaluation
		CVSReader gs_reader = new CVSReader(config.t2d_path + config.extended_type_gs_file);
		
		CVSReader prediction_reader = new CVSReader(predicted_types_file);
		
				
		if (gs_reader.getTable().isEmpty()){
			System.err.println("File '" + config.t2d_path + config.extended_type_gs_file + "' is empty.");
			return;
		}		
		
		
		
		//GROUND TRUTH
		//We init with first table id
		String table_id=gs_reader.getTable().getRow(0)[0];
		List<Integer> cololunn_ids= new ArrayList<Integer>();
		Map<Integer, Set<String>> reference_map= new HashMap<Integer, Set<String>>();
		
		for (int rid=0; rid<gs_reader.getTable().getSize(); rid++){
			String[] row = gs_reader.getTable().getRow(rid);
			
			//new table row
			if (!table_id.equals(row[0])){
								
				//store reference values
				gold_standard_types.put(table_id, new HashMap<Integer, Set<String>>());
				gold_standard_types.get(table_id).putAll(reference_map);
				
				//Set new table and clear values
				table_id=row[0];
				cololunn_ids.clear();
				reference_map.clear();				
			}
			
			
			//Populate elements for working table
			if (!only_primary_columns || Boolean.valueOf(row[2])){
				cololunn_ids.add(Integer.valueOf(row[1]));
				reference_map.put(Integer.valueOf(row[1]), new HashSet<String>());
				
				//There may be more than one type
				for (int i=3; i<row.length; i++)
					reference_map.get(Integer.valueOf(row[1])).add(dbpedia_uri+row[i]);
			}	
			//if (table_id.equals("29414811_12_251152470253168163"))
			//	System.out.println(reference_map);
			
		}		
		//store reference types
		gold_standard_types.put(table_id, new HashMap<Integer, Set<String>>());
		gold_standard_types.get(table_id).putAll(reference_map);
		
		
		
		
		
		//PREDICTION
		table_id=prediction_reader.getTable().getRow(0)[0];		
		Map<Integer, Set<String>> prediction_map= new HashMap<Integer, Set<String>>();
		
		for (int rid=0; rid<prediction_reader.getTable().getSize(); rid++){
			String[] row = prediction_reader.getTable().getRow(rid);
			
			//new table row
			if (!table_id.equals(row[0])){
								
				//store prediction values
				predicted_types.put(table_id, new HashMap<Integer, Set<String>>());
				predicted_types.get(table_id).putAll(prediction_map);
				
				//Set new table and clear values
				table_id=row[0];
				cololunn_ids.clear();
				prediction_map.clear();	
			}
			
			
			//System.out.println(predicted_types_file);
			//System.out.println(table_id);

				
			prediction_map.put(Integer.valueOf(row[1]), new HashSet<String>());
			
			//There may be more than one type
			for (int i=2; i<row.length; i++)
				prediction_map.get(Integer.valueOf(row[1])).add(dbpedia_uri+row[i]);

			
			//if (table_id.equals("29414811_12_251152470253168163"))
			//	System.out.println(prediction_map);
			
		}		
		//store prediction types
		predicted_types.put(table_id, new HashMap<Integer, Set<String>>());
		predicted_types.get(table_id).putAll(prediction_map);
		
		
				
	}
	
	
	
	
	

	
	
	
	
	
	
	/**
	 * Computes standard precision and recall
	 * @throws Exception 
	 */
	protected void computeStandardMeaures() throws Exception{
		
		
		double local_precision, local_recall = 0.0;
		int total_columns = 0;
		
		//1. Extend types with superclasses using DBpedia ontology: filter by URI
		dbpo = new DBpediaOntology(false);
		dbpo.classifyOntology();
		
		Set<String> gt_local_types = new HashSet<String>();
		Set<String> p_local_types = new HashSet<String>();
		Set<String> intersection = new HashSet<String>();
		
		
		//Entailed types file (replace entialed file if given as input)
		predicted_types_file = predicted_types_file.replaceAll("_entailed", "");
		predicted_types_file = predicted_types_file.replaceAll(".csv", "");
		WriteFile writer = new WriteFile(predicted_types_file+"_entailed.csv");
		
		
		for (String table_id : gold_standard_types.keySet()){
			
			if (!predicted_types.containsKey(table_id))  //in case there are missing tables in prediction
				continue;
			
			for (Integer col_id : gold_standard_types.get(table_id).keySet()){
				
				if (!predicted_types.get(table_id).containsKey(col_id))
					continue;		//in case we only consider primary columns
					
				
				//Local ground truth types for a given colums
				for (String gt_local_type : gold_standard_types.get(table_id).get(col_id)){
					
					gt_local_types.add(gt_local_type);
					
					//Super types
					for (OWLClass cls : dbpo.getSuperClasses(gt_local_type, false)){
						//Ifnore Top and external dbpedia types (e.g. yago)
						if (!filterType(cls))
							gt_local_types.add(cls.getIRI().toString());
					}
					
					//Equivalent types
					for (OWLClass cls : dbpo.getEquivalentClasses(gt_local_type)){
						//Ifnore Top and external dbpedia types (e.g. yago)
						if (!filterType(cls))
							gt_local_types.add(cls.getIRI().toString());
					}
					
				}
				
				
				//Local predicated types for a given colums
				for (String p_local_type : predicted_types.get(table_id).get(col_id)){
					
					p_local_types.add(p_local_type);
					
					//Super types
					for (OWLClass cls : dbpo.getSuperClasses(p_local_type, false)){
						//Ifnore Top and external dbpedia types (e.g. yago)
						if (!filterType(cls))
							p_local_types.add(cls.getIRI().toString());
					}
					
					//Equivalent types
					for (OWLClass cls : dbpo.getEquivalentClasses(p_local_type)){
						//Ifnore Top and external dbpedia types (e.g. yago)
						if (!filterType(cls))
							p_local_types.add(cls.getIRI().toString());
					}
				}
				
				
				
				String line = "\""+ table_id + "\",\"" + col_id + "\"";
				
				for (String type: p_local_types){
					line+= ",\"" + type.replaceAll(dbpedia_uri, "") + "\""; 
				}
				
				writer.writeLine(line);
				
				
				
				//Get local precision and recall
				total_columns++;
				intersection.addAll(p_local_types);
				intersection.retainAll(gt_local_types);
				
				if (p_local_types.isEmpty()){
					local_precision=0.0;
					local_recall=0.0;
				}
				else{
					local_precision = (double)intersection.size()/(double)p_local_types.size();
					local_recall = (double)intersection.size()/(double)gt_local_types.size();
				}
				
				precision+=local_precision;
				recall+=local_recall;
				
				
				//System.out.println(local_precision + " " + local_recall);
				
				
				//reset local variables
				p_local_types.clear();
				gt_local_types.clear();
				intersection.clear();
				
			}
			
		}//end table-column iterations
		
		writer.closeBuffer();
		
		
		//Global precision and recall as avreage of local values
		precision = precision/(double)total_columns;		
		recall = recall/(double)total_columns;
		
		//harmonic average
		fmeasure = (2*precision*recall) / (precision + recall);
		
				
		

	}
	
	
	
	public double getPrecision(){
		return precision;
	}
	
	public double getRecall(){
		return recall;
	}
	public double getFmeasure(){
		return fmeasure;
	}
	
	
	
	/**
	 * @param cls
	 * @return
	 */
	private boolean filterType(OWLClass cls) {
		return cls.isOWLThing() || !cls.getIRI().toString().contains(dbpedia_uri);
	}
	
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
		try {
			
			T2DConfiguration config = new T2DConfiguration();
			config.loadConfiguration();
			
			String path = config.t2d_path + "output_results/";
			
			File file =  new File(path);
			
			
			TreeSet<String> ordered_files = new TreeSet<String>();
			for (String file_name : file.list()){
				ordered_files.add(file_name);
			}
			
			
			
			for (String file_name : ordered_files){
				
				if (file_name.contains("_col_classes_") && file_name.endsWith(".csv")){
					//TestPrecomputedPredictions test = new TestPrecomputedPredictions(false, config.t2d_path + "output_results/lookup_col_classes_jiaoyan.csv");
					//TestPrecomputedPredictions test = new TestPrecomputedPredictions(false, config.t2d_path + "output_results/lookup_col_classes_hits_1_types_2_entailed.csv");
					TestPrecomputedPredictions test = new TestPrecomputedPredictions(false, path + file_name);
					
					test.performTest();
					
					System.out.println(file_name + " "+ test.getPrecision() + " " + test.getRecall() + " " + test.getFmeasure());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
