package es.um.dis.utils;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

public class OWLUtils {

	public static final String IAO_NS = "http://purl.obolibrary.org/obo/";
	public static final String SKOS_NS = "http://www.w3.org/2004/02/skos/core#";
	public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	public static final String IAO_DEFINITION_IRI = IAO_NS + "IAO_0000115";
	public static final String SKOS_ALT_LABEL_IRI = SKOS_NS + "altLabel";
	public static final String DECIMAL_IRI = XSD_NS + "decimal";
	public static final String DOUBLE_IRI = XSD_NS + "double";

	public static void addDataPropertyRange(OWLOntology ontology, OWLDataProperty property, OWL2Datatype range) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAxiom axiom = df.getOWLDataPropertyRangeAxiom(property, range);
		ontology.add(axiom);
	}
	public static void addAnnotation(OWLOntology ontology, OWLEntity entity, IRI annotationPropertyIRI,
			String annotationValue, String lang) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		//OWLAnnotationProperty annotationProperty = df.getOWLAnnotationProperty(annotationPropertyIRI);
		OWLLiteral annotationValueLiteral = null;
		if (lang != null) {
			annotationValueLiteral = df.getOWLLiteral(annotationValue, lang);
		} else {
			annotationValueLiteral = df.getOWLLiteral(annotationValue);
		}
		//OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(annotationProperty, entity.getIRI(), annotationValueLiteral);
		//ontology.addAxiom(axiom);
		addAnnotation(ontology, entity, annotationPropertyIRI, annotationValueLiteral);
	}

	public static void addAnnotation(OWLOntology ontology, OWLEntity entity, IRI annotationPropertyIRI,
			String annotationValue) {
		addAnnotation(ontology, entity, annotationPropertyIRI, annotationValue, null);
	}
	
	public static void addAnnotation(OWLOntology ontology, OWLEntity entity, IRI annotationPropertyIRI,
			OWLAnnotationValue annotationValue) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAnnotationProperty annotationProperty = df.getOWLAnnotationProperty(annotationPropertyIRI);
		OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(annotationProperty, entity.getIRI(), annotationValue);
		ontology.add(axiom);
	}

	public static void addSubclassOf(OWLOntology ontology, OWLClass entity, OWLClassExpression parentEntity) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAxiom axiom = df.getOWLSubClassOfAxiom(entity, parentEntity);
		ontology.add(axiom);
	}

	public static OWLClass createClass(OWLOntology ontology, IRI classIRI) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLClass owlClass = df.getOWLClass(classIRI);
		OWLAxiom axiom = df.getOWLDeclarationAxiom(owlClass);
		ontology.add(axiom);
		return owlClass;
	}

	public static OWLObjectProperty createObjectProperty(OWLOntology ontology, IRI propertyIRI) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLObjectProperty owlObjectProperty = df.getOWLObjectProperty(propertyIRI);
		OWLAxiom axiom = df.getOWLDeclarationAxiom(owlObjectProperty);
		ontology.add(axiom);
		return owlObjectProperty;
	}
	
	public static OWLNamedIndividual createIndividual(OWLOntology ontology, IRI individualIRI, OWLClass type) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLNamedIndividual individual = df.getOWLNamedIndividual(individualIRI);
		if (type != null) {
			OWLAxiom axiom = df.getOWLClassAssertionAxiom(type, individual);
			ontology.add(axiom);
		}
		OWLAxiom axiom = df.getOWLDeclarationAxiom(individual);
		ontology.add(axiom);
		return individual;
	}
	
	public static OWLDataProperty createDataProperty(OWLOntology ontology, IRI propertyIRI) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLDataProperty owlDataProperty = df.getOWLDataProperty(propertyIRI);
		OWLAxiom axiom = df.getOWLDeclarationAxiom(owlDataProperty);
		ontology.add(axiom);
		return owlDataProperty;
	}

	public static void addValuesAxiom(OWLOntology ontology, OWLClassExpression owlClass, IRI objectPropertyIRI,
			OWLNamedIndividual individual) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLObjectProperty objectProperty = df.getOWLObjectProperty(objectPropertyIRI);
		OWLClassExpression classExpression = df.getOWLObjectHasValue(objectProperty, individual);
		OWLAxiom owlAxiom = df.getOWLSubClassOfAxiom(owlClass, classExpression);
		ontology.add(owlAxiom);
	}

	public static void addIndividualRelation(OWLOntology ontology, OWLNamedIndividual subject, IRI objectPropertyIRI,
			OWLNamedIndividual object) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLObjectProperty objectProperty = df.getOWLObjectProperty(objectPropertyIRI);
		OWLAxiom owlAxiom = df.getOWLObjectPropertyAssertionAxiom(objectProperty, subject, object);
		ontology.add(owlAxiom);
	}

	public static void addIndividualDataProperty(OWLOntology ontology, OWLNamedIndividual subject, IRI dataPropertyIRI,
			double value) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLDataProperty dataProperty = df.getOWLDataProperty(dataPropertyIRI);
		String valueString = "";
		if (value == Double.POSITIVE_INFINITY) {
			valueString = "INF";
		} else if (value == Double.NEGATIVE_INFINITY) {
			valueString = "-INF";
		} else {
			valueString = String.valueOf(value);
		}
		OWLLiteral literal = df.getOWLLiteral(valueString, df.getOWLDatatype(DOUBLE_IRI));
		OWLAxiom owlAxiom = df.getOWLDataPropertyAssertionAxiom(dataProperty, subject, literal);
		ontology.add(owlAxiom);
	}

	public static void addIndividualDataProperty(OWLOntology ontology, OWLNamedIndividual subject, IRI dataPropertyIRI,
			int value) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLDataProperty dataProperty = df.getOWLDataProperty(dataPropertyIRI);
		OWLAxiom owlAxiom = df.getOWLDataPropertyAssertionAxiom(dataProperty, subject, value);
		ontology.add(owlAxiom);
	}


	
	public static void addClassAssertion(OWLOntology ontology, IRI individualIRI, IRI owlClassIRI) {
		OWLNamedIndividual individual = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(individualIRI);
		addClassAssertion(ontology, individual, owlClassIRI);
	}
	
	public static void addClassAssertion(OWLOntology ontology, OWLNamedIndividual individual, IRI owlClassIRI) {
		OWLClass owlClass = createClass(ontology, owlClassIRI);
		OWLAxiom axiom = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClassAssertionAxiom(owlClass, individual);
		ontology.add(axiom);
	}
	
	public static void addObjectSomeValuesFromRestriction(OWLOntology ontology, OWLProperty property, OWLClass owlClass, OWLClassExpression classExpression) {
		OWLClassExpression someValuesFromExpression = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectSomeValuesFrom((OWLObjectPropertyExpression) property, classExpression);
		addSubclassOf(ontology, owlClass, someValuesFromExpression);
		
	}
	public static void addDataSomeValuesFromRestriction(OWLOntology ontology, OWLDataProperty property, OWLClass owlClass, OWLDataRange datatype) {
		OWLClassExpression someValuesFromExpression = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLDataSomeValuesFrom(property, datatype);
		addSubclassOf(ontology, owlClass, someValuesFromExpression);
		
	}
	

	
	public static List<OWLNamedIndividual> getIndividualsFromList(OWLOntology ontology, String prefixIRI, List<String> listOfNames) {
		List<OWLNamedIndividual> result = new ArrayList<>();
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		for(String name : listOfNames) {
			OWLNamedIndividual individual = df.getOWLNamedIndividual(prefixIRI + name);
			result.add(individual);
		}
		return result;
	}
	
	public static void addOneOfAxiom (OWLOntology ontology, OWLClass owlClass, List<OWLNamedIndividual> individuals) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLObjectOneOf oneOf = df.getOWLObjectOneOf(individuals);
		OWLAxiom equivalentAxiom = df.getOWLEquivalentClassesAxiom(owlClass, oneOf);
		ontology.add(equivalentAxiom);
	}

	
}
