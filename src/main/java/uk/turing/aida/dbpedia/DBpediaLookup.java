/*******************************************************************************
 * 
 *  Copyright 2018 by The Alan Turing Institute
 * 
 *
 *******************************************************************************/
package uk.turing.aida.dbpedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

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
	private String query;
	
	private final ObjectMapper mapper = new ObjectMapper();

	
	
	
	private JsonNode jsonToNode(String json) throws JsonProcessingException, IOException {

		return mapper.readTree(json);

	}
	
	private HttpURLConnection getConnection(String urlToGet) throws IOException {

		URL url;
		HttpURLConnection conn;
		
		url = new URL(urlToGet);
		conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		//conn.setRequestProperty("Authorization", "apikey token="
		//		+ API_KEY_Ernesto);
		conn.setRequestProperty("Accept", "application/json");

		return conn;

	}
	
	public Set<String> getDBpediaEntities(String query) throws JsonProcessingException, IOException{
		return getDBpediaEntities(query, hits);
	}

	
	/**
	 * Return a Set of URIs containing the DBPedia entities related to the query string.
	 * @param query
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public Set<String> getDBpediaEntities(String query, int max_hits) throws JsonProcessingException, IOException{
		
		
		Set<String> entities = new HashSet<String>();
		
		String urlToGet = REST_URL	+ MaxHits + "="+ max_hits + "&" + QueryString + "=" + query;
		
		
		
		for (JsonNode result : jsonToNode(getRequest(urlToGet)).get("results")){
			
			entities.add(result.get("uri").asText());
		}
		
		return entities;
		
	}
	
	
	
	
	private String getRequest(String urlToGet) throws IOException {

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
		
		try {
			System.out.println(lookup.getDBpediaEntities("berlin").toString());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	

}
