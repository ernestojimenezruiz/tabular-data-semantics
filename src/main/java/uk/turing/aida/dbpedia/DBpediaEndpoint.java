/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.dbpedia;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;


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

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Model model;
		//String endpoint = "http://dbpedia.org/snorql/";
		String endpoint = "https://dbpedia.org/sparql";
		//http://wiki.dbpedia.org/public-sparql-endpoint
		
		
		String uri_subject = "http://dbpedia.org/resource/Berlin";
		
		String qStr = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "
				+ "SELECT ?p ?o \n"
				+ "WHERE { <" + uri_subject + "> ?p ?o . "
				+ "FILTER (?p != <http://dbpedia.org/ontology/wikiPageWikiLink> && ?p != <http://www.w3.org/2000/01/rdf-schema#comment> && ?p != <http://dbpedia.org/ontology/abstract>)"
						+ "}";
		Query q = QueryFactory.create(qStr);
		
		
		QueryExecution qe =
				QueryExecutionFactory.sparqlService(endpoint,q); 
				try {
				ResultSet res = qe.execSelect();
				while( res.hasNext()) {
					QuerySolution soln = res.next();
					RDFNode p = soln.get("?p");
					RDFNode o = soln.get("?o");
					//if (!p.toString().equals("http://dbpedia.org/ontology/wikiPageWikiLink"))
					System.out.println(""+p + " " + o);
				}
			    
				} finally {
				qe.close();
				}
		
		
		
	}

	
	
}
