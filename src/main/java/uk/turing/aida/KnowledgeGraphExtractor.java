package uk.turing.aida;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Statement;


import uk.turing.aida.tabulardata.Table;
import uk.turing.aida.tabulardata.utils.ReadFile;

/**
 * This abstracts class defines the necessary methods to extract a fragment from KGs like dbpedia and wikidata 
 * @author ejimenez-ruiz
 *
 */
public abstract class KnowledgeGraphExtractor {
	
	
	protected Set<String> stopwordsSet= new HashSet<String>();
	protected LookupService lookup;
	protected SPARQLEndpointService endpoint;
	
	private Set<String> related_entities = new HashSet<String>();
	private Set<Statement> related_triples = new HashSet<Statement>();
	private Set<String> referenced_entities = new HashSet<String>();
	
	protected Set<String> visited_labels = new HashSet<String>();
	
	
	public KnowledgeGraphExtractor() throws Exception {
	
		loadStopWords();
		
		lookup = createLookupService();
		endpoint = createEndpointService();
	
	}
	
	
	protected abstract LookupService createLookupService();
	
	protected abstract SPARQLEndpointService createEndpointService();
	
	
	
	
	public Set<Statement> getRelatedTriplesForCell(String cell_value) throws Exception{
	
		Set<String> words;
		
		//init
		related_entities.clear();
		related_triples.clear();
		referenced_entities.clear();
		
		//Already visited
		if (visited_labels.contains(cell_value))
			return related_triples; 
		
		
		//LOOKUP
		//1. Look-up for whole cell label (hits=5)
		related_entities.addAll(lookup.getEntityURIs(cell_value, "", 5, "en"));
		visited_labels.add(cell_value);
		
		//2. Look up for words in cell label (hits=30)
		words = getWordsFromLabel(cell_value);
		for (String word : words) {
			if (!visited_labels.contains(word)) {
				related_entities.addAll(lookup.getEntityURIs(word, "", 30, "en"));
				visited_labels.add(word);
			}
		}
		
		
		//Triples with SPARQL
		//3. Get triples for related entities
		for (String uri_entity : related_entities) {
			related_triples.addAll(endpoint.getTriplesForSubject(uri_entity));
		}
		
		//4. Get triples of objects in above triples
		for (Statement triple : related_triples) {
			if (triple.getObject().isURIResource()){
				referenced_entities.add(triple.getObject().asResource().getURI().toString());
			}
		}
		for (String uri_entity : referenced_entities) {
			if (!related_entities.contains(uri_entity)) {//To avoid extracting the same triples for visited uris
				related_triples.addAll(endpoint.getTriplesForSubject(uri_entity));
				related_entities.add(uri_entity);
			}
		}
		
		
		
		//5. Evaluate coverage with T2D (probably as test)
		
	
		
		
		
		return related_triples;
	}
	
	
	
	/**
	 * 
	 * @param table Read from a CSV file
	 * @param entity columns Index of columns with string or categorical information
	 * //columns_types For example provided by Ptype
	 */
	public Set<Statement> getRelatedTriplesForTable(Table table, List<Integer> entity_columns) throws Exception{
		
		Set<Statement> triples = new HashSet<Statement>();
		
		for (int rid=0; rid<table.getNumberOfRows(); rid++) {
			for (int cid : entity_columns) {
				triples.addAll(getRelatedTriplesForCell(table.getCell(rid, cid)));
			}
		}
		
		return triples;
	
	}
	
	
	
	private Set<String> getWordsFromLabel(String label) {
		
		String label_value=label.replace(",", "");
		String[] words;
		
		
		if (label_value.startsWith("_")){
			label_value = label_value.substring(1, label_value.length());
		}
		if (label_value.endsWith("_")){
			label_value = label_value.substring(0, label_value.length()-1);
		}
		
		
		if (label_value.indexOf("_")>0){ 
			words=label_value.split("_");
		}
		else if (label_value.indexOf(" ")>0){ 
			words=label_value.split(" ");
		}
		//Split capitals...
		//else{
			//label_value = Utilities.capitalPrepositions(label_value);
		//	words=Utilities.splitStringByCapitalLetter(label_value);
		//}
		else {
			words=new String[1];
			words[0]=label_value;
		}
		
		Set<String> cleanWords = new HashSet<String>();
		for (int i=0; i<words.length; i++){
			
			words[i]=words[i].toLowerCase(); //to lower case
			
			if (words[i].length()>0){
			
				
				if (!getStopwordsSet().contains(words[i])){ 
					//words[i].length()>2 &&  Not for exact IF: it may contain important numbers					
					cleanWords.add(words[i]);
				}				
			}			
		}
		
		
		return cleanWords;
		
		
	}
	
	
	private Set<String> getStopwordsSet() {
		return stopwordsSet;
	}



	private void loadStopWords() throws Exception{
		
		ReadFile reader = new ReadFile(KnowledgeGraphExtractor.class.getResourceAsStream("stopwords.txt"));
		String line;
		
		while ((line = reader.readLine()) != null){
			if (!line.startsWith("#"))
				getStopwordsSet().add(line);
		}
		reader.closeBuffer();
		
	}
	

}
