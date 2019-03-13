package uk.turing.aida;

import java.io.IOException;

import uk.turing.aida.tabulardata.reader.CVSReader;
import uk.turing.aida.tabulardata.utils.WriteFile;
import uk.turing.aida.typeprediction.ExtractSuperTypes;

public class AdaptAIDAFiles {
	
	//input format
	//dataset	file	col id	col name	primary key	num rows	different rows	semantic type	source
	//TraitHub	TTT_cleaned_dataset_final.csv	1	AccSpeciesName	false	66308	538	Plant	dbpedia
	//output format
	//"table_id" "col_id" "PK?" "type1" "type2" ... "type_n"
	/**
	 * We only consider so far dbpedia
	 * @throws Exception 
	 */
	protected AdaptAIDAFiles() throws Exception{
		CVSReader gs_reader = new CVSReader(AIDADatasets.getGTFile(), true);
		
		WriteFile writer = new WriteFile(AIDADatasets.getBasePath()+"column_types_aida.csv");
		
		StringBuilder sb = new StringBuilder();
		
		for (int rid=0; rid<gs_reader.getTable().getSize(); rid++){
		
			//TODO only for dbpedia so far
			if (gs_reader.getTable().getCell(rid, 8).equals("dbpedia")) {
			
				sb.append("\"").append(gs_reader.getTable().getCell(rid, 1)).append("\",\"")
				.append(gs_reader.getTable().getCell(rid, 2)).append("\",\"")
				.append("False").append("\",\"");
				
				ExtractSuperTypes extractor = new ExtractSuperTypes();
							
				String type  = gs_reader.getTable().getCell(rid, 7);
				
				sb.append(type).append("\"");
				
				for (String superType: extractor.getSuperTypesSparql(type)) {
					sb.append(",\"").append(superType.replaceAll(extractor.getDbpediaURINamespace(), "")).append("\"");
				}
							
				writer.writeLine(sb.toString());				
				
			}			
						
			//clear
			sb.setLength(0);			
		}
		
		
		writer.closeBuffer();
		
		
		
	}

	
	
	
	//input format
	//"Broadband","november_2013.csv","4","ISP","false","2390","20","Agent:0.6","Organisation:0.6","Company:0.55"
	//output format
	//"table_id col_id" "type1" "confidence" ... "type_n"
	/**
	 * We only consider so far dbpedia
	 * @throws Exception 
	 */
	protected AdaptAIDAFiles(String prediction_file_in, String prediction_file_out) throws Exception{
		CVSReader pred_reader = new CVSReader(prediction_file_in, false);
		
		WriteFile writer = new WriteFile(prediction_file_out);
		
		//StringBuilder sb = new StringBuilder();
		String prediction_line;
		String[] type_score;
		
		for (int rid=0; rid<pred_reader.getTable().getSize(); rid++){
		
			
			prediction_line="\"" + pred_reader.getTable().getCell(rid, 1)+ " " + 
			pred_reader.getTable().getCell(rid, 2) + "\",\"";
		
			
			
			for (int i=7; i<pred_reader.getTable().getRow(rid).length; i++) { 
				
				
				type_score  = pred_reader.getTable().getCell(rid, i).split(":");
			
				if (!type_score[0].equals("Wikidata"))
					writer.writeLine(prediction_line + type_score[0] + "\",\"" + type_score[1] + "\"");
			
			}
					
						
			//clear
			prediction_line="";			
		}
		
		
		writer.closeBuffer();
		
		
		
	}
	
	public static void main(String args[]) {
		
		try {
			//new AdaptAIDAFiles();
			new AdaptAIDAFiles(
					"/home/ejimenez-ruiz/Documents/ATI_AIDA/Datasets/AnalysedDatasets/lookup_column_types_aida_boadband_full_log.log",
					"/home/ejimenez-ruiz/Documents/ATI_AIDA/Datasets/AnalysedDatasets/lookup_column_types_aida_boadband.csv");
			
			
			new AdaptAIDAFiles(
					"/home/ejimenez-ruiz/Documents/ATI_AIDA/Datasets/AnalysedDatasets/lookup_column_types_aida_trait_full_log.log",
					"/home/ejimenez-ruiz/Documents/ATI_AIDA/Datasets/AnalysedDatasets/lookup_column_types_aida_trait.csv");
			
			
			new AdaptAIDAFiles(
					"/home/ejimenez-ruiz/Documents/ATI_AIDA/Datasets/AnalysedDatasets/lookup_column_types_aida_hes_full_log.log",
					"/home/ejimenez-ruiz/Documents/ATI_AIDA/Datasets/AnalysedDatasets/lookup_column_types_aida_hes.csv");
			
			
			new AdaptAIDAFiles(
					"/home/ejimenez-ruiz/Documents/ATI_AIDA/Datasets/AnalysedDatasets/lookup_column_types_aida_full_log.log",
					"/home/ejimenez-ruiz/Documents/ATI_AIDA/Datasets/AnalysedDatasets/lookup_column_types_aida.csv");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	

}
