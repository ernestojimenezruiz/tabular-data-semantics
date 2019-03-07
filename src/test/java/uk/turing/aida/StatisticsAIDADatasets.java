package uk.turing.aida;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.math.NumberUtils;

import uk.turing.aida.tabulardata.Column;
import uk.turing.aida.tabulardata.Table;
import uk.turing.aida.tabulardata.reader.CVSReader;
import uk.turing.aida.tabulardata.utils.WriteFile;
import uk.turing.aida.typeprediction.DBpediaLookUpTypePredictor;
import uk.turing.aida.typeprediction.RefinedDBpediaLookUpTypePredictor;

/**
 * Input:
 * CSV file
 * Output:
 * Statistics file with information about entity columns
 * - Dataset	
 * - CSV file	
 * - Column id	
 * - Column name (if any)	
 * - Primary key? if mostly unique values	
 * - Number of rows	
 * - Number of different entities (same entity may appear in different rows)	
 * - Types (semantic types from a KG/KB/Ontology)
 * 
 * @author ejimenez-ruiz
 *
 */
public class StatisticsAIDADatasets {
	
	String dataset;
	String file_name;
	
	String dbpedia_uri = "http://dbpedia.org/ontology/";
	
	WriteFile writer = new WriteFile("/home/ejimenez-ruiz/Documents/ATI_AIDA/Datasets/annotation.log", true);
	//WriteFile writer = new WriteFile("/home/ejimenez-ruiz/Documents/ATI_AIDA/Datasets/statistics.log", true);
	
	
	public StatisticsAIDADatasets(String dataset, String path, String file, boolean contains_header) throws Exception {
		
		this.dataset = dataset;
		file_name = file;
		
		CVSReader reader = new CVSReader(path+file, contains_header);
		System.out.print(file);
		System.out.println("\trows: " +reader.getTable().getSize());//tests
		
		analyseTable(reader.getTable());
		
		writer.closeBuffer();
	
				
		
		
	}

	private void analyseTable(Table table) throws Exception {
		
		//hasColumnNames
		for (int cindex=0; cindex<table.getNumberOfColumns(); cindex++) {
			
			//System.out.println("Analysing column: " + cindex);
			analyseColumn(table, cindex);
			
		}
	}

	private void analyseColumn(Table table, int cid) throws Exception {
		
		int n_rows = table.getSize();
		
		//column-name if any
		String c_name = "";
		if (table.hasColumnNames())
			c_name = table.getColumnName(cid);
		
		
		boolean isPrimaryKey=false;
		Set<String> diff_values_set = new HashSet<String>(); //without repetition
		
		
		//Do not continue if majority of number values (rough check)
		if (!checkIfEntityColumn(table, cid))
			return;
		
		
		
		//Different values
		diff_values_set.addAll(table.getColumnValues(cid));
		int diff_values = diff_values_set.size();
		isPrimaryKey = ((double) diff_values/ (double)n_rows) > 0.9; //Min 0.9 ratio to be similar to a PK 
		//TODO if only 2 values -> almost boolean?
		
		
		
		
		
		
		//System.out.println(diff_values_set.size() +" of "+ n_rows);
		
	
		//Get type prediction over different values (we avoid querying for the same entity many times...)
		Column c_different =  new Column();		
		for (String cell : diff_values_set){
			if (!cell.equals("Unknown") && !cell.equals("#NULL!") && !cell.equals("N/A") && !cell.equals("NaN")&& !cell.equals("Inf") &&!cell.equals("NA") && !cell.equals("NULL") && !cell.isEmpty()) //Better organise this. Standard ways of defining NULL/NA
				//e.g. https://cran.r-project.org/doc/contrib/de_Jonge+van_der_Loo-Introduction_to_data_cleaning_with_R.pdf
			c_different.addColumnnValue(cell);
		}
		
		
		//Check if it contains meaningful words: WordNet
		//RefinedDBpediaLookUpTypePredictor predictor = new RefinedDBpediaLookUpTypePredictor(5);
		DBpediaLookUpTypePredictor predictor = new DBpediaLookUpTypePredictor(5,"");
		TreeMap<String, Double> type_prediction_map = predictor.getClassTypesForColumn(c_different);
		//TreeMap<String, Double> type_prediction_map = new TreeMap<String, Double>();
		
		
		//Print line
		//TODO
		StringBuilder sb = new StringBuilder();
		
		sb.append( "\"").append(dataset).append("\",\"")
				.append(file_name).append("\",\"")
				.append(cid).append("\",\"")
				.append(c_name).append("\",\"")
				.append(isPrimaryKey).append("\",\"")
				.append(n_rows).append("\",\"")
				.append(diff_values).append("\"")
				;
		
		
		
		/*sb.append("\\multirow{4}{*}{").append(file_name).append("} & ")
		.append("\\multirow{4}{*}{").append(n_rows).append("} & ")
		.append(cid).append(" & ")
		.append(c_name).append(" & ")
		.append(diff_values).append(" \\\\");
		*/
		
		//TODO filter types if voting <0.25?
		for (String type: type_prediction_map.descendingKeySet()){
			
			sb.append(",\"").append(type.replaceAll(dbpedia_uri, "")).append(":")
				.append(type_prediction_map.get(type)).append("\"");
			
		}
		
		//Print
		System.out.println(sb.toString());
		
		if (diff_values<5)
			System.out.println(diff_values_set);
		
		
		writer.writeLine(sb.toString());
		
		
	}
	
	
	
	/**
	 * Approximate method. Use other heuristics: e,g, Taha techniques + WordNet (e.g. meaningful words and not codes)
	 * @param table
	 * @param cid
	 * @return
	 */
	private boolean checkIfEntityColumn(Table table, int cid) {
		
		//Number of checks
		int n_checks = 20;
		int rid;
		String value_cell;
		
		int number_cases=0;
		int empty_cases=0;
		int booelan_cases=0;
		
		
		for (int i=0; i<n_checks; i++) {
			
			//Select random row
			rid = getRandomIntegerBetweenRange(table.getSize()-1);
			value_cell = table.getCell(rid, cid);
			
			value_cell = value_cell.replaceAll("-", "");
			value_cell = value_cell.replaceAll("%", "");
			value_cell = value_cell.replaceAll(" ", "");
			value_cell = value_cell.replaceAll("\\(", "");
			value_cell = value_cell.replaceAll("\\)", "");
			value_cell = value_cell.replaceAll("\\\\", "");
			
			//Unknown or empty
			if (value_cell.equals("Unknown") || value_cell.equals("#NULL!") || value_cell.equals("N/A") || value_cell.equals("NaN") || value_cell.equals("Inf") || value_cell.equals("NA") || value_cell.equals("NULL") || value_cell.isEmpty())
				empty_cases++;
			
			
			if (NumberUtils.isCreatable(value_cell))
				number_cases++;
			
			
			if (value_cell.toLowerCase().equals("true")||value_cell.toLowerCase().equals("false")|| value_cell.toLowerCase().equals("y")|| value_cell.toLowerCase().equals("n")||value_cell.toLowerCase().equals("yes")||value_cell.toLowerCase().equals("no"))
				booelan_cases++;
			
		}
		
		
		//Min 30% non numeric and non empty and non boolean
		return ((double) (number_cases+empty_cases+booelan_cases)/ (double)n_checks) < 0.7;
		
		
		
		
	}

	public static int getRandomIntegerBetweenRange(int max){

		int min=0;
	    int x = (int) ((int)(Math.random()*((max-min)+1))+min);

	    return x;

	}
	

	
	public static void main(String[] args) {
		
			
		//TODO
		//better filtering of NULL, etc. and other heuristics to filter numbers (cell with mostly digits)... Combine with Taha?
		//Query bioportal too for more specialised IDs
		
		
		AIDADatasets.setAIDADatastets();
		
		
		try {
			
			for (AIDACSVFile file_aida: AIDADatasets.getCSVFiles()) {
			
				new StatisticsAIDADatasets(file_aida.getDataset(), file_aida.getPath(), file_aida.getFile(), true);
		
			}	
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	

}
