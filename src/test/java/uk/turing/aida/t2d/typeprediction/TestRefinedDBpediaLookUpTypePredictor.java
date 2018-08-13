/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.t2d.typeprediction;

import uk.turing.aida.typeprediction.RefinedDBpediaLookUpTypePredictor;

/**
 *
 * Test DBpeddia lookup on type prediction 
 * 
 * @author ernesto
 * Created on 6 Aug 2018
 *
 */
public class TestRefinedDBpediaLookUpTypePredictor extends TestTypePredictor {

	int max_hits;
	
	public TestRefinedDBpediaLookUpTypePredictor(boolean only_primary_columns, int max_hits) throws Exception{
		super(only_primary_columns);
		this.max_hits = max_hits;
	}
	
	
	
	protected void createPredictor(){
		
		type_predictor = new RefinedDBpediaLookUpTypePredictor(max_hits);
	}
	
	
	public static void main (String[] args){
		
		
		int[] hits = {1,2,3,4,5};
		//int[] hits = {1};

		
		//Change to continue with partial tests
		int starting_row=0;
		
		try {
			
			
			for (int n_hits : hits){
				
				TestRefinedDBpediaLookUpTypePredictor test = new TestRefinedDBpediaLookUpTypePredictor(false, n_hits);
				test.performTest(starting_row);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	@Override
	protected String getOutputTypesFile() {
		return "refined-lookup_col_classes_hits_"+ max_hits + ".csv";
	}
	
	protected String getOutputEntailedTypesFile() {
		return "refined-lookup_col_classes_hits_"+ max_hits + "_entailed" + ".csv";
	}



	@Override
	protected String getOutputEntitiesFile() {
		return "refined-lookup_entities_hits_"+ max_hits + ".csv";
	}
	
		
	
	
	
	
	
	
	
}
