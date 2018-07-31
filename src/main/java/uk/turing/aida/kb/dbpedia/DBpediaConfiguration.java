/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.kb.dbpedia;

import java.util.Properties;

import uk.turing.aida.Configuration;

/**
 *
 * @author ernesto
 * Created on 30 Jul 2018
 *
 */
public class DBpediaConfiguration extends Configuration{
	
	String uri_ontology;
	
	String sparql_endpoint;
	
	String sparql_lookup;
	

	public DBpediaConfiguration() {
		super("dbpedia");
		
	}

	@Override
	protected void readProperties(Properties properties) {
		
		uri_ontology = properties.getProperty("uri_ontology");
		sparql_endpoint = properties.getProperty("sparql_endpoint");
		sparql_lookup = properties.getProperty("sparql_lookup");
		
	}

}
