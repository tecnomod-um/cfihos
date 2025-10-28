package es.um.dis.cfihos.main;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFConstants;
import org.semanticweb.owlapi.search.EntitySearcher;

import es.um.dis.utils.OWLUtils;

public class CFIHOSUtils {
	public static void addEquipmentClass(OWLOntology ontology, String prefixIRI, String prefixIRIForEquipment,
			String classCode, String parentClassCode, String className, String classDefinition, String classSynonym) {

		OWLClass owlClass = OWLUtils.createClass(ontology, IRI.create(prefixIRIForEquipment + classCode));
		OWLUtils.addAnnotation(ontology, owlClass, IRI.create(prefixIRI + "hasCFIHOSCode"), classCode);

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
	
	public static void addEquipmentProperty(OWLOntology ontology, String prefixIRI, String prefixIRIForEquipment, String equipmentCode,
			String propertyCode, String propertyName, String unitOfMeasureSICode, String unitOfMeasureSIName,
			String unitOfMeasureImperialCode, String unitOfMeasureImperialName) {
		OWLObjectProperty property = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + propertyCode));
		//OWLClass measureClass = createClass(ontology, IRI.create("http://www.ontology-of-units-of-measure.org/resource/om-2/Measure"));

		if (propertyName != null && !propertyName.isEmpty()) {
			//addAnnotation(ontology, property, IRI.create(RDFConstants.RDFS_LABEL), propertyName);
		}
		if (unitOfMeasureSICode != null && !unitOfMeasureSICode.isEmpty()) {
			OWLNamedIndividual siUnitOfMeasure = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(prefixIRI + unitOfMeasureSICode);
			OWLUtils.addClassAssertion(ontology, siUnitOfMeasure, IRI.create(prefixIRI + "InternationalSystemUnit"));
			
			if (unitOfMeasureSIName != null && !unitOfMeasureSIName.isEmpty()) {
				OWLUtils.addAnnotation(ontology, siUnitOfMeasure, IRI.create(RDFConstants.RDFS_LABEL), unitOfMeasureSIName);
			}
		}
		
		if (unitOfMeasureImperialCode != null && !unitOfMeasureImperialCode.isEmpty()) {
			OWLNamedIndividual imperialUnitOfMeasure = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(prefixIRI + unitOfMeasureImperialCode);
			OWLUtils.addClassAssertion(ontology, imperialUnitOfMeasure, IRI.create(prefixIRI + "ImperialSystemUnit"));
			if (unitOfMeasureImperialName != null && !unitOfMeasureImperialName.isEmpty()) {
				OWLUtils.addAnnotation(ontology, imperialUnitOfMeasure, IRI.create(RDFConstants.RDFS_LABEL), unitOfMeasureImperialName);
			}
		}
		
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
		OWLUtils.addAnnotation(ontology, owlClass, IRI.create(prefixIRI + "hasCFIHOSCode"), disciplineCFIHOSCode);
		OWLUtils.addSubclassOf(ontology, owlClass, parentClass);
		if (disciplineCode != null && !disciplineCode.isEmpty()) {
			OWLUtils.addAnnotation(ontology, owlClass, IRI.create("http://schema.org/identifier"), disciplineCode);
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
		OWLUtils.addAnnotation(ontology, owlClass, IRI.create(prefixIRI + "hasCFIHOSCode"), documentCFIHOSCode);
		OWLUtils.addSubclassOf(ontology, owlClass, parentClass);
		
		if (documentShortCode != null && !documentShortCode.isEmpty()) {
			OWLUtils.addAnnotation(ontology, owlClass, IRI.create("http://schema.org/identifier"), documentShortCode);
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
		OWLUtils.addAnnotation(ontology, owlClass, IRI.create(prefixIRI + "hasCFIHOSCode"), tagClassCode);

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

		//if (propertyName != null && !propertyName.isEmpty()) {
			//addAnnotation(ontology, property, IRI.create(RDFConstants.RDFS_LABEL), propertyName);
		//}
		if (unitOfMeasureSICode != null && !unitOfMeasureSICode.isEmpty()) {
			OWLNamedIndividual siUnitOfMeasure = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(prefixIRI + unitOfMeasureSICode);
			OWLUtils.addClassAssertion(ontology, siUnitOfMeasure, IRI.create(prefixIRI + "InternationalSystemUnit"));
			
			if (unitOfMeasureSIName != null && !unitOfMeasureSIName.isEmpty()) {
				OWLUtils.addAnnotation(ontology, siUnitOfMeasure, IRI.create(RDFConstants.RDFS_LABEL), unitOfMeasureSIName);
			}
		}
		
		if (unitOfMeasureImperialCode != null && !unitOfMeasureImperialCode.isEmpty()) {
			OWLNamedIndividual imperialUnitOfMeasure = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(prefixIRI + unitOfMeasureImperialCode);
			OWLUtils.addClassAssertion(ontology, imperialUnitOfMeasure, IRI.create(prefixIRI + "ImperialSystemUnit"));
			if (unitOfMeasureImperialName != null && !unitOfMeasureImperialName.isEmpty()) {
				OWLUtils.addAnnotation(ontology, imperialUnitOfMeasure, IRI.create(RDFConstants.RDFS_LABEL), unitOfMeasureImperialName);
			}
		}
		
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
		OWLUtils.addAnnotation(ontology, disciplineDocumentClass, IRI.create(prefixIRI + "hasCFIHOSCode"), disciplineDocumentCode);
		
		if (disciplineCode != null && !disciplineCode.isEmpty()) {
			OWLObjectProperty hasDiscipline = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(prefixIRI + "hasDiscipline");
			OWLClass disciplineClass = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(prefixIRI + disciplineCode);
			OWLUtils.addObjectSomeValuesFromRestriction(ontology, hasDiscipline, disciplineDocumentClass, disciplineClass);
		}
		
		if(documentCode != null && !documentCode.isEmpty()) {
			OWLObjectProperty hasDocumentType = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(prefixIRI + "hasDocumentType");
			OWLClass document = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(prefixIRI + documentCode);
			OWLUtils.addObjectSomeValuesFromRestriction(ontology, hasDocumentType, disciplineDocumentClass, document);
		}
		
		if(disciplineDocumentShortCode != null && !disciplineDocumentShortCode.isEmpty()) {
			OWLUtils.addAnnotation(ontology, disciplineDocumentClass, IRI.create("http://schema.org/identifier"), disciplineDocumentShortCode);
		}
		
		if(assetTypeReference != null && !assetTypeReference.isEmpty()) {
			OWLUtils.addAnnotation(ontology, disciplineDocumentClass, IRI.create(prefixIRI + "hasAssetTypeReference"), assetTypeReference);
		}
		
		if(representationType != null && !representationType.isEmpty()) {
			OWLUtils.addAnnotation(ontology, disciplineDocumentClass, IRI.create(prefixIRI + "hasRepresentationType"), representationType);
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
		

		OWLUtils.addAnnotation(ontology, standard, IRI.create(prefixIRI + "hasCFIHOSCode"), standardCFIHOSCode);
		
		
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
			OWLUtils.addAnnotation(ontology, equipmentClass, IRI.create(prefixIRI + "hasSourceStandard"), standard.getIRI());
		}
		if(ontology.containsClassInSignature(tagIRI)) {
			OWLClass tagClass = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(tagIRI);
			OWLUtils.addAnnotation(ontology, tagClass, IRI.create(prefixIRI + "hasSourceStandard"), standard.getIRI());
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
		OWLClass parentPicklistClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + "Picklist"));
		OWLClass picklistClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + propertyPicklistCFIHOSCode));
		OWLAxiom axiom = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLSubClassOfAxiom(picklistClass, parentPicklistClass);
		ontology.add(axiom);
		
		OWLUtils.addAnnotation(ontology, picklistClass, IRI.create(prefixIRI + "hasCFIHOSCode"), propertyPicklistCFIHOSCode);
		if (propertyPicklistName != null && !propertyPicklistName.isEmpty()) {
			OWLUtils.addAnnotation(ontology, picklistClass, IRI.create(RDFConstants.RDFS_LABEL), propertyPicklistName);
		}
		
		OWLNamedIndividual propertyValue = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNamedIndividual(prefixIRI + propertyPicklistValueCFIHOSCode);
		OWLClass propertyPickListValue = OWLUtils.createClass(ontology, IRI.create(prefixIRI + "PropertyPickListValue"));
		axiom = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClassAssertionAxiom(propertyPickListValue, propertyValue);
		ontology.add(axiom);
		OWLUtils.addAnnotation(ontology, propertyValue, IRI.create(prefixIRI + "hasCFIHOSCode"), propertyPicklistValueCFIHOSCode);
		if(propertyPicklistValueCode != null && !propertyPicklistValueCode.isEmpty()) {
			OWLUtils.addAnnotation(ontology, propertyValue, IRI.create(RDFConstants.RDFS_LABEL), propertyPicklistValueCode);
		}
		if(propertyPicklistValueDescription != null && !propertyPicklistValueDescription.isEmpty()) {
			OWLUtils.addAnnotation(ontology, propertyValue, IRI.create(OWLUtils.IAO_DEFINITION_IRI), propertyPicklistValueDescription);
		}
		
		if(sourceStandardCFIHOSCode != null && !sourceStandardCFIHOSCode.isEmpty()) {
			OWLUtils.addAnnotation(ontology, picklistClass, IRI.create(prefixIRI + "hasSourceStandard"), IRI.create(prefixIRI + sourceStandardCFIHOSCode));
		}
	}

	public static void linkPropertyValues(OWLOntology ontology, String prefixIRI,
			Map<String, List<String>> propertyPickValuesMap) {
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		for(Entry<String, List<String>> entry : propertyPickValuesMap.entrySet()) {
			String propertyCode = entry.getKey();
			List<String> propertyValues = entry.getValue();
			OWLClass domainClass = df.getOWLClass(prefixIRI + propertyCode);
			List<OWLNamedIndividual> propertyValuesIndividuals = OWLUtils.getIndividualsFromList(ontology, prefixIRI, propertyValues);
			OWLObjectOneOf oneOf = df.getOWLObjectOneOf(propertyValuesIndividuals);
			OWLAxiom equivalentAxiom = df.getOWLEquivalentClassesAxiom(domainClass, oneOf);
			ontology.add(equivalentAxiom);
		}
		
	}
	public static void addProperty(OWLOntology ontology, String prefixIRI, String propertyCFIHOSCode,
			String propertyName, String propertyDefinition, String propertyRange) {
		if (propertyCFIHOSCode == null || propertyCFIHOSCode.isEmpty()) {
			return;
		}
		OWLObjectProperty objectProperty = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + propertyCFIHOSCode));
		OWLUtils.addAnnotation(ontology, objectProperty, IRI.create(prefixIRI + "hasCFIHOSCode"), propertyCFIHOSCode);
		
		if (propertyName != null && !propertyName.isEmpty()) {
			OWLUtils.addAnnotation(ontology, objectProperty, IRI.create(RDFConstants.RDFS_LABEL), propertyName);
		}
		
		if (propertyDefinition != null && !propertyDefinition.isEmpty()) {
			OWLUtils.addAnnotation(ontology, objectProperty, IRI.create(OWLUtils.IAO_DEFINITION_IRI), propertyDefinition);
		}
		
		if (propertyRange != null && !propertyRange.isEmpty()) {
			OWLClass rangeClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + propertyRange));
			OWLAxiom axiom = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectPropertyRangeAxiom(objectProperty, rangeClass);
			ontology.add(axiom);
		}
	}

	public static void addDocumentRequiredPerClass(OWLOntology ontology, String prefixIRI, String equipmentPrefixIRI,
			String tagPrefixIRI, String dataRequirementCFIHOSCode, String tagOrEquipmentCFIHOSCode,
			String standardCFIHOSCode, String documentTypeCFIHOSCode) {
		if (dataRequirementCFIHOSCode == null || dataRequirementCFIHOSCode.isEmpty()) {
			return;
		}
		OWLClass documentRequiredPerClassParentClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + "SourceStandardDocumentAndDataRequirement"));
		OWLClass documentRequiredPerClassClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + dataRequirementCFIHOSCode));
		OWLUtils.addSubclassOf(ontology, documentRequiredPerClassClass, documentRequiredPerClassParentClass);
		OWLUtils.addAnnotation(ontology, documentRequiredPerClassClass, IRI.create(prefixIRI + "hasCFIHOSCode"), dataRequirementCFIHOSCode);
		
		if(tagOrEquipmentCFIHOSCode != null && !tagOrEquipmentCFIHOSCode.isEmpty()) {
			if(ontology.containsClassInSignature(IRI.create(equipmentPrefixIRI + tagOrEquipmentCFIHOSCode))) {
				OWLClass equipmentClass = OWLUtils.createClass(ontology, IRI.create(equipmentPrefixIRI + tagOrEquipmentCFIHOSCode));
				OWLObjectProperty hasEquipment = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + "hasEquipment"));
				OWLUtils.addObjectSomeValuesFromRestriction(ontology, hasEquipment, documentRequiredPerClassClass, equipmentClass);
			}
			if(ontology.containsClassInSignature(IRI.create(tagPrefixIRI + tagOrEquipmentCFIHOSCode))) {
				OWLClass tagClass = OWLUtils.createClass(ontology, IRI.create(tagPrefixIRI + tagOrEquipmentCFIHOSCode));
				OWLObjectProperty hasTag = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + "hasTag"));
				OWLUtils.addObjectSomeValuesFromRestriction(ontology, hasTag, documentRequiredPerClassClass, tagClass);
			}
		}
		
		if(standardCFIHOSCode != null && !standardCFIHOSCode.isEmpty()) {
			OWLUtils.addAnnotation(ontology, documentRequiredPerClassClass, IRI.create(prefixIRI + "hasSourceStandard"), IRI.create(prefixIRI + standardCFIHOSCode));
		}
		
		if (documentTypeCFIHOSCode != null && !documentTypeCFIHOSCode.isEmpty()) {
			OWLProperty hasDocumentType = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(prefixIRI + "hasDocumentType");
			OWLClass documentTypeClass = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(prefixIRI + documentTypeCFIHOSCode);
			OWLUtils.addObjectSomeValuesFromRestriction(ontology, hasDocumentType, documentRequiredPerClassClass, documentTypeClass);
		}
		
	}

}
