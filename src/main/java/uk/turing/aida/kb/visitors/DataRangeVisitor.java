package uk.turing.aida.kb.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataRangeVisitor;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;

import uk.turing.aida.tabulardata.utils.Namespace;


/**
 * Visitor to capture enumerations of values and min-max datatype restrictions
 * @author ernesto
 *
 */
public class DataRangeVisitor implements OWLDataRangeVisitor{

	private boolean isEnumeration = false;
	private boolean isDatatypeRestriction = false;
	
	
	private List<String> range_values = new ArrayList<String>();
	
	//private OWLDatatype datatype;
	
	private Set<OWLDatatype> datatypes = new HashSet<OWLDatatype>();
	
	
	public List<String> getRangeValues(){
		return range_values;
	}
	
	//public OWLDatatype getDatatype(){
	//	return datatype;
	//}
	
	public Set<OWLDatatype> getDatatypes(){
		return datatypes;
	}
	
	public void clearDatatypes(){
		datatypes.clear();
	}
	
	
	
	public void visit(OWLDataOneOf range) {
	
		setEnumerationRange(true);
		setDatatypeRestrictionRange(false);
		clearDatatypes();
		
		range_values.clear();
		
		boolean same_type = true;
		
		String datatypeString;
		
		datatypeString = "";
		
		for (OWLLiteral lit : range.getValues()){
			range_values.add(lit.getLiteral().toString());
			if (!datatypeString.equals("") && !datatypeString.equals(lit.getDatatype().toStringID()))
				 same_type = false;
			
			datatypeString = lit.getDatatype().toStringID();
			
			datatypes.add(lit.getDatatype());
			
			
		}
		
		//Problems with Hermit creating Constant terms if datatype is outside OWL 2
		if (!same_type || datatypeString.equals(Namespace.rdf_plainliteral) || datatypeString.equals(Namespace.rdfs_literal)){
			datatypeString = Namespace.XSD_STRING;
			//TODO removel all datatypes and keep string?
			
			
		}
		
	}
	
	
	
	public void visit(OWLDatatypeRestriction range) {
		
		setEnumerationRange(false);
		setDatatypeRestrictionRange(true);
		clearDatatypes();
		
		try {
			
			String value_facet;
			String symbol_facet;
			String text_facet;
			
			//datatypeString = range.getDatatype().toStringID();
			datatypes.add(range.getDatatype());
			
			range_values.clear();
			
			
			
			for (OWLFacetRestriction facet : range.getFacetRestrictions()){
				
				value_facet = facet.getFacetValue().getLiteral().toString();
				symbol_facet = facet.getFacet().getSymbolicForm();
				text_facet = facet.getFacet().getShortForm();
				
				//For convenience we store symbol, text_form and number in different positions
				//e.g.: ">" and "1"
				range_values.add(symbol_facet);
				range_values.add(text_facet);
				range_values.add(value_facet);
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public void visit(OWLDatatype datatype) {
		setEnumerationRange(false);
		setDatatypeRestrictionRange(false);
		clearDatatypes();
		
		datatypes.add(datatype);
	}
	

	
	public void visit(OWLDataComplementOf range) {
		setEnumerationRange(false);
		setDatatypeRestrictionRange(false);
		clearDatatypes();
		
		
	}

	
	public void visit(OWLDataIntersectionOf range) {
		setEnumerationRange(false);
		setDatatypeRestrictionRange(false);
		clearDatatypes();
		
		
		for (OWLDataRange dr : range.getOperands()){
			if (dr.isDatatype())
				datatypes.add(dr.asOWLDatatype());
		}
		
		
	}

	public void visit(OWLDataUnionOf range) {
		setEnumerationRange(false);
		setDatatypeRestrictionRange(false);
		clearDatatypes();
		
		for (OWLDataRange dr : range.getOperands()){
			if (dr.isDatatype())
				datatypes.add(dr.asOWLDatatype());
		}
	}


	public boolean isEnumerationRange() {
		return isEnumeration;
	}


	private void setEnumerationRange(boolean isEnumeration) {
		this.isEnumeration = isEnumeration;
	}


	public boolean isDatatypeRestrictionRange() {
		return isDatatypeRestriction;
	}


	private void setDatatypeRestrictionRange(boolean isDatatypeRestriction) {
		this.isDatatypeRestriction = isDatatypeRestriction;
	}



}
