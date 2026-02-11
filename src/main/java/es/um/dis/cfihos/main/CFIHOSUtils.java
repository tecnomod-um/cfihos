package es.um.dis.cfihos.main;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.semanticweb.owlapi.model.HasComponents;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFConstants;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import es.um.dis.utils.OWLEntityType;
import es.um.dis.utils.OWLUtils;

public class CFIHOSUtils {
	
	private static final String CFIHOS_TAG_CODE = "CFIHOS-30000311";
	public static final String HAS_TAG = "hasTag";
	public static final String HAS_UNECE_CODE = "hasUNECECode";
	public static final String HAS_SOURCE_STANDARD = "hasSourceStandard";
	public static final String HAS_REPRESENTATION_TYPE = "hasRepresentationType";
	public static final String HAS_ASSET_TYPE_REFERENCE = "hasAssetTypeReference";
	public static final String HAS_MEASUREMENT_SYSTEM = "hasMeasurementSystem";
	public static final String HAS_EQUIPMENT = "hasEquipment";
	public static final String HAS_DOCUMENT_TYPE = "hasDocumentType";
	public static final String HAS_DISCIPLINE = "hasDiscipline";
	public static final String EQUIPMENT_CODE = "CFIHOS-30000311";
	public static final String TAG_CODE = "CFIHOS-30000311";
	public static final String QUALITATIVE_PROPERTY_CODE = "CFIHOS-00000071";
	public static final String QUANTITATIVE_PROPERTY_CODE = "CFIHOS-00000070";
	public static final String PROPERTY_CODE = "CFIHOS-00000029";
	public static final String PROPERTY_GROUP_CODE = "CFIHOS-00000141";
	public static final String PURPOSE_CODE = "Purpose";
	public static final String PROPERTY_PICKLIST_CODE = "CFIHOS-00000019";
	public static final String STANDARD_CODE = "CFIHOS-00000061";
	public static final String SOURCE_STANDARD_DOCUMENT_AND_DATA_REQUIREMENT_CODE = "CFIHOS-00000132";
	public static final String MEASURE = "Measure";
	public static final String IMPERIAL_SYSTEM_UNIT = "ImperialSystemUnit";
	public static final String INTERNATIONAL_SYSTEM_UNIT = "InternationalSystemUnit";
	public static final String DISCIPLINE_CODE = "CFIHOS-00000021";
	public static final String DOCUMENT_TYPE_CODE = "CFIHOS-00000032";
	public static final String DIMENSION_CODE = "CFIHOS-00000072";
	public static final String DISCIPLINE_DOCUMENT_TYPE_CODE = "CFIHOS-00000027";
	public static final String UNIT_OF_MEASUREMENT_CODE = "CFIHOS-00000073";
	public static final String CFIHOS_OBJECT_EQUIVALENT_MAPPING = "CFIHOS-00000052";
	
	public static final String CFIHOS_SYSTEME_INTERNATIONAL_CODE = "CFIHOS-60001649";
	public static final String CFIHOS_IMPERIAL_SYSTEM_CODE = "CFIHOS-60001650";
	public static final String CFIHOS_HAS_CFIHOS_CODE = "hasCFIHOSCode";
	public static final String CFIHOS_HAS_PURPOSE_CODE = "hasPurpose";
	public static void addEquipmentClass(OWLOntology ontology, String prefixIRI, String prefixIRIForEquipment,
			String classCode, String parentClassCode, String className, String classDefinition, String classSynonym) {

		OWLClass owlClass = OWLUtils.createClass(ontology, IRI.create(prefixIRIForEquipment + classCode));
		OWLUtils.addAnnotation(ontology, owlClass, getHasCFIHOSCode (prefixIRI), classCode);

		if (parentClassCode != null && !parentClassCode.isEmpty()) {
			OWLClass parentClass = ontology.getOWLOntologyManager().getOWLDataFactory()
					.getOWLClass(prefixIRIForEquipment + parentClassCode);
			OWLUtils.addSubclassOf(ontology, owlClass, parentClass);
		}
		if (className != null && !className.isEmpty()) {
			OWLUtils.addAnnotation(ontology, owlClass, IRI.create(RDFS.LABEL.stringValue()), className);
		}
		if (classDefinition != null && !classDefinition.isEmpty()) {
			OWLUtils.addAnnotation(ontology, owlClass, IRI.create(OWLUtils.IAO_DEFINITION_IRI), classDefinition);
		}
		if (classSynonym != null && !classSynonym.isEmpty()) {
			for (String synonym : classSynonym.split(";")) {
				OWLUtils.addAnnotation(ontology, owlClass, IRI.create(OWLUtils.SKOS_ALT_LABEL_IRI), synonym.strip());
			}
		}
	}
	
	private static IRI getHasCFIHOSCode (String prefixIRI) {
		return IRI.create(prefixIRI + CFIHOS_HAS_CFIHOS_CODE);
	}
	
	private static IRI getHasPurpose (String prefixIRI) {
		return IRI.create(prefixIRI + CFIHOS_HAS_PURPOSE_CODE);
	}
	
	public static void addEquipmentProperty(OWLOntology ontology, String prefixIRI, String prefixIRIForEquipment, String equipmentCode,
			String propertyCode, String propertyName, String unitOfMeasureSICode, String unitOfMeasureSIName,
			String unitOfMeasureImperialCode, String unitOfMeasureImperialName) {
		OWLObjectProperty property = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + propertyCode));
		
		if (equipmentCode != null && !equipmentCode.isEmpty()) {
			OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
			OWLClass equipmentClass = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(prefixIRIForEquipment + equipmentCode);
			List<OWLClassExpression> ranges = EntitySearcher.getRanges(property, ontology).toList();
			if(ranges.size() == 0) {
				OWLUtils.addObjectSomeValuesFromRestriction(ontology, property, equipmentClass, df.getOWLThing());
			} else if(ranges.size() == 1) {
				OWLUtils.addObjectSomeValuesFromRestriction(ontology, property, equipmentClass, ranges.get(0));
			} else if (ranges.size() > 1) {
				OWLClassExpression classExpression = df.getOWLObjectUnionOf(ranges);
				OWLUtils.addObjectSomeValuesFromRestriction(ontology, property, equipmentClass, classExpression);
			}
		}
	}
	public static void addDiscipline(OWLOntology ontology, String prefixIRI, String disciplineCFIHOSCode,
			String disciplineCode, String disciplineName, String disciplineDescription, OWLClass parentClass) {
		
		OWLClass owlClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + disciplineCFIHOSCode));
		OWLUtils.addAnnotation(ontology, owlClass, getHasCFIHOSCode (prefixIRI), disciplineCFIHOSCode);
		OWLUtils.addSubclassOf(ontology, owlClass, parentClass);
		if (disciplineCode != null && !disciplineCode.isEmpty()) {
			OWLUtils.addAnnotation(ontology, owlClass, IRI.create(OWLUtils.SCHEMA_IDENTIFIER), disciplineCode);
		}
		if (disciplineName != null && !disciplineName.isEmpty()) {
			OWLUtils.addAnnotation(ontology, owlClass, IRI.create(RDFConstants.RDFS_LABEL), disciplineName);
		}
		if (disciplineDescription != null && !disciplineDescription.isEmpty()) {
			OWLUtils.addAnnotation(ontology, owlClass, IRI.create(OWLUtils.IAO_DEFINITION_IRI), disciplineDescription);
		}
		
	}

	public static void addDocumentType(OWLOntology ontology, String prefixIRI, String documentCFIHOSCode,
			String documentShortCode, String documentName, String documentDescription,
			String documentTypeClassification, String documentTypeSynonym, OWLClass parentClass) {
		
		OWLClass owlClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + documentCFIHOSCode));
		OWLUtils.addAnnotation(ontology, owlClass, getHasCFIHOSCode (prefixIRI), documentCFIHOSCode);
		OWLUtils.addSubclassOf(ontology, owlClass, parentClass);
		
		if (documentShortCode != null && !documentShortCode.isEmpty()) {
			OWLUtils.addAnnotation(ontology, owlClass, IRI.create(OWLUtils.SCHEMA_IDENTIFIER), documentShortCode);
		}
		if (documentName != null && !documentName.isEmpty()) {
			OWLUtils.addAnnotation(ontology, owlClass, IRI.create(RDFConstants.RDFS_LABEL), documentName);
		}
		if (documentDescription != null && !documentDescription.isEmpty()) {
			OWLUtils.addAnnotation(ontology, owlClass, IRI.create(OWLUtils.IAO_DEFINITION_IRI), documentDescription);
		}
		if (documentTypeSynonym != null && !documentTypeSynonym.isEmpty()) {
			for (String synonym : documentTypeSynonym.split(";")) {
				OWLUtils.addAnnotation(ontology, owlClass, IRI.create(OWLUtils.SKOS_ALT_LABEL_IRI), synonym.strip());
			}
		}
		
	}

	public static void addTagClass(OWLOntology ontology, String prefixIRI, String prefixIRIForTags, String tagClassCode,
			String parentTagClassCode, String tagClassName, String tagClassDefinition, String tagClassSynonym) {

		if (tagClassCode == null || tagClassCode.isEmpty()) {
			return;
		}
		OWLClass owlClass = OWLUtils.createClass(ontology, IRI.create(prefixIRIForTags + tagClassCode));
		OWLUtils.addAnnotation(ontology, owlClass, getHasCFIHOSCode (prefixIRI), tagClassCode);

		if (parentTagClassCode != null && !parentTagClassCode.isEmpty()) {
			OWLClass parentClass = ontology.getOWLOntologyManager().getOWLDataFactory()
					.getOWLClass(prefixIRIForTags + parentTagClassCode);
			OWLUtils.addSubclassOf(ontology, owlClass, parentClass);
		}
		if (tagClassName != null && !tagClassName.isEmpty()) {
			OWLUtils.addAnnotation(ontology, owlClass, IRI.create(RDFS.LABEL.stringValue()), tagClassName);
		}
		if (tagClassDefinition != null && !tagClassDefinition.isEmpty()) {
			OWLUtils.addAnnotation(ontology, owlClass, IRI.create(OWLUtils.IAO_DEFINITION_IRI), tagClassDefinition);
		}
		if (tagClassSynonym != null && !tagClassSynonym.isEmpty()) {
			for (String synonym : tagClassSynonym.split(";")) {
				OWLUtils.addAnnotation(ontology, owlClass, IRI.create(OWLUtils.SKOS_ALT_LABEL_IRI), synonym.strip());
			}
		}
	}
	
	public static void addTagProperty(OWLOntology ontology, String prefixIRI, String prefixIRIForTags, String tagCode,
			String propertyCode, String propertyName, String unitOfMeasureSICode, String unitOfMeasureSIName,
			String unitOfMeasureImperialCode, String unitOfMeasureImperialName) {
		OWLObjectProperty property = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + propertyCode));

		
		if (tagCode != null && !tagCode.isEmpty()) {
			OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
			OWLClass tagClass = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(prefixIRIForTags + tagCode);
			List<OWLClassExpression> ranges = EntitySearcher.getRanges(property, ontology).toList();
			if(ranges.size() == 0) {
				OWLUtils.addObjectSomeValuesFromRestriction(ontology, property, tagClass, df.getOWLThing());
			} else if(ranges.size() == 1) {
				OWLUtils.addObjectSomeValuesFromRestriction(ontology, property, tagClass, ranges.get(0));
			} else if (ranges.size() > 1) {
				OWLClassExpression classExpression = df.getOWLObjectUnionOf(ranges);
				OWLUtils.addObjectSomeValuesFromRestriction(ontology, property, tagClass, classExpression);
			}
		}
	}

	public static void addTagProperty(OWLOntology ontology, String prefixIRI, String prefixIRIForTags,
			String prefixIRIForEquipment, String tagCode, String equipmentCode, OWLObjectProperty relationship) {
		if (tagCode != null && equipmentCode != null) {
			OWLClass tag = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(prefixIRIForTags + tagCode);
			OWLClass equipment = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(prefixIRIForEquipment + equipmentCode);
			OWLUtils.addObjectSomeValuesFromRestriction(ontology, relationship, equipment, tag);
		}
		
	}

	public static void addDisciplineDocumentType(OWLOntology ontology, String prefixIRI, String prefixIRIForEquipment, String prefixIRIForTags,
			String disciplineDocumentCode, String disciplineCode, String documentCode,
			String disciplineDocumentShortCode, String assetTypeReference, String representationType, OWLClass parentClass) {
		
		if (disciplineDocumentCode == null || disciplineDocumentCode.isEmpty()) {
			return;
		}
		OWLClass disciplineDocumentClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + disciplineDocumentCode));
		OWLUtils.addAnnotation(ontology, disciplineDocumentClass, getHasCFIHOSCode (prefixIRI), disciplineDocumentCode);
		
		if (disciplineCode != null && !disciplineCode.isEmpty()) {
			OWLObjectProperty hasDiscipline = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(prefixIRI + HAS_DISCIPLINE);
			OWLClass disciplineClass = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(prefixIRI + disciplineCode);
			OWLUtils.addObjectSomeValuesFromRestriction(ontology, hasDiscipline, disciplineDocumentClass, disciplineClass);
		}
		
		if(documentCode != null && !documentCode.isEmpty()) {
			OWLObjectProperty hasDocumentType = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(prefixIRI + HAS_DOCUMENT_TYPE);
			OWLClass document = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(prefixIRI + documentCode);
			OWLUtils.addObjectSomeValuesFromRestriction(ontology, hasDocumentType, disciplineDocumentClass, document);
		}
		
		if(disciplineDocumentShortCode != null && !disciplineDocumentShortCode.isEmpty()) {
			OWLUtils.addAnnotation(ontology, disciplineDocumentClass, IRI.create(OWLUtils.SCHEMA_IDENTIFIER), disciplineDocumentShortCode);
		}
		
		if(assetTypeReference != null && !assetTypeReference.isEmpty()) {
			OWLUtils.addAnnotation(ontology, disciplineDocumentClass, IRI.create(prefixIRI + HAS_ASSET_TYPE_REFERENCE), assetTypeReference);
		}
		
		if(representationType != null && !representationType.isEmpty()) {
			OWLUtils.addAnnotation(ontology, disciplineDocumentClass, IRI.create(prefixIRI + HAS_REPRESENTATION_TYPE), representationType);
		}
		
		if (parentClass != null) {
			OWLUtils.addSubclassOf(ontology, disciplineDocumentClass, parentClass);
		}
		
	}

	public static void addStandard(OWLOntology ontology, String prefixIRI, String standardCFIHOSCode,
			String standardName, String standardDescription, OWLClass standardClass) {
		if(standardCFIHOSCode == null || standardCFIHOSCode.isEmpty()) {
			return;
		}
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLNamedIndividual standard = df.getOWLNamedIndividual(prefixIRI + standardCFIHOSCode);
		OWLAxiom axiom = df.getOWLClassAssertionAxiom(standardClass, standard);
		ontology.add(axiom);
		

		OWLUtils.addAnnotation(ontology, standard, getHasCFIHOSCode (prefixIRI), standardCFIHOSCode);
		
		
		if(standardName != null && !standardName.isEmpty()) {
			OWLUtils.addAnnotation(ontology, standard, IRI.create(RDFConstants.RDFS_LABEL), standardName);
		}
		
		if(standardDescription != null && !standardDescription.isEmpty()) {
			OWLUtils.addAnnotation(ontology, standard, IRI.create(OWLUtils.IAO_DEFINITION_IRI), standardDescription);
		}
	}

	public static void addTagOrEquipmentStandards(OWLOntology ontology, String prefixIRI, String equipmentPrefixIRI,
			String tagPrefixIRI, String tagOrEquipmentCode, String sourceStandardCode) {
		if(tagOrEquipmentCode == null || tagOrEquipmentCode.isEmpty()) {
			return;
		}
		if(sourceStandardCode == null || sourceStandardCode.isEmpty()) {
			return;
		}
		
		IRI equipmentIRI = IRI.create(equipmentPrefixIRI + tagOrEquipmentCode);
		IRI tagIRI = IRI.create(tagPrefixIRI + tagOrEquipmentCode);
		OWLNamedIndividual standard = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(prefixIRI + sourceStandardCode);
		if(ontology.containsClassInSignature(equipmentIRI)) {
			OWLClass equipmentClass = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(equipmentIRI);
			OWLUtils.addAnnotation(ontology, equipmentClass, IRI.create(prefixIRI + HAS_SOURCE_STANDARD), standard.getIRI());
		}
		if(ontology.containsClassInSignature(tagIRI)) {
			OWLClass tagClass = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(tagIRI);
			OWLUtils.addAnnotation(ontology, tagClass, IRI.create(prefixIRI + HAS_SOURCE_STANDARD), standard.getIRI());
		}
		
	}

	
	public static void addPropertyPicklistValue(OWLOntology ontology, String prefixIRI,
			String propertyPicklistCFIHOSCode, String propertyPicklistName, String propertyPicklistValueCFIHOSCode,
			String propertyPicklistValueCode, String propertyPicklistValueDescription,
			String sourceStandardCFIHOSCode) {
		
		if(propertyPicklistCFIHOSCode == null || propertyPicklistCFIHOSCode.isEmpty()) {
			return;
		}
		if(propertyPicklistValueCFIHOSCode == null || propertyPicklistValueCFIHOSCode.isEmpty()) {
			return;
		}
		OWLClass parentPicklistClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + PROPERTY_PICKLIST_CODE));
		OWLClass picklistClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + propertyPicklistCFIHOSCode));
		OWLAxiom axiom = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLSubClassOfAxiom(picklistClass, parentPicklistClass);
		ontology.add(axiom);
		
		OWLUtils.addAnnotation(ontology, picklistClass, getHasCFIHOSCode (prefixIRI), propertyPicklistCFIHOSCode);
		if (propertyPicklistName != null && !propertyPicklistName.isEmpty()) {
			OWLUtils.addAnnotation(ontology, picklistClass, IRI.create(RDFConstants.RDFS_LABEL), propertyPicklistName);
		}
		
		OWLNamedIndividual propertyValue = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(prefixIRI + propertyPicklistValueCFIHOSCode);
//		OWLClass propertyPickListValue = OWLUtils.createClass(ontology, IRI.create(prefixIRI + "PropertyPickListValue"));
//		axiom = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClassAssertionAxiom(propertyPickListValue, propertyValue);
//		ontology.add(axiom);
		OWLUtils.addAnnotation(ontology, propertyValue, getHasCFIHOSCode (prefixIRI), propertyPicklistValueCFIHOSCode);
		if(propertyPicklistValueCode != null && !propertyPicklistValueCode.isEmpty()) {
			OWLUtils.addAnnotation(ontology, propertyValue, IRI.create(RDFConstants.RDFS_LABEL), propertyPicklistValueCode);
		}
		if(propertyPicklistValueDescription != null && !propertyPicklistValueDescription.isEmpty()) {
			OWLUtils.addAnnotation(ontology, propertyValue, IRI.create(OWLUtils.IAO_DEFINITION_IRI), propertyPicklistValueDescription);
		}
		
		if(sourceStandardCFIHOSCode != null && !sourceStandardCFIHOSCode.isEmpty()) {
			OWLUtils.addAnnotation(ontology, picklistClass, IRI.create(prefixIRI + HAS_SOURCE_STANDARD), IRI.create(prefixIRI + sourceStandardCFIHOSCode));
		}
	}

	public static void linkPropertyValues(OWLOntology ontology, String prefixIRI,
			Map<String, List<String>> propertyPickValuesMap) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		
		//OWLObjectProperty hasPickListValues = df.getOWLObjectProperty(prefixIRI + "hasPickListValues");
		for(Entry<String, List<String>> entry : propertyPickValuesMap.entrySet()) {
			String propertyCode = entry.getKey();
			List<String> propertyValues = entry.getValue();
			OWLClass domainClass = df.getOWLClass(prefixIRI + propertyCode);
			List<OWLNamedIndividual> propertyValuesIndividuals = OWLUtils.getIndividualsFromList(ontology, prefixIRI, propertyValues, domainClass);
			OWLObjectOneOf oneOf = df.getOWLObjectOneOf(propertyValuesIndividuals);
			//OWLObjectAllValuesFrom allValuesFrom = df.getOWLObjectAllValuesFrom(hasPickListValues, oneOf);
			//OWLAxiom equivalentAxiom = df.getOWLEquivalentClassesAxiom(domainClass, allValuesFrom);
			//ontology.add(equivalentAxiom);
			OWLAxiom equivalentAxiom = df.getOWLEquivalentClassesAxiom(domainClass, oneOf);
			ontology.add(equivalentAxiom);
		}
		
	}
	public static void addProperty(OWLOntology ontology, String prefixIRI, String propertyCFIHOSCode,
			String propertyName, String propertyDefinition, String propertyDataType, String unitOfMeasureDimension, String propertyRange) {
		if (propertyCFIHOSCode == null || propertyCFIHOSCode.isEmpty()) {
			return;
		}
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLObjectProperty objectProperty = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + propertyCFIHOSCode));
		OWLUtils.addAnnotation(ontology, objectProperty, getHasCFIHOSCode (prefixIRI), propertyCFIHOSCode);
		
		OWLObjectProperty quantitativeProperty = df.getOWLObjectProperty(prefixIRI + QUANTITATIVE_PROPERTY_CODE);
		OWLObjectProperty qualitativeProperty = df.getOWLObjectProperty(prefixIRI + QUALITATIVE_PROPERTY_CODE);
		
		if (propertyName != null && !propertyName.isEmpty()) {
			OWLUtils.addAnnotation(ontology, objectProperty, IRI.create(RDFConstants.RDFS_LABEL), propertyName);
		}
		
		if (propertyDefinition != null && !propertyDefinition.isEmpty()) {
			OWLUtils.addAnnotation(ontology, objectProperty, IRI.create(OWLUtils.IAO_DEFINITION_IRI), propertyDefinition);
		}
		
		/* If property picklist is defined -> qualitative property */
		if (propertyRange != null && !propertyRange.isEmpty()) {
			OWLClass rangeClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + propertyRange));
			OWLAxiom axiom = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectPropertyRangeAxiom(objectProperty, rangeClass);
			ontology.add(axiom);
			
			OWLUtils.addSubPropertyOf(ontology, objectProperty, qualitativeProperty);
		}
		
		/* If unit of measure dimension is defined -> quantitative property*/
		if (unitOfMeasureDimension != null && !unitOfMeasureDimension.isEmpty()) {
			OWLNamedIndividual dimension = df.getOWLNamedIndividual(prefixIRI + unitOfMeasureDimension);
			OWLClass unit = df.getOWLClass(prefixIRI + UNIT_OF_MEASUREMENT_CODE);
			OWLObjectProperty hasDimension = df.getOWLObjectProperty(OWLUtils.OM2_HAS_DIMENSION);
			OWLClassExpression hasDimensionClassExpression = df.getOWLObjectHasValue(hasDimension, dimension);
			OWLClassExpression unitAndHasDimensionExpression = df.getOWLObjectIntersectionOf(unit, hasDimensionClassExpression);
			
			OWLObjectProperty hasUnit = df.getOWLObjectProperty(OWLUtils.OM2_HAS_UNIT);
			OWLClassExpression hasUnitSomeUnitAndHasDimensionExpression = df.getOWLObjectSomeValuesFrom(hasUnit, unitAndHasDimensionExpression);
			
			OWLClass measure = df.getOWLClass(OWLUtils.OM2_MEASURE);
			OWLClassExpression classExpression = df.getOWLObjectIntersectionOf(measure, hasUnitSomeUnitAndHasDimensionExpression);
			
			OWLUtils.addRange(ontology, objectProperty, classExpression);
			
			
			OWLUtils.addSubPropertyOf(ontology, objectProperty, quantitativeProperty);
		}
		
		/* If the property does not have picklist nor dimension */
		if ((unitOfMeasureDimension == null || unitOfMeasureDimension.isBlank()) 
				&& (propertyRange == null || propertyRange.isBlank())) {
			/* If the datatype is numeric -> quantitative property */
			if ("Number".equals(propertyDataType)) {
				OWLUtils.addSubPropertyOf(ontology, objectProperty, quantitativeProperty);
			} 
			/* Else -> qualitative property */
			else {
				OWLUtils.addSubPropertyOf(ontology, objectProperty, qualitativeProperty);
			}
		}
			
	}

	public static void addDocumentRequiredPerClass(OWLOntology ontology, String prefixIRI, String equipmentPrefixIRI,
			String tagPrefixIRI, String dataRequirementCFIHOSCode, String tagOrEquipmentCFIHOSCode,
			String standardCFIHOSCode, String documentTypeCFIHOSCode) {
		if (dataRequirementCFIHOSCode == null || dataRequirementCFIHOSCode.isEmpty()) {
			return;
		}
		OWLClass documentRequiredPerClassParentClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI+ SOURCE_STANDARD_DOCUMENT_AND_DATA_REQUIREMENT_CODE));
		OWLClass documentRequiredPerClassClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + dataRequirementCFIHOSCode));
		OWLUtils.addSubclassOf(ontology, documentRequiredPerClassClass, documentRequiredPerClassParentClass);
		OWLUtils.addAnnotation(ontology, documentRequiredPerClassClass, getHasCFIHOSCode (prefixIRI), dataRequirementCFIHOSCode);
		
		if(tagOrEquipmentCFIHOSCode != null && !tagOrEquipmentCFIHOSCode.isEmpty()) {
			if(ontology.containsClassInSignature(IRI.create(equipmentPrefixIRI + tagOrEquipmentCFIHOSCode))) {
				OWLClass equipmentClass = OWLUtils.createClass(ontology, IRI.create(equipmentPrefixIRI + tagOrEquipmentCFIHOSCode));
				OWLObjectProperty hasEquipment = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + HAS_EQUIPMENT));
				OWLUtils.addObjectSomeValuesFromRestriction(ontology, hasEquipment, documentRequiredPerClassClass, equipmentClass);
			}
			if(ontology.containsClassInSignature(IRI.create(tagPrefixIRI + tagOrEquipmentCFIHOSCode))) {
				OWLClass tagClass = OWLUtils.createClass(ontology, IRI.create(tagPrefixIRI + tagOrEquipmentCFIHOSCode));
				OWLObjectProperty hasTag = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + HAS_TAG));
				OWLUtils.addObjectSomeValuesFromRestriction(ontology, hasTag, documentRequiredPerClassClass, tagClass);
			}
		}
		
		if(standardCFIHOSCode != null && !standardCFIHOSCode.isEmpty()) {
			OWLUtils.addAnnotation(ontology, documentRequiredPerClassClass, IRI.create(prefixIRI + HAS_SOURCE_STANDARD), IRI.create(prefixIRI + standardCFIHOSCode));
		}
		
		if (documentTypeCFIHOSCode != null && !documentTypeCFIHOSCode.isEmpty()) {
			OWLProperty hasDocumentType = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(prefixIRI + HAS_DOCUMENT_TYPE);
			OWLClass documentTypeClass = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(prefixIRI + documentTypeCFIHOSCode);
			OWLUtils.addObjectSomeValuesFromRestriction(ontology, hasDocumentType, documentRequiredPerClassClass, documentTypeClass);
		}
		
	}

	public static void addUnitOfMeasurement(OWLOntology ontology, String prefixIRI, String unitCFIHOSCode,
			String uneceCode, String unitName, String unitSymbol, String unitDimensionsCFIHOSCodes,
			String unitDimensionsCodes, String unitDimensionsNames, String measurementSystemCFIHOSCode,
			String measurementSystemCode, String unitSynonymName) {
		if (unitCFIHOSCode == null) {
			return;
		}
		OWLNamedIndividual unitOfMeasure = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(prefixIRI + unitCFIHOSCode);
		OWLUtils.addAnnotation(ontology, unitOfMeasure, getHasCFIHOSCode (prefixIRI), unitCFIHOSCode);
		if(uneceCode != null) {
			OWLUtils.addAnnotation(ontology, unitOfMeasure, IRI.create(prefixIRI + HAS_UNECE_CODE), uneceCode);
		}
		
		if(unitName != null) {
			OWLUtils.addAnnotation(ontology, unitOfMeasure, IRI.create(RDFConstants.RDFS_LABEL), unitName);
		}
		
		if(unitSymbol != null) {
			OWLUtils.addAnnotation(ontology, unitOfMeasure, IRI.create(OWLUtils.OM2_SYMBOL), unitSymbol);
		}
		
		/* A unit of measurement can describe more than one dimension. They appear separated by ";". */
		if (unitDimensionsCodes != null) {
			String [] unitDimensionsCFIHOSCodesSplit = unitDimensionsCFIHOSCodes.split(";");
			String [] unitDimensionsCodesSplit = unitDimensionsCodes.split(";");
			String [] unitDimensionsNamesSplit = unitDimensionsNames.split(";");
			for(int i = 0; i < unitDimensionsCodesSplit.length; i++) {
				OWLNamedIndividual dimension = addDimension(ontology, prefixIRI, unitDimensionsCFIHOSCodesSplit[i], unitDimensionsCodesSplit[i], unitDimensionsNamesSplit[i]);
				OWLUtils.addIndividualRelation(ontology, unitOfMeasure, IRI.create(OWLUtils.OM2_HAS_DIMENSION), dimension);
			}
		}
		
		if (measurementSystemCFIHOSCode != null) {
			OWLNamedIndividual system = addMeasurementSystem(ontology, prefixIRI, measurementSystemCFIHOSCode, measurementSystemCode);
			OWLUtils.addIndividualRelation(ontology, unitOfMeasure, IRI.create(prefixIRI + HAS_MEASUREMENT_SYSTEM), system);
			if(CFIHOS_SYSTEME_INTERNATIONAL_CODE.equals(measurementSystemCFIHOSCode)) {
				OWLUtils.addClassAssertion(ontology, unitOfMeasure, IRI.create(prefixIRI + INTERNATIONAL_SYSTEM_UNIT));
			} else if (CFIHOS_IMPERIAL_SYSTEM_CODE.equals(measurementSystemCFIHOSCode)) {
				OWLUtils.addClassAssertion(ontology, unitOfMeasure, IRI.create(prefixIRI + IMPERIAL_SYSTEM_UNIT));
			} 
		} else {
			OWLUtils.addClassAssertion(ontology, unitOfMeasure, IRI.create(prefixIRI + UNIT_OF_MEASUREMENT_CODE));
		}
		if(unitSynonymName != null) {
			OWLUtils.addAnnotation(ontology, unitOfMeasure, IRI.create(OWLUtils.SKOS_ALT_LABEL_IRI), unitSynonymName);
		}
		
	}

	private static OWLNamedIndividual addMeasurementSystem(OWLOntology ontology, String prefixIRI,
			String measurementSystemCFIHOSCode, String measurementSystemCode) {
		String systemIRI = prefixIRI + measurementSystemCFIHOSCode;
		if(ontology.containsIndividualInSignature(IRI.create(systemIRI))) {
			return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(systemIRI);
		}
		
		OWLNamedIndividual system = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(systemIRI);
		OWLUtils.addAnnotation(ontology, system, getHasCFIHOSCode (prefixIRI), measurementSystemCFIHOSCode);
		if(measurementSystemCode != null) {
			OWLUtils.addAnnotation(ontology, system, IRI.create(RDFConstants.RDFS_LABEL), measurementSystemCode);
		}
		
		return system;
	}

	private static OWLNamedIndividual addDimension(OWLOntology ontology, String prefixIRI, String unitDimensionCFIHOSCode,
			String unitDimensionCode, String unitDimensionName) {
		String dimensionIRI = prefixIRI + unitDimensionCFIHOSCode;
		if(ontology.containsIndividualInSignature(IRI.create(dimensionIRI))) {
			return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(dimensionIRI);
		}
		
		OWLNamedIndividual dimension = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(dimensionIRI);
		OWLUtils.addAnnotation(ontology, dimension, getHasCFIHOSCode (prefixIRI), unitDimensionCFIHOSCode);
		OWLUtils.addClassAssertion(ontology, dimension, IRI.create(prefixIRI + DIMENSION_CODE));
		
		if(unitDimensionCode != null) {
			OWLUtils.addAnnotation(ontology, dimension, IRI.create(OWLUtils.SKOS_ALT_LABEL_IRI), unitDimensionCode);
		}
		
		if(unitDimensionName != null) {
			OWLUtils.addAnnotation(ontology, dimension, IRI.create(RDFConstants.RDFS_LABEL), unitDimensionName);
		}
		
		return dimension;
	}

//	public static OWLObjectProperty createPropertyGroup(OWLOntology ontology, String prefixIRI, String propertyGroupCFIHOSUniqueCode, String propertyGroupCode,
//			String propertyGroupDescription) {
//		
//		if (propertyGroupCFIHOSUniqueCode == null || propertyGroupCFIHOSUniqueCode.isBlank()) {
//			return null;
//		}
//		IRI propertyGroupIRI = IRI.create(prefixIRI + propertyGroupCFIHOSUniqueCode);
//		if(ontology.containsObjectPropertyInSignature(propertyGroupIRI)) {
//			return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(propertyGroupIRI);
//		}
//		
//		OWLObjectProperty propertyGroup = OWLUtils.createObjectProperty(ontology, propertyGroupIRI);
//		OWLUtils.addAnnotation(ontology, propertyGroup, getHasCFIHOSCode (prefixIRI), propertyGroupCFIHOSUniqueCode);
//		
//		if (propertyGroupDescription != null && !propertyGroupDescription.isBlank()) {
//			OWLUtils.addAnnotation(ontology, propertyGroup, IRI.create(RDFConstants.RDFS_LABEL), propertyGroupDescription);
//			OWLUtils.addAnnotation(ontology, propertyGroup, IRI.create(OWLUtils.IAO_DEFINITION_IRI), propertyGroupDescription);
//		}
//		
//		if (propertyGroupCode != null && !propertyGroupCode.isBlank()) {
//			OWLUtils.addAnnotation(ontology, propertyGroup, IRI.create(OWLUtils.SCHEMA_IDENTIFIER), propertyGroupCode);
//		}
//		return propertyGroup;
//	}
	
	public static OWLClass createPropertyGroup(OWLOntology ontology, String prefixIRI, String propertyGroupCFIHOSUniqueCode, String propertyGroupCode,
			String propertyGroupDescription) {
		
		if (propertyGroupCFIHOSUniqueCode == null || propertyGroupCFIHOSUniqueCode.isBlank()) {
			return null;
		}
		IRI propertyGroupIRI = IRI.create(prefixIRI + propertyGroupCFIHOSUniqueCode);
		if(ontology.containsClassInSignature(propertyGroupIRI)) {
			return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(propertyGroupIRI);
		}
		
		OWLClass propertyGroup = OWLUtils.createClass(ontology, propertyGroupIRI);
		OWLUtils.addAnnotation(ontology, propertyGroup, getHasCFIHOSCode (prefixIRI), propertyGroupCFIHOSUniqueCode);
		
		if (propertyGroupDescription != null && !propertyGroupDescription.isBlank()) {
			OWLUtils.addAnnotation(ontology, propertyGroup, IRI.create(RDFConstants.RDFS_LABEL), propertyGroupDescription);
			OWLUtils.addAnnotation(ontology, propertyGroup, IRI.create(OWLUtils.IAO_DEFINITION_IRI), propertyGroupDescription);
		}
		
		if (propertyGroupCode != null && !propertyGroupCode.isBlank()) {
			OWLUtils.addAnnotation(ontology, propertyGroup, IRI.create(OWLUtils.SCHEMA_IDENTIFIER), propertyGroupCode);
		}
		
		OWLClass propertyGroupClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + PROPERTY_GROUP_CODE));
		OWLUtils.addSubclassOf(ontology, propertyGroup, propertyGroupClass);
		return propertyGroup;
	}
	
	private static OWLEntity addPropertyGroupingOrDecompositionPurpose (OWLOntology ontology, String prefixIRI, String propertyGroupingOrDecompositionPurposeCFIHOSUniqueCode, String propertyGroupingPurposeCode,
			String propertyGroupingPurposeDescription) {
		if (propertyGroupingOrDecompositionPurposeCFIHOSUniqueCode == null || propertyGroupingOrDecompositionPurposeCFIHOSUniqueCode.isBlank()) {
			return null;
		}
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		IRI propertyGroupingOrDecompositionPurposeIRI = IRI.create(prefixIRI + propertyGroupingOrDecompositionPurposeCFIHOSUniqueCode);
		if (ontology.containsClassInSignature(propertyGroupingOrDecompositionPurposeIRI)) {
			return df.getOWLClass(propertyGroupingOrDecompositionPurposeIRI);
		}
		
		OWLClass propertyGroupingOrDecompositionPurpose = df.getOWLClass(propertyGroupingOrDecompositionPurposeIRI);
		OWLUtils.addAnnotation(ontology, propertyGroupingOrDecompositionPurpose, getHasCFIHOSCode(prefixIRI), propertyGroupingOrDecompositionPurposeCFIHOSUniqueCode);
		
		if(propertyGroupingPurposeCode != null && !propertyGroupingPurposeCode.isBlank()) {
			OWLUtils.addAnnotation(ontology, propertyGroupingOrDecompositionPurpose, OWLRDFVocabulary.RDFS_LABEL.getIRI(), propertyGroupingPurposeCode);
		}
		
		if(propertyGroupingPurposeDescription != null && !propertyGroupingPurposeDescription.isBlank()) {
			OWLUtils.addAnnotation(ontology, propertyGroupingOrDecompositionPurpose, IRI.create(OWLUtils.IAO_DEFINITION_IRI), propertyGroupingPurposeDescription);
		}

		OWLClass purposeClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + "Purpose"));
		OWLUtils.addSubclassOf(ontology, propertyGroupingOrDecompositionPurpose, purposeClass);
		return propertyGroupingOrDecompositionPurpose;
	}
	
	private static OWLObjectProperty addPropertyGroupAllowedForPurpose (OWLOntology ontology, String prefixIRI, String propertyGroupAllowedForPurposeCFIHOSUniqueCode, String propertyGroupingOrDecompositionPurposeCFIHOSUniqueCode, String propertyGroupingPurposeCode,
			String propertyGroupingPurposeDescription) {
		if (propertyGroupAllowedForPurposeCFIHOSUniqueCode == null || propertyGroupAllowedForPurposeCFIHOSUniqueCode.isBlank()) {
			return null;
		}
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		IRI propertyGroupAllowedForPurposeIRI = IRI.create(prefixIRI + propertyGroupAllowedForPurposeCFIHOSUniqueCode);
		OWLObjectProperty propertyGroupAllowedForPurpose = df.getOWLObjectProperty(propertyGroupAllowedForPurposeIRI);
		OWLUtils.addAnnotation(ontology, propertyGroupAllowedForPurpose, getHasCFIHOSCode(prefixIRI), propertyGroupAllowedForPurposeCFIHOSUniqueCode);
		OWLEntity purpose = addPropertyGroupingOrDecompositionPurpose (ontology, prefixIRI, propertyGroupingOrDecompositionPurposeCFIHOSUniqueCode, propertyGroupingPurposeCode,
				propertyGroupingPurposeDescription);
		if(purpose != null) {
			OWLUtils.addAnnotation(ontology, propertyGroupAllowedForPurpose, getHasPurpose(prefixIRI), purpose.getIRI());
		}
		return propertyGroupAllowedForPurpose;
	}
	public static void addPropertyGrouping(OWLOntology ontology, String prefixIRI, String propertyGroupAllowedForPurposeCFIHOSUniqueCode,
			String propertyGroupingOrDecompositionPurposeCFIHOSUniqueCode, String propertyGroupingPurposeCode,
			String propertyGroupingPurposeDescription, String propertyGroupCFIHOSUniqueCode, String propertyGroupCode,
			String propertyGroupDescription, String propertyToGroupAssignmentCFIHOSUniqueCode,
			String propertyCFIHOSUniqueCode) {
		
		OWLClass propertyGroup = createPropertyGroup(ontology, prefixIRI, propertyGroupCFIHOSUniqueCode, propertyGroupCode, propertyGroupDescription);
		if (propertyGroup != null) {
			IRI propertyIRI = IRI.create(prefixIRI + propertyCFIHOSUniqueCode);
			OWLObjectProperty property = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(propertyIRI);
			//OWLUtils.addSubPropertyOf(ontology, property, propertyGroup);
			OWLUtils.addAnnotation(ontology, property, IRI.create(OWLUtils.SCHEMA_CATEGORY), propertyGroup.getIRI());
		}
//		OWLObjectProperty propertyGroupAllowedForPurpose = addPropertyGroupAllowedForPurpose (ontology, prefixIRI, propertyGroupAllowedForPurposeCFIHOSUniqueCode, propertyGroupingOrDecompositionPurposeCFIHOSUniqueCode, propertyGroupingPurposeCode,
//				propertyGroupingPurposeDescription);
//		
//		if(propertyGroupAllowedForPurpose != null) {
//			OWLUtils.addSubPropertyOf(ontology, propertyGroup, propertyGroupAllowedForPurpose);
//		}
	}

	/**
	 * Create quiality classes from the properties defined by CFIHOS for interoperability with IDO.
	 * @param ontology
	 */
	public static void includeQualitiesForIDO(OWLOntology ontology, String cfihosPrefix) {
		/* Get CFIHOS ontology */
		OWLOntology cfihosOntology = ontology.imports().filter(ont -> (Cfihos.CFIHOS_ONTOLOGY_IRI.equals(ont.getOntologyID().getOntologyIRI().get()))).findFirst().get();
		
		/* Create quality classes */
		OWLObjectProperty qualitativeProperty = OWLUtils.createObjectProperty(ontology, IRI.create(cfihosPrefix + QUALITATIVE_PROPERTY_CODE));
		OWLObjectProperty quantitativeProperty = OWLUtils.createObjectProperty(ontology, IRI.create(cfihosPrefix + 	QUANTITATIVE_PROPERTY_CODE));
		List<OWLObjectProperty> qualitativeProperties = EntitySearcher.getSubProperties(qualitativeProperty, ontology.importsClosure()).collect(Collectors.toList());
		List<OWLObjectProperty> quantitativeProperties = EntitySearcher.getSubProperties(quantitativeProperty, ontology.importsClosure()).collect(Collectors.toList());
		
		OWLClass parentQualityClass = OWLUtils.createClass(ontology, IRI.create(OWLUtils.IDO_NS + "Quality"));
		for(OWLObjectProperty property : qualitativeProperties) {
			includeQualityForIDO(ontology, property, cfihosPrefix, parentQualityClass);
		}
		
		OWLClass parentPhysicalQuantityClass = OWLUtils.createClass(ontology, IRI.create(OWLUtils.IDO_NS + "PhysicalQuantity"));
		for(OWLObjectProperty property : quantitativeProperties) {
			includeQualityForIDO(ontology, property, cfihosPrefix, parentPhysicalQuantityClass);
		}
		
		/* Add OWL axioms to tags */
		OWLClass tagParentClass = OWLUtils.createClass(ontology, IRI.create(Cfihos.getPrefixIRIForTags() + CFIHOS_TAG_CODE));
		OWLObjectProperty hasPhysicalQuantity = OWLUtils.createObjectProperty(ontology, IRI.create(OWLUtils.IDO_NS + "hasPhysicalQuantity"));
		OWLObjectProperty hasQuality = OWLUtils.createObjectProperty(ontology, IRI.create(OWLUtils.IDO_NS + "hasQuality"));
		OWLObjectProperty qualityQuantifiedAs = OWLUtils.createObjectProperty(ontology, IRI.create(OWLUtils.IDO_NS + "qualityQuantifiedAs"));
		OWLReasoner reasoner = new StructuralReasonerFactory().createReasoner(ontology);
		Set<OWLClass> tagClasses = reasoner.subClasses(tagParentClass, false).collect(Collectors.toSet());
		for(OWLClass tagClass : tagClasses) {
			cfihosOntology.subClassAxiomsForSubClass(tagClass).filter(axiom -> axiom.getSuperClass().isAnonymous()).forEach(subClassOfAxiom -> {
				OWLClassExpression subClassOfExpression = subClassOfAxiom.getSuperClass();
				
				/* Identify object property and class expression */
				List<?> components = subClassOfExpression.componentsWithoutAnnotations().collect(Collectors.toList());
				OWLObjectProperty objectProperty = (OWLObjectProperty) components.get(0);
				OWLClassExpression classExpression = (OWLClassExpression) components.get(1);
				
				/* Convert them into IDO schema */
				IRI qualityClassIRI = IRI.create(objectProperty.getIRI().toString() + "Quality");
				OWLClass qualityClass = OWLUtils.createClass(ontology, qualityClassIRI);
				/* If the quality is a physical quantity, use "Tag hasPhysicalQuantity Quality"; if not, use "Tag hasQuality Quality". */
				OWLObjectProperty tagToQualityRelationship = hasQuality;
				if(reasoner.getSuperClasses(qualityClass).containsEntity(parentPhysicalQuantityClass)) {
					tagToQualityRelationship = hasPhysicalQuantity;
				}
				OWLAxiom classExpression1 = OWLUtils.addObjectSomeValuesFromRestriction(ontology, tagToQualityRelationship, tagClass, qualityClass);
				OWLAxiom classExpression2 = OWLUtils.addObjectSomeValuesFromRestriction(ontology, qualityQuantifiedAs, qualityClass, classExpression);
				
				/* Add comment to CFIHOS ontology indicating the translation from object property to IDO schema */
				
				StringBuilder comment = new StringBuilder();
				comment.append("Conversion to IDO schema:\n");
				comment.append(classExpression1.toString()).append('\n');
				comment.append(classExpression2.toString());
				
				OWLUtils.addAnnotation(cfihosOntology, subClassOfAxiom, OWLRDFVocabulary.RDFS_COMMENT.getIRI(), comment.toString());
			});
		}
		
	}
	
	public static void includeQualityForIDO(OWLOntology ontology, OWLObjectProperty property, String cfihosPrefix, OWLClass parentQualityClass) {
		IRI qualityClassIRI = IRI.create(property.getIRI().toString() + "Quality");
		OWLClass qualityClass = OWLUtils.createClass(ontology, qualityClassIRI);
		OWLUtils.addSubclassOf(ontology, qualityClass, parentQualityClass);
		OWLUtils.addAnnotation(ontology, qualityClass, IRI.create("http://purl.org/pav/derivedFrom"), property.getIRI());
		ontology.importsClosure().forEach(importedOntology -> {
			EntitySearcher.getAnnotationAssertionAxioms(property, importedOntology).forEach(annotationAssertionAxiom -> {
				OWLAnnotationProperty annotationProperty = annotationAssertionAxiom.getProperty();
				if (!annotationProperty.getIRI().toString().equals(cfihosPrefix + CFIHOS_HAS_CFIHOS_CODE)) {
					OWLAnnotationValue value = annotationAssertionAxiom.getValue();
					OWLUtils.addAnnotation(ontology, qualityClass, annotationProperty.getIRI(), value);
				}
			});
		});
		
	}
	
	public static OWLEntity getEntityFromCFIHOS(OWLOntology ontology, String prefixIRI, String cfihosCode, OWLEntityType entityType) {
		OWLEntity entity = null;
		IRI entityIRI = IRI.create(prefixIRI + cfihosCode);
		if(entityType.equals(OWLEntityType.CLASS)) {
			if(ontology.containsClassInSignature(entityIRI)) {
				return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(entityIRI);
			} else {
				return null;
			}
		} else if (entityType.equals(OWLEntityType.OBJECT_PROPERTY)) {
			if(ontology.containsObjectPropertyInSignature(entityIRI)) {
				return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(entityIRI);
			} else {
				return null;
			}
		} else if (entityType.equals(OWLEntityType.DATA_PROPERTY)) {
			if(ontology.containsDataPropertyInSignature(entityIRI)) {
				return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLDataProperty(entityIRI);
			} else {
				return null;
			}
			
		} else if (entityType.equals(OWLEntityType.INDIVIDUAL)) {
			if(ontology.containsIndividualInSignature(entityIRI)) {
				return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(entityIRI);
			} else {
				return null;
			}
			
		} else if (entityType.equals(OWLEntityType.ANNOTATION_PROPERTY)) {
			if(ontology.containsAnnotationPropertyInSignature(entityIRI)) {
				return ontology.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationProperty(entityIRI);
			} else {
				return null;
			}
			
		}
		return entity;
	}

	public static void includeCFIHOSEquivalentMapping(OWLOntology ontology, String prefixIRI, String equipmentPrefixIRI,
			String tagPrefixIRI, String cfihosCode, String codingSourceCode, String cfihosEquivalentCode) {
		OWLEntity entity = null;
		IRI equivalentMappingPropertyIRI = IRI.create(prefixIRI + CFIHOSUtils.CFIHOS_OBJECT_EQUIVALENT_MAPPING);
		entity = getEntityFromCFIHOS(ontology, prefixIRI, cfihosCode, OWLEntityType.CLASS);
		if(entity != null) {
			OWLUtils.addAnnotationWithComment(ontology, entity, equivalentMappingPropertyIRI, cfihosEquivalentCode, codingSourceCode);
		}
		
		entity = getEntityFromCFIHOS(ontology, equipmentPrefixIRI, cfihosCode, OWLEntityType.CLASS);
		if(entity != null) {
			OWLUtils.addAnnotationWithComment(ontology, entity, equivalentMappingPropertyIRI, cfihosEquivalentCode, codingSourceCode);
		}
		
		
		entity = getEntityFromCFIHOS(ontology, tagPrefixIRI, cfihosCode, OWLEntityType.CLASS);
		if(entity != null) {
			OWLUtils.addAnnotationWithComment(ontology, entity, equivalentMappingPropertyIRI, cfihosEquivalentCode, codingSourceCode);
		}
		
		
		entity = getEntityFromCFIHOS(ontology, prefixIRI, cfihosCode, OWLEntityType.OBJECT_PROPERTY);
		if(entity != null) {
			OWLUtils.addAnnotationWithComment(ontology, entity, equivalentMappingPropertyIRI, cfihosEquivalentCode, codingSourceCode);
		}
		
		
		entity = getEntityFromCFIHOS(ontology, prefixIRI, cfihosCode, OWLEntityType.DATA_PROPERTY);
		if(entity != null) {
			OWLUtils.addAnnotationWithComment(ontology, entity, equivalentMappingPropertyIRI, cfihosEquivalentCode, codingSourceCode);
		}
		
		
		entity = getEntityFromCFIHOS(ontology, prefixIRI, cfihosCode, OWLEntityType.INDIVIDUAL);
		if(entity != null) {
			OWLUtils.addAnnotationWithComment(ontology, entity, equivalentMappingPropertyIRI, cfihosEquivalentCode, codingSourceCode);
		}
		
		
		entity = getEntityFromCFIHOS(ontology, prefixIRI, cfihosCode, OWLEntityType.ANNOTATION_PROPERTY);
		if(entity != null) {
			OWLUtils.addAnnotationWithComment(ontology, entity, equivalentMappingPropertyIRI, cfihosEquivalentCode, codingSourceCode);
		}
		
		

		
	}



}
