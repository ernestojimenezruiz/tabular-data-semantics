/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.typeprediction;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;

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
	private int MAX_NUM_HITS=10;
	
	private DBpediaLookup dblup = new DBpediaLookup();
	
	
	public DBpediaLookUpTypePredictor(){
		
	}
	
	

	@Override
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
		
		for (int cell_id=0; cell_id<MAX_NUM_CALLS; cell_id++){		
			
			
			//Entity to set of types
			Map<String, Set<String>> lookup_hits = 
					dblup.getDBpediaEntitiesAndClasses(
							col.getElement(cell_id), 
							MAX_NUM_HITS);
			
			
			for (String entity : lookup_hits.keySet()){
				
				types.addAll(lookup_hits.get(entity));
				
				
			}
			
		}
		
		
		return types;
	}
	
	

}
