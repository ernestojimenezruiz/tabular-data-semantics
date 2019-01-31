package uk.turing.aida.kb.wikidata;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Statement;

import com.fasterxml.jackson.core.JsonProcessingException;

import uk.turing.aida.KnowledgeGraphExtractor;
import uk.turing.aida.LookupService;
import uk.turing.aida.SPARQLEndpointService;
import uk.turing.aida.tabulardata.Table;

/**
 * This class aims at extracting a (over approximation) portion of Wikidata relevant to a table. 
 * @author ejimenez-ruiz
 *
 */
public class WikidataExtractor extends KnowledgeGraphExtractor {

	public WikidataExtractor() throws Exception {
		super();
	}

	@Override
	protected LookupService createLookupService() {
		return new WikidataLookup();
	}

	@Override
	protected SPARQLEndpointService createEndpointService() {
		return new WikidataEndpoint();
	}
	
	@Override
	public Set<Statement> getRelatedTriplesForTable(Table table, List<Integer> entity_columns) throws Exception{
		
		//
		related_entities.clear();		
		referenced_entities.clear();
		
		Set<Statement> triples = new HashSet<Statement>();
		
		for (int rid=0; rid<table.getNumberOfRows(); rid++) {
			for (int cid : entity_columns) {
				triples.addAll(getRelatedTriplesForCell(table.getCell(rid, cid)));
			}
		}
		
		return triples;
	
	}
	
	
	public Set<Statement> getRelatedTriplesForCell(String cell_value) throws Exception{
		
		//init
		related_triples.clear();
		
		extractRelatedEntitiesLookup(cell_value);
		
		extractRelatedTriplesEndPoint();
		
		return related_triples;
	}
	
	

	protected void extractRelatedEntitiesLookup(String cell_value) throws JsonProcessingException, IOException, URISyntaxException {
		
		Set<String> words;
		
		
		//Already visited
		if (visited_labels.contains(cell_value))
			return; 
				
				
		System.out.println("Cell: " + cell_value);
		
		//LOOKUP
		//1. Look-up for whole cell label (hits=5)
		related_entities.addAll(lookup.getEntityURIs(cell_value, "", 5, "en"));
		visited_labels.add(cell_value);
		
		
		
		//2. Look up for words in cell label (hits=30)
		words = getWordsFromLabel(cell_value);
		for (String word : words) {
			if (!visited_labels.contains(word)) {
				related_entities.addAll(lookup.getEntityURIs(word, "", 10, "en"));
				visited_labels.add(word);
			}
		}
		
		//Filter by type later?
		
	}
	
}
