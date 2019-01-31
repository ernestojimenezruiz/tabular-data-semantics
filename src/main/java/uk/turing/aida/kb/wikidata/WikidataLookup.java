package uk.turing.aida.kb.wikidata;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import uk.turing.aida.LookupService;


public class WikidataLookup extends LookupService{
	
	//Help: //https://www.wikidata.org/w/api.php?action=help&modules=wbsearchentities
	//Example: https://www.wikidata.org/w/api.php?action=wbsearchentities&search=virgin%20media&language=en
	
		
	private final String REST_URL = "https://www.wikidata.org/w/api.php?action=wbsearchentities&format=json&";
		
	private final String limit = "limit";
	private final String search = "search";
	private final String language = "language";	
	//Not supported
	//private final String QueryClass = "QueryClass";
	
	private int hits=5;
	private String lang="en";
	
	protected URL buildRequestURL(String query, int max_hits, String lang)
			throws URISyntaxException, MalformedURLException {
		URIBuilder ub = new URIBuilder(getREST_URL());
		//ub.addParameter(QueryClass, cls_type);
		ub.addParameter(limit, String.valueOf(max_hits));
		ub.addParameter(language, lang);
		ub.addParameter(search, query);
		return ub.build().toURL();
		
	}

	@Override
	protected String getREST_URL() {		
		return REST_URL;
	}

	@Override
	public Set<String> getEntityURIs(String query, String cls_type, int max_hits, String language)
			throws JsonProcessingException, IOException, URISyntaxException {
		return getWikidataEntities(query, max_hits, language);
	}
	
	
	
	public Set<String> getWikidataEntities(String query) throws JsonProcessingException, IOException, URISyntaxException{
		return getWikidataEntities(query, hits, lang);
	}
	
	
	/**
	 * 
	 * @param query
	 * @param max_hits
	 * @param language
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Set<String> getWikidataEntities(String query, int max_hits, String language)
			throws JsonProcessingException, IOException, URISyntaxException {
		Set<String> entities = new HashSet<String>();
		
		
		URL urlToGet = buildRequestURL(query, max_hits, language);
		
		//System.out.println(urlToGet);
		//System.out.println(getRequest(urlToGet));
		
		
		for (JsonNode result : jsonToNode(getRequest(urlToGet)).get("search")){
			
			//System.out.println(result.toString());
			
			entities.add(result.get("concepturi").asText());
		}
		
		return entities;
	}
	
	
	
	
	
	public static void main(String[] args){
		WikidataLookup lookup = new WikidataLookup();
		
		String word;
		word="virgin";
		//word="berlin";
		word="west%20midlands";
		word="conglomerates";
		word="tetris";
		//word="puzzle";
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
			System.out.println(lookup.getWikidataEntities(word).toString());
			//System.out.println(lookup.getDBpediaEntitiesAndClasses(word).toString());
			//Map<String, Set<String>> map = lookup.getDBpediaEntitiesAndClasses(word, type);
			//for (String key : map.keySet()){
			//	System.out.println(key + "  "  + map.get(key).toString());
			//}
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
