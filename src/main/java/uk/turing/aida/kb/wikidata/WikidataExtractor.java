package uk.turing.aida.kb.wikidata;

import uk.turing.aida.KnowledgeGraphExtractor;
import uk.turing.aida.LookupService;
import uk.turing.aida.SPARQLEndpointService;

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
	
}
