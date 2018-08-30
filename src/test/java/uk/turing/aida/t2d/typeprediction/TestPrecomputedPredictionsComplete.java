/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.t2d.typeprediction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLClass;

import uk.turing.aida.kb.dbpedia.DBpediaEndpoint;
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
public class TestPrecomputedPredictionsComplete {

	
	
	T2DConfiguration config = new T2DConfiguration();
	
	DBpediaOntology dbpo;	
	
	String dbpedia_uri = "http://dbpedia.org/ontology/";
	
	private double micro_precision = 0.0, micro_recall = 0.0, micro_fmeasure = 0.0;
	private double macro_precision = 0.0, macro_recall = 0.0, macro_fmeasure = 0.0;
	private double t2k_precision = 0.0, t2k_recall = 0.0, t2k_fmeasure = 0.0;
	private double tp = 0.0, fp = 0.0, fn = 0.0;
	private double tp_t2k = 0.0, fp_t2k = 0.0, fn_t2k = 0.0;
	
	DBpediaEndpoint dbe = new DBpediaEndpoint();
	
	
	
	//Links tables id to the id of the PK (according to GS)
	Map<String, String> table2PKid = new HashMap<String, String>();
	
	
	public enum EVAL_LEVEL  {PK, NONPK, ALL};
	EVAL_LEVEL eval;
	
	
	
	String best_hit="";
	Map<String, Set<String>> groundTruth_types = new HashMap<String, Set<String>>();
	Map<String, Set<String>> prediction_types = new HashMap<String, Set<String>>();
	
	
	
	String predicted_types_file;
	double MIN_VOTES=0.5;
	//int MIN_TYPES=3;
	boolean RESTRICTED_EVAL = true;
	
	
	static Map<String, Set<String>> type2supertypes_sparql = new HashMap<String, Set<String>>();
	
	
	
	public TestPrecomputedPredictionsComplete(EVAL_LEVEL eval_level, String predicted_types_file, double min_votes, boolean strict) throws Exception{
		
		this.eval=eval_level;
		
		this.predicted_types_file = predicted_types_file;
		
		MIN_VOTES = min_votes;
		
		config.loadConfiguration();	
		
		RESTRICTED_EVAL = strict;
		
	}
	
	
	public void performTest() throws Exception{
		readGroundTruth();
		readPrediction();
							
		computeStandardMeaures();		
		
	}
	
	
	
	protected void readGroundTruth() throws IOException{
		CVSReader gs_reader = new CVSReader(config.t2d_path + config.extended_type_gs_file);
		
		String[] row;
		String key_name;
		boolean isPK;
		boolean include;
		
		if (gs_reader.getTable().isEmpty()){
			System.err.println("File '" + config.t2d_path + config.extended_type_gs_file + "' is empty.");
			return;
		}	
		
		

		for (int rid=0; rid<gs_reader.getTable().getSize(); rid++){
			
			row = gs_reader.getTable().getRow(rid);
			isPK=Boolean.valueOf(row[2]);
			
			//ass to store the id of the primary keays
			if (isPK){
				table2PKid.put(row[0], row[1]);
			}
			
			
			key_name= row[0] + "-" + row[1];
			
			
			 switch (eval) {
	            case PK:
	            	include=isPK;
	            	break;
	            case NONPK:
	            	include=!isPK;
	            	break;
	            default://all
	            	include=true;
			 }
			
			if (include){
				if (!groundTruth_types.containsKey(key_name))
					groundTruth_types.put(key_name, new HashSet<String>());
		
				groundTruth_types.get(key_name).add(dbpedia_uri+row[3]);
				
			}
		}
	}
	
	protected void readPrediction() throws IOException{
		
		CVSReader prediction_reader = new CVSReader(predicted_types_file);
		
		String[] row;
		String key_name;
		boolean isPK;
		boolean include;
		
		if (prediction_reader.getTable().isEmpty()){
			System.err.println("File '" + predicted_types_file + "' is empty.");
			return;
		}		
		
		
		Map<String, Double> votesForType= new HashMap<String, Double>();
		String previous_key_name = prediction_reader.getTable().getRow(0)[0] + "-" + prediction_reader.getTable().getRow(0)[1];
		
		for (int rid=0; rid<prediction_reader.getTable().getSize(); rid++){
			row = prediction_reader.getTable().getRow(rid);
			
			key_name= row[0] + "-" + row[1];
			
			isPK = table2PKid.containsKey(row[0]) && table2PKid.get(row[0]).equals(row[1]); 
			
			
			switch (eval) {
	            case PK:
	            	include=isPK;
	            	break;
	            case NONPK:
	            	include=!isPK;
	            	break;
	            default://all
	            	include=true;
			}
			
			
			//change
			if (!previous_key_name.equals(key_name)){
				
				//System.out.println(previous_key_name +  "  " + votesForType.size());
				
			
				for (String type : votesForType.keySet()){ //if not fitting the PK or nonPK requirement it will be empty
					
					if (votesForType.get(type)>=MIN_VOTES){
						
						if (!prediction_types.containsKey(previous_key_name))
							prediction_types.put(previous_key_name, new HashSet<String>());
					
						prediction_types.get(previous_key_name).add(dbpedia_uri+type);
						
					}
				}
				
				
				previous_key_name = key_name;
				votesForType.clear();
				
			}
			
			
			if (include)
				votesForType.put(row[2], Double.valueOf(row[3]));
			//empty otherwise
			
		}//end for
		
		
		
		//Last group table-column set of types
		for (String type : votesForType.keySet()){ //if not fitting the PK or nonPK requirement it will be empty
			
			if (votesForType.get(type)>=MIN_VOTES){
				
				if (!prediction_types.containsKey(previous_key_name))
					prediction_types.put(previous_key_name, new HashSet<String>());
				
				prediction_types.get(previous_key_name).add(dbpedia_uri+type);
				
			}
		}
		
		
	}
		
	
	
	
	

	
	
	
	
	
	
	/**
	 * Computes standard precision and recall
	 * @throws Exception 
	 */
	protected void computeStandardMeaures() throws Exception{
		
		
		double local_precision, local_recall = 0.0;
		
		double aux_tp=0.0;
		
		int total_columns = 0;
		
		//1. Extend types with superclasses using DBpedia ontology: filter by URI
		dbpo = new DBpediaOntology(false);
		dbpo.classifyOntology();
		
		Set<String> gt_local_types = new HashSet<String>();
		Set<String> p_local_types = new HashSet<String>();
		Set<String> intersection = new HashSet<String>();
		
		
		//Entailed types file (replace entialed file if given as input)
		//predicted_types_file = predicted_types_file.replaceAll("_entailed", "");
		//predicted_types_file = predicted_types_file.replaceAll(".csv", "");
		//WriteFile writer = new WriteFile(predicted_types_file+"_entailed.csv");
		
		
		for (String key_id : groundTruth_types.keySet()){
							
			//Local ground truth types for a given colums
			for (String gt_local_type : groundTruth_types.get(key_id)){
					
				//For restricted mode
				best_hit = gt_local_type;
					
				gt_local_types.add(gt_local_type);
				
				//Super types
				/*for (OWLClass cls : dbpo.getSuperClasses(gt_local_type, false)){
					//Ifnore Top and external dbpedia types (e.g. yago)
					if (!filterType(cls))
						gt_local_types.add(cls.getIRI().toString());
				}
				
				//Equivalent types
				for (OWLClass cls : dbpo.getEquivalentClasses(gt_local_type)){
					//Ifnore Top and external dbpedia types (e.g. yago)
					if (!filterType(cls))
						gt_local_types.add(cls.getIRI().toString());
				}*/
				// If we do not have the types yet, query dbpedia endpoint
				if (!type2supertypes_sparql.containsKey(gt_local_type)){
					
					type2supertypes_sparql.put(gt_local_type, new HashSet<String>());
					
					
					for (String cls : dbe.getAllSuperClassesForSubject(gt_local_type)){
						if (!filterType(cls, gt_local_type))
							type2supertypes_sparql.get(gt_local_type).add(cls);
					}
				}
				
				//If not strict evaluation we  extend GT types with super types
				//if (!STRICT_EVAL)
				gt_local_types.addAll(type2supertypes_sparql.get(gt_local_type));
				
			}
				
				
			//Local predicated types for a given colums
			if (prediction_types.containsKey(key_id)){
				for (String p_local_type : prediction_types.get(key_id)){
					
					p_local_types.add(p_local_type);
					
					//Super types
					/*for (OWLClass cls : dbpo.getSuperClasses(p_local_type, false)){
						//Ifnore Top and external dbpedia types (e.g. yago)
						if (!filterType(cls))
							p_local_types.add(cls.getIRI().toString());
					}
					
					//Equivalent types
					for (OWLClass cls : dbpo.getEquivalentClasses(p_local_type)){
						//Ifnore Top and external dbpedia types (e.g. yago)
						if (!filterType(cls))
							p_local_types.add(cls.getIRI().toString());
					}*/
					
					if (!type2supertypes_sparql.containsKey(p_local_type)){
						
						type2supertypes_sparql.put(p_local_type, new HashSet<String>());
						
						
						for (String cls : dbe.getAllSuperClassesForSubject(p_local_type)){
							if (!filterType(cls, p_local_type))
								type2supertypes_sparql.get(p_local_type).add(cls);
						}
					}
					p_local_types.addAll(type2supertypes_sparql.get(p_local_type));
					
					
				}
			}
			//else p_local_types will be empty for that key
			//else{
			//	System.err.println(key_id);
				//continue;
			//}
			
			
			//TODO split key_id
			//String line = "\""+ table_id + "\",\"" + col_id + "\"";
			
			//for (String type: p_local_types){
			//	line+= ",\"" + type.replaceAll(dbpedia_uri, "") + "\""; 
			//}
			
			//writer.writeLine(line);
			
			
			total_columns++;
			
			//Get local precision and recall
			intersection.addAll(p_local_types);
			intersection.retainAll(gt_local_types);
			
			
			aux_tp = intersection.size();
			
			
			if (RESTRICTED_EVAL){
				if (p_local_types.contains(best_hit)){
					tp+=aux_tp;  //positive hits
					tp_t2k++;
				}
				//else do nothing (we add '0')
			}
			else{
				tp+=aux_tp;  //positive hits
				if (aux_tp>0)//positive hit
					tp_t2k++;  //positive hits
			}
			
			
			
			fp+=p_local_types.size()-aux_tp; //wrong types
			fn+=gt_local_types.size()-aux_tp; //missed types
			
			//if (intersection.size()>0)//positive hit
			//	tp_t2k++;  //positive hits
			//else {
			if (aux_tp==0){
				fn_t2k++; //missed types
				if (p_local_types.size()>0)
					fp_t2k++;  //wrong types
			}
			
			
				
			
			if (p_local_types.isEmpty()){
				local_precision=0.0;
				local_recall=0.0;
			}
			else{
				local_precision = (double)intersection.size()/(double)p_local_types.size();
				local_recall = (double)intersection.size()/(double)gt_local_types.size();
			}
			
			micro_precision+=local_precision;
			micro_recall+=local_recall;
			
			
			//System.out.println(local_precision + " " + local_recall);
			
			
			//reset local variables
			p_local_types.clear();
			gt_local_types.clear();
			intersection.clear();
				
			
		}//end table-column iterations
		
		//writer.closeBuffer();
		
		
		//Global precision and recall as avreage of local values
		micro_precision = micro_precision/(double)total_columns;		
		micro_recall = micro_recall/(double)total_columns;
		
		//harmonic average
		micro_fmeasure = (2*micro_precision*micro_recall) / (micro_precision + micro_recall);
		
		
		
		//Global precision and recall as avreage of local values
		macro_precision = (double) tp / (double) (tp+fp); 		
		macro_recall =  (double) tp / (double) (tp+fn);
				
		//harmonic average
		macro_fmeasure = (2*macro_precision*macro_recall) / (macro_precision + macro_recall);
		
		
		
		
		//Global precision and recall as avreage of local values
		t2k_precision = (double) tp_t2k / (double) (tp_t2k+fp_t2k); 		
		t2k_recall =  (double) tp_t2k / (double) (tp_t2k+fn_t2k);
						
		//harmonic average
		t2k_fmeasure = (2*t2k_precision*t2k_recall) / (t2k_precision + t2k_recall);
		

	}
	
	
	
	public double getMicroPrecision(){
		return micro_precision;
	}
	
	public double getMicroRecall(){
		return micro_recall;
	}
	public double getMicroFmeasure(){
		return micro_fmeasure;
	}
	
	
	
	public double getMacroPrecision(){
		return macro_precision;
	}
	
	public double getMacroRecall(){
		return macro_recall;
	}
	public double getMacroFmeasure(){
		return macro_fmeasure;
	}
	
	
	public double getT2KPrecision(){
		return t2k_precision;
	}
	
	public double getT2KRecall(){
		return t2k_recall;
	}
	public double getT2KFmeasure(){
		return t2k_fmeasure;
	}
	
	
	
	private boolean filterType(OWLClass cls, String cls_uri) {
		return cls.isOWLThing() || filterType(cls.getIRI().toString(), cls_uri);
		//!cls.getIRI().toString().contains(dbpo.getDbpediaURINamespace());
		//return true;
	}
	
	private boolean filterType(String cls, String cls_uri) {
		return !cls.contains(dbpo.getDbpediaURINamespace()) || cls.equals(cls_uri);
		//return true;
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
			
				
				double threshold = 0.0;
				TestPrecomputedPredictionsComplete  test_tolerant;
				TestPrecomputedPredictionsComplete  test_restricted;
				
				
				EVAL_LEVEL eval_level = EVAL_LEVEL.ALL;
				
				
				
				//if (file_name.contains("t2k_col_classes_all")){
				if (file_name.contains("_col_classes") && file_name.endsWith(".csv") && !file_name.contains("_entailed") && !file_name.contains("_supertypes") && !file_name.contains("_jiaoyan")){
					
					while (threshold<=1.0){
					
						//TestPrecomputedPredictions test = new TestPrecomputedPredictions(false, config.t2d_path + "output_results/lookup_col_classes_jiaoyan.csv");
						//TestPrecomputedPredictions test = new TestPrecomputedPredictions(false, config.t2d_path + "output_results/lookup_col_classes_hits_1_types_2_entailed.csv");
						test_tolerant = new TestPrecomputedPredictionsComplete(eval_level, path + file_name, threshold, false); //default: 0.5, 1
						
						test_tolerant.performTest();
						
						//System.out.println(file_name);
						//System.out.println("\tRestricted mode: false");
						//System.out.println("\tMicro measures: " + test.getMicroPrecision() + " " + test.getMicroRecall() + " " + test.getMicroFmeasure());
						//System.out.println("\tMacro measures: " + test_tolerant.getMacroPrecision() + " " + test_tolerant.getMacroRecall() + " " + test_tolerant.getMacroFmeasure());
						//System.out.println("\tT2K evaluation: " + test_tolerant.getT2KPrecision() + " " + test_tolerant.getT2KRecall() + " " + test_tolerant.getT2KFmeasure());
						
						
						test_restricted = new TestPrecomputedPredictionsComplete(eval_level, path + file_name, threshold, true); //default: 0.5, 1
						
						test_restricted.performTest();
						
						//System.out.println(file_name);
						//System.out.println("\tRestricted mode: true");
						//System.out.println("\tMicro measures: " + test.getMicroPrecision() + " " + test.getMicroRecall() + " " + test.getMicroFmeasure());
						//System.out.println("\tMacro measures: " + test_restricted.getMacroPrecision() + " " + test_restricted.getMacroRecall() + " " + test_restricted.getMacroFmeasure());
						//System.out.println("\tT2K evaluation: " + test_restricted.getT2KPrecision() + " " + test_restricted.getT2KRecall() + " " + test_restricted.getT2KFmeasure());
						
						
						
						System.out.println(file_name + "\t" + threshold + "\t" + 
									"false" + "\t" + 
									test_tolerant.getMacroPrecision() + "\t" + test_tolerant.getMacroRecall() + "\t" + test_tolerant.getMacroFmeasure() + "\t" +
									test_tolerant.getT2KPrecision() + "\t" + test_tolerant.getT2KRecall() + "\t" + test_tolerant.getT2KFmeasure() + "\t" +
									"true" + "\t" + 
									test_restricted.getMacroPrecision() + "\t" + test_restricted.getMacroRecall() + "\t" + test_restricted.getMacroFmeasure() + "\t" +
									test_restricted.getT2KPrecision() + "\t" + test_restricted.getT2KRecall() + "\t" + test_restricted.getT2KFmeasure()
						);
						
						threshold+=0.10;
						
						
					}
				}
				//}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected class ValueComparator implements Comparator<String> {

	    private Map<String, Double> map;

	    public ValueComparator(Map<String, Double> map) {
	        this.map = map;
	    }

	    public int compare(String a, String b) {
	        if (map.get(a).doubleValue()>map.get(b).doubleValue())
	        	return 1;
	        if (map.get(a).doubleValue()==map.get(b).doubleValue()) //Very important in case of same percentage 
	        	return b.compareTo(a);
	        
	        return -1;
	    }
	}
	

}
