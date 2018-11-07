package uk.turing.aida.typeprediction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.turing.aida.kb.dbpedia.DBpediaExtractor;
import uk.turing.aida.kb.wikidata.WikidataExtractor;
import uk.turing.aida.tabulardata.Table;
import uk.turing.aida.tabulardata.reader.CVSReader;

public class TestKnowledgeGraphExtractor {

	//Load configuration...
	
	
	
	
	public static void main (String[] args) {
		
		try {
			CVSReader table_reader = new CVSReader("file.csv");
			
			Table table = table_reader.getTable();
			
			List<Integer> entity_columns = new ArrayList<Integer>();
			
			//add column ids
			
			
			DBpediaExtractor dbpedia_extractor = new DBpediaExtractor();
			dbpedia_extractor.getRelatedTriplesForTable(table, entity_columns);
			
			WikidataExtractor wikidata_extractor = new WikidataExtractor();
			
			wikidata_extractor.getRelatedTriplesForTable(table, entity_columns);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	
}
