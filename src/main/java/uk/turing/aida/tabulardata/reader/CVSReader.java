/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata.reader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;
import uk.turing.aida.tabulardata.Table;
import uk.turing.aida.tabulardata.utils.ReadFile;

/**
 * Using OpenCSV
 * @author ernesto
 *
 * Created on 24 Jul 2018
 * 
 */
public class CVSReader {

	//If very large tables, it may be necessary to divide or partially read the table.
	
	Table table = new Table();
	
	
	public CVSReader (String fstring) throws IOException{
		this(fstring, false);
		
	}
	
	
	public CVSReader (String fstring, boolean withTableHeader) throws IOException{
		readCSVFile(fstring, withTableHeader);
		
	}
	
	
	
	
	protected void readCSVFile(String fstring, boolean withTableHeader) throws IOException{
		
		//Clear table 
		table.setEmptyTable();
				
		 //CSVIterator iterator = new CSVIterator(new CSVReader(new FileReader(fstring)));
		 CSVReader reader = new CSVReader(new FileReader(fstring));
		 String [] nextRecord;//nextRecord
		  while ((nextRecord = reader.readNext()) != null) {
		 //for(String[] nextLine : iterator) {
			 // nextLine[] is an array of values from the line
		     //System.out.println(nextLine[0] + nextLine[1] + "etc...");
			  
			//header
			if (!table.hasColumnNames() && withTableHeader)
				table.addColumnNames(nextRecord);
			else
				table.addRow(nextRecord);
			  
		 }
		  
		  reader.close();
		
		 
	}
	
	/**
	 * Custom an incomplete csv reader
	 * @deprecated
	 */
	protected void readCVSFile(String fstring, boolean withTableHeader) throws FileNotFoundException{
		
		ReadFile reader = new ReadFile(fstring);
		
		String line;
				
		line=reader.readLine();		
				
		//Clear table
		table.setEmptyTable();
		
		while (line!=null) {
			
			if (line.indexOf(",")<0){
				System.out.println("Wrong line: " + line);
				line=reader.readLine();
				continue;
			}
			
			//header
			if (!table.hasColumnNames() && withTableHeader)
				table.addColumnNames(line.split(","));
			else
				table.addRow(line.split(","));
						
			
			line=reader.readLine();		
		}
	}
	
	
	
	public Table getTable(){
		return table;
	}
	
	
	
}
