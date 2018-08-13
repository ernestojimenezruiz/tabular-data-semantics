/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.typeprediction;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import uk.turing.aida.tabulardata.Column;

/**
 *
 * Simple predictor based on simple DBPedia look-up queries.
 * Keeps max voted types(s).
 * @author ernesto
 * Created on 6 Aug 2018
 *
 */
public class DBpediaLookUpTypePredictor extends DBpediaBasedTypePredictor{
	
	
	/**
	 * 
	 * @param max_hits Number of entities we keep in each call
	 * @param max_types Number of types we return (Top-k) 
	 * @param class_filter Class filter for look-up
	 */
	public DBpediaLookUpTypePredictor(int max_hits, String class_filter){
		MAX_NUM_HITS=max_hits;
		
		class_filter_lookup=class_filter;
		
	}
	
	

	
	

	@Override
	public TreeMap<String,Double> getClassTypesForColumn(Column col) throws Exception {
		
		MAX_NUM_CALLS = col.getSize();
		
		
		TreeMap<String, Double> hitsfortypes = new TreeMap<String, Double>();
		Set<String> tmp_types = new HashSet<String>();
		
		
		
		
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
						lookup_hits_refined.get(entity).add(cls);
						tmp_types.add(cls);
					}
				}
				
				//Endpoint types (some times the types provided by look-up are incomplete)
				for (String cls: dbend.getTypesForSubject(entity)){
					
					if (!filter(cls)){
						lookup_hits_refined.get(entity).add(cls);
						tmp_types.add(cls);
					}
				}
				
				
				
			}//for:entities retrieved by look up
			
			
			//Voting per cell!
			//We add voting here to avoid duplication from different hits and look-up and dbpedia searches
			for (String cls: tmp_types){
				
				if (!hitsfortypes.containsKey(cls))
					hitsfortypes.put(cls, 0.0);
				
				hitsfortypes.put(cls, hitsfortypes.get(cls)+1.0);
				
			}
			tmp_types.clear();
			
			
			
		}//for:cells
		
		
		
		//Get % of occurrerence of a type instead of number of votes
		double percentage_votes = 0.0;
		for (String cls: hitsfortypes.keySet()){
			percentage_votes = (hitsfortypes.get(cls)/(double)MAX_NUM_CALLS);
			percentage_votes = (double)Math.round(percentage_votes * 1000d) / 1000d;
			hitsfortypes.put(cls, percentage_votes);
		}
		
		
		
		
		//Probably not the best solution but a clean one
		TreeMap<String, Double> sortedhitsfortypes = new TreeMap<String, Double>(new ValueComparator(hitsfortypes));
		sortedhitsfortypes.putAll(hitsfortypes);
		//for (String key: sortedhitsfortypes.navigableKeySet()){
		//	System.out.println(key + "  " + sortedhitsfortypes.get(key));
		//}
		
		
		//@deprecated Top types (not filtered here)
		for (String key: sortedhitsfortypes.descendingKeySet()){
			//System.out.println("\tT1 " + key + " " + sortedhitsfortypes.get(key));
		//	types.add(key);
		//	if (types.size()>=TOP_K_TYPES)
		//		break;
		}
		
		
		
		return sortedhitsfortypes;
		
	}
		
	

}
