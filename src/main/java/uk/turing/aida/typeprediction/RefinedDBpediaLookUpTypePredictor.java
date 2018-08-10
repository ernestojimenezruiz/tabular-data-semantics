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
 * Predictor based on a 2-round DBPedia look-up queries.
 * First round we get an approximation of types
 * Second round we perform the look-up by filter by class
 * Keeps max voted types(s).  
 * @author ernesto
 * Created on 6 Aug 2018
 *
 */
public class RefinedDBpediaLookUpTypePredictor extends DBpediaBasedTypePredictor{
	
	
	
	/**
	 * 
	 * @param max_hits Number of entities we keep in each call
	 * @param max_types Number of types we return (Top-k) 
	 */
	public RefinedDBpediaLookUpTypePredictor(int max_hits, int max_types){
		MAX_NUM_HITS=max_hits;
	}
	
	

	

	@Override
	public TreeMap<String,Double> getClassTypesForColumn(Column col) throws Exception {
		
		
		MAX_NUM_CALLS = col.getSize();
		
		Set<String> types = new HashSet<String>();
		
		//First round: regular look-up with 5 hits and top-3 types
		DBpediaLookUpTypePredictor firstRoundPredictor = new DBpediaLookUpTypePredictor(4, 4, "");
		
		//Top-k classes should be filter here
		Set<String> typesFirstRound = firstRoundPredictor.getClassTypesForColumn(col);
		
		System.out.println(typesFirstRound);
		
		//Hits for type
		TreeMap<String, Integer> hitsfortypes = new TreeMap<String, Integer>();
		
		
		for (int cell_id=0; cell_id<MAX_NUM_CALLS; cell_id++){		
		
			//Calls using a filter. It should retrieve a more accurate set of entities and types.
			for (String type_filter : typesFirstRound){
			
				//Entity to set of types
				Map<String, Set<String>> lookup_hits = 
						dblup.getDBpediaEntitiesAndClasses(
								col.getElement(cell_id), 
								type_filter,
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
					
				}//for entities retrieves  by look-up
				
			}//for types filter	
		}//for cells
		
		
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
	
	
	
	

	
	
	
	
	
	

}
