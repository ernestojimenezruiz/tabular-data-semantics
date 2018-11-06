/*******************************************************************************
 * 
 *  Copyright 2018 by The Alan Turing Institute
 * 
 *
 *******************************************************************************/
package uk.turing.aida.kb.dbpedia;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.turing.aida.LookupService;

/**
 *
 * Class to access the DBpedia look up REST API: https://github.com/dbpedia/lookup
 *
 * @author ernesto
 * Created on 23 Jul 2018
 *
 */
public class DBpediaLookup extends LookupService{
		
	//http://lookup.dbpedia.org/api/search/KeywordSearch?MaxHits=3&QueryString=berlin
	private final String REST_URL = "http://lookup.dbpedia.org/api/search/KeywordSearch?";
	
	private final String MaxHits = "MaxHits";
	private final String QueryString = "QueryString";
	private final String QueryClass = "QueryClass";
	//Not supported
	//private final String language = "language";	
	
	private int hits=5;
	//private String query;
	
	
	
	
	public Set<String> getDBpediaEntities(String query) throws JsonProcessingException, IOException, URISyntaxException{
		return getDBpediaEntities(query,"", hits);
	}
	
	public Set<String> getDBpediaEntities(String query, int max_hits) throws JsonProcessingException, IOException, URISyntaxException{
		return getDBpediaEntities(query,"", max_hits);
	}
	
	public Set<String> getDBpediaEntities(String query, String type) throws JsonProcessingException, IOException, URISyntaxException{
		return getDBpediaEntities(query,type, hits);
	}

	
	/**
	 * Return a Set of URIs containing the DBPedia entities related to the query string.
	 * @param query
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @throws URISyntaxException 
	 */
	public Set<String> getDBpediaEntities(String query, String cls_type, int max_hits) throws JsonProcessingException, IOException, URISyntaxException{
		
		
		Set<String> entities = new HashSet<String>();
		
		//String urlToGet = 
		//REST_URL + QueryClass + "=" + cls_type + "&"	+ 
		//MaxHits + "=" + max_hits + "&" + QueryString + "=" + query;			
		URL urlToGet = buildRequestURL(query, cls_type, max_hits);
		
		System.out.println(urlToGet);
		System.out.println(getRequest(urlToGet));
		
		
		for (JsonNode result : jsonToNode(getRequest(urlToGet)).get("results")){
			
			//System.out.println(result.toString());
			
			entities.add(result.get("uri").asText());
		}
		
		return entities;
		
	}
	
	public Set<String> getEntityURIs(String query, String cls_type, int max_hits, String language) throws JsonProcessingException, IOException, URISyntaxException{
		return getDBpediaEntities(query, cls_type, max_hits);
	}

	
		
	public Map<String, Set<String>> getDBpediaEntitiesAndClasses(String query) throws JsonProcessingException, IOException, URISyntaxException{
		return getDBpediaEntitiesAndClasses(query,"", hits);
	}
	
	public Map<String, Set<String>> getDBpediaEntitiesAndClasses(String query, int max_hits) throws JsonProcessingException, IOException, URISyntaxException{
		return getDBpediaEntitiesAndClasses(query,"", max_hits);
	}
	
	public Map<String, Set<String>> getDBpediaEntitiesAndClasses(String query, String type) throws JsonProcessingException, IOException, URISyntaxException{
		return getDBpediaEntitiesAndClasses(query,type, hits);
	}

	
	
	/**
	 * Return a Map with "key"=URIs containing the DBPedia entities related to the query string, and "values"=sets of (ontology) class uris
	 * @param query
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @throws URISyntaxException 
	 */
	public Map<String, Set<String>> getDBpediaEntitiesAndClasses(String query, String cls_type, int max_hits) throws JsonProcessingException, IOException, URISyntaxException{
		
		
		Map<String, Set<String>> entities2classes = new HashMap<String, Set<String>>();
		
		//String urlToGet = REST_URL + QueryClass + "=" + cls_type + "&" + MaxHits + "="+ max_hits + "&" + QueryString + "=" + query;
		URL urlToGet = buildRequestURL(query, cls_type, max_hits);
		
		//System.out.println(urlToGet);
		
		JsonNode results = jsonToNode(getRequest(urlToGet));
		
		String uri_entity;
		String cls_uri;
		
		for (JsonNode result : results.get("results")){
			
			uri_entity = result.get("uri").asText();
			
			entities2classes.put(uri_entity, new HashSet<String>());
			
			for (JsonNode cls : result.get("classes")){
			
				cls_uri=cls.get("uri").asText();
				
				if (!cls_uri.equals("http://www.w3.org/2002/07/owl#Thing"))				
					entities2classes.get(uri_entity).add(cls_uri);
			}
			
		}
		
		return entities2classes;
		
	}
	
	
	
	protected String getREST_URL() {
		return REST_URL;
	}
	
	
	protected URL buildRequestURL(String query, String cls_type, int max_hits) throws URISyntaxException, MalformedURLException{
		URIBuilder ub = new URIBuilder(getREST_URL());
		ub.addParameter(QueryClass, cls_type);
		ub.addParameter(MaxHits, String.valueOf(max_hits));
		ub.addParameter(QueryString, query);
		return ub.build().toURL();
	}
	
	
	
	
	
	
	
	
	public static void main(String[] args){
		DBpediaLookup lookup = new DBpediaLookup();
		
		String word;
		word="virgin";
		//word="berlin";
		word="west%20midlands";
		word="conglomerates";
		word="tetris";
		word="puzzle";
		//word="batman:%20arkham%20city";
		//word="batman: arkham city";
		//word="action-adventure";
		//word="Side-scroller";
		//word="{action-adventure|brawler|stealth}";
		//word="brooklyn%20museum%20of%20art";
		//word="brooklyn+museum+of+art";
		//word="jordan";
		
		String type="";
		//type= "AdministrativeRegion";
		//type = "http://dbpedia.org/ontology/AdministrativeRegion";
		//type = "http://dbpedia.org/ontology/PopulatedPlace";
		
		try {
			System.out.println(lookup.getDBpediaEntities(word, type).toString());
			//System.out.println(lookup.getDBpediaEntitiesAndClasses(word).toString());
			Map<String, Set<String>> map = lookup.getDBpediaEntitiesAndClasses(word, type);
			for (String key : map.keySet()){
				System.out.println(key + "  "  + map.get(key).toString());
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	

}
