/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata.t2d;

/**
 *
 * This class will prepare the extended T2D dataset for T2K. Driven by gold standard.
 * T2K only matches entioties in PK columns. Type assig
 * (1) Creating new tables for non-primary columns (they will represent something like an inverse relationship). 
 * (2) Format: Col1=Non-PKm Col2=original PK
 * (3) Remove duplicates from Non-PK column (T2K identifies as PK column the leftmost with most unique values). 
 * (4) New table name: tablename-columnid: 73242003_5_4847571983313033360_1-2  -> also for primary keys
 * (5) GSformat: "86747932_0_7532457067740920052","1","True","Agent","Company","Organisation","Airline"
 *
 * @author ernesto
 * Created on 22 Aug 2018
 *
 */
public class PrepareDatasetForT2K {
	
	
	
	
	
	
	
	

}
