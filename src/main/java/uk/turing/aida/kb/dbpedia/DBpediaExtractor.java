package uk.turing.aida.kb.dbpedia;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.jena.rdf.model.Statement;

import uk.turing.aida.KnowledgeGraphExtractor;
import uk.turing.aida.LookupService;
import uk.turing.aida.SPARQLEndpointService;
import uk.turing.aida.tabulardata.Column;
import uk.turing.aida.tabulardata.Table;
import uk.turing.aida.typeprediction.DBpediaLookUpTypePredictor;


/**
 * This class aims at extracting a (over approximation) portion of DBpedia relevant to a table.
 * @author ejimenez-ruiz
 *
 */
public class DBpediaExtractor extends KnowledgeGraphExtractor{
	
	
	public DBpediaExtractor() throws Exception{ 
		super();
		
	}

	@Override
	protected LookupService createLookupService() {
		return new DBpediaLookup();
	}

	@Override
	protected SPARQLEndpointService createEndpointService() {
		return new DBpediaEndpoint();
	}

	
	
	@Override
	public Set<Statement> getRelatedTriplesForTable(Table table, List<Integer> entity_columns) throws Exception {
	
		related_entities.clear();		
		referenced_entities.clear();
		
		Set<Statement> triples = new HashSet<Statement>();
		
		
		for (int cid : entity_columns) {
				triples.addAll(getRelatedTriplesForColumn(table.getColumn(cid)));		
		}
		
		return triples;
		
		
	}

	
	
	public Set<Statement> getRelatedTriplesForColumn(Column col) throws Exception{
		
		//init
		related_triples.clear();
		
		extractRelatedEntitiesLookup(col);
		
		extractRelatedTriplesEndPoint();
		
		return related_triples;
	}

	
	protected void extractRelatedEntitiesLookup(Column col) throws Exception {
		
		//1. Look-up + prediction for cell values
		DBpediaLookUpTypePredictor dbpediapredictor = new DBpediaLookUpTypePredictor(5, "");		
		TreeMap<String, Double> typesHits = dbpediapredictor.getClassTypesForColumn(col);
		
		//Lookup hits
		related_entities.addAll(dbpediapredictor.getEntityHits().keySet());
		
		
		
		Set<String> words;
		
		
		for (int rid=0; rid<col.getSize(); rid++) {
		
			String cell_value = col.getElement(rid);
			
			//Already visited
			if (visited_labels.contains(cell_value))
				return; 
			
			visited_labels.add(cell_value);
			
			//2. Look-up by word restricting to predicted look-up types (all)
			words = getWordsFromLabel(cell_value);
			for (String word : words) {
				if (!visited_labels.contains(word)) {
					visited_labels.add(word);
					
					//Iterate over types: typesHits
					for (String type: typesHits.keySet()) { 
						//look up with class restriction (without namespace)
						related_entities.addAll(lookup.getEntityURIs(
								word, type.replaceAll(DBpediaOntology.dbpedia_uri_namespace, ""), 10, "en"));
					}
				}
			}
		}
		
	}

	
	

}
