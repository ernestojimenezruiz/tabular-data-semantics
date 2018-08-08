/*******************************************************************************
 * 
 *  Copyright 2018 by The Alan Turing Institute
 * 
 *
 *******************************************************************************/
package uk.turing.aida.kb.dbpedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

/**
 *
 * Class to access the DBpedia look up REST API: https://github.com/dbpedia/lookup
 *
 * @author ernesto
 * Created on 23 Jul 2018
 *
 */
public class DBpediaLookup {
		
	//http://lookup.dbpedia.org/api/search/KeywordSearch?MaxHits=3&QueryString=berlin
	private final String REST_URL = "http://lookup.dbpedia.org/api/search/KeywordSearch?";
	
	private final String MaxHits = "MaxHits";
	private final String QueryString = "QueryString";
	private final String QueryClass = "QueryClass";
	
	private int hits=5;
	//private String query;
	
	private final ObjectMapper mapper = new ObjectMapper();

	
	
	
	private JsonNode jsonToNode(String json) throws JsonProcessingException, IOException {

		return mapper.readTree(json);

	}
	
	private HttpURLConnection getConnection(URL urlToGet) throws IOException {

		URL url;
		HttpURLConnection conn;
		
		//url = new URL(urlToGet);
		url = urlToGet;
		conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		//conn.setRequestProperty("Authorization", "apikey token="
		//		+ API_KEY_Ernesto);
		conn.setRequestProperty("Accept", "application/json");

		return conn;

	}
	
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
		
		//System.out.println(urlToGet);
		
		
		for (JsonNode result : jsonToNode(getRequest(urlToGet)).get("results")){
			
			//System.out.println(result.toString());
			
			entities.add(result.get("uri").asText());
		}
		
		return entities;
		
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
	
	
	
	private URL buildRequestURL(String query, String cls_type, int max_hits) throws URISyntaxException, MalformedURLException{
		URIBuilder ub = new URIBuilder(REST_URL);
		ub.addParameter(QueryClass, cls_type);
		ub.addParameter(MaxHits, String.valueOf(max_hits));
		ub.addParameter(QueryString, query);
		return ub.build().toURL();
	}
	
	
	
	
	private String getRequest(URL urlToGet) throws IOException {

		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		
		
		//In some cases it fails the connection. Try several times
		boolean success=false;
		int attempts=0;
		

		//TODO how many attempts?
		//while(!success && attempts<25){
		while(!success && attempts<3){	

			
			attempts++;
			
			try{
				conn = getConnection(urlToGet);

				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

				while ((line = rd.readLine()) != null) {
					result += line;
				}
				rd.close();
				
				if (!result.isEmpty())
					success=true;
			}
			
			catch(IOException e){
				System.out.println("Error accessing: " + urlToGet + "  Attempt: " + attempts);
			}
			
		}
		
		if (!success)
			throw new IOException(); //We throw error to check next page
		else if (attempts>1)
			System.out.println("SUCCESS accessing: " + urlToGet + "  Attempt: " + attempts);
				
		return result;
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
		word="batman:%20arkham%20city";
		word="batman: arkham city";
		word="action-adventure";
		//word="{action-adventure|brawler|stealth}";
		
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
