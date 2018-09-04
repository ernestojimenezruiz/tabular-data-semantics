/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.typeprediction;

import java.io.File;

import java.util.TreeSet;

import uk.turing.aida.tabulardata.t2d.T2DConfiguration;


/**
 * Computes Precision and Recall for already computed predictions
 * @author ernesto
 * Created on 8 Aug 2018
 *
 */
public class TestPrecomputedPredictionsT2D extends TestPrecomputedPredictions {

	
	
	T2DConfiguration config = new T2DConfiguration();
	
	
	
	
	public TestPrecomputedPredictionsT2D(EVAL_LEVEL eval_level, String predicted_types_file, double min_votes) throws Exception{
		super(eval_level, predicted_types_file, min_votes);
		
		config.loadConfiguration();	
	
	}
	
	
	
	@Override
	protected String getPath() {
		return config.t2d_path;
	}


	@Override
	protected String getGroundTruthFile() {
		return config.extended_type_gs_file;
	}
	@Override
	protected String getGroundTruthFileParents() {
		return config.extended_type_sc_gs_file;
	}

		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
		try {
			
			T2DConfiguration config = new T2DConfiguration();
			config.loadConfiguration();
			
			String path = config.t2d_path + "output_results/";
			
			File file =  new File(path);
			
			
			TreeSet<String> ordered_files = new TreeSet<String>();
			for (String file_name : file.list()){
				ordered_files.add(file_name);
			}
			
			
			
			System.out.println("file_name" + "\t" + "threshold" + "\t" + 
					"Micro Precision" + "\t" + "Micro Recall" + "\t" + "Micro F-score" + "\t" +
					"Tolerant Macro Precision" + "\t" + "Tolerant Macro Recall" + "\t" + "Tolerant Macro F-score" + "\t" +
					//test_tolerant.getT2KPrecision() + "\t" + test_tolerant.getT2KRecall() + "\t" + test_tolerant.getT2KFmeasure() + "\t" +
					"Strict Macro Precision" + "\t" + "Strict Macro Recall" + "\t" + "Strict Macro F-score"
					//test_restricted.getT2KPrecision() + "\t" + test_restricted.getT2KRecall() + "\t" + test_restricted.getT2KFmeasure()
			);
			
			
			for (String file_name : ordered_files){
			
				
				double threshold = 0.0;
				TestPrecomputedPredictionsT2D  test_prediction;
				
				
				
				EVAL_LEVEL eval_level = EVAL_LEVEL.ALL;
				
				
				
				
				
				//if (file_name.contains("t2k_col_classes_all")){
				//if (file_name.contains("_col_classes") && file_name.endsWith(".csv") && !file_name.contains("_entailed") && !file_name.contains("_supertypes") && !file_name.contains("_jiaoyan")){
				//if (file_name.equals("p_lookup.csv")){
				//if (file_name.contains("partial") && file_name.endsWith(".csv")){
				//if (file_name.startsWith("p_cnn_1_2_1.00")){
				if (file_name.endsWith(".csv")){
					System.out.println(file_name);
					
					while (threshold<=1.0){
					
						//TestPrecomputedPredictions test = new TestPrecomputedPredictions(false, config.t2d_path + "output_results/lookup_col_classes_jiaoyan.csv");
						//TestPrecomputedPredictions test = new TestPrecomputedPredictions(false, config.t2d_path + "output_results/lookup_col_classes_hits_1_types_2_entailed.csv");
						test_prediction = new TestPrecomputedPredictionsT2D(eval_level, path + file_name, threshold); //default: 0.5, 1
						
						test_prediction.performTest();
						
						//System.out.println(file_name);
						//System.out.println("\tRestricted mode: false");
						//System.out.println("\tMicro measures: " + test.getMicroPrecision() + " " + test.getMicroRecall() + " " + test.getMicroFmeasure());
						//System.out.println("\tMacro measures: " + test_tolerant.getMacroPrecision() + " " + test_tolerant.getMacroRecall() + " " + test_tolerant.getMacroFmeasure());
						//System.out.println("\tT2K evaluation: " + test_tolerant.getT2KPrecision() + " " + test_tolerant.getT2KRecall() + " " + test_tolerant.getT2KFmeasure());
						
						
						//test_restricted = new TestPrecomputedPredictionsComplete(eval_level, path + file_name, threshold, true); //default: 0.5, 1
						
						//test_restricted.performTest();
						
						//System.out.println(file_name);
						//System.out.println("\tRestricted mode: true");
						//System.out.println("\tMicro measures: " + test.getMicroPrecision() + " " + test.getMicroRecall() + " " + test.getMicroFmeasure());
						//System.out.println("\tMacro measures: " + test_restricted.getMacroPrecision() + " " + test_restricted.getMacroRecall() + " " + test_restricted.getMacroFmeasure());
						//System.out.println("\tT2K evaluation: " + test_restricted.getT2KPrecision() + " " + test_restricted.getT2KRecall() + " " + test_restricted.getT2KFmeasure());
						
						
						
						System.out.println(file_name + "\t" + threshold + "\t" + 
									test_prediction.getMicroPrecision() + "\t" + test_prediction.getMicroRecall() + "\t" + test_prediction.getMicroFmeasure() + "\t" +
									test_prediction.getMacroPrecision() + "\t" + test_prediction.getMacroRecall() + "\t" + test_prediction.getMacroFmeasure() + "\t" +
									//test_tolerant.getT2KPrecision() + "\t" + test_tolerant.getT2KRecall() + "\t" + test_tolerant.getT2KFmeasure() + "\t" +
									test_prediction.getMacroPrecisionStrictMode() + "\t" + test_prediction.getMacroRecallStrictMode() + "\t" + test_prediction.getMacroFmeasureStrictMode()// + "\t" +
									//test_restricted.getT2KPrecision() + "\t" + test_restricted.getT2KRecall() + "\t" + test_restricted.getT2KFmeasure()
						);
						
						if (threshold >0.39 && threshold < 0.59 )
							threshold+=0.05;
						else
							threshold+=0.10;
						
						
					}
				}
				//}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	
	

	

}
