package es.um.dis.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class OWLUtils {

	public static final String IAO_NS = "http://purl.obolibrary.org/obo/";
	public static final String SKOS_NS = "http://www.w3.org/2004/02/skos/core#";
	public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	public static final String QUDT_NS = "http://qudt.org/schema/qudt/";
	public static final String IAO_DEFINITION_IRI = IAO_NS + "IAO_0000115";
	public static final String SKOS_ALT_LABEL_IRI = SKOS_NS + "altLabel";
	public static final String IDO_NS = "http://rds.posccaesar.org/ontology/lis14/rdl/";
	public static final String OM2_NS = "http://www.ontology-of-units-of-measure.org/resource/om-2/";
	public static final String DC_ELEMENTS_NS = "http://purl.org/dc/elements/1.1/";
	public static final String DC_TERMS_NS = "http://purl.org/dc/terms/";
	public static final String DC_ELEMENTS_CONTRIBUTOR = DC_ELEMENTS_NS + "contributor";
	public static final String DC_ELEMENTS_CREATOR = DC_ELEMENTS_NS + "creator";
	public static final String DC_TERMS_LICENSE = DC_TERMS_NS + "license";
	public static final String DC_ELEMENTS_DESCRIPTION = DC_ELEMENTS_NS + "description";

	public static final String DECIMAL_IRI = XSD_NS + "decimal";
	public static final String DOUBLE_IRI = XSD_NS + "double";
	public static final String QUDT_VALUE_IRI = QUDT_NS + "value";
	public static final String SCHEMA_NS = "http://schema.org/";
	public static final String SCHEMA_IDENTIFIER = SCHEMA_NS + "identifier";
	public static final String SCHEMA_CATEGORY = OWLUtils.SCHEMA_NS + "category";
	//public static final String OM2_DIMENSION = OWLUtils.OM2_NS + "Dimension";
	public static final String OM2_SYMBOL = OWLUtils.OM2_NS + "symbol";
	public static final String OM2_HAS_UNIT = OWLUtils.OM2_NS + "hasUnit";
	public static final String OM2_HAS_NUMERICAL_VALUE = OWLUtils.OM2_NS + "hasNumericalValue";
	//public static final String OM2_UNIT = OWLUtils.OM2_NS + "Unit";
	public static final String OM2_MEASURE = OWLUtils.OM2_NS + "Measure";
	public static final String OM2_HAS_DIMENSION = OWLUtils.OM2_NS + "hasDimension";
	public static final String OM2_SOURCE_RAW_IRI = "https://raw.githubusercontent.com/HajoRijgersberg/OM/refs/heads/master/om-2.0.rdf";

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

	public static void addAnnotationWithComment(OWLOntology ontology, OWLEntity entity, IRI annotationPropertyIRI,
			String annotationValue, String comment) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAnnotationProperty annotationProperty = df.getOWLAnnotationProperty(annotationPropertyIRI);
		OWLAnnotationProperty rdfsComment = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT);
		OWLAnnotation commentAnnotation = df.getOWLAnnotation(rdfsComment, df.getOWLLiteral(comment));
		OWLAnnotation annotation = df.getOWLAnnotation(annotationProperty, df.getOWLLiteral(annotationValue));

		OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(entity.getIRI(), annotation, Arrays.asList(commentAnnotation));
		ontology.add(axiom);
	}

	public static void addAnnotation(OWLOntology ontology, OWLEntity entity, IRI annotationPropertyIRI,
			OWLAnnotationValue annotationValue) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAnnotationProperty annotationProperty = df.getOWLAnnotationProperty(annotationPropertyIRI);
		OWLAxiom axiom = df.getOWLAnnotationAssertionAxiom(annotationProperty, entity.getIRI(), annotationValue);
		ontology.add(axiom);
	}

	public static void addAnnotation(OWLOntology ontology, OWLAxiom owlAxiom, IRI annotationPropertyIRI,
			OWLAnnotationValue annotationValue) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAnnotationProperty annotationProperty = df.getOWLAnnotationProperty(annotationPropertyIRI);
		OWLAnnotation annotation = df.getOWLAnnotation(annotationProperty, annotationValue);
		Set<OWLAnnotation> newAnnotations = owlAxiom.annotations().collect(Collectors.toSet());
		newAnnotations.add(annotation);

		OWLAxiom annotatedAxiom = owlAxiom.getAnnotatedAxiom(newAnnotations);
		ontology.add(annotatedAxiom);
		ontology.remove(owlAxiom);
	}

	public static void addAnnotation(OWLOntology ontology, OWLAxiom owlAxiom, IRI annotationPropertyIRI,
			String literal) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLLiteral annotationValue = df.getOWLLiteral(literal);
		addAnnotation(ontology, owlAxiom, annotationPropertyIRI, annotationValue);
	}

	public static OWLAxiom addSubclassOf(OWLOntology ontology, OWLClass entity, OWLClassExpression parentEntity) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAxiom axiom = df.getOWLSubClassOfAxiom(entity, parentEntity);
		ontology.add(axiom);
		return axiom;
	}

	public static void addSubPropertyOf(OWLOntology ontology, OWLProperty property, OWLProperty parentProperty) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAxiom axiom = null;
		if (property.isObjectPropertyExpression() && parentProperty.isObjectPropertyExpression()) {
			axiom = df.getOWLSubObjectPropertyOfAxiom(property.asObjectPropertyExpression(), parentProperty.asObjectPropertyExpression());
		}

		if (property.isDataPropertyExpression() && parentProperty.isDataPropertyExpression()) {
			axiom = df.getOWLSubDataPropertyOfAxiom(property.asDataPropertyExpression(), parentProperty.asDataPropertyExpression());
		}

		if (property.isOWLAnnotationProperty() && parentProperty.isOWLAnnotationProperty()) {
			axiom = df.getOWLSubAnnotationPropertyOfAxiom(property.asOWLAnnotationProperty(), parentProperty.asOWLAnnotationProperty());
		}

		if (axiom != null) {
			ontology.add(axiom);
		} else {
			System.out.println("addSubPropertyOf - incompatible properties");
		}
	}

	public static void addEquivalentClass(OWLOntology ontology, OWLClass entity, OWLClassExpression equivalentEntity) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(entity, equivalentEntity);
		ontology.add(axiom);
	}

	public static void addEquivalentObjectProperties(OWLOntology ontology, OWLObjectPropertyExpression property1, OWLObjectPropertyExpression property2) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAxiom axiom = df.getOWLEquivalentObjectPropertiesAxiom(property1, property2);
		ontology.add(axiom);
	}

	public static void addEquivalentDataProperties(OWLOntology ontology, OWLDataPropertyExpression property1, OWLDataPropertyExpression property2) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAxiom axiom = df.getOWLEquivalentDataPropertiesAxiom(property1, property2);
		ontology.add(axiom);
	}

	public static void addDomain(OWLOntology ontology, OWLProperty property, OWLClassExpression classExpression) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAxiom axiom = null;
		if(property.isOWLObjectProperty()) {
			axiom = df.getOWLObjectPropertyDomainAxiom((OWLObjectProperty) property, classExpression);
		} else if (property.isOWLDataProperty()) {
			axiom = df.getOWLDataPropertyDomainAxiom((OWLDataProperty) property, classExpression);
		}
		if(axiom != null) {
			ontology.add(axiom);
		}
	}

	public static void addRange(OWLOntology ontology, OWLObjectProperty objectProperty, OWLClassExpression classExpression) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAxiom axiom = df.getOWLObjectPropertyRangeAxiom(objectProperty, classExpression);
		ontology.add(axiom);
	}

	public static void addDomain(OWLOntology ontology, OWLObjectProperty objectProperty, OWLClassExpression classExpression) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAxiom axiom = df.getOWLObjectPropertyDomainAxiom(objectProperty, classExpression);
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

	public static OWLAnnotationProperty createAnnotationProperty(OWLOntology ontology, IRI propertyIRI) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAnnotationProperty annotationProperty = df.getOWLAnnotationProperty(propertyIRI);
		OWLAxiom axiom = df.getOWLDeclarationAxiom(annotationProperty);
		ontology.add(axiom);
		return annotationProperty;
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

	public static OWLAxiom addObjectSomeValuesFromRestriction(OWLOntology ontology, OWLProperty property, OWLClass owlClass, OWLClassExpression classExpression) {
		OWLClassExpression someValuesFromExpression = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectSomeValuesFrom((OWLObjectPropertyExpression) property, classExpression);
		return addSubclassOf(ontology, owlClass, someValuesFromExpression);
	}
	public static OWLAxiom addDataSomeValuesFromRestriction(OWLOntology ontology, OWLDataProperty property, OWLClass owlClass, OWLDataRange datatype) {
		OWLClassExpression someValuesFromExpression = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLDataSomeValuesFrom(property, datatype);
		OWLAxiom axiom = addSubclassOf(ontology, owlClass, someValuesFromExpression);
		return axiom;

	}

	public static OWLAxiom addObjectAllValuesFromRestriction(OWLOntology ontology, OWLObjectProperty property, OWLClass owlClass, OWLClassExpression classExpression) {
		OWLClassExpression allValuesFromExpression = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectAllValuesFrom(property, classExpression);
		OWLAxiom axiom = addSubclassOf(ontology, owlClass, allValuesFromExpression);
		return axiom;
	}

	public static List<OWLNamedIndividual> getIndividualsFromList(OWLOntology ontology, String prefixIRI, List<String> listOfNames, OWLClass type) {
		List<OWLNamedIndividual> result = new ArrayList<>();
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		for(String name : listOfNames) {
			OWLNamedIndividual individual = df.getOWLNamedIndividual(prefixIRI + name);
			OWLAxiom axiom = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClassAssertionAxiom(type, individual);
			ontology.add(axiom);
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

	public static void setDisjointClasses(OWLOntology ontology, Collection<OWLClass> classes) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAxiom axiom = df.getOWLDisjointClassesAxiom(classes);
		ontology.add(axiom);
	}

	public static boolean containsAnnotation(OWLOntology ontology, OWLEntity entity, OWLAnnotationProperty annotationProperty) {

		return EntitySearcher.getAnnotationObjects(entity, ontology).anyMatch(annotation -> {
			return annotation.getProperty().equals(annotationProperty);
		});
	}

	public static boolean containsAnnotation(OWLOntology ontology, OWLEntity entity, IRI annotationPropertyIRI) {

		return EntitySearcher.getAnnotationObjects(entity, ontology).anyMatch(annotation -> {
			return annotation.getProperty().getIRI().equals(annotationPropertyIRI);
		});
	}

	public static void addOntologyAnnotation(OWLOntology ontology, IRI annotationPropertyIRI, String value) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAnnotationProperty owlAnnotationProperty = df.getOWLAnnotationProperty(annotationPropertyIRI);
		OWLAnnotation owlAnnotation = df.getOWLAnnotation(owlAnnotationProperty, df.getOWLLiteral(value));
		ontology.getOWLOntologyManager().applyChange(new AddOntologyAnnotation(ontology, owlAnnotation));
	}

}
