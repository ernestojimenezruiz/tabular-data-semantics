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
	public RefinedDBpediaLookUpTypePredictor(int max_hits){
		MAX_NUM_HITS=max_hits;
	}
	
	

	

	@Override
	public TreeMap<String,Double> getClassTypesForColumn(Column col) throws Exception {
		
		
		MAX_NUM_CALLS = col.getSize();
		
		
		///----------------------------------------
		//First round: regular look-up with 5 hits and top-5 types
		
		int HITS_FIRST_ROUND = 5;
		int MIN_TYPES_FIRST_ROUND = 3; 
		double MIN_VOTES_FIRST_ROUND = 0.5;
		
		DBpediaLookUpTypePredictor firstRoundPredictor = new DBpediaLookUpTypePredictor(HITS_FIRST_ROUND, "");
		
		//Top-k classes should be filter here
		TreeMap<String, Double> typesHitsFirstRound = firstRoundPredictor.getClassTypesForColumn(col);
		
		Set<String> topTypesFirstRound = new HashSet<String>();
		
		for (String key: typesHitsFirstRound.descendingKeySet()){			
			//System.out.println("\tT1" + key + " " + typesHitsFirstRound.get(key));
			if (typesHitsFirstRound.get(key)>MIN_VOTES_FIRST_ROUND)
				topTypesFirstRound.add(key.replaceAll(dbpedia_onto_ns_uri, ""));
			else //finish as they are ordered
				break;
		}
		
		//only if empty
		if (topTypesFirstRound.isEmpty()){ //return 3 types
			for (String key: typesHitsFirstRound.descendingKeySet()){			
				//System.out.println("\tT1" + key + " " + typesHitsFirstRound.get(key));
				topTypesFirstRound.add(key.replaceAll(dbpedia_onto_ns_uri, ""));
				
				if (topTypesFirstRound.size()>=MIN_TYPES_FIRST_ROUND)
					break;
			}
		}
		//end first round look-up
		
		//System.out.println(topTypesFirstRound);
		
		//Hits for type
		TreeMap<String, Double> hitsfortypes = new TreeMap<String, Double>();
		Set<String> tmp_types = new HashSet<String>();
		
		
		for (int cell_id=0; cell_id<MAX_NUM_CALLS; cell_id++){		
		
			//Calls using a filter. It should retrieve a more accurate set of entities and types.
			for (String type_filter : topTypesFirstRound){
			
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
					
				}//for entities retrieves  by look-up
				
			}//for types filter	
			
			
			//Voting per cell!
			//We add voting here to avoid duplication from different hits and look-up and dbpedia searches
			for (String cls: tmp_types){
				
				if (!hitsfortypes.containsKey(cls))
					hitsfortypes.put(cls, 0.0);
				
				hitsfortypes.put(cls, hitsfortypes.get(cls)+1.0);
				
			}
			
			tmp_types.clear();
			
			
		}//for cells
		
		
		///Get % of occurrerence of a type instead of number of votes
		double percentage_votes = 0.0;
		for (String cls: hitsfortypes.keySet()){
			percentage_votes = (hitsfortypes.get(cls)/(double)MAX_NUM_CALLS);
			percentage_votes = (double)Math.round(percentage_votes * 1000d) / 1000d;
			hitsfortypes.put(cls, percentage_votes);
		}
		
		
		//System.out.println("TMP types: "+ hitsfortypes);
		
		
		
		//Probably not the best solution but a clean one
		TreeMap<String, Double> sortedhitsfortypes = new TreeMap<String, Double>(new ValueComparator(hitsfortypes));
		sortedhitsfortypes.putAll(hitsfortypes);
		//for (String key: sortedhitsfortypes.navigableKeySet()){
		//	System.out.println(key + "  " + sortedhitsfortypes.get(key));
		//}
		
		
		//@deprecated Top types (not filtered here)
		//for (String key: sortedhitsfortypes.descendingKeySet()){			
			//System.out.println("\t" + key + " " + sortedhitsfortypes.get(key));
		//}
		
		
		
		return sortedhitsfortypes;
		
	}
	
	
	
	

	
	
	
	
	
	

}
