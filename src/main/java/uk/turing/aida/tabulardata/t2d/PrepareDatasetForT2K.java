/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata.t2d;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.turing.aida.tabulardata.reader.CVSReader;
import uk.turing.aida.tabulardata.utils.WriteFile;

/**
 *
 * This class will prepare the extended T2D dataset for T2K. Driven by gold standard.
 * T2K only matches entities in PK columns. Type assig
 * (1) Creating new tables for non-primary columns (they will represent something like an inverse relationship). 
 * (2) Format: Col1=Non-PKm Col2=original PK
 * (3) Remove duplicates from Non-PK column (T2K identifies as PK column the leftmost with most unique values). 
 * (4) New table name: tablename-columnid: 73242003_5_4847571983313033360_1-2  -> also for primary keys?
 * (5) GSformat: "86747932_0_7532457067740920052","1","True","Agent","Company","Organisation","Airline"
 *
 * @author ernesto
 * Created on 22 Aug 2018
 *
 */
public class PrepareDatasetForT2K {
	
	T2DConfiguration config = new T2DConfiguration();
	
	String folder_name = "tables_non_pk_t2k";
	
	
	//Links tables id to the id of the PK (according to GS)
	Map<String, Integer> table2PKid = new HashMap<String, Integer>();
	
	
	public PrepareDatasetForT2K() throws NumberFormatException, IOException{
		//Read GS which will lead the evaluation
		
		config.loadConfiguration();
		
		CVSReader gs_reader = new CVSReader(config.t2d_path + config.extended_type_gs_file);
			
		String[] row;
		
		
		//First pass to store the id of the primary keays
		for (int rid=0; rid<gs_reader.getTable().getSize(); rid++){
			
			row = gs_reader.getTable().getRow(rid);
			
			if (Boolean.valueOf(row[2])){
				table2PKid.put(row[0], Integer.valueOf(row[1]));
			}
		}
		
		
		for (int rid=0; rid<gs_reader.getTable().getSize(); rid++){
			
			row = gs_reader.getTable().getRow(rid);
			
			//System.out.println(row[0] + " " + row[1] + "  " +  row[2] + " " + row[3]);
			File file = new File(config.t2d_path + config.tables_folder + row[0] + ".csv");
			if (!file.exists()){
				System.err.println("The file '" + config.t2d_path + config.tables_folder + row[0] + ".csv' does not exixt.");
				continue;
			}
			
			//We avoid PK columns
			if (!Boolean.valueOf(row[2])){
				createTableForNonPK(row[0], Integer.valueOf(row[1]), table2PKid.get(row[0]));
			}
			
		}
		
	}
	
	
	
	private void createTableForNonPK(String table_id, int column_id, int column_id_pk) throws IOException{
		
		//Remove duplicates... keep table title and cosnsietnt row for PK column
		
		CVSReader table_reader = new CVSReader(config.t2d_path + config.tables_folder + table_id + ".csv", true);
		
		Set<String> values = new HashSet<String>();
		
		WriteFile writer = new WriteFile(config.t2d_path + folder_name + "/" + table_id + "-" + column_id + ".csv");
		
		writer.writeLine("\"" + table_reader.getTable().getColumnNames()[column_id] + "\",\"" + table_reader.getTable().getColumnNames()[column_id_pk] + "\"");
		
		String value;
		for (int rid=0; rid<table_reader.getTable().getSize(); rid++){
			
			value = table_reader.getTable().getCell(rid,column_id);
			
			if (!values.contains(value)){
				writer.writeLine("\"" + value + "\",\"" + table_reader.getTable().getCell(rid,column_id_pk) + "\"");
				values.add(value);
			}//otherwise continue
			
		}
		
		writer.closeBuffer();
		
		
		
	}
	
	
	
	public static void main(String[] args) {
			
		
		try {
			new PrepareDatasetForT2K();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

	
	
	
	
	
	
	

}
