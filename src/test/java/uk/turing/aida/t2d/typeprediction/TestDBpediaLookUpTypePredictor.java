/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.t2d.typeprediction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.turing.aida.tabulardata.Table;
import uk.turing.aida.tabulardata.reader.CVSReader;
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

	
	
	public TestDBpediaLookUpTypePredictor(boolean only_primary_columns) throws Exception{
		super(only_primary_columns);
	}
	
	
	
	public void createPredictor(){
		type_predictor = new DBpediaLookUpTypePredictor(0, 0, null);
	}
	
		
	
	
	
	
	
	
	
}
