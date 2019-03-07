/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata.t2k;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import uk.turing.aida.tabulardata.reader.CVSReader;
import uk.turing.aida.tabulardata.t2d.T2DConfiguration;
import uk.turing.aida.tabulardata.utils.WriteFile;

/**
 * 
 * Support class for IJCAI tests
 * 
 * This class will prepare the extended T2D dataset for T2K. Driven by ground tructh for test.
 * T2K only matches entities in PK columns. Type assig
 * (1) Creating new tables for non-primary columns (they will represent something like an inverse relationship). 
 * (2) Format: Col1=Non-PKm Then rest of columns
 * (3) Remove duplicates from Non-PK column (T2K identifies as PK column the leftmost with most unique values). 
 * (4) New table name: tablename-columnid: 73242003_5_4847571983313033360_1-2
 * (5) GSformat: "86747932_0_7532457067740920052 1","Agent"
 *
 * @author ernesto
 * Created on 02 Feb 2019
 *
 */
public class PrepareDatasetForT2K {
	
	String folder_tables_original;
	String folder_tables_target;
	String gt_file;
	
	protected void setT2D() {
		
		folder_tables_original = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/test_tables_t2d_ijcai_original/";
		folder_tables_target = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/test_tables_t2d_ijcai/";
		gt_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/test_tables_t2d.csv";
		
	}
	
	protected void setLimaye() {
		
		folder_tables_original = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/test_tables_limaye_ijcai_original/";
		folder_tables_target = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/test_tables_limaye_ijcai/";
		gt_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/test_tables_limaye.csv";
		
	}


	protected void setWikipedia() {
	
		folder_tables_original = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/test_tables_wikipedia_ijcai_original/";
		folder_tables_target = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/test_tables_wikipedia_ijcai/";
		gt_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/test_tables_wikipedia.csv";
	
	}
	
	
	protected void setAIDA() {
		
		folder_tables_original = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/test_tables_aida_original/";
		folder_tables_target = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/T2K/matcher/test_tables_aida/";
		gt_file = "/home/ejimenez-ruiz/Documents/ATI_AIDA/TabularSemantics/WebTableMatching/test_tables_aida.csv";
	
	}
	
	
	
	
	public PrepareDatasetForT2K() throws NumberFormatException, IOException{
		
		//setT2D();
		//setLimaye();
		//setWikipedia();
		setAIDA();
		
		
		//Read GS which will lead the evaluation
		
		
		CVSReader gs_reader = new CVSReader(gt_file);
			
		String[] row;
		
		
		String table;
		String column;
		
		
		for (int rid=0; rid<gs_reader.getTable().getSize(); rid++){
			
			row = gs_reader.getTable().getRow(rid);
			
			table = row[0].split(" ")[0];
			column = row[0].split(" ")[1];
			
			//System.out.println(row[0] + " " + row[1] + "  " +  row[2] + " " + row[3]);
			File file = new File(folder_tables_original +table + ".csv");
			if (!file.exists()){
				System.err.println("The file '" + table + ".csv' does not exixt.");
				continue;
			}
			
			//We avoid PK columns
			//if (!column.equals("0")){
				createTableForNonPK(folder_tables_original, folder_tables_target, table, Integer.valueOf(column));
			//}
			/*else {
				File source = new File(folder_tables_original + table + ".csv");
				File dest = new File(folder_tables_target + table + "--c-" + column + ".csv");
				try {
				    FileUtils.copyFile(source, dest);
				} catch (IOException e) {
				    e.printStackTrace();
				}
			}*/
			
		}
		
	}
	
	
	
	private void createTableForNonPK(String path_tables_original, String path_tables_target, String table_id, int column_id) throws IOException{
		
		//Put given column in position 0 (left most) and remove duplicates so that T2K considers the column as primary key
		
		System.out.println(table_id);
		
		CVSReader table_reader = new CVSReader(path_tables_original + table_id + ".csv", true);
		
		Set<String> values = new HashSet<String>();
		
		WriteFile writer = new WriteFile(path_tables_target + table_id + "--c-" + column_id + ".csv");
		
	
		//Header
		String value = table_reader.getTable().getColumnNames()[column_id];
		String line;
		if (value!=null && !value.isEmpty()) {
			line="\"" + table_reader.getTable().getColumnNames()[column_id] + "\"";
			
			for (int i=0; i<table_reader.getTable().getColumnNames().length; i++) {
				if (i==column_id)
					continue;
				line += ",\"" + table_reader.getTable().getColumnNames()[i] + "\"";
			}
			writer.writeLine(line);
			values.add(value);
		}
		
		//Rest of lines		
		for (int rid=0; rid<table_reader.getTable().getSize(); rid++){
			
			if (table_reader.getTable().getRow(rid).length<=column_id)
				continue;
			
			line="";
			//System.out.println(table_reader.getTable().getRow(rid)[column_id]);
			value = table_reader.getTable().getCell(rid,column_id);
			
			if (value!=null && !values.contains(value) && !value.isEmpty()){//duplicates and empty
				
				line="\"" + value + "\"";
				
				
				for (int i=0; i<table_reader.getTable().getRow(rid).length; i++) {
					if (i==column_id)
						continue;
					line += ",\"" +  table_reader.getTable().getCell(rid,i) + "\"";
				}
				writer.writeLine(line);
				
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
