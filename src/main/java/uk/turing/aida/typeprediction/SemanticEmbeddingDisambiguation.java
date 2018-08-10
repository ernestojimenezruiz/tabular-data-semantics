/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.typeprediction;

/**
 *
 * @author ernesto
 * Created on 9 Aug 2018
 *
 */
public class SemanticEmbeddingDisambiguation {

	
	//We need to extract postion of dbpedia for table or for column. Perhaps for table, if neighbours can play a role...
	
	//Create document to compute the word embeddings: reuser from doser
	//Key how to navigate graph and what to store...
	//Check vectors! and see if model makes sense...
	//How to capture unreateness and relateness?
	
	
	//Get hits from dbpedia and select best according to the random walk: reuse from doser or directly use page rank.
	//We may want to get refined hits to filter undesired things....
	//Then list/merge all hits for the different class types and select the most suitable one according to page rank (relying on the embddings).
	
	
	
}
