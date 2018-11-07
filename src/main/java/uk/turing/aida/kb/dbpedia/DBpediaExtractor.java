package uk.turing.aida.kb.dbpedia;

import uk.turing.aida.KnowledgeGraphExtractor;
import uk.turing.aida.LookupService;
import uk.turing.aida.SPARQLEndpointService;


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
	
	
	

}
