/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.typeprediction;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;

import uk.turing.aida.tabulardata.Column;
import uk.turing.aida.tabulardata.Table;

/**
 * Given a csv file with tabular data, the aim is to find the most suitable 
 * set of class types for the "entity" columns. Entity columns contain string 
 * labels referring to potential resources in a knowledge base.
 * 
 * @author ernesto
 * Created on 24 Jul 2018
 *
 */
public abstract class ColumnClassTypePredictor {
	
	
	/**
	 *  
	 * @param tbl
	 * @param entity_columns list of columns (if known) for which type should be discovered
	 * @return Map between column index and a set of KB class type URL
	 */
	 public abstract Map<Integer, Set<String>> getClassTypesForTable(Table tbl, List<Integer> entity_columns) throws Exception;	
	
	
	/**
	 * 
	 * @param tbl
	 * @return Map between column index and a set of KB class type URL
	 * @throws Exception 
	 */
	public Map<Integer, Set<String>> getClassTypesForTable(Table tbl) throws Exception{
		return getClassTypesForTable(tbl, tbl.getColumnIndexesAsList());	
	}
	
	
	
	public abstract Set<String> getClassTypesForColumn(Column col) throws Exception;
	
	
	
	/**
	 * Return candiate entities and their types
	 * @return
	 */
	public abstract Map<String, Set<String>> getEntityHits();

	
	
	
	
		
	
	//It will get as input a Table or Column
	//To implement:
	//Look-up service: how many hits?
	//Flexible look-up?: using words
	//LogMap-based predictor
	//1. Convert Table or column to triples and a flat ontology (if no column names -> ? dummy names?)
	//2. Match flat onto to dbpedia onto
	//3. Discover links between table entities and dbpedia entities: assess compatibility, assume conservativity
	//4. Predict (compatible) list of types
	
}
