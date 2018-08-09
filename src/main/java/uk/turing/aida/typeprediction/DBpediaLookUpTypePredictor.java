/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.typeprediction;

import java.io.IOException;
import java.net.URISyntaxException;
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
 *
 * Simple predictor based on simple DBPedia look-up queries.
 * Keeps max voted types(s).
 * @author ernesto
 * Created on 6 Aug 2018
 *
 */
public class DBpediaLookUpTypePredictor extends ColumnClassTypePredictor{
	
	//Number of cells for which to call to DBpedia: ALL, or a percentage of cells 10%...90% see variability
	private int MAX_NUM_CALLS=-1;
	//Number of hits per call
	private int MAX_NUM_HITS=5;
	
	//Top-k types
	private int TOP_K_TYPES=5;
	
	
	private String class_filter_lookup="";
	
	private DBpediaLookup dblup = new DBpediaLookup();
	
	private DBpediaEndpoint dbend = new DBpediaEndpoint();
	
	
	private Map<String, Set<String>> lookup_hits_refined = new HashMap<String, Set<String>>();
	
	
	/**
	 * 
	 * @param max_hits Number of entities we keep in each call
	 * @param max_types Number of types we return (Top-k) 
	 * @param class_filter Class filter for look-up
	 */
	public DBpediaLookUpTypePredictor(int max_hits, int max_types, String class_filter){
		MAX_NUM_HITS=max_hits;
		TOP_K_TYPES=max_types;
		
		class_filter_lookup=class_filter;
		
	}
	
	

	@Override
	/**
	 * If the columns storing "entities" are known. Useful for tests
	 */
	public Map<Integer, Set<String>> getClassTypesForTable(Table tbl, List<Integer> entity_columns) throws JsonProcessingException, IOException, URISyntaxException {
		
		
		//We check all rows. May be expensive for large tables
		//MAX_NUM_CALLS = tbl.getSize();
		
		
		Map<Integer, Set<String>> map_types = new HashMap<Integer, Set<String>>();
		
		//If empty all columns are analyzed
		if (entity_columns.isEmpty())
			entity_columns.addAll(tbl.getColumnIndexesAsList());
		
		//We keep entity hits for table
		lookup_hits_refined.clear();
		
		for (int c : entity_columns){
			
			//System.out.println(c);
			
			map_types.put(c, getClassTypesForColumn(tbl.getColumnValues(c)));
			
		}
		
		return map_types;
	}
	

	@Override
	public Set<String> getClassTypesForColumn(Column col) throws JsonProcessingException, IOException, URISyntaxException {
		
		MAX_NUM_CALLS = col.getSize();
		
		
		Set<String> types = new HashSet<String>();
		
		TreeMap<String, Integer> hitsfortypes = new TreeMap<String, Integer>();
		
		
		for (int cell_id=0; cell_id<MAX_NUM_CALLS; cell_id++){		
			
			//Entity to set of types
			Map<String, Set<String>> lookup_hits = 
					dblup.getDBpediaEntitiesAndClasses(
							col.getElement(cell_id), 
							class_filter_lookup,
							MAX_NUM_HITS);
			
			
			
			for (String entity : lookup_hits.keySet()){
				
				lookup_hits_refined.put(entity, new HashSet<String>());

				//Lookup types
				for (String cls: lookup_hits.get(entity)){
					
					if (!filter(cls)){
						if (!hitsfortypes.containsKey(cls))
							hitsfortypes.put(cls, 0);
						
						hitsfortypes.put(cls, hitsfortypes.get(cls)+1);
						
						lookup_hits_refined.get(entity).add(cls);
					}
				}
				
				//Endpoint types (some times the types provided by look-up are incomplete)
				for (String cls: dbend.getTypesForSubject(entity)){
					
					if (!filter(cls)){
						if (!hitsfortypes.containsKey(cls))
							hitsfortypes.put(cls, 0);
						
						hitsfortypes.put(cls, hitsfortypes.get(cls)+1);
						
						lookup_hits_refined.get(entity).add(cls);
						
					}
				}
				
			}//for:entities retrieved by look up
			
		}//for:cells
		
		
		//Probably not the best solution but a clean one
		TreeMap<String, Integer> sortedhitsfortypes = new TreeMap<String, Integer>(new ValueComparator(hitsfortypes));
		sortedhitsfortypes.putAll(hitsfortypes);
		//for (String key: sortedhitsfortypes.navigableKeySet()){
		//	System.out.println(key + "  " + sortedhitsfortypes.get(key));
		//}
		
		
		//Top types
		for (String key: sortedhitsfortypes.descendingKeySet()){			
			//System.out.println(key);
			types.add(key);
			if (types.size()>=TOP_K_TYPES)
				break;
		}
		
		
		
		return types;
	}
	

	
	@Override
	public Map<String, Set<String>> getEntityHits() {
		return lookup_hits_refined;
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
