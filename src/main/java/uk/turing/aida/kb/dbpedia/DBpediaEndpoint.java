/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.kb.dbpedia;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
	
	
	
	
	public Set<String> getTypesOfObjectForPredicate(String uri_predicate) throws Exception{
		
		return getURIsForQuery(
				craeteSPARQLQuery_TypeObjectsForPredicate(uri_predicate));
		
	}
	
	
	
	public Set<String> getTypesForSubject(String uri_resource) throws Exception{
		
		return getURIsForQuery(
				createSPARQLQuery_TypesForSubject(uri_resource));
		
		
	}
	
	
	
	
	protected Set<String> getURIsForQuery(String query) throws Exception{
		
		
		Set<String> types = new HashSet<String>();
		
		
		//Query to retrieve predicates and objects for subject
		Query q = QueryFactory.create(query);

		//System.out.println(query);
		
		
		//In some cases it fails the connection. Try several times
		boolean success=false;
		int attempts=0;
		
		while(!success && attempts<3){	
			
			attempts++;
		
			QueryExecution qe = QueryExecutionFactory.sparqlService(ENDPOINT, q); 
			try {
				ResultSet res = qe.execSelect();
				while( res.hasNext()) {
					
					QuerySolution soln = res.next();				
					RDFNode object_type = soln.get("?t");
					//System.out.println(""+object_type);
					
					types.add(object_type.toString());
					
				}
				
				success=true;
			    
			} 
			catch (Exception e) {
				System.out.println("Error accessing " + ENDPOINT + " with  SPARQL:\n" + query + "  Attempt: " + attempts);
				TimeUnit.SECONDS.sleep(1+attempts); //wait a couple of seconds and try again
			}
			finally {
				qe.close();
			}
			
		}
		if (!success)
			throw new Exception(); 
		else if (attempts>1)
			System.out.println("SUCCESS accessing SPARQL\n: " + query + "  Attempt: " + attempts);
		
		return types;
		
	}
	
	
	
	
	//TimeUnit.SECONDS.sleep(1);
	
	
	
	
	
	
	
	
	public Set<Statement> getTriplesForSubject(String uri_subject) throws Exception{
		
		Set<Statement> triples = new HashSet<Statement>();
		
		Model model = ModelFactory.createDefaultModel();
		
		//subject
		Resource subject = model.createResource(uri_subject);
		
		
		//Query to retrieve predicates and objects for subject
		String query = createSPARQLQueryForSubject(uri_subject);
		Query q = QueryFactory.create(query);
		
		
		//In some cases it fails the connection. Try several times
		boolean success=false;
		int attempts=0;

		
		while(!success && attempts<3){	
			
			attempts++;
		
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
				
				success=true;
			    
			} 
			catch (Exception e){
				System.out.println("Error accessing " + ENDPOINT + " with  SPARQL:\n" + query + "  Attempt: " + attempts);
				TimeUnit.SECONDS.sleep(1+attempts); //wait a couple of seconds and try again			    
			} 
			finally {
				qe.close();
			}
		}
		
		if (!success)
			throw new Exception(); 
		else if (attempts>1)
			System.out.println("SUCCESS accessing SPARQL\n: " + query + "  Attempt: " + attempts);
		
		
		
		return triples;
		
	}
	
	
	public Set<Statement> getTriplesForObject(String uri_object) throws Exception{
		
		Set<Statement> triples = new HashSet<Statement>();
		
		Model model = ModelFactory.createDefaultModel();
		
		//subject
		Resource object = model.createResource(uri_object);
		
		
		//Query to retrieve predicates and objects for subject
		String query = createSPARQLQueryForObject(uri_object);
		Query q = QueryFactory.create(query);
		
		
		//In some cases it fails the connection. Try several times
		boolean success=false;
		int attempts=0;

		
		while(!success && attempts<3){	
			
			attempts++;
		
			QueryExecution qe = QueryExecutionFactory.sparqlService(ENDPOINT, q); 
			try {
				ResultSet res = qe.execSelect();
				while( res.hasNext()) {
					
					QuerySolution soln = res.next();
					RDFNode subject = soln.get("?s");
					RDFNode predicate = soln.get("?p");					
					//System.out.println(""+predicate + " " + object);
					
					triples.add(model.createStatement(subject.asResource(), model.createProperty(predicate.toString()), object));
				}
				
				success=true;
			    
			} 
			catch (Exception e){
				System.out.println("Error accessing " + ENDPOINT + " with  SPARQL:\n" + query + "  Attempt: " + attempts);
				TimeUnit.SECONDS.sleep(1+attempts); //wait a couple of seconds and try again			    
			} 
			finally {
				qe.close();
			}
		}
		
		if (!success)
			throw new Exception(); 
		else if (attempts>1)
			System.out.println("SUCCESS accessing SPARQL\n: " + query + "  Attempt: " + attempts);
		
		
		
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
	 * To extract a portion of dbpedia relevant to the object
	 * @param uri_subject
	 * @return
	 */	
	private String createSPARQLQueryForObject(String uri_object){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT ?s ?p \n"
				+ "WHERE { ?s ?p <" + uri_object + "> . "
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
		uri_subject = "http://dbpedia.org/resource/Tetris";
		uri_subject = "http://dbpedia.org/resource/Puzzle_video_game";
		uri_subject = "http://dbpedia.org/resource/Side-scrolling_video_game";
		//uri_subjecturi_subject ="http://dbpedia.org/ontology/genre";
		//uri_subject = "http://dbpedia.org/resource/Side_scroller";
		
		uri_subject = "http://dbpedia.org/resource/Yemen_Airways";
		uri_subject = "http://dbpedia.org/resource/Airway_(disambiguation)";
		uri_subject = "http://dbpedia.org/resource/Airways";
		//uri_subject = "http://en.wikipedia.org/wiki/Yemen_Airways";
		//uri_subject = "http://www.wikidata.org/entity/Q4699067";
		
		DBpediaEndpoint dbe = new DBpediaEndpoint();
		
		
	
		try {
			
			System.out.println(dbe.getTypesForSubject(uri_subject));
			
			for (Statement st : dbe.getTriplesForSubject(uri_subject)){
			//for (Statement st : dbe.getTriplesForObject(uri_subject)){
				System.out.println(st.toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		//dbe.getTypesOfObjectForPredicate("http://dbpedia.org/ontology/industry");
	}

	
	
}
