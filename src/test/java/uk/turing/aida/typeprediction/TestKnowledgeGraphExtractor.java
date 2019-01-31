package uk.turing.aida.typeprediction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Statement;

import uk.turing.aida.kb.dbpedia.DBpediaExtractor;
import uk.turing.aida.kb.wikidata.WikidataExtractor;
import uk.turing.aida.tabulardata.Table;
import uk.turing.aida.tabulardata.reader.CVSReader;

public class TestKnowledgeGraphExtractor {

	//Load configuration...
	
	
	
	
	public static void main (String[] args) {
		
		try {
			
			String file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/T2D_dataset/tables/1146722_1_7558140036342906956.csv";
			
			CVSReader table_reader = new CVSReader(file);
			
			Table table = table_reader.getTable();
			
			List<Integer> entity_columns = new ArrayList<Integer>();			
			//add column ids: this is sth ptype will be given 
			entity_columns.add(0);
			
			
			//DBpediaExtractor dbpedia_extractor = new DBpediaExtractor();
			//Set<Statement> dbpedia_fragment = dbpedia_extractor.getRelatedTriplesForTable(table, entity_columns);
			
			WikidataExtractor wikidata_extractor = new WikidataExtractor();			
			Set<Statement> wikidata_fragment = wikidata_extractor.getRelatedTriplesForTable(table, entity_columns);
			
			//System.out.println("Size triples dbpedia: " + dbpedia_fragment.size());
			System.out.println("Size triples wikidata: " + wikidata_fragment.size());
			
			
			//1. Find links from dbpedia to wikidata (perform alignment too?)
			
			//2. Evaluate coverage with T2D (probably as test)
			//a. Entity coverage
			//b. Entity pairs (e.g. (sparql) query of how two entities may be related. Find paths between "x" and "y" if exist).
			//Training samples: pairs in KB?
			//Download and create ttl file and use RDFlib to retrieve things
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	
}
