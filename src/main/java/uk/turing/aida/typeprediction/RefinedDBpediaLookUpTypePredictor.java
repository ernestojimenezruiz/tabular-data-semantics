/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.typeprediction;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonProcessingException;

import uk.turing.aida.kb.dbpedia.DBpediaEndpoint;
import uk.turing.aida.kb.dbpedia.DBpediaLookup;
import uk.turing.aida.tabulardata.Column;
import uk.turing.aida.tabulardata.Table;

/**
 * Predictor based on a 2-round DBPedia look-up queries.
 * First round we get an approximation of types
 * Second round we perform the look-up by filter by class
 * Keeps max voted types(s).  
 * @author ernesto
 * Created on 6 Aug 2018
 *
 */
public class RefinedDBpediaLookUpTypePredictor extends ColumnClassTypePredictor{
	
	//Number of cells for which to call to DBpedia: ALL, or a percentage of cells 10%...90% see variability
	private int MAX_NUM_CALLS=-1;
	//Number of hits per call
	private int MAX_NUM_HITS=5;
	
	//Top-k types
	private int TOP_K_TYPES=5;
	
	private DBpediaLookup dblup = new DBpediaLookup();
	
	private DBpediaEndpoint dbend = new DBpediaEndpoint();
	
	
	/**
	 * 
	 * @param max_hits Number of entities we keep in each call
	 * @param max_types Number of types we return (Top-k) 
	 */
	public RefinedDBpediaLookUpTypePredictor(int max_hits, int max_types){
		
	}
	
	
	/**
	 * If the columns storing "entities" are known. Useful for tests
	 */
	public Map<Integer, Set<String>> getClassTypesForTable(Table tbl, List<Integer> entity_columns) throws JsonProcessingException, IOException {
		
		
		//We check all rows. Expensive for large tables
		MAX_NUM_CALLS = tbl.getSize();
		
		
		Map<Integer, Set<String>> map_types = new HashMap<Integer, Set<String>>();
		
		//If empty all columns are analyzed
		if (entity_columns.isEmpty())
			entity_columns.addAll(tbl.getColumnIndexesAsList());
		
		for (int c : entity_columns){
			
			map_types.put(c, getClassTypesForColumn(tbl.getColumnValues(c)));
			
		}
		
		return map_types;
	}
	

	@Override
	public Set<String> getClassTypesForColumn(Column col) throws JsonProcessingException, IOException {
		
		Set<String> types = new HashSet<String>();
		
		//TBC
		
		
		
		return types;
	}
	
	
	
	
	private boolean filter(String cls){
		//Keep only types from dbpedia
		if (cls.startsWith("http://dbpedia.org/ontology/"))
			return false;
		
		return true;
	}
	
	
	
	
	class ValueComparator implements Comparator<String> {

	    private Map<String, Integer> map;

	    public ValueComparator(Map<String, Integer> map) {
	        this.map = map;
	    }

	    public int compare(String a, String b) {
	        return map.get(a).compareTo(map.get(b));
	    }
	}
	
	
	

}
