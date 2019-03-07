import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.turing.aida.tabulardata.reader.CVSReader;
import uk.turing.aida.tabulardata.utils.WriteFile;

public class TestPredictionsAAAI {
	
	String predicted_types_file;
	String predicted_types_filtered_file;
	String gt_types_file;
	
	String candidate_classes_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/candidate_classes.csv";
	Set<String> candidate_classes = new HashSet<String>();
	
	
	//Only one type per table/column
	Map<String, String> prediction_types = new HashMap<String, String>();
	Map<String, String> gt_types = new HashMap<String, String>();
	
	
	
	
	
	protected void setT2D() {
		
		predicted_types_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/T2K-results-ijcai/t2k_t2d_ijcai.csv";
		
		predicted_types_filtered_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/T2K-results-ijcai/t2k_limaye_top_ijcai.csv";
		
		gt_types_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/test_tables_t2d.csv";
		
	}
	
	protected void setLimaye() {
		
		predicted_types_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/T2K-results-ijcai/t2k_limaye_ijcai.csv";
		
		predicted_types_filtered_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/T2K-results-ijcai/t2k_limaye_top_ijcai.csv";
		
		gt_types_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/test_tables_limaye.csv";
		
	}


	protected void setWikipedia() {
	
		predicted_types_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/T2K-results-ijcai/t2k_wikipedia_ijcai.csv";
		
		predicted_types_filtered_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/T2K-results-ijcai/t2k_wikipedia_top_ijcai.csv";
		
		gt_types_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/test_tables_wikipedia.csv";
		
	}
	
	
	
	
	
	protected void loadCandidateClasses() throws IOException{
		
		CVSReader prediction_reader = new CVSReader(candidate_classes_file);
		
		String[] row;
		
		if (prediction_reader.getTable().isEmpty()){
			System.err.println("File '" + gt_types_file + "' is empty.");
			return;
		}		
		
		
		for (int rid=0; rid<prediction_reader.getTable().getSize(); rid++){
			
			row = prediction_reader.getTable().getRow(rid);

			candidate_classes.add(row[0]);
			

		}
		
	}
	
	
	
	
	/**
	 * One line per types with score
	 * "77694908_0_6083291340991074532 1","Company"
	 * 
	 * @throws IOException
	 */
	protected void readGT() throws IOException{
		
		CVSReader prediction_reader = new CVSReader(gt_types_file);
		
		String[] row;
		String key_name;
		
		
		if (prediction_reader.getTable().isEmpty()){
			System.err.println("File '" + gt_types_file + "' is empty.");
			return;
		}		
		
		
		
		for (int rid=0; rid<prediction_reader.getTable().getSize(); rid++){
			
			row = prediction_reader.getTable().getRow(rid);
			
			
			key_name= row[0].replaceAll(" ", "-");
			
			gt_types.put(key_name, row[1]);	
				
				
		}//end for
		
		
		
	}

	
	
	/**
	 * One line per types with score
	 * "77694908_0_6083291340991074532 1","Company","0.10"
	 * 
	 * @throws IOException
	 */
	protected void readPrediction() throws IOException{
		
		CVSReader prediction_reader = new CVSReader(predicted_types_file);
		
		WriteFile writer = new WriteFile(predicted_types_filtered_file);
		
		String[] row = null;
		String key_name;
		String col0;
		double max_score;
		
		
		if (prediction_reader.getTable().isEmpty()){
			System.err.println("File '" + predicted_types_file + "' is empty.");
			return;
		}		
		
		
		Map<String, Double> votesForType= new HashMap<String, Double>();
		String previous_key_name = prediction_reader.getTable().getRow(0)[0].replaceAll(" ", "-");
		max_score=-1.0;
		String previous_col0 = prediction_reader.getTable().getRow(0)[0];
		
		for (int rid=0; rid<prediction_reader.getTable().getSize(); rid++){
			
			row = prediction_reader.getTable().getRow(rid);
			
			if (row.length<3)
				continue;
			

			key_name= row[0].replaceAll(" ", "-");
			col0 = row[0];
			
			
			//change
			if (!previous_key_name.equals(key_name)){
				
				for (String type : votesForType.keySet()){ 
					
					if (votesForType.get(type)>=max_score){ //if several then keep only one
						prediction_types.put(previous_key_name, type);	
						writer.writeLine("\""+previous_col0+"\",\""+type+"\"");
					}
				}
				
				//Reset
				previous_key_name = key_name;
				previous_col0 = col0;
				votesForType.clear();
				max_score=-1.0;
				
			}
			
			
			//Keep only type from candidate classes and top predicted class, so in principle only one predictions per table/column
			if (candidate_classes.contains(row[1])){
				votesForType.put(row[1], Double.valueOf(row[2]));
				if (max_score<Double.valueOf(row[2])){
					max_score=Double.valueOf(row[2]);
				}
			}
			//empty otherwise
			
		}//end for
		
		
		
		//Last group table-column set of types
		for (String type : votesForType.keySet()){ 
			
			if (votesForType.get(type)>=max_score){
				
				if (votesForType.get(type)>=max_score){
					prediction_types.put(previous_key_name, type);	
					writer.writeLine("\""+previous_col0+"\",\""+type+"\"");
				}
				
			}
		}
		
		
		writer.closeBuffer();
		
		
	}
	
	
	
	
	
	protected void compareResults() {
		
		int fn=0;
		int tp=0;
		int fp=0;
				
			
		for (String test_case: gt_types.keySet()) {
			
			if (!prediction_types.containsKey(test_case)) {
				fn++;
				System.out.println("-NO PREDICTION: " + test_case);
				
			}
			else if (prediction_types.get(test_case).equals(gt_types.get(test_case))) {
				tp++;
			}
			else {
				fp++;
				fn++;
				System.out.println("+WRONG PREDICTION: " + test_case);
				
			}
		}
		
		
		double precision = Math.round((double)tp/(double)(tp+fp)*1000.0)/1000.0;
		double recall = Math.round((double)tp/(double)(tp+fn)*1000.0)/1000.0;
		double fscore = Math.round((2*precision*recall)/(precision+recall)*1000.0)/1000.0;
		
		
		
		
		System.out.println("TP\tFP\tFN");
		System.out.println(tp + "\t" + fp + "\t" + fn);
		
		
		System.out.println("P\tR\tF");
		System.out.println(precision + "\t" + recall + "\t" + fscore);
		
		
		
	}
	
	
	
	
	public static void main (String[] args) {
		
		TestPredictionsAAAI test = new TestPredictionsAAAI();
		
		try {

			test.setT2D();
			//test.setLimaye();
			//test.setWikipedia();
			
			test.loadCandidateClasses();
			
			System.out.println("Candidate classes: " + test.candidate_classes.size());
			
			test.readGT();
			System.out.println("GT size: "+ test.gt_types.keySet().size());
			
			test.readPrediction();
			System.out.println("Prediction size: "+test.prediction_types.keySet().size());
			
			
			test.compareResults();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

}
