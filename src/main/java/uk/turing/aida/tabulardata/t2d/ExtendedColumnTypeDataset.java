/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata.t2d;

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
import uk.turing.aida.tabulardata.utils.Namespace;
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
	List<String> type_tables = new ArrayList<String>();
	
	public ExtendedColumnTypeDataset() throws IOException, OWLOntologyCreationException{
		
		config.loadConfiguration();
		
		readTableFilesWithType();
		
		extractColumnTypes();
		
	}
	
	//1. Read GS_types file, for each file name in col1 then search file in "property folder"
	private void readTableFilesWithType() throws IOException{
		
		//Format 
		//"68779923_2_1000046510804975562.tar.gz","Country","http://dbpedia.org/ontology/Country"

		CVSReader cvs_reader = new CVSReader(config.t2d_path + config.type_gs_file);
		
		for (int i=0; i<cvs_reader.getTable().getSize(); i++){
			
			files.add(Utils.removeQuotes(cvs_reader.getTable().getRow(i)[0]).replaceAll(".tar.gz", ""));
			type_tables.add(Utils.removeQuotes(cvs_reader.getTable().getRow(i)[2]));
		}		
		
		System.out.println(Utils.removeQuotes(cvs_reader.getTable().getRow(0)[0]).replaceAll(".tar.gz", ""));
		//System.out.println(cvs_reader.getTable().getRow(0)[0].replaceAll(".tar.gz", ""));
		System.out.println(Utils.removeQuotes(cvs_reader.getTable().getRow(0)[2]).replaceAll(".tar.gz", ""));
		
	}
	
	
	//2. Get column type from dbpedia ontology. 
	//Type of primary key given by gold standard (list with Types)
	private void extractColumnTypes() throws IOException, OWLOntologyCreationException{
		
		String output_path = System.getProperty("user.dir") + "/output/";
		String column_types_path = output_path + "column_types/";
		
		String column_annotation_file;
		
		
		//TODO ignore annotations
		DBpediaOntology dbo = new DBpediaOntology(false);
		
		
		
		WriteFile global_writer = new WriteFile(output_path+"column_types.csv");
		WriteFile global_writer_only_classes = new WriteFile(output_path+"column_types_only_classes.csv");
		
		
		for (int i=0; i<files.size(); i++){
		//for (int i=0; i<5; i++){
			
			column_annotation_file = config.t2d_path + config.columns_annotations_folder + files.get(i) + ".csv";
			
			System.out.println(column_annotation_file);
			
			CVSReader cvs_reader = new CVSReader(column_annotation_file);
			
			if (cvs_reader.getTable().isEmpty()){
				System.out.println("\tEmpty");
				continue;
			}
			
			
			WriteFile local_writer = new WriteFile(column_types_path + files.get(i) + ".csv");
			
			
			for (int j=0; j<cvs_reader.getTable().getSize(); j++){
				//File format: "http://dbpedia.org/ontology/birthDate","geb.","False","2"
				
				
				//Get if primary key: rdfs:label or column type=true
				//Use table type then.
				
				
				if(cvs_reader.getTable().getRow(j).length<4){
					System.out.println("\tWrong row");
					continue;
				}
				
				//In case of primary key, the rdfs:label is given. Use then teh type of the table instead of the type of the range of the property
				boolean isPrimaryKey = Utils.removeQuotes(cvs_reader.getTable().getRow(j)[2]).equals("True") || Utils.removeQuotes(cvs_reader.getTable().getRow(j)[0]).equals(Namespace.RDFS_LABEL);
				
				

				String types_str="";
				boolean object_range=false;
				
				
				if (!isPrimaryKey){
					String uri_dbpedia = Utils.removeQuotes(cvs_reader.getTable().getRow(j)[0]);
					
					
					//No correspondence to dbpedia
					OWLEntity ent = dbo.getOWLEntity(IRI.create(uri_dbpedia));
					if (ent==null)
						continue;
					

					if (ent.isOWLDataProperty()){
						for (OWLDatatype datatype : dbo.getRangeDatatypesDataProperty(ent.asOWLDataProperty())){
							types_str+=datatype.toStringID()+"|";
						}
						object_range=false;
					}
					else if (ent.isOWLObjectProperty()){
						for (OWLClass cls : dbo.getExplicitRangeClassesObjectProperty(ent.asOWLObjectProperty())){
							types_str+=cls.toStringID()+"|";
						}
						object_range=true;
					}
					
					//No types in dbpedia
					if (types_str.equals("")){
						System.err.println("No range types for " + uri_dbpedia);
						continue;
					}
					else{ //remove last "|"
						types_str = types_str.substring(0, types_str.length()-1);
					}
				}
				else{
					//We use the type of the table
					types_str+=type_tables.get(i);
					object_range=true;
				}
				
				
				//Only meaningfull class types
				if (object_range && !types_str.contains("http://www.w3.org/2002/07/owl#Thing"))
					global_writer_only_classes.writeLine("\""+files.get(i) + "\",\"" + cvs_reader.getTable().getRow(j)[3] + "\",\"" + cvs_reader.getTable().getRow(j)[2] + "\",\"" + types_str + "\"");
				
				global_writer.writeLine("\""+files.get(i) + "\",\"" + cvs_reader.getTable().getRow(j)[3] + "\",\"" + cvs_reader.getTable().getRow(j)[2] + "\",\"" + types_str + "\"");
				
				//
				local_writer.writeLine("\""+cvs_reader.getTable().getRow(j)[3]+ "\",\"" + cvs_reader.getTable().getRow(j)[2] + "\",\"" + types_str + "\"");
			
			}
			
			local_writer.closeBuffer();
			
		}
		
		global_writer.closeBuffer();
		global_writer_only_classes.closeBuffer();
		
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
