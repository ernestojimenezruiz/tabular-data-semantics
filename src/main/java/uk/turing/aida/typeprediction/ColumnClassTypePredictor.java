/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.typeprediction;

/**
 * Given a csv file with tabular data, the aim is to find the most suitable 
 * set of class types for the "entity" columns. Entity columns contain string 
 * labels referring to potential resources in a knowledge base.
 * 
 * @author ernesto
 * Created on 24 Jul 2018
 *
 */
public class ColumnClassTypePredictor {
	
	
	
	
	public ColumnClassTypePredictor(){
		
	}
	
	

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
