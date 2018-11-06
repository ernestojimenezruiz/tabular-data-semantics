/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.kb.dbpedia;


import uk.turing.aida.SPARQLEndpointService;


/**
 *
 * Class to connect to the public DBpedia SPARQL endpoint. See contained 
 * datasets and more information here: http://wiki.dbpedia.org/public-sparql-endpoint
 *
 * @author ernesto
 * Created on 23 Jul 2018
 *
 */
public class DBpediaEndpoint extends SPARQLEndpointService {

	
	@Override
	public String getENDPOINT() {
		return "https://dbpedia.org/sparql";
	}

	
	
	
	/**
	 * To extract a portion of dbpedia relevant to the subject
	 * @param uri_subject
	 * @return
	 */	
	protected String createSPARQLQueryForSubject(String uri_subject){
		
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
	protected String createSPARQLQueryForObject(String uri_object){
		
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
	protected String createSPARQLQuery_TypesForSubject(String uri_subject){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT DISTINCT ?t \n"
				+ "WHERE { <" + uri_subject + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?t . "
				+ "}";
		
	}
	
	
	protected String createSPARQLQuery_AllTypesForSubject(String uri_subject){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT DISTINCT ?t \n"
				+ "WHERE {\n"
				+ "{<" + uri_subject + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?dt . "
				+ "?dt <http://www.w3.org/2000/01/rdf-schema#subClassOf>* ?t "
				+ "}\n"
				+ "UNION \n{"
				+ "<" + uri_subject + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?t . " //direct types
				+ "}\n"
				+ "UNION \n{"
				+ "<" + uri_subject + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?dt . "
				+ "?dt <http://www.w3.org/2002/07/owl#equivalentClass> ?t "
				+ "}\n"
				+ "}";
		
	}
	
	
	protected String createSPARQLQuery_AllSuperClassesForSubject(String uri_subject){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT DISTINCT ?t \n"
				+ "WHERE {\n"
				+ "{<" + uri_subject + "> <http://www.w3.org/2000/01/rdf-schema#subClassOf>* ?t "
				+ "}\n"
				+ "UNION \n{"
				+ "<" + uri_subject + "> <http://www.w3.org/2002/07/owl#equivalentClass> ?t "
				+ "}\n"
				+ "}";
		
	}
	
	
	
	
	
	
	protected String craeteSPARQLQuery_TypeObjectsForPredicate(String uri_predicate){
		
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
		
		uri_subject ="http://dbpedia.org/resource/Puzzle_video_game";
		uri_subject = "http://dbpedia.org/ontology/MusicGenre";
		
		uri_subject ="http://dbpedia.org/resource/Capcom";
		
		DBpediaEndpoint dbe = new DBpediaEndpoint();
		
		
	
		try {
			
			System.out.println(dbe.getTypesForSubject(uri_subject).size() + " " + dbe.getTypesForSubject(uri_subject));
			//System.out.println(dbe.getAllTypesForSubject(uri_subject).size() + " " + dbe.getAllTypesForSubject(uri_subject));
			
			//System.out.println(dbe.getAllSuperClassesForSubject(uri_subject).size() + " " +  dbe.getAllSuperClassesForSubject(uri_subject));
			
			//System.out.println(createSPARQLQuery_AllSuperClassesForSubject(uri_subject));
			
			//for (Statement st : dbe.getTriplesForSubject(uri_subject)){
			//for (Statement st : dbe.getTriplesForObject(uri_subject)){
			//	System.out.println(st.toString());
		//	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		//dbe.getTypesOfObjectForPredicate("http://dbpedia.org/ontology/industry");
	}



	
	
	
}
