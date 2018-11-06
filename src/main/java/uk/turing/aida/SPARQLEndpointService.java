package uk.turing.aida;

import java.util.HashSet;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public abstract class SPARQLEndpointService {
		

	public abstract String getENDPOINT();
	
	
	//Query to retrieve predicates and objects for subject
	protected abstract String createSPARQLQueryForSubject(String uri_subject);
	
	//Query to retrieve predicates and subjects for object
	protected abstract String createSPARQLQueryForObject(String uri_subject);
	
	
	protected abstract String craeteSPARQLQuery_TypeObjectsForPredicate(String uri_predicate);
	
	
	protected abstract String createSPARQLQuery_TypesForSubject(String uri_resource);
	
	
	protected abstract String createSPARQLQuery_AllTypesForSubject(String uri_resource);
	
	
	

	protected abstract String createSPARQLQuery_AllSuperClassesForSubject(String uri_resource);
	
	
	
	
	protected String createSPARQLQuery_LabelForSubject(String uri_subject){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT DISTINCT ?l \n"
				+ "WHERE { <" + uri_subject + "> <http://www.w3.org/2000/01/rdf-schema#label> ?l . "
				+ "}";
		
	}
	
	
	public Set<String> getTypesOfObjectForPredicate(String uri_predicate) throws Exception{
		
		return getValuesForQuery(
				craeteSPARQLQuery_TypeObjectsForPredicate(uri_predicate));
		
	}
	
	
	
	


	public Set<String> getLabelsForSubject(String uri_resource) throws Exception{
		
		return getValuesForQuery(
				createSPARQLQuery_LabelForSubject(uri_resource));
		
		
	}
	
	
	public Set<String> getTypesForSubject(String uri_resource) throws Exception{
		
		return getValuesForQuery(
				createSPARQLQuery_TypesForSubject(uri_resource));
		
		
	}
	
	
	
	public Set<String> getAllTypesForSubject(String uri_resource) throws Exception{
		
		return getValuesForQuery(
				createSPARQLQuery_AllTypesForSubject(uri_resource));
		
		
	}
	
	
	public Set<String> getAllSuperClassesForSubject(String uri_resource) throws Exception{
		
		return getValuesForQuery(
				createSPARQLQuery_AllSuperClassesForSubject(uri_resource));
		
		
	}
	
	
	
	
	
	public Set<String> getValuesForQuery(String query) throws Exception{
		
		
		Set<String> types = new HashSet<String>();
		
		
		//Query to retrieve predicates and objects for subject
		Query q = QueryFactory.create(query);

		//System.out.println(query);
		
		
		//In some cases it fails the connection. Try several times
		boolean success=false;
		int attempts=0;
		
		while(!success && attempts<3){	
			
			attempts++;
		
			QueryExecution qe = QueryExecutionFactory.sparqlService(getENDPOINT(), q); 
			try {
				ResultSet res = qe.execSelect();
				
				while( res.hasNext()) {
					
					QuerySolution soln = res.next();				
					RDFNode object_type = soln.get("?t");
					//System.out.println(""+object_type);
					
					//TODO: no rdf:label?
					//System.out.println(object_type);
					if (object_type!=null)
						types.add(object_type.toString());
					
				}
				
				success=true;
			    
			} 
			catch (Exception e) {
				System.out.println("Error accessing " + getENDPOINT() + " with  SPARQL:\n" + query + "  Attempt: " + attempts);
				e.printStackTrace();
				TimeUnit.MINUTES.sleep(1); //wait 1 minute and try again
			}
			finally {
				qe.close();
			}
			
		}
		if (!success)
			throw new Exception(); 
		else if (attempts>1)
			System.out.println("SUCCESS accessing SPARQL\n: " + query + "  Attempt: " + attempts);
		
		return types;
		
	}
	
	
	
	
	//TimeUnit.SECONDS.sleep(1);
	
	
	
	
	
	
	
	
	public Set<Statement> getTriplesForSubject(String uri_subject) throws Exception{
		
		Set<Statement> triples = new HashSet<Statement>();
		
		Model model = ModelFactory.createDefaultModel();
		
		//subject
		Resource subject = model.createResource(uri_subject);
		
		
		//Query to retrieve predicates and objects for subject
		String query = createSPARQLQueryForSubject(uri_subject);
		Query q = QueryFactory.create(query);
		
		
		//In some cases it fails the connection. Try several times
		boolean success=false;
		int attempts=0;

		
		while(!success && attempts<3){	
			
			attempts++;
		
			QueryExecution qe = QueryExecutionFactory.sparqlService(getENDPOINT(), q); 
			try {
				ResultSet res = qe.execSelect();
								
				while( res.hasNext()) {
					
					QuerySolution soln = res.next();
					RDFNode predicate = soln.get("?p");
					RDFNode object = soln.get("?o");
					System.out.println(""+predicate + " " + object);
					
					triples.add(model.createStatement(subject, model.createProperty(predicate.toString()), object));
				}
				
				success=true;
			    
			} 
			catch (Exception e){
				System.out.println("Error accessing " + getENDPOINT() + " with  SPARQL:\n" + query + "  Attempt: " + attempts);
				TimeUnit.MINUTES.sleep(1); //wait 1 minute and try again			    
			} 
			finally {
				qe.close();
			}
		}
		
		if (!success)
			throw new Exception(); 
		else if (attempts>1)
			System.out.println("SUCCESS accessing SPARQL\n: " + query + "  Attempt: " + attempts);
		
		
		
		return triples;
		
	}
	
	
	public Set<Statement> getTriplesForObject(String uri_object) throws Exception{
		
		Set<Statement> triples = new HashSet<Statement>();
		
		Model model = ModelFactory.createDefaultModel();
		
		//subject
		Resource object = model.createResource(uri_object);
		
		
		//Query to retrieve predicates and subjects for object
		String query = createSPARQLQueryForObject(uri_object);
		Query q = QueryFactory.create(query);
		
		
		//In some cases it fails the connection. Try several times
		boolean success=false;
		int attempts=0;

		
		while(!success && attempts<3){	
			
			attempts++;
		
			QueryExecution qe = QueryExecutionFactory.sparqlService(getENDPOINT(), q); 
			try {
				ResultSet res = qe.execSelect();
				while( res.hasNext()) {
					
					QuerySolution soln = res.next();
					RDFNode subject = soln.get("?s");
					RDFNode predicate = soln.get("?p");					
					//System.out.println(""+predicate + " " + object);
					
					triples.add(model.createStatement(subject.asResource(), model.createProperty(predicate.toString()), object));
				}
				
				success=true;
			    
			} 
			catch (Exception e){
				System.out.println("Error accessing " + getENDPOINT() + " with  SPARQL:\n" + query + "  Attempt: " + attempts);
				TimeUnit.MINUTES.sleep(1); //wait 1 minute and try again			    
			} 
			finally {
				qe.close();
			}
		}
		
		if (!success)
			throw new Exception(); 
		else if (attempts>1)
			System.out.println("SUCCESS accessing SPARQL\n: " + query + "  Attempt: " + attempts);
		
		
		
		return triples;
		
	}
	
	
}
