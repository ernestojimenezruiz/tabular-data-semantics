/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.kb.dbpedia;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.turing.aida.kb.visitors.ClassRangeVisitor;
import uk.turing.aida.kb.visitors.DataRangeVisitor;
import uk.turing.aida.reasoner.HermiTAccess;
import uk.turing.aida.reasoner.ReasonerAccessImpl;


/**
 *
 * Class dealing with the loading of the ontology and basic access (e.g. range of property x)
 * 
 * @author ernesto
 * Created on 30 Jul 2018
 *
 */
public class DBpediaOntology {

	protected OWLDataFactory dataFactory;
	protected OWLOntologyManager manager_onto;
	protected OWLOntologyLoaderConfiguration loader_config;
	protected OWLOntology dbp_ontology;
	
	protected ReasonerAccessImpl reasoner;
	
	
	DBpediaConfiguration dbp_config = new DBpediaConfiguration();
	
	public DBpediaOntology(boolean load_annotations) throws IOException, OWLOntologyCreationException{
		dbp_config.loadConfiguration();
		loadOWLOntology(load_annotations);
	}
	
	

	
	protected void loadOWLOntology(boolean load_annotations) throws OWLOntologyCreationException{		
		
		
		manager_onto = OWLManager.createConcurrentOWLOntologyManager();
		dataFactory=manager_onto.getOWLDataFactory();
		
		try {
			
			//Deprecated
			//managerOnto.setMissingImportsHandlingStrategy();
			
			manager_onto = OWLManager.createOWLOntologyManager();
	        OWLOntologyLoaderConfiguration loader_config = new OWLOntologyLoaderConfiguration();
	        loader_config = loader_config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
	        //TODO we do not load annotattions
	        loader_config = loader_config.setLoadAnnotationAxioms(load_annotations);
	     	manager_onto.setOntologyLoaderConfiguration(loader_config);   
	        
	     	
			dbp_ontology = manager_onto.loadOntology(IRI.create(dbp_config.uri_ontology));
			
	        
			//Init reasoner
			reasoner = new HermiTAccess(manager_onto, dbp_ontology, false);
			
			
	        //System.out.println("DBpedia ontology axioms: " + dbp_ontology.getAxiomCount());
	        
	        
			
		}
		catch(Exception e){
			System.err.println("Error loading OWL ontology: " + e.getMessage());
			//e.printStackTrace();
			throw new OWLOntologyCreationException();
		}
	}
	
	
	
	public void classifyOntology() throws Exception{

		if (!reasoner.isOntologyClassified())
			reasoner.classifyOntology(true, true);
		
		
		
	}
	
	
	
	
	//Problem if more than one occurrence
	public OWLEntity getOWLEntity(IRI entityIRI){
		
		if (dbp_ontology.getEntitiesInSignature(entityIRI).isEmpty()){
			System.err.println("No correspondences in ontology for: " + entityIRI);
			return null;
		}
		else if (dbp_ontology.getEntitiesInSignature(entityIRI).size()>1){
			
			//for (OWLEntity ent : dbp_ontology.getEntitiesInSignature(entityIRI)){
			//	System.out.println(ent.getEntityType() + " " + ent.isOWLClass() + " " + ent.isOWLDataProperty() + " " + ent.isOWLObjectProperty());
			//}
			
			System.err.println("More than one correspondence in ontology for: " + entityIRI + ":  " + dbp_ontology.getEntitiesInSignature(entityIRI));
			return null;
		}
		
		for (OWLEntity ent : dbp_ontology.getEntitiesInSignature(entityIRI)){
			return ent;
			
		}
		
		return null;
		
		
	}
	
	
	
	/**
	 * Get entailed class types
	 * @param oprop
	 * @return
	 * @throws Exception 
	 */
	public Set<OWLClass> getSuperClasses(OWLClass cls, boolean direct) throws Exception{
	
		if (!reasoner.isOntologyClassified())
			reasoner.classifyOntology(true, true);
		
		return reasoner.getReasoner().getSuperClasses(cls, direct).getFlattened();
		
	}
	
	
		
	
	
	/**
	 * Get entailed class types
	 * @param oprop
	 * @return
	 * @throws Exception 
	 */
	public Set<OWLClass> getEntailedRangeClassesObjectProperty(OWLObjectProperty oprop, boolean direct) throws Exception{
	
		if (!reasoner.isOntologyClassified())
			reasoner.classifyOntology(true, true);
		
		return reasoner.getReasoner().getObjectPropertyRanges(oprop, direct).getFlattened();
		
	}
	
	
	
	
	
	/**
	 * Direct class types
	 * @param oprop
	 * @return
	 */
	public Set<OWLClass> getExplicitRangeClassesObjectProperty(OWLObjectProperty oprop){
		
		Set<OWLClass> types = new HashSet<OWLClass>();
		
		ClassRangeVisitor rangeVisitor = new ClassRangeVisitor();
		
		for (OWLObjectPropertyRangeAxiom ax: dbp_ontology.getObjectPropertyRangeAxioms(oprop)){
			ax.getRange().accept(rangeVisitor);
			
			types.addAll(rangeVisitor.getTypes());
			
		}
		
		if (types.isEmpty())
			types.add(dataFactory.getOWLThing());
		
		return types;
		
	}
	
	
	public Set<OWLDatatype> getRangeDatatypesDataProperty(OWLDataProperty dprop){
		
		Set<OWLDatatype> datatypes = new HashSet<OWLDatatype>();
		
		DataRangeVisitor dataRangeVisitor = new DataRangeVisitor();
		
		//More complex as it may tell you an interval too!
		for (OWLDataPropertyRangeAxiom ax: dbp_ontology.getDataPropertyRangeAxioms(dprop)){
			ax.getRange().accept(dataRangeVisitor);
			
			datatypes.addAll(dataRangeVisitor.getDatatypes());
			
		}
		
		if (datatypes.isEmpty())
			datatypes.add(dataFactory.getTopDatatype());
		
		return datatypes;
		
	}
	
	
	
	
	public static void main(String[] args){
		try {
			DBpediaOntology dbo = new DBpediaOntology(true);
			
			System.out.println(dbo.dataFactory.getTopDatatype());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
}
