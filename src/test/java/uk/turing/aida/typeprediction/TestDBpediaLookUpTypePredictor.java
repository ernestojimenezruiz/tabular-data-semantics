/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.typeprediction;

import uk.turing.aida.Configuration;
import uk.turing.aida.tabulardata.aida.AIDAConfiguration;
import uk.turing.aida.tabulardata.t2d.T2DConfiguration;
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
	String filter_type;
	
	
	public TestDBpediaLookUpTypePredictor(boolean only_primary_columns, int max_hits, String filtertype, Configuration conf) throws Exception{
		super(only_primary_columns, conf);
		this.max_hits = max_hits;
		this.filter_type = filtertype;
	}
	
	
	
	protected void createPredictor(){
		
		type_predictor = new DBpediaLookUpTypePredictor(max_hits, filter_type);
	}
	
	
	public static void main (String[] args){
		
		
		//Configuration config = new T2DConfiguration(); 
		Configuration config = new AIDAConfiguration(); 
		
		
		//int[] hits = {1,2,3,4,5};
		int[] hits = {5};
		
		
		//Change to continue with partial tests
		int starting_row=0;
		//Computing predictions for table '21585935_0_294037497010176843' row-id: 219
		
		try {
			
			
			for (int n_hits : hits){
					
				TestDBpediaLookUpTypePredictor test = new TestDBpediaLookUpTypePredictor(false, n_hits, "", config);
				test.performTest(starting_row);

			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	@Override
	protected String getOutputTypesFile() {
		return config.getConfigName() + "lookup_col_classes_hits_"+ max_hits + ".csv";
	}



	@Override
	protected String getOutputEntailedTypesFile() {
		return config.getConfigName() + "lookup_col_classes_hits_"+ max_hits + ".csv";
	}
	
		

	@Override
	protected String getOutputEntitiesFile() {
		return config.getConfigName() + "lookup_entities_hits_"+ max_hits + ".csv";
	}	
	
	
	
	
	
	
}
