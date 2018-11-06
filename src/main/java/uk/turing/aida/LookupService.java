package uk.turing.aida;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class LookupService {
	
	
private final ObjectMapper mapper = new ObjectMapper();

	
	
	protected JsonNode jsonToNode(String json) throws JsonProcessingException, IOException {

		return mapper.readTree(json);

	}
	
	
	
	protected HttpURLConnection getConnection(URL urlToGet) throws IOException {

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
	
	
	protected String getRequest(URL urlToGet) throws IOException {

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
	
	//protected abstract URL buildRequestURL(String query, String cls_type, int max_hits, String language) throws URISyntaxException, MalformedURLException;
	
	protected abstract String getREST_URL();

	public abstract Set<String> getEntityURIs(String query, String cls_type, int max_hits, String language) throws JsonProcessingException, IOException, URISyntaxException;
}
