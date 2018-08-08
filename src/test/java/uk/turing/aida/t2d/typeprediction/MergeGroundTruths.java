/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.t2d.typeprediction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import uk.turing.aida.tabulardata.reader.CVSReader;
import uk.turing.aida.tabulardata.t2d.T2DConfiguration;
import uk.turing.aida.tabulardata.utils.WriteFile;
import uk.turing.aida.typeprediction.ColumnType;

/**
 * Aux class to merge type prediction ground truths 
 *
 * @author ernesto
 * Created on 8 Aug 2018
 *
 */
public class MergeGroundTruths {
	
	
	T2DConfiguration config = new T2DConfiguration();

	
	String file1;
	String file2;	
	String fileout;
	
	
	CVSReader gs_reader1;	
	CVSReader gs_reader2;
	
	WriteFile writer;
	
	
	MergeGroundTruths() throws Exception{
		
		config.loadConfiguration();
		
		
		file1 = config.t2d_path + "colulmn_class_types_revised.csv";
		
		file2 = config.t2d_path + "column_gt_extend_fg.csv";
		
		fileout = config.t2d_path + "colulmn_class_types_revised_Aug8.csv";
		
		Map<String, Map<Integer, ColumnType>> map = new HashMap<String, Map<Integer, ColumnType>>();
		
		gs_reader1 = new CVSReader(file1);	
		gs_reader2 = new CVSReader(file2);
		
		
		for (int i=0; i<gs_reader2.getTable().getSize(); i++){
			//gs_reader2.getTable().getRow(i);
			///http://dbpedia.org/ontology/
			
			String[] row = gs_reader2.getTable().getRow(i);
			
			String[] table_column=row[0].split(" ");
			
			String table = table_column[0];
			Integer column = Integer.valueOf(table_column[1]);
			
			
			ColumnType ct;
			
			if (!map.containsKey(table)){
				map.put(table,  new HashMap<Integer, ColumnType>());
			}
			if (!map.get(table).containsKey(column)){
				map.get(table).put(column, new ColumnType(table, column));
			}
			
			ct = map.get(table).get(column);
			
			
			for (int j=1; j<row.length; j++){
				ct.addType(row[j]);
			}
			
			
			map.get(table).put(column, ct);
			
			
			
		}
		
		
		for (int i=0; i<gs_reader1.getTable().getSize(); i++){
			
			String table = gs_reader1.getTable().getRow(i)[0];
			Integer column = Integer.valueOf(gs_reader1.getTable().getRow(i)[1]);
			Boolean is_pc = Boolean.valueOf(gs_reader1.getTable().getRow(i)[2]);
			String type = gs_reader1.getTable().getRow(i)[3];
			
			ColumnType ct;
			
			if (!map.containsKey(table)){
				//map.put(table,  new HashMap<Integer, ColumnType>());
				continue;
			}
			if (!map.get(table).containsKey(column)){
				//map.get(table).put(column, new ColumnType(table, column));
				continue;
			}
			
			ct = map.get(table).get(column);
			ct.setPrimaryColumn(is_pc);		
			ct.addType(type.replaceAll("http://dbpedia.org/ontology/", ""));
			
			map.get(table).put(column, ct);
			
			
		}
		
		
		
		
		writer = new WriteFile(fileout);
		
		for (String t : map.keySet()){
			for (Integer c: map.get(t).keySet()){
				writer.writeLine(map.get(t).get(c).toStringCSV());
			}
		}
		
		writer.closeBuffer();
		
	}
	
	
	
	
	public static void main(String[] args){
		try {
			new MergeGroundTruths();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
