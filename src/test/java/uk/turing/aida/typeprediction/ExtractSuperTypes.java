/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.typeprediction;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLClass;

import uk.turing.aida.kb.dbpedia.DBpediaEndpoint;
import uk.turing.aida.kb.dbpedia.DBpediaOntology;
import uk.turing.aida.tabulardata.reader.CVSReader;
import uk.turing.aida.tabulardata.t2d.T2DConfiguration;
import uk.turing.aida.tabulardata.utils.WriteFile;

/**
 * This class aims at extracting the super types using the dbpedia ontology and the sparql endpoint 
 * and comparing results
 * @author ernesto
 * Created on 15 Aug 2018
 *
 */
public class ExtractSuperTypes {
	
	//Extensions with types using dbpedia endpoint
	WriteFile writer_sparql;
	//Extensions with types using dbpedia ontology (classification)
	WriteFile writer_classification;
	
	WriteFile writer_log;
	
	//reader to read the prediction in csv
	CVSReader prediction_reader;
	
	DBpediaOntology dbpo;
	DBpediaEndpoint dbe = new DBpediaEndpoint();

	
	Map<String, Set<String>> type2supertypes_sparql = new HashMap<String, Set<String>>();
	Map<String, Set<String>> type2supertypes_classification = new HashMap<String, Set<String>>();
	
	
	public ExtractSuperTypes(String predicted_types_file) throws Exception{
		
		writer_sparql = new WriteFile(predicted_types_file.replaceAll(".csv", "")+"_sparql_supertypes.csv");
		
		writer_classification = new WriteFile(predicted_types_file.replaceAll(".csv", "")+"_classification_supertypes.csv");
		
		writer_log = new WriteFile(predicted_types_file.replaceAll(".csv", "")+"_supertypes.log");
		
		prediction_reader = new CVSReader(predicted_types_file);
		
		
		setUpDBPedia();		
		
		readPredictionFile();
		
		
		writer_classification.closeBuffer();
		
		writer_sparql.closeBuffer();
		
		
		compareResults();
		
		writer_log.closeBuffer();
		
		
	}
	
	/**
	 * 
	 */
	private void compareResults() {
	//compare type2supertypes_sparql to type2supertypes_class
		
		for (String key: type2supertypes_classification.keySet()){
			if (!type2supertypes_sparql.containsKey(key))
				writer_log.writeLine("Missing key: " + key);
				
			
			if (!type2supertypes_classification.get(key).equals(type2supertypes_sparql.get(key))){
				writer_log.writeLine("Different superclasses for: " + key + 
						"\n\tCls: " + type2supertypes_classification.get(key) +
						"\n\tSparql: " + type2supertypes_sparql.get(key));
			}
			
		}
		
		
	}

	//"86747932_0_7532457067740920052","1","Agent","0.899"
	private void readPredictionFile() throws Exception{
	
		for (int rid=0; rid<prediction_reader.getTable().getSize(); rid++){
			String[] row = prediction_reader.getTable().getRow(rid);
			
			printSuperTypesSparql(row);
			
			printSuperTypesClassification(row);
			
		}
	}
	
	
	private void setUpDBPedia() throws Exception{
		//1. Extend types with superclasses using DBpedia ontology: filter by URI
		dbpo = new DBpediaOntology(false);
		dbpo.classifyOntology();
		
	}
	
	
	
	private void printSuperTypesSparql(String[] row) throws Exception{
		
		
		String cls_uri = dbpo.getDbpediaURINamespace()+row[2];
		
		
		//If we already have the types
		if (!type2supertypes_sparql.containsKey(cls_uri)){
			
			
			type2supertypes_sparql.put(cls_uri, new HashSet<String>());
			
			
			for (String cls : dbe.getAllSuperClassesForSubject(cls_uri)){
				if (!filterType(cls, cls_uri))
					type2supertypes_sparql.get(cls_uri).add(cls);
			}
			

		}
		//else do nothing as we already know the types
		
		//Print results
		String line = "\""+ row[0] + "\",\"" + row[1]  + "\",\"" + row[2]  + "\",\"" + row[3] + "\"";
		
		//Super types
		for (String type: type2supertypes_sparql.get(cls_uri)){
			line+= ",\"" + type.replaceAll(dbpo.getDbpediaURINamespace(), "") + "\""; 
		}
		writer_sparql.writeLine(line);
		
	}
	
	
	private void printSuperTypesClassification(String[] row) throws Exception{
	
		String cls_uri = dbpo.getDbpediaURINamespace()+row[2];
		
		
		//We already have the types
		if (!type2supertypes_classification.containsKey(cls_uri)){
			
			
			type2supertypes_classification.put(cls_uri, new HashSet<String>());
			
			
			for (OWLClass cls : dbpo.getSuperClasses(cls_uri, false)){
				//Ignore Top and external dbpedia types (e.g. yago)
				if (!filterType(cls, cls_uri))
					type2supertypes_classification.get(cls_uri).add(cls.getIRI().toString());
			}
			
			//Equivalent types
			for (OWLClass cls : dbpo.getEquivalentClasses(cls_uri)){
				//Ignore Top and external dbpedia types (e.g. yago)
				if (!filterType(cls, cls_uri))
					type2supertypes_classification.get(cls_uri).add(cls.getIRI().toString());
			}
		}
		//else do nothing as we already know the types
		
		//Print results
		String line = "\""+ row[0] + "\",\"" + row[1]  + "\",\"" + row[2]  + "\",\"" + row[3] + "\"";
		
		//Super types
		for (String type: type2supertypes_classification.get(cls_uri)){
			line+= ",\"" + type.replaceAll(dbpo.getDbpediaURINamespace(), "") + "\""; 
		}
		writer_classification.writeLine(line);
		
		
		
		

		
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
			
			//File file =  new File(path);
			String file_name_pattern = "lookup_col_classes_hits_1.csv";
			
			
			File file =  new File(path);
			
			
			TreeSet<String> ordered_files = new TreeSet<String>();
			for (String file_name : file.list()){
				ordered_files.add(file_name);
			}
			
			for (String file_name : ordered_files){
				//if (file_name.contains(file_name_pattern)){
				if (file_name.contains("_col_classes_hits") && file_name.endsWith(".csv") && !file_name.contains("_entailed")  && !file_name.contains("_supertypes")){
					System.out.println(file_name);
					new ExtractSuperTypes(path + file_name);
				}
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	}
	
	
	

}
