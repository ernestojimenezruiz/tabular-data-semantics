/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.t2d.typeprediction;

import uk.turing.aida.typeprediction.DBpediaLookUpTypePredictor;

/**
 *
 * Test DBpeddia lookup on type prediction 
 * 
 * @author ernesto
 * Created on 6 Aug 2018
 *
 */
public class TestDBpediaLookUpTypePredictor extends TestTypePredictor {

	int max_hits;
	int max_types;
	String filter_type;
	
	
	public TestDBpediaLookUpTypePredictor(boolean only_primary_columns, int max_hits, int max_types, String filtertype) throws Exception{
		super(only_primary_columns);
		this.max_hits = max_hits;
		this.max_types = max_types;
		this.filter_type = filtertype;
	}
	
	
	
	protected void createPredictor(){
		
		type_predictor = new DBpediaLookUpTypePredictor(max_hits, max_types, filter_type);
	}
	
	
	public static void main (String[] args){
		
		
		int[] hits = {1,2,3,5};
		int[] types = {1,2,3,5};
		//int[] hits = {1};
		//int[] types = {1};
		
		
		try {
			
			
			for (int n_hits : hits){
				for (int n_types : types){
			
					TestDBpediaLookUpTypePredictor test = new TestDBpediaLookUpTypePredictor(false, n_hits, n_types, "");
					test.performTest();
					System.out.println(n_hits + " " + n_types +" " + test.getPrecision() + " " + test.getRecall() + " " + test.getFmeasure());

				}
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	@Override
	protected String getOutputTypesFile() {
		return "lookup_col_classes_hits_"+ max_hits + "_types_" + max_types + ".csv";
	}



	@Override
	protected String getOutputEntailedTypesFile() {
		return "lookup_col_classes_hits_"+ max_hits + "_types_" + max_types + "_entailed" + ".csv";
	}
	
		

	@Override
	protected String getOutputEntitiesFile() {
		return "lookup_entities_hits_"+ max_hits + ".csv";
	}	
	
	
	
	
	
	
}
