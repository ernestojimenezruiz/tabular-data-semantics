/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.kb.dbpedia;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;


/**
 *
 * Class to connect to the public DBpedia SPARQL endpoint. See contained 
 * datasets and more information here: http://wiki.dbpedia.org/public-sparql-endpoint
 *
 * @author ernesto
 * Created on 23 Jul 2018
 *
 */
public class DBpediaEndpoint {

	private final String ENDPOINT = "https://dbpedia.org/sparql";
	
	
	public Set<Statement> getTriplesForSubject(String uri_subject){
		
		
		Set<Statement> triples = new HashSet<Statement>();
		
		Model model = ModelFactory.createDefaultModel();
		
		//subject
		Resource subject = model.createResource(uri_subject);
		
		
		//Query to retrieve predicates and objects for subject
		Query q = QueryFactory.create(createSPARQLQueryForSubject(uri_subject));

		
		QueryExecution qe = QueryExecutionFactory.sparqlService(ENDPOINT, q); 
		try {
			ResultSet res = qe.execSelect();
			while( res.hasNext()) {
				
				QuerySolution soln = res.next();
				RDFNode predicate = soln.get("?p");
				RDFNode object = soln.get("?o");
				//System.out.println(""+predicate + " " + object);
				
				triples.add(model.createStatement(subject, model.createProperty(predicate.toString()), object));
			}
		    
		} finally {
			qe.close();
		}
		
		return triples;
		
	}
	
	
	
	private String createSPARQLQueryForSubject(String uri_subject){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT ?p ?o \n"
				+ "WHERE { <" + uri_subject + "> ?p ?o . "
				+ "FILTER (?p != <http://dbpedia.org/ontology/wikiPageWikiLink> "
				+ "&& ?p != <http://www.w3.org/2000/01/rdf-schema#comment> "
				+ "&& ?p != <http://dbpedia.org/ontology/abstract>)"
				+ "}";
		
		
		
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		
		String uri_subject = "http://dbpedia.org/resource/Berlin";
		
		DBpediaEndpoint dbe = new DBpediaEndpoint();
	
		for (Statement st : dbe.getTriplesForSubject(uri_subject)){
			System.out.println(st.toString());
		}
		
	}

	
	
}
