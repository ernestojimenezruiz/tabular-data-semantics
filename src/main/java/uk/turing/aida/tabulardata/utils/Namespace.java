package uk.turing.aida.tabulardata.utils;

public class Namespace {
	
    public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String OWL_NS = "http://www.w3.org/2002/07/owl#";
    public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
    public static final String SWRL_NS = "http://www.w3.org/2003/11/swrl#";
    public static final String SWRLB_NS = "http://www.w3.org/2003/11/swrlb#";
    public static final String SWRLX_NS = "http://www.w3.org/2003/11/swrlx#";
    public static final String RULEML_NS = "http://www.w3.org/2003/11/ruleml#";
    
    public static final String RDF_TYPE_QUOTED = "<" + RDF_NS + "type>"; 
    public static final String RDF_TYPE = RDF_NS + "type"; 
    public static final String RDF_TYPE_ABBR = "rdf:type";
    
    public static final String EQUALITY = OWL_NS + "sameAs"; 
    public static final String EQUALITY_QUOTED = "<" + EQUALITY + ">"; 
    public static final String EQUALITY_ABBR = "owl:sameAs";

    public static final String INEQUALITY = OWL_NS + "differentFrom"; 
    public static final String INEQUALITY_ABBR = "owl:differentFrom"; 
	public static final String INEQUALITY_QUOTED = "<" + INEQUALITY + ">";

	public static final String RDF_PLAIN_LITERAL = RDF_NS + "PlainLiteral";
	public static final String XSD_STRING = XSD_NS + "string";
	
	public static final String PAGODA_ANONY = "http://www.cs.ox.ac.uk/PAGOdA/skolemised#";
	public static final String PAGODA_AUX = "http://www.cs.ox.ac.uk/PAGOdA/auxiliary#";
	public static final String KARMA_ANONY = "http://www.cs.ox.ac.uk/KARMA/anonymous"; 
	public static final String PAGODA_ORIGINAL = PAGODA_AUX + "Original";
	
	
	
	public static final String namespace_newatoms = "http://constraints/newatoms#";
	public static final String max_violation_iri = "http://constraints/newatoms#MaxCardViolation";
	public static final String min_violation_iri = "http://constraints/newatoms#MinCardViolation";
	//Range out of datatype interval or enumeration
	public static final String range_violation_iri = "http://constraints/newatoms#RangeViolation";
	
	public static final String sameAS_iri = "http://www.w3.org/2002/07/owl#sameAs";
	public static final String differentFrom_iri = "http://www.w3.org/2002/07/owl#differentFrom";
	public static final String nothing_iri = "http://www.w3.org/2002/07/owl#Nothing";
	
	public static final String xsd_int = "http://www.w3.org/2001/XMLSchema#int";

	public static final String xsd_integer = "http://www.w3.org/2001/XMLSchema#integer";
	
	
	public static final String rdfs_literal = "http://www.w3.org/2000/01/rdf-schema#Literal";
	public static final String rdf_plainliteral = "http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral";
 
	
	
	//IRIS constants/namespaces
	public static final String IRIS_EQUAL = "EQUAL";
	public static final String IRIS_NOT_EQUAL = "NOT_EQUAL";
	
	public static final String IRIS_EQUAL_IRI = "http://iris-reasoner.org#EQUAL";
	public static final String IRIS_NOT_EQUAL_IRI = "http://iris-reasoner.org#NOT_EQUAL";
	
	public static final String EQUALITY_NOPREFIX = "sameAs";
     
    public static final String INEQUALITY_NOPREFIX = "differentFrom"; 
	
	
}
