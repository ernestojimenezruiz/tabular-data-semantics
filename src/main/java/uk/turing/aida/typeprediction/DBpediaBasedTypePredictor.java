/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.typeprediction;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import uk.turing.aida.kb.dbpedia.DBpediaEndpoint;
import uk.turing.aida.kb.dbpedia.DBpediaLookup;
import uk.turing.aida.tabulardata.Table;

/**
 *
 * @author ernesto
 * Created on 10 Aug 2018
 *
 */
public abstract class DBpediaBasedTypePredictor extends ColumnClassTypePredictor{
	
	
	//Number of cells for which to call to DBpedia: ALL, or a percentage of cells 10%...90% see variability
	protected int MAX_NUM_CALLS=-1;
	//Number of hits per call
	protected int MAX_NUM_HITS=5;
		
	//Top-k types
	//protected int TOP_K_TYPES=5; //not used
		
		
	protected String class_filter_lookup="";
		
	protected DBpediaLookup dblup = new DBpediaLookup();
		
	protected DBpediaEndpoint dbend = new DBpediaEndpoint();

	//Entities and types
	protected Map<String, Set<String>> lookup_hits_refined = new HashMap<String, Set<String>>();
	
	protected String dbpedia_onto_ns_uri = "http://dbpedia.org/ontology/";
	
	
	
	
	@Override
	/**
	 * If the columns storing "entities" are known. Useful for tests
	 */
	public Map<Integer, TreeMap<String,Double>> getClassTypesForTable(Table tbl, List<Integer> entity_columns) throws Exception {
		
		
		//We check all rows. May be expensive for large tables
		//MAX_NUM_CALLS = tbl.getSize();
		
		
		Map<Integer, TreeMap<String,Double>> map_types = new HashMap<Integer, TreeMap<String,Double>>();
		
		//If empty all columns are analyzed
		if (entity_columns.isEmpty())
			entity_columns.addAll(tbl.getColumnIndexesAsList());
		
		//We keep entity hits for table
		lookup_hits_refined.clear();
		
		for (int c : entity_columns){
		
			//System.out.println("Column: "+ c);
			
			//System.out.println(c);
			
			map_types.put(c, getClassTypesForColumn(tbl.getColumn(c)));
			
		}
		
		return map_types;
	}
	
			
	
	/**
	 * Return candiate entities and their types
	 * @return
	 */
	public Map<String, Set<String>> getEntityHits() {
		return lookup_hits_refined;
	}
	
	
	
	protected boolean filter(String cls){
		//Keep only types from dbpedia
		if (cls.startsWith(dbpedia_onto_ns_uri))
			return false;
		
		return true;
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
