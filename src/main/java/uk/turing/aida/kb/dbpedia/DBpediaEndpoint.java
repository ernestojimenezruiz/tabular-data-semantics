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
import org.apache.jena.rdf.model.Property;
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
	
	
	
	
	public Set<String> getTypesOfObjectForPredicate(String uri_predicate){
		
		return getURIsForQuery(
				craeteSPARQLQuery_TypeObjectsForPredicate(uri_predicate));
		
	}
	
	
	
	public Set<String> getTypesForSubject(String uri_resource){
		
		return getURIsForQuery(
				createSPARQLQuery_TypesForSubject(uri_resource));
		
		
	}
	
	
	
	
	protected Set<String> getURIsForQuery(String query){
		
		
		Set<String> types = new HashSet<String>();
		
		
		//Query to retrieve predicates and objects for subject
		Query q = QueryFactory.create(query);

		
		QueryExecution qe = QueryExecutionFactory.sparqlService(ENDPOINT, q); 
		try {
			ResultSet res = qe.execSelect();
			while( res.hasNext()) {
				
				QuerySolution soln = res.next();				
				RDFNode object_type = soln.get("?t");
				System.out.println(""+object_type);
				
				types.add(object_type.toString());
				
			}
		    
		} finally {
			qe.close();
		}
		
		return types;
		
	}
	
	
	
	
	
	
	
	
	
	
	
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
	
	
	
	/**
	 * To extract a portion of dbpedia relevant to the subject
	 * @param uri_subject
	 * @return
	 */	
	private String createSPARQLQueryForSubject(String uri_subject){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT ?p ?o \n"
				+ "WHERE { <" + uri_subject + "> ?p ?o . "
				+ "FILTER (?p != <http://dbpedia.org/ontology/wikiPageWikiLink> "
				+ "&& ?p != <http://www.w3.org/2000/01/rdf-schema#comment> "
				+ "&& ?p != <http://dbpedia.org/ontology/abstract>)"
				+ "}";
		
	}
	
	
	
	/**
	 * To extract class types of the subject
	 * @param uri_subject
	 * @return
	 */	
	private String createSPARQLQuery_TypesForSubject(String uri_subject){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT DISTINCT ?t \n"
				+ "WHERE { <" + uri_subject + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?t . "
				+ "}";
		
	}
	
	
	
	
	private String craeteSPARQLQuery_TypeObjectsForPredicate(String uri_predicate){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT DISTINCT ?t \n"
				+ "WHERE { ?s <" + uri_predicate + "> ?o . "
				+ "?o <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?t ."
				+ "}";
		
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		
		String uri_subject;
		uri_subject = "http://dbpedia.org/resource/Berlin";
		
		uri_subject = "http://dbpedia.org/resource/Plusnet";
		uri_subject = "http://dbpedia.org/resource/Virgin";
		uri_subject = "http://dbpedia.org/resource/Source_(game_engine)";
		
		DBpediaEndpoint dbe = new DBpediaEndpoint();
		
		System.out.println(dbe.createSPARQLQuery_TypesForSubject(uri_subject));
	
		//for (Statement st : dbe.getTriplesForSubject(uri_subject)){
		//	System.out.println(st.toString());
		//}	
		
		
		//dbe.getTypesOfObjectForPredicate("http://dbpedia.org/ontology/industry");
	}

	
	
}
