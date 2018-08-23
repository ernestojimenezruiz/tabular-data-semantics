/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.t2d.typeprediction;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import uk.turing.aida.kb.dbpedia.DBpediaEndpoint;
import uk.turing.aida.tabulardata.reader.CVSReader;
import uk.turing.aida.tabulardata.t2d.T2DConfiguration;
import uk.turing.aida.tabulardata.utils.WriteFile;


/**
 *
 * @author ernesto
 * Created on 22 Aug 2018
 *
 */
public class CreateTypeResultsForT2K {

	//T2K-Format results file 
	//"capcom","http://dbpedia.org/resource/Capcom","1.0","false",,,"73242003_5_4847571983313033360_1.csv","FP","FN"
	//"d3p","","0.0","true","","","73242003_5_4847571983313033360_1.csv","TN","TN"
	//
	//GS format: "86747932_0_7532457067740920052","1","True","Agent","Company","Organisation","Airline"
	//Aimed format: "86747932_0_7532457067740920052","1","Agent","0.85" (one type per line)
	
	
	T2DConfiguration config = new T2DConfiguration();
	
	
	public CreateTypeResultsForT2K(String input_file, String output_file) throws Exception{
		
		config.loadConfiguration();
		
		CVSReader reader = new CVSReader(input_file);
		CVSReader gsreader = new CVSReader(config.t2d_path+config.extended_type_gs_file);
		
		
		WriteFile writer = new WriteFile(output_file);
		
		DBpediaEndpoint dbend = new DBpediaEndpoint();
		
		
		
		TreeMap<String, Double> hitsfortypes = new TreeMap<String, Double>();
		
		
		String[] row;
		
		
		//ID for primarykey columns
		Map<String, String> tableToIdPK = new HashMap<String, String>();
		for (int r=0; r<gsreader.getTable().getSize(); r++){
			row=gsreader.getTable().getRow(r);
			if (Boolean.valueOf(row[2])){
				tableToIdPK.put(row[0], row[1]);
			}
		}
		
		
		
		
		
		
		
		//For first row
		String table_id=reader.getTable().getRow(0)[6];
		
		int num_entities_table=0;
		
		//table id: 73242003_5_4847571983313033360-2  -> -2 is the column id, 73242003_5_4847571983313033360 is the original table id
		String original_table_id; //without the addition of column for T2K
		String column_id;
		
		
		
		boolean last_row;
		for (int rid=0; rid<=reader.getTable().getSize(); rid++){
		
			last_row=false;
			if (rid==reader.getTable().getSize()){
				row=reader.getTable().getRow(0); //read first one
				last_row=true;
			}
			else{
				row=reader.getTable().getRow(rid);
			}
			
			//new table row or last row
			if (last_row || !table_id.equals(row[6])){
								
				//store new format
				
				table_id = table_id.replaceAll(".csv", "");
				if (table_id.contains("-")){//new tables created for non primary keys
					original_table_id = table_id.split("-")[0];
					column_id = table_id.split("-")[1];
				}
				else{//original table ids
					original_table_id=table_id;
					column_id = tableToIdPK.get(original_table_id); //from gs
				}
				
				//Get % of occurrerence of a type instead of number of votes
				double percentage_votes = 0.0;
				for (String cls: hitsfortypes.keySet()){
					percentage_votes = (hitsfortypes.get(cls)/(double)num_entities_table);
					percentage_votes = (double)Math.round(percentage_votes * 1000d) / 1000d;
					hitsfortypes.put(cls, percentage_votes);
				}
				
				//Probably not the best solution but a clean one
				TreeMap<String, Double> sortedhitsfortypes = new TreeMap<String, Double>(new ValueComparator(hitsfortypes));
				sortedhitsfortypes.putAll(hitsfortypes);
				//for (String key: sortedhitsfortypes.navigableKeySet()){
				//	System.out.println(key + "  " + sortedhitsfortypes.get(key));
				//}
				//@deprecated Top types (not filtered here)
				for (String key: sortedhitsfortypes.descendingKeySet()){
					//print line
					//"86747932_0_7532457067740920052","1","Agent","0.85" (one type per line)
					writer.writeLine("\""+original_table_id+"\",\"" + column_id + "\",\"" + key.replaceAll(dbpedia_onto_ns_uri, "") + "\",\"" + sortedhitsfortypes.get(key) + "\"");	
				}
				
				//Set new table and clear values
				table_id=row[6];				
				hitsfortypes.clear();
				num_entities_table=0;	
			}
			
			num_entities_table++;
			
			//no suggested/mapped entity (like d3p)
			if (row[1].equals(""))
				continue;
			
			
			//get types from endpoints and add hits
			for (String cls: dbend.getTypesForSubject(row[1])){
				if (!filter(cls)){
					if (!hitsfortypes.containsKey(cls))
						hitsfortypes.put(cls, 0.0);
					
					hitsfortypes.put(cls, hitsfortypes.get(cls)+1.0);
				}
			}
			
		}
		
		
		
		writer.closeBuffer();
		
	}
	
	
	protected String dbpedia_onto_ns_uri = "http://dbpedia.org/ontology/";
	
	protected boolean filter(String cls){
		//Keep only types from dbpedia
		if (cls.startsWith(dbpedia_onto_ns_uri))
			return false;
		
		return true;
	}
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args)  {
		
		
		String path="/home/ernesto/Documents/AIDA_Alan_Turing/WebTableMatching/T2K/matcher/";
		
		
		try {
			new CreateTypeResultsForT2K(path +"instances.csv", path+"t2k_col_classes.csv");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	protected class ValueComparator implements Comparator<String> {

	    private Map<String, Double> map;

	    public ValueComparator(Map<String, Double> map) {
	        this.map = map;
	    }

	    public int compare(String a, String b) {
	        if (map.get(a).doubleValue()>map.get(b).doubleValue())
	        	return 1;
	        if (map.get(a).doubleValue()==map.get(b).doubleValue()) //Very important in case of same percentage 
	        	return b.compareTo(a);
	        
	        return -1;
	    }
	}
	
	
	
	
	
	
	
	
	
}
