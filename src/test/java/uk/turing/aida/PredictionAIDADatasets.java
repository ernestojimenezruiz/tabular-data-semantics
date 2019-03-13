package uk.turing.aida;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.turing.aida.tabulardata.reader.CVSReader;

public class PredictionAIDADatasets {

	//TODO To be completed. We aim at producing a better training samples for ColNet
	
	public PredictionAIDADatasets() {
	
		//AIDADatasets.setAIDADatastets();
	
		//ColumnClassTypePredictor
		
	}
	
	
	
	//Read GT to get entity columns.
	protected void startPrediction() throws IOException {
		//"dataset","file","col id","col name","primary key","num rows","different rows","semantic type","source"
		//"TraitHub","TTT_cleaned_dataset_final.csv","1","AccSpeciesName","false","66308","538","Plant","dbpedia"
	
		
		CVSReader gs_reader = new CVSReader(AIDADatasets.getGTFile(), true);
		
		if (gs_reader.getTable().isEmpty()){
			System.err.println("File '" + AIDADatasets.getGTFile() + "' is empty.");
			return;
		}		
		
		
		
		
		for (int i=0; i<gs_reader.getTable().getSize(); i++) {
		
			String file_name_csv = AIDADatasets.getBasePath() + gs_reader.getTable().getRow(i)[0] + "/"  + gs_reader.getTable().getRow(i)[1];
			
			
			File file = new File(file_name_csv);
			if (!file.exists()){
				System.err.println("The file '" + file_name_csv + "' does not exixt.");
				continue;
				
			}
			
			
			//TODO store indexes of columns to perform prediction. Then perform prediction for table and list of columns... (ColumnClassTypePredictor)
			//Store log of prediction for "unique" entities.
			//Top 5 and above a give threshold?
			//CSV: query1;KGEntity1.toString();....
			//     query1;KGEntity2.toString();....
			
			
			
			CVSReader file_reader = new CVSReader(file_name_csv);
			
			
			if (file_reader.getTable().isEmpty()){
				System.err.println("File '" + file_name_csv + "' is empty.");
				return;
			}		
			
			
			
		}
	
	}
	
	
	
	//Query KG and wikidata? and then dbpedia
	//Try to refine types....
	
	
	//Also get potential entity matching
	
	
	//Focus on concrete cases... like researchers, BT 180
	
	
	
	public static void main(String args[]) {

		
		PredictionAIDADatasets prediction = new PredictionAIDADatasets();
		
		try {
			prediction.startPrediction();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
}
