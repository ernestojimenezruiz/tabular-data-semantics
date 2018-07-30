/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata.reader;

import java.io.FileNotFoundException;

import uk.turing.aida.tabulardata.Table;

/**
 *
 * @author ernesto
 * Created on 24 Jul 2018
 *
 */
public class CVSReader {

	//If very large tables, it may be necessary to divide or partially read the table.
	
	Table table = new Table();
	
	
	public CVSReader (String fstring) throws FileNotFoundException{
		this(fstring, false);
		
	}
	
	
	public CVSReader (String fstring, boolean withTableHeader) throws FileNotFoundException{
		readCVSFile(fstring, withTableHeader);
		
	}
	
	public void readCVSFile(String fstring, boolean withTableHeader) throws FileNotFoundException{
		
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
