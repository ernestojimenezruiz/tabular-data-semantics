/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.kb.visitors;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

/**
 *
 * @author ernesto
 * Created on 31 Jul 2018
 *
 */
public class ClassRangeVisitor implements OWLClassExpressionVisitor {

	
	Set<OWLClass> types = new HashSet<OWLClass>();
	
	
	public void clearTypes(){
		types.clear();
	}
	
	public Set<OWLClass> getTypes(){
		return types;
	}
	
	public void visit(OWLClass ce) {
		clearTypes();
		
		types.add(ce);
		
	}

	public void visit(OWLObjectIntersectionOf ce) {
		//clear types
		clearTypes();
		
		for (OWLClassExpression ce2 : ce.getOperands()){
			if (!ce2.isAnonymous())
				types.add(ce2.asOWLClass());
		}
		
	}

	public void visit(OWLObjectUnionOf ce) {
		clearTypes();
		
		for (OWLClassExpression ce2 : ce.getOperands()){
			if (!ce2.isAnonymous())
				types.add(ce2.asOWLClass());
		}
		
	}

	public void visit(OWLObjectComplementOf ce) {
		clearTypes();
		
	}

	public void visit(OWLObjectSomeValuesFrom ce) {
		clearTypes();
		
	}

	public void visit(OWLObjectAllValuesFrom ce) {
		clearTypes();
		
	}

	public void visit(OWLObjectHasValue ce) {
		clearTypes();
		
	}

	public void visit(OWLObjectMinCardinality ce) {
		clearTypes();
		
	}

	public void visit(OWLObjectExactCardinality ce) {
		clearTypes();
		
	}

	public void visit(OWLObjectMaxCardinality ce) {
		clearTypes();
		
	}

	public void visit(OWLObjectHasSelf ce) {
		clearTypes();
		
	}

	public void visit(OWLObjectOneOf ce) {
		clearTypes();
		
	}

	public void visit(OWLDataSomeValuesFrom ce) {
		clearTypes();
		
	}

	public void visit(OWLDataAllValuesFrom ce) {
		clearTypes();
		
	}

	public void visit(OWLDataHasValue ce) {
		clearTypes();
		
	}

	public void visit(OWLDataMinCardinality ce) {
		clearTypes();
		
	}

	public void visit(OWLDataExactCardinality ce) {
		clearTypes();
		
	}

	public void visit(OWLDataMaxCardinality ce) {
		clearTypes();
		
	}

}
