/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata.t2d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.turing.aida.kb.dbpedia.DBpediaOntology;
import uk.turing.aida.tabulardata.reader.CVSReader;
import uk.turing.aida.tabulardata.utils.Utils;
import uk.turing.aida.tabulardata.utils.WriteFile;

/**
 *
 * Extension of the T2D dataset http://webdatacommons.org/webtables/goldstandardV2.html
 * to provide the types of other columns apart from primary keys.
 * For example in the table "Country", we have the column "capital", "government", "languages", "date"
 * for which dbpedia can also give us the type
 * @author ernesto
 * Created on 30 Jul 2018
 *
 */
public class ExtendedColumnTypeDataset {
	
	T2DConfiguration config = new T2DConfiguration();
	
	List<String> files = new ArrayList<String>();
	List<String> types = new ArrayList<String>();
	
	public ExtendedColumnTypeDataset() throws IOException, OWLOntologyCreationException{
		
		config.loadConfiguration();
		
		readTableFilesWithType();
		
		extractColumnTypes();
		
	}
	
	//1. Read GS_types file, for each file name in col1 then search file in "property folder"
	private void readTableFilesWithType() throws FileNotFoundException{
		
		
		//Format 
		//"68779923_2_1000046510804975562.tar.gz","Country","http://dbpedia.org/ontology/Country"

		CVSReader cvs_reader = new CVSReader(config.t2d_path + config.type_gs_file);
		
		for (int i=0; i<cvs_reader.getTable().getSize(); i++){
			
			files.add(Utils.removeQuotes(cvs_reader.getTable().getRow(i)[0]).replaceAll(".tar.gz", ""));
			types.add(Utils.removeQuotes(cvs_reader.getTable().getRow(i)[2]));
		}		
		
		System.out.println(Utils.removeQuotes(cvs_reader.getTable().getRow(0)[0]).replaceAll(".tar.gz", ""));
		System.out.println(Utils.removeQuotes(cvs_reader.getTable().getRow(0)[2]).replaceAll(".tar.gz", ""));
		
	}
	
	
	//2. Get column type from dbpedia ontology. 
	//Type of primary key given by gold standard (list with Types)
	private void extractColumnTypes() throws IOException, OWLOntologyCreationException{
		
		String output_path = System.getProperty("user.dir") + "/output/";
		String column_types_path = output_path + "column_types/";
		
		String column_annotation_file;
		
		
		DBpediaOntology dbo = new DBpediaOntology();
		
		
		
		WriteFile global_writer = new WriteFile(output_path+"column_types.csv");
		
		
		for (String file_name : files){
			
			WriteFile local_writer = new WriteFile(column_types_path + file_name + ".csv");
			
			
			column_annotation_file = config.t2d_path + config.columns_annotations_folder + file_name + ".csv";
			
			//System.out.println(column_annotation_file);
			
			CVSReader cvs_reader = new CVSReader(column_annotation_file);
			
			for (int i=0; i<cvs_reader.getTable().getSize(); i++){
				String uri_dbpedia = Utils.removeQuotes(cvs_reader.getTable().getRow(i)[0]);
				
				//No correspondence to dbpedia
				OWLEntity ent = dbo.getOWLEntity(IRI.create(uri_dbpedia));
				if (ent==null)
					continue;
				
				String types="";
				if (ent.isOWLDataProperty()){
					for (OWLDatatype datatype : dbo.getRangeDatatypesDataProperty(ent.asOWLDataProperty())){
						types+=datatype.toStringID()+"|";
					}
				}
				else if (ent.isOWLObjectProperty()){
					for (OWLClass cls : dbo.getRangeClassesObjectProperty(ent.asOWLObjectProperty())){
						types+=cls.toStringID()+"|";
					}
				}
				
				//No types in dbpedia
				if (types.equals("")){
					System.err.println("No range types for " + uri_dbpedia);
					continue;
				}
				
				
				//TODO get if primary key!!!: rdfs:label or column type =true
				//Use table type then....
				
				global_writer.writeLine("\""+file_name + "\"," + cvs_reader.getTable().getRow(i)[3] + ",\"" + types + "\"");
				
				//
				local_writer.writeLine(cvs_reader.getTable().getRow(i)[3] + ",\"" + types + "\"");
			
			}
			
			//read csv file
			
			//get column information
			//"http://dbpedia.org/ontology/birthDate","geb.","False","2"
			//"http://dbpedia.org/ontology/deathDate","gest.","False","3"
			//"http://www.w3.org/2000/01/rdf-schema#label","name","True","0"
			
			
			local_writer.closeBuffer();
			
		}
		
		
		global_writer.closeBuffer();
		
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		try {
			new ExtendedColumnTypeDataset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	

}
