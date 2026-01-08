package es.um.dis.cfihos.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFConstants;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import es.um.dis.utils.AnnotationEnricher;
import es.um.dis.utils.OWLUtils;

public class Cfihos {

	/* Input parameters */
	public static final String EXCEL_FILE = "CORE-CFIHOS-V2.0-excel-FINAL.xlsx";
	private static final String OUTPUT_FILE = "CORE-CFIHOS-V2.0.owl";

	private static final String OUTPUT_FILE_IDO = "CORE-CFIHOS-V2.0_ido.owl";
	public static final IRI CFIHOS_ONTOLOGY_IRI = IRI.create("http://infohub.siemens-energy.com/CFIHOS");
	public static final IRI CFIHOS_IDO_ONTOLOGY_IRI = IRI.create("http://infohub.siemens-energy.com/CFIHOS-IDO");
	
	/* Internal constants */
	private static final String DISCIPLINE_DOCUMENT_TYPE_SHEET_NAME = "discipline document type";
	private static final String EQUIPMENT_CLASS_SHEET_NAME = "equipment class";
	private static final String TAG_CLASS_SHEET_NAME = "tag class";
	private static final String EQUIPMENT_CLASS_PROPERTY_SHEET_NAME = "equipment class property";
	private static final String TAG_CLASS_PROPERTY_SHEET_NAME = "tag class property";
	private static final String DISCIPLINE_SHEET_NAME = "discipline";
	private static final String DOCUMENT_TYPE_SHEET_NAME = "document type";
	private static final String TAG_EQUIPMENT_CLASS_RELATIONSHIP_SHEET_NAME = "tag equipment class relationshi";
	private static final String SOURCE_STANDARD_SHEET_NAME = "source standard";
	private static final String TAG_OR_EQUIP_CLASS_SRC_STANDARD_SHEET_NAME = "tag or equip class src standard";
	private static final String PROPERTY_PICKLIST_VALUES_SHEET_NAME = "property picklist values";
	private static final String PROPERTY_SHEET_NAME = "property";
	private static final String DOCUMENT_REQUIRED_PER_CLASS_SHEET_NAME = "document required per class";
	private static final String UNIT_OF_MEASURE_SHEET_NAME = "unit of measure";
	private static final String PROPERTY_GROUPINGS_SHEET_NAME = "property groupings";
	
	/* IDO */
	private static final IRI IDO_ONTOLOGY_IRI = IRI.create("http://rds.posccaesar.org/ontology/lis14/ont/core");
	
	
	
	public static void main(String[] args) throws OWLOntologyStorageException, OWLOntologyCreationException, IOException {
		InputStream inputStream = Cfihos.class.getClassLoader().getResourceAsStream(EXCEL_FILE);
		OWLOntology ontology = generateOntology(inputStream);
		ontology.saveOntology(new FileOutputStream(new File(OUTPUT_FILE)));
		
		OWLOntology ontologyIDO = generateIDOCompliantOntology(OUTPUT_FILE, getPrefixIRI());
		ontologyIDO.imports().filter(x -> x.getOntologyID().getOntologyIRI().get().equals(CFIHOS_ONTOLOGY_IRI)).findFirst().get().saveOntology(new FileOutputStream(new File(OUTPUT_FILE)));
		ontologyIDO.saveOntology(new FileOutputStream(new File(OUTPUT_FILE_IDO)));
	}
	
	

	private static String getPrefixIRI() {
		String prefixIRI = CFIHOS_ONTOLOGY_IRI.getIRIString() + "#";
		return prefixIRI;
	}
	
	private static String getPrefixIRIForEquipment() {
		String prefixIRI = CFIHOS_ONTOLOGY_IRI.getIRIString() + "/equipment" + "#";
		return prefixIRI;
	}
	
	public static String getPrefixIRIForTags() {
		String prefixIRI = CFIHOS_ONTOLOGY_IRI.getIRIString() + "/tag" + "#";
		return prefixIRI;
	}
	
	public static OWLOntology generateOntology(InputStream is) throws OWLOntologyCreationException, IOException {
		OWLOntology ontology = OWLManager.createOWLOntologyManager().createOntology(CFIHOS_ONTOLOGY_IRI);
		Workbook workbook = new XSSFWorkbook(is);

		includeBaseEntities(ontology);
		includeUnitsOfMeasurement(workbook, ontology);
		includeProperties(workbook, ontology);
		includePropertiesGroupings(workbook, ontology);
		includeEquipmentClasses(workbook, ontology);
		includeEquipmentProperties(workbook, ontology);
		includeDisciplines(workbook, ontology);
		includeDocumentType(workbook, ontology);
		includeDisciplineDocumentType(workbook, ontology);
		includeTagClasses(workbook, ontology);
		includeTagProperties(workbook, ontology);
		includeEquipmentTagRelationships(workbook, ontology);
		includeStandards(workbook, ontology);
		includeTagOrEquipmentStandards(workbook, ontology);
		includePropertyPickListValues(workbook, ontology);
		includeDocumentRequiredPerClass(workbook, ontology);
		includeDisjointClasses(ontology);
		enrichWithExternalAnnotations(ontology);
		return ontology;
	}

	private static void includeBaseEntities(OWLOntology ontology) {
		String prefixIRI = getPrefixIRI();
		
		OWLClass unitClass = OWLUtils.createClass(ontology, IRI.create(OWLUtils.OM2_UNIT));
		OWLClass internationalSystemUnitClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + CFIHOSUtils.INTERNATIONAL_SYSTEM_UNIT));
		OWLUtils.addAnnotation(ontology, internationalSystemUnitClass, IRI.create(RDFConstants.RDFS_LABEL), "international system unit");
		OWLClass imperialSystemUnitClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + CFIHOSUtils.IMPERIAL_SYSTEM_UNIT));
		OWLUtils.addAnnotation(ontology, imperialSystemUnitClass, IRI.create(RDFConstants.RDFS_LABEL), "imperial system unit");
		OWLUtils.addSubclassOf(ontology, internationalSystemUnitClass, unitClass);
		OWLUtils.addSubclassOf(ontology, imperialSystemUnitClass, unitClass);
		
		OWLClass measureClass = OWLUtils.createClass(ontology, IRI.create(OWLUtils.OM2_NS + CFIHOSUtils.MEASURE));
		OWLDataProperty hasNumericalValue = OWLUtils.createDataProperty(ontology, IRI.create(OWLUtils.OM2_HAS_NUMERICAL_VALUE));
		OWLUtils.addDomain(ontology, hasNumericalValue, measureClass);
		OWLUtils.addDataPropertyRange(ontology, hasNumericalValue, OWL2Datatype.XSD_DECIMAL);
		OWLObjectProperty hasUnit = OWLUtils.createObjectProperty(ontology, IRI.create(OWLUtils.OM2_HAS_UNIT));
		OWLUtils.addDomain(ontology, hasUnit, measureClass);
		OWLUtils.addRange(ontology, hasUnit, unitClass);
		
		OWLClass sourceStandardDocumentAndDataRequirementClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + CFIHOSUtils.SOURCE_STANDARD_DOCUMENT_AND_DATA_REQUIREMENT));
		OWLUtils.addAnnotation(ontology, sourceStandardDocumentAndDataRequirementClass, IRI.create(RDFConstants.RDFS_LABEL), "source standard document and data requirement");
		OWLUtils.addAnnotation(ontology, sourceStandardDocumentAndDataRequirementClass, IRI.create(OWLUtils.IAO_DEFINITION_IRI), "A requirement for document or data, as expressed in a source standard, irrespective of the equipment class.");
		
		OWLClass standardClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + CFIHOSUtils.STANDARD));
		OWLUtils.addAnnotation(ontology, standardClass, IRI.create(RDFConstants.RDFS_LABEL), "standard");
		OWLUtils.addAnnotation(ontology, standardClass, IRI.create(OWLUtils.SKOS_ALT_LABEL_IRI), "source standard");
		OWLUtils.addAnnotation(ontology, standardClass, IRI.create(OWLUtils.IAO_DEFINITION_IRI), "A standard used to define requirements like engineering, operations, etc..");
		
		
		
		OWLClass pickListClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + CFIHOSUtils.PROPERTY_PICKLIST));
		OWLUtils.addAnnotation(ontology, pickListClass, IRI.create(RDFConstants.RDFS_LABEL), "property picklist");
		OWLUtils.addAnnotation(ontology, pickListClass, IRI.create(OWLUtils.IAO_DEFINITION_IRI), "A set of possible values that can be assigned to a property.");

		
//		OWLClass purposeClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + CFIHOSUtils.PURPOSE));
//		OWLUtils.addAnnotation(ontology, purposeClass, IRI.create(RDFConstants.RDFS_LABEL), "purpose");
//		OWLUtils.addAnnotation(ontology, purposeClass, IRI.create(OWLUtils.SKOS_ALT_LABEL_IRI), "property grouping or decomposition purpose");
//		OWLUtils.addAnnotation(ontology, purposeClass, IRI.create(OWLUtils.IAO_DEFINITION_IRI), "A reason, or purpose, for which properties need to be grouped.");
		
		OWLClass propertyGroupClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + CFIHOSUtils.PROPERTY_GROUP));
		OWLUtils.addAnnotation(ontology, propertyGroupClass, IRI.create(RDFConstants.RDFS_LABEL), "property group");
		OWLUtils.addAnnotation(ontology, propertyGroupClass, IRI.create(OWLUtils.IAO_DEFINITION_IRI), "Property groups can be used for multiple purposes. But if a property group can be used for a given purpose, then it should be documented as a property group allowed for that purpose.");
		
		OWLObjectProperty property = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + CFIHOSUtils.PROPERTY));
		OWLUtils.addAnnotation(ontology, property, IRI.create(RDFConstants.RDFS_LABEL), CFIHOSUtils.PROPERTY);
		OWLUtils.addAnnotation(ontology, property, IRI.create(OWLUtils.IAO_DEFINITION_IRI), "A type of feature that is used to distinguish and describe tags, equipment, models) or their class.");
		
		
		OWLObjectProperty quantitativeProperty = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + CFIHOSUtils.QUANTITATIVE_PROPERTY));
		OWLUtils.addAnnotation(ontology, quantitativeProperty, IRI.create(RDFConstants.RDFS_LABEL), "quantitative property");
		OWLUtils.addAnnotation(ontology, quantitativeProperty, IRI.create(OWLUtils.IAO_DEFINITION_IRI), "A type of feature that (a) is used to distinguish and describe tags, equipment, models or their class (b) is numeric (c) may have an associated unit of measure.");
		OWLUtils.addRange(ontology, quantitativeProperty, measureClass);
		OWLUtils.addSubPropertyOf(ontology, quantitativeProperty, property);
		
		OWLObjectProperty qualitativeProperty = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + CFIHOSUtils.QUALITATIVE_PROPERTY));
		OWLUtils.addAnnotation(ontology, qualitativeProperty, IRI.create(RDFConstants.RDFS_LABEL), "qualitative property");
		OWLUtils.addAnnotation(ontology, qualitativeProperty, IRI.create(OWLUtils.IAO_DEFINITION_IRI), "A type of feature that (a) is used to distinguish and describe tags, equipment, models or their class (b) may have defined list of values to select from (c) is non-numeric.");
		OWLUtils.addSubPropertyOf(ontology, qualitativeProperty, property);
		
		
		OWLObjectProperty hasDiscipline = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + CFIHOSUtils.HAS_DISCIPLINE));
		OWLUtils.addAnnotation(ontology, hasDiscipline, IRI.create(RDFConstants.RDFS_LABEL), "has discipline");
		
		OWLObjectProperty hasDocumentType = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + CFIHOSUtils.HAS_DOCUMENT_TYPE));
		OWLUtils.addAnnotation(ontology, hasDocumentType, IRI.create(RDFConstants.RDFS_LABEL), "has document type");
		
		OWLObjectProperty hasEquipment = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + CFIHOSUtils.HAS_EQUIPMENT));
		OWLUtils.addAnnotation(ontology, hasEquipment, IRI.create(RDFConstants.RDFS_LABEL), "has equipment");
		
		OWLObjectProperty hasMeasurementSystem = OWLUtils.createObjectProperty(ontology, IRI.create(prefixIRI + CFIHOSUtils.HAS_MEASUREMENT_SYSTEM));
		OWLUtils.addAnnotation(ontology, hasMeasurementSystem, IRI.create(RDFConstants.RDFS_LABEL), "has measurement system");
		
		OWLAnnotationProperty hasCFIHOSCode = OWLUtils.createAnnotationProperty(ontology, IRI.create(prefixIRI + CFIHOSUtils.CFIHOS_HAS_CFIHOS_CODE));
		OWLUtils.addAnnotation(ontology, hasCFIHOSCode, IRI.create(RDFConstants.RDFS_LABEL), "has CFIHOS code");
		
		OWLAnnotationProperty hasAssetTypeReference = OWLUtils.createAnnotationProperty(ontology, IRI.create(prefixIRI + CFIHOSUtils.HAS_ASSET_TYPE_REFERENCE));
		OWLUtils.addAnnotation(ontology, hasAssetTypeReference, IRI.create(RDFConstants.RDFS_LABEL), "has asset type reference");
		
		OWLAnnotationProperty hasRepresentationType = OWLUtils.createAnnotationProperty(ontology, IRI.create(prefixIRI + CFIHOSUtils.HAS_REPRESENTATION_TYPE));
		OWLUtils.addAnnotation(ontology, hasRepresentationType, IRI.create(RDFConstants.RDFS_LABEL), "has representation type");
		
		OWLAnnotationProperty hasSourceStandard = OWLUtils.createAnnotationProperty(ontology, IRI.create(prefixIRI + CFIHOSUtils.HAS_SOURCE_STANDARD));
		OWLUtils.addAnnotation(ontology, hasSourceStandard, IRI.create(RDFConstants.RDFS_LABEL), "has source standard");
		
		OWLAnnotationProperty hasUNECECode = OWLUtils.createAnnotationProperty(ontology, IRI.create(prefixIRI + CFIHOSUtils.HAS_UNECE_CODE));
		OWLUtils.addAnnotation(ontology, hasUNECECode, IRI.create(RDFConstants.RDFS_LABEL), "has UNECE code");
		
	}

	

	

	private static void includeDisjointClasses(OWLOntology ontology) {
		OWLReasoner reasoner = new StructuralReasonerFactory().createReasoner(ontology);
		OWLClass thing = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing();
		Collection<OWLClass> parentClasses = reasoner.subClasses(thing, true).filter(x -> !x.isOWLNothing()).collect(Collectors.toSet());
		OWLUtils.setDisjointClasses(ontology, parentClasses);
	}
	
	private static void includeUnitsOfMeasurement(Workbook workbook, OWLOntology ontology) {
		Sheet sheet = workbook.getSheet(UNIT_OF_MEASURE_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		for(Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String unitCFIHOSCode = null;
			String uneceCode = null;
			String unitName = null;
			String unitSymbol = null;
			String unitDimensionCFIHOSCode = null;
			String unitDimensionCode = null;
			String unitDimensionName = null;
			String measurementSystemCFIHOSCode = null;
			String measurementSystemCode = null;
			String unitSynonymName = null;
			
			if(row.getCell(0) != null) {
				unitCFIHOSCode = row.getCell(0).getStringCellValue();
			}
			
			if(row.getCell(1) != null) {
				uneceCode = row.getCell(1).getStringCellValue();
			}
			
			if(row.getCell(2) != null) {
				unitName = row.getCell(2).getStringCellValue();
			}
			
			if(row.getCell(3) != null) {
				unitSymbol = row.getCell(3).getStringCellValue();
			}
			
			if(row.getCell(4) != null) {
				unitDimensionCFIHOSCode = row.getCell(4).getStringCellValue();
			}
			
			if(row.getCell(5) != null) {
				unitDimensionCode = row.getCell(5).getStringCellValue();
			}
			
			if(row.getCell(6) != null) {
				unitDimensionName = row.getCell(6).getStringCellValue();
			}
			
			if(row.getCell(7) != null) {
				measurementSystemCFIHOSCode = row.getCell(7).getStringCellValue();
			}
			
			if(row.getCell(8) != null) {
				measurementSystemCode = row.getCell(8).getStringCellValue();
			}
			
			if(row.getCell(9) != null) {
				unitSynonymName = row.getCell(9).getStringCellValue();
			}
			
			CFIHOSUtils.addUnitOfMeasurement(ontology, prefixIRI, unitCFIHOSCode, uneceCode, unitName, unitSymbol, unitDimensionCFIHOSCode, unitDimensionCode, unitDimensionName, measurementSystemCFIHOSCode, measurementSystemCode, unitSynonymName);
		}
	}

	private static void includeDocumentRequiredPerClass(Workbook workbook, OWLOntology ontology) {
		Sheet sheet = workbook.getSheet(DOCUMENT_REQUIRED_PER_CLASS_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		String equipmentPrefixIRI = getPrefixIRIForEquipment();
		String tagPrefixIRI = getPrefixIRIForTags();
		for(Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			
			String dataRequirementCFIHOSCode = null;
			String tagOrEquipmentCFIHOSCode = null;
			String standardCFIHOSCode = null;
			String documentTypeCFIHOSCode = null;
			
			if(row.getCell(0) != null) {
				dataRequirementCFIHOSCode = row.getCell(0).getStringCellValue();
			}
			
			if(row.getCell(1) != null) {
				tagOrEquipmentCFIHOSCode = row.getCell(1).getStringCellValue();
			}
			
			if(row.getCell(4) != null) {
				standardCFIHOSCode = row.getCell(4).getStringCellValue();
			}
			
			if(row.getCell(6) != null) {
				documentTypeCFIHOSCode = row.getCell(6).getStringCellValue();
			}
			
			CFIHOSUtils.addDocumentRequiredPerClass(ontology, prefixIRI, equipmentPrefixIRI, tagPrefixIRI, dataRequirementCFIHOSCode, tagOrEquipmentCFIHOSCode, standardCFIHOSCode, documentTypeCFIHOSCode);
		}
	}

	private static void includeProperties(Workbook workbook, OWLOntology ontology) {
		Sheet sheet = workbook.getSheet(PROPERTY_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		for(Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String propertyCFIHOSCode = null;
			String propertyName = null;
			String propertyDefinition = null;
			String propertyDataType = null;
			String unitOfMeasureDimension = null;
			String propertyRange = null;
			
			if(row.getCell(0) != null) {
				propertyCFIHOSCode = row.getCell(0).getStringCellValue();
			}
			if(row.getCell(1) != null) {
				propertyName = row.getCell(1).getStringCellValue();
			}
			if(row.getCell(2) != null) {
				propertyDefinition = row.getCell(2).getStringCellValue();
			}
			if(row.getCell(3) != null) {
				propertyDataType = row.getCell(3).getStringCellValue();
			}
			if(row.getCell(5) != null) {
				unitOfMeasureDimension = row.getCell(5).getStringCellValue();
			}
			if(row.getCell(7) != null) {
				propertyRange = row.getCell(7).getStringCellValue();
			}
			
			CFIHOSUtils.addProperty(ontology, prefixIRI, propertyCFIHOSCode, propertyName, propertyDefinition, propertyDataType, unitOfMeasureDimension, propertyRange);
		}
	}
	
	private static void includePropertiesGroupings(Workbook workbook, OWLOntology ontology) {
		Sheet sheet = workbook.getSheet(PROPERTY_GROUPINGS_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		for(Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String propertyGroupAllowedForPurposeCFIHOSUniqueCode = null;
			String propertyGroupingOrDecompositionPurposeCFIHOSUniqueCode = null;
			String propertyGroupingPurposeCode = null;
			String propertyGroupingPurposeDescription = null;
			String propertyGroupCFIHOSUniqueCode = null;
			String propertyGroupCode = null;
			String propertyGroupDescription = null;
			String propertyToGroupAssignmentCFIHOSUniqueCode = null;
			String propertyCFIHOSUniqueCode = null;
			
			if(row.getCell(0) != null) {
				propertyGroupAllowedForPurposeCFIHOSUniqueCode = row.getCell(0).getStringCellValue();
			}
			
			if(row.getCell(1) != null) {
				propertyGroupingOrDecompositionPurposeCFIHOSUniqueCode = row.getCell(1).getStringCellValue();
			}
			if(row.getCell(2) != null) {
				propertyGroupingPurposeCode = row.getCell(2).getStringCellValue();
			}
			if(row.getCell(3) != null) {
				propertyGroupingPurposeDescription = row.getCell(3).getStringCellValue();
			}
			if(row.getCell(6) != null) {
				propertyGroupCFIHOSUniqueCode = row.getCell(6).getStringCellValue();
			}
			if(row.getCell(7) != null) {
				propertyGroupCode = row.getCell(7).getStringCellValue();
			}
			if(row.getCell(8) != null) {
				propertyGroupDescription = row.getCell(8).getStringCellValue();
			}
			if(row.getCell(9) != null) {
				propertyToGroupAssignmentCFIHOSUniqueCode = row.getCell(9).getStringCellValue();
			}
			if(row.getCell(12) != null) {
				propertyCFIHOSUniqueCode = row.getCell(12).getStringCellValue();
			}
			CFIHOSUtils.addPropertyGrouping(ontology, prefixIRI, propertyGroupAllowedForPurposeCFIHOSUniqueCode, propertyGroupingOrDecompositionPurposeCFIHOSUniqueCode, 
					propertyGroupingPurposeCode, propertyGroupingPurposeDescription, propertyGroupCFIHOSUniqueCode, 
					propertyGroupCode, propertyGroupDescription, propertyToGroupAssignmentCFIHOSUniqueCode, propertyCFIHOSUniqueCode);
		}
	}

	private static void includePropertyPickListValues(Workbook workbook, OWLOntology ontology) {
		Map<String, List<String>> propertyPickValuesMap = getPropertyPickValuesMap(workbook);
		Sheet sheet = workbook.getSheet(PROPERTY_PICKLIST_VALUES_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		
		for(Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String propertyPicklistCFIHOSCode = null;
			String propertyPicklistName = null;
			String propertyPicklistValueCFIHOSCode = null;
			String propertyPicklistValueCode = null;
			String propertyPicklistValueDescription = null;
			String sourceStandardCFIHOSCode = null;
			
			if(row.getCell(0)!= null) {
				propertyPicklistCFIHOSCode = row.getCell(0).getStringCellValue();
			}
			
			if(row.getCell(1)!= null) {
				propertyPicklistName = row.getCell(1).getStringCellValue();
			}
			
			if(row.getCell(2)!= null) {
				propertyPicklistValueCFIHOSCode = row.getCell(2).getStringCellValue();
			}
			
			if(row.getCell(3)!= null) {
				propertyPicklistValueCode = row.getCell(3).getStringCellValue();
			}
			
			if(row.getCell(4)!= null) {
				propertyPicklistValueDescription = row.getCell(4).getStringCellValue();
			}
			
			if(row.getCell(5)!= null) {
				sourceStandardCFIHOSCode = row.getCell(5).getStringCellValue();
			}
			
			CFIHOSUtils.addPropertyPicklistValue(ontology, prefixIRI, propertyPicklistCFIHOSCode, propertyPicklistName, propertyPicklistValueCFIHOSCode, propertyPicklistValueCode, propertyPicklistValueDescription, sourceStandardCFIHOSCode);
		}
		CFIHOSUtils.linkPropertyValues(ontology, prefixIRI, propertyPickValuesMap);
		
	}

	private static Map<String, List<String>> getPropertyPickValuesMap(Workbook workbook) {
		Map<String, List<String>> propertyMap = new HashMap<String, List<String>>();
		Sheet sheet = workbook.getSheet(PROPERTY_PICKLIST_VALUES_SHEET_NAME);
		
		for(Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String propertyPicklistCode = row.getCell(0).getStringCellValue();
			String propertyPicklistValueCode = row.getCell(2).getStringCellValue();
			if (!propertyMap.containsKey(propertyPicklistCode)) {
				propertyMap.put(propertyPicklistCode, new ArrayList<>());
			}
			propertyMap.get(propertyPicklistCode).add(propertyPicklistValueCode);
		}
		return propertyMap;
	}

	private static void includeTagOrEquipmentStandards(Workbook workbook, OWLOntology ontology) {
		Sheet sheet = workbook.getSheet(TAG_OR_EQUIP_CLASS_SRC_STANDARD_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		String equipmentPrefixIRI = getPrefixIRIForEquipment();
		String tagPrefixIRI = getPrefixIRIForTags();
		for(Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String tagOrEquipmentCode = null;
			String sourceStandardCode = null;
			
			if(row.getCell(0)!= null) {
				tagOrEquipmentCode = row.getCell(0).getStringCellValue();
			}
			
			if(row.getCell(2)!= null) {
				sourceStandardCode = row.getCell(2).getStringCellValue();
			}
			
			CFIHOSUtils.addTagOrEquipmentStandards(ontology, prefixIRI, equipmentPrefixIRI, tagPrefixIRI, tagOrEquipmentCode, sourceStandardCode);
		}
		
	}

	private static void includeStandards(Workbook workbook, OWLOntology ontology) {
		Sheet standardsSheet = workbook.getSheet(SOURCE_STANDARD_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		OWLClass standardClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + CFIHOSUtils.STANDARD));
		for (Row row : standardsSheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String standardCFIHOSCode = null;
			String standardName = null;
			String standardDescription = null;
			
			if(row.getCell(0)!= null) {
				standardCFIHOSCode = row.getCell(0).getStringCellValue();
			}
			if(row.getCell(1)!= null) {
				standardName = row.getCell(1).getStringCellValue();
			}
			if(row.getCell(2)!= null) {
				standardDescription = row.getCell(2).getStringCellValue();
			}
			
			CFIHOSUtils.addStandard(ontology, prefixIRI, standardCFIHOSCode, standardName, standardDescription, standardClass);
		}
		
	}

	private static void includeEquipmentClasses(Workbook workbook, OWLOntology ontology) {
		Sheet equipmentClassSheet = workbook.getSheet(EQUIPMENT_CLASS_SHEET_NAME);
		
		Map<String, String> codeByNameMap = getCodeByNameMap(equipmentClassSheet);
		String prefixIRI = getPrefixIRI();
		String prefixIRIForEquipment = getPrefixIRIForEquipment();
		for(Row row : equipmentClassSheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String parentClassName = null;
			String parentClassCode = null;
			String classCode = null;
			String className = null;
			String classDefinition = null;
			String classSynonym = null;
			
			if (row.getCell(0) != null) {
				parentClassName = row.getCell(0).getStringCellValue();
				parentClassCode = codeByNameMap.get(parentClassName);
			}
			if (row.getCell(1) != null) {
				classCode = row.getCell(1).getStringCellValue();
			}
			if (row.getCell(2) != null) {
				className = row.getCell(2).getStringCellValue();
				/* Change label for "class" class */
				if ("class".equals(className)) {
					className = "Equipment";
				}
			}
			if (row.getCell(3) != null) {
				classDefinition = row.getCell(3).getStringCellValue();
			}
			if (row.getCell(7) != null) {
				classSynonym = row.getCell(7).getStringCellValue();
			}
			
			CFIHOSUtils.addEquipmentClass(ontology, prefixIRI, prefixIRIForEquipment, classCode, parentClassCode, className, classDefinition, classSynonym);
		}
	}

	

	private static Map<String, String> getCodeByNameMap(Sheet equipmentClassSheet) {
		Map<String, String> codeByNameMap = new HashMap<>();
		for(Row row : equipmentClassSheet) {
			if (row.getCell(1) != null && row.getCell(2) != null) {
				String classCode = row.getCell(1).getStringCellValue();
				String className = row.getCell(2).getStringCellValue();
				codeByNameMap.put(className, classCode);
			}
			
		}
		return codeByNameMap;
	}
	
	private static void includeEquipmentProperties(Workbook workbook, OWLOntology ontology) {
		Sheet equipmentClassPropertySheet = workbook.getSheet(EQUIPMENT_CLASS_PROPERTY_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		String prefixIRIForEquipment = getPrefixIRIForEquipment();
		
		for(Row row : equipmentClassPropertySheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String equipmentCode = null;
			String propertyCode = null;
			String propertyName = null;
			String unitOfMeasureSICode = null;
			String unitOfMeasureSIName = null;
			String unitOfMeasureImperialCode = null;
			String unitOfMeasureImperialName = null;
			
			if (row.getCell(0) != null) {
				equipmentCode = row.getCell(0).getStringCellValue();
			}
			
			if (row.getCell(2) != null) {
				propertyCode = row.getCell(2).getStringCellValue();
			}
			
			if (row.getCell(3) != null) {
				propertyName = row.getCell(3).getStringCellValue();
			}
			
			if (row.getCell(6) != null) {
				unitOfMeasureSICode = row.getCell(6).getStringCellValue();
			}
			if (row.getCell(7) != null) {
				unitOfMeasureSIName = row.getCell(7).getStringCellValue();
			}
			if (row.getCell(8) != null) {
				unitOfMeasureImperialCode = row.getCell(8).getStringCellValue();
			}
			if (row.getCell(9) != null) {
				unitOfMeasureImperialName = row.getCell(9).getStringCellValue();
			}
			
			CFIHOSUtils.addEquipmentProperty(ontology, prefixIRI, prefixIRIForEquipment, equipmentCode, propertyCode, propertyName, unitOfMeasureSICode, unitOfMeasureSIName, unitOfMeasureImperialCode, unitOfMeasureImperialName);
		}
	}	
	
	private static void includeDisciplines(Workbook workbook, OWLOntology ontology) {
		Sheet disciplineSheet = workbook.getSheet(DISCIPLINE_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		OWLClass disciplineClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + "Discipline"));
		OWLUtils.addAnnotation(ontology, disciplineClass, IRI.create(RDFConstants.RDFS_LABEL), "Discipline");
		OWLUtils.addAnnotation(ontology, disciplineClass, IRI.create(OWLUtils.IAO_DEFINITION_IRI), "A branch of knowledge of expertise which is responsible for the content of the deliverable.");
		for(Row row : disciplineSheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String disciplineCFIHOSCode = null;
			String disciplineCode = null;
			String disciplineName = null;
			String disciplineDescription = null;
			
			if (row.getCell(0) != null) {
				disciplineCFIHOSCode = row.getCell(0).getStringCellValue();
			}
			if (row.getCell(1) != null) {
				disciplineCode = row.getCell(1).getStringCellValue();
			}
			if (row.getCell(2) != null) {
				disciplineName = row.getCell(2).getStringCellValue();
			}
			if (row.getCell(3) != null) {
				disciplineDescription = row.getCell(3).getStringCellValue();
			}
			
			CFIHOSUtils.addDiscipline(ontology, prefixIRI, disciplineCFIHOSCode, disciplineCode, disciplineName, disciplineDescription, disciplineClass);
		}
		
	}
	
	
	private static void includeDocumentType(Workbook workbook, OWLOntology ontology) {
		Sheet sheet = workbook.getSheet(DOCUMENT_TYPE_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		OWLClass documentClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + "Document"));
		OWLUtils.addAnnotation(ontology, documentClass, IRI.create(RDFConstants.RDFS_LABEL), "Document");
		OWLUtils.addAnnotation(ontology, documentClass, IRI.create(OWLUtils.IAO_DEFINITION_IRI), "A classification of documents.");
		for(Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String documentCFIHOSCode = null;
			String documentShortCode = null;
			String documentName = null;
			String documentDescription = null;
			String documentTypeClassification = null;
			String documentTypeSynonym = null;
			
			if (row.getCell(0) != null) {
				documentCFIHOSCode = row.getCell(0).getStringCellValue();
			}
			if (row.getCell(1) != null) {
				documentShortCode = row.getCell(1).getStringCellValue();
			}
			if (row.getCell(2) != null) {
				documentName = row.getCell(2).getStringCellValue();
			}
			if (row.getCell(3) != null) {
				documentDescription = row.getCell(3).getStringCellValue();
			}
			if (row.getCell(4) != null) {
				documentTypeClassification = row.getCell(4).getStringCellValue();
			}
			if (row.getCell(5) != null) {
				documentTypeSynonym = row.getCell(5).getStringCellValue();
			}
			
			CFIHOSUtils.addDocumentType(ontology, prefixIRI, documentCFIHOSCode, documentShortCode, documentName, documentDescription, documentTypeClassification, documentTypeSynonym, documentClass);
		}
		
	}
	
	private static void includeDisciplineDocumentType(Workbook workbook, OWLOntology ontology) {
		Sheet sheet = workbook.getSheet(DISCIPLINE_DOCUMENT_TYPE_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		String prefixIRIForTags = getPrefixIRIForTags();
		String prefixIRIForEquipment = getPrefixIRIForEquipment();
		OWLClass disciplineDocumentClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + "DisciplineDocumentType"));
		OWLUtils.addAnnotation(ontology, disciplineDocumentClass, IRI.create(RDFConstants.RDFS_LABEL), "discipline document");
		OWLUtils.addAnnotation(ontology, disciplineDocumentClass, IRI.create(OWLUtils.IAO_DEFINITION_IRI), "An identification of what type of document is required for what discipline, and what are their delivery requirements.");
		
		for(Row row : sheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String disciplineDocumentCode = null;
			String disciplineCode = null;
			String documentCode = null;
			String disciplineDocumentShortCode = null;
			String assetTypeReference = null;
			String representationType = null;
			
			if (row.getCell(0) != null) {
				disciplineDocumentCode = row.getCell(0).getStringCellValue();
			}
			if (row.getCell(1) != null) {
				disciplineCode = row.getCell(1).getStringCellValue();
			}
			if (row.getCell(4) != null) {
				documentCode = row.getCell(4).getStringCellValue();
			}
			if (row.getCell(8) != null) {
				disciplineDocumentShortCode = row.getCell(8).getStringCellValue();
			}
			if (row.getCell(9) != null) {
				assetTypeReference = row.getCell(9).getStringCellValue();
			}
			if (row.getCell(10) != null) {
				representationType = row.getCell(10).getStringCellValue();
			}
			
			CFIHOSUtils.addDisciplineDocumentType(ontology, prefixIRI, prefixIRIForEquipment, prefixIRIForTags, disciplineDocumentCode, disciplineCode, documentCode, disciplineDocumentShortCode, assetTypeReference, representationType, disciplineDocumentClass);
		}
	}
	
	private static void includeTagClasses(Workbook workbook, OWLOntology ontology) {
		Sheet tagClassSheet = workbook.getSheet(TAG_CLASS_SHEET_NAME);
		
		Map<String, String> codeByNameMap = getCodeByNameMap(tagClassSheet);
		String prefixIRI = getPrefixIRI();
		String prefixIRIForTags = getPrefixIRIForTags();
		for(Row row : tagClassSheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String parentTagClassName = null;
			String parentTagClassCode = null;
			String tagClassCode = null;
			String tagClassName = null;
			String tagClassDefinition = null;
			String tagClassSynonym = null;
			
			if (row.getCell(0) != null) {
				parentTagClassName = row.getCell(0).getStringCellValue();
				parentTagClassCode = codeByNameMap.get(parentTagClassName);
			}
			if (row.getCell(1) != null) {
				tagClassCode = row.getCell(1).getStringCellValue();
			}
			if (row.getCell(2) != null) {
				tagClassName = row.getCell(2).getStringCellValue();
				/* Change label for "class" class */
				if ("class".equals(tagClassName)) {
					tagClassName = "Tag";
				}
			}
			if (row.getCell(3) != null) {
				tagClassDefinition = row.getCell(3).getStringCellValue();
			}
			if (row.getCell(8) != null) {
				tagClassSynonym = row.getCell(8).getStringCellValue();
			}
			
			CFIHOSUtils.addTagClass(ontology, prefixIRI, prefixIRIForTags, tagClassCode, parentTagClassCode, tagClassName, tagClassDefinition, tagClassSynonym);
		}
	}
	
	private static void includeTagProperties(Workbook workbook, OWLOntology ontology) {
		Sheet tagClassPropertySheet = workbook.getSheet(TAG_CLASS_PROPERTY_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		String prefixIRIForTags = getPrefixIRIForTags();
		
		for(Row row : tagClassPropertySheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			String tagCode = null;
			String propertyCode = null;
			String propertyName = null;
			String unitOfMeasureSICode = null;
			String unitOfMeasureSIName = null;
			String unitOfMeasureImperialCode = null;
			String unitOfMeasureImperialName = null;
			
			if (row.getCell(0) != null) {
				tagCode = row.getCell(0).getStringCellValue();
			}
			
			if (row.getCell(2) != null) {
				propertyCode = row.getCell(2).getStringCellValue();
			}
			
			if (row.getCell(3) != null) {
				propertyName = row.getCell(3).getStringCellValue();
			}
			
			if (row.getCell(4) != null) {
				unitOfMeasureSICode = row.getCell(4).getStringCellValue();
			}
			if (row.getCell(5) != null) {
				unitOfMeasureSIName = row.getCell(5).getStringCellValue();
			}
			if (row.getCell(6) != null) {
				unitOfMeasureImperialCode = row.getCell(6).getStringCellValue();
			}
			if (row.getCell(7) != null) {
				unitOfMeasureImperialName = row.getCell(7).getStringCellValue();
			}
			
			CFIHOSUtils.addTagProperty(ontology, prefixIRI, prefixIRIForTags, tagCode, propertyCode, propertyName, unitOfMeasureSICode, unitOfMeasureSIName, unitOfMeasureImperialCode, unitOfMeasureImperialName);
		}
	}
	
	private static void includeEquipmentTagRelationships(Workbook workbook, OWLOntology ontology) {
		Sheet tagEquipmentClassRelationshipSheet = workbook.getSheet(TAG_EQUIPMENT_CLASS_RELATIONSHIP_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		String prefixIRIForTags = getPrefixIRIForTags();
		String prefixIRIForEquipment = getPrefixIRIForEquipment();
		
		/* Create hasTag object property */
		OWLObjectProperty hasTag = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(prefixIRI + "hasTag");
		OWLUtils.addAnnotation(ontology, hasTag, IRI.create(RDFConstants.RDFS_LABEL), "has tag");
		
		for(Row row : tagEquipmentClassRelationshipSheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			
			String tagCode = null;
			String equipmentCode = null;
			
			if (row.getCell(0) != null) {
				tagCode = row.getCell(0).getStringCellValue();
			}
			
			if (row.getCell(2) != null) {
				equipmentCode = row.getCell(2).getStringCellValue();
			}
			
			CFIHOSUtils.addTagProperty(ontology, prefixIRI, prefixIRIForTags, prefixIRIForEquipment, tagCode, equipmentCode, hasTag);
		}
		
		
	}
	
	private static OWLOntology generateIDOCompliantOntology(String cfihosOntologyPath, String cfihosPrefix) throws OWLOntologyCreationException {
		/* Create ontology */
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.createOntology(CFIHOS_IDO_ONTOLOGY_IRI);
		
		/* Add imports */
		OWLImportsDeclaration cfihosImportDeclaration = manager.getOWLDataFactory().getOWLImportsDeclaration(CFIHOS_ONTOLOGY_IRI);
		manager.applyChange(new AddImport(ontology, cfihosImportDeclaration));
		
		OWLImportsDeclaration idoImportDeclaration = manager.getOWLDataFactory().getOWLImportsDeclaration(IDO_ONTOLOGY_IRI);
		manager.applyChange(new AddImport(ontology, idoImportDeclaration));
		
		/* IRI mapper */
		SimpleIRIMapper mapper = new SimpleIRIMapper(CFIHOS_ONTOLOGY_IRI, IRI.create(new File(cfihosOntologyPath)));
		manager.getIRIMappers().add(mapper);
		
		/* Load imported ontologies*/
		manager.loadOntology(CFIHOS_ONTOLOGY_IRI);
		manager.loadOntology(CFIHOS_ONTOLOGY_IRI);
		
		/* Add subclasses */
		/* CFIHOS equipment sub class of IDO physical artefact and IDO inanimate physical object */
		OWLClass idoPhysicalArtefactClass = manager.getOWLDataFactory().getOWLClass(OWLUtils.IDO_NS + "PhysicalArtefact");
		OWLClass idoInanimatePhysicalObjectClass = manager.getOWLDataFactory().getOWLClass(OWLUtils.IDO_NS + "InanimatePhysicalObject");
		OWLClass cfihosEquipmentClass = manager.getOWLDataFactory().getOWLClass(getPrefixIRIForEquipment() + "CFIHOS-30000311");
		OWLUtils.addSubclassOf(ontology, cfihosEquipmentClass, idoPhysicalArtefactClass);
		OWLUtils.addSubclassOf(ontology, cfihosEquipmentClass, idoInanimatePhysicalObjectClass);
		
		/* CFIHOS tag sub class of IDO functional object */
		OWLClass idoFunctionalObjectClass = manager.getOWLDataFactory().getOWLClass(OWLUtils.IDO_NS + "FunctionalObject");
		OWLClass cfihosTagClass = manager.getOWLDataFactory().getOWLClass(getPrefixIRIForTags() + "CFIHOS-30000311");
		OWLUtils.addSubclassOf(ontology, cfihosTagClass, idoFunctionalObjectClass);
		
		/* CFIHOS document sub class of IDO information object */
		OWLClass idoInformationObjectClass = manager.getOWLDataFactory().getOWLClass(OWLUtils.IDO_NS + "InformationObject");
		OWLClass cfihosDocumentClass = manager.getOWLDataFactory().getOWLClass(getPrefixIRI() + "Document");
		OWLUtils.addSubclassOf(ontology, cfihosDocumentClass, idoInformationObjectClass);
		
		/* CFIHOS discipline document type sub class of IDO information object */
		OWLClass cfihosDisciplineDocumentTypeClass = manager.getOWLDataFactory().getOWLClass(getPrefixIRI() + "DisciplineDocumentType");
		OWLUtils.addSubclassOf(ontology, cfihosDisciplineDocumentTypeClass, idoInformationObjectClass);
		
		/* CFIHOS standard sub class of IDO information object */
		OWLClass cfihosStandardClass = manager.getOWLDataFactory().getOWLClass(getPrefixIRI() + CFIHOSUtils.STANDARD);
		OWLUtils.addSubclassOf(ontology, cfihosStandardClass, idoInformationObjectClass);
		
		/* CFIHOS picklist sub class of IDO information object */
		OWLClass cfihosPropertyPicklistClass = manager.getOWLDataFactory().getOWLClass(getPrefixIRI() + CFIHOSUtils.PROPERTY_PICKLIST);
		OWLUtils.addSubclassOf(ontology, cfihosPropertyPicklistClass, idoInformationObjectClass);
		
		/* CFIHOS property picklist value sub class of IDO information object */
//		OWLClass cfihosPropertyPicklistValueClass = manager.getOWLDataFactory().getOWLClass(getPrefixIRI() + "PropertyPickListValue");
//		OWLUtils.addSubclassOf(ontology, cfihosPropertyPicklistValueClass, idoInformationObjectClass);
		
		/* CFIHOS SourceStandardDocumentAndDataRequirement sub class of IDO information object */
		OWLClass cfihosSourceStandardDocumentAndDataRequirementClass = manager.getOWLDataFactory().getOWLClass(getPrefixIRI() + CFIHOSUtils.SOURCE_STANDARD_DOCUMENT_AND_DATA_REQUIREMENT);
		OWLUtils.addSubclassOf(ontology, cfihosSourceStandardDocumentAndDataRequirementClass, idoInformationObjectClass);
		
		/* CFIHOS discipline sub class of IDO Role */
		OWLClass idoRoleClass = manager.getOWLDataFactory().getOWLClass(OWLUtils.IDO_NS + "Role");
		OWLClass cfihosDisciplineClass = manager.getOWLDataFactory().getOWLClass(getPrefixIRI() + "Discipline");
		OWLUtils.addSubclassOf(ontology, cfihosDisciplineClass, idoRoleClass);
		
		/* om2:Unit sub class of IDO information object  */
		OWLClass cfihosUnitClass = manager.getOWLDataFactory().getOWLClass(OWLUtils.OM2_UNIT);
		OWLUtils.addSubclassOf(ontology, cfihosUnitClass, idoInformationObjectClass);
		
		/* om2:dimension subclass of information object */
		//OWLClass idoPhysicalQuantityClass = manager.getOWLDataFactory().getOWLClass(OWLUtils.IDO_NS + "PhysicalQuantity");
		OWLClass cfihosDimensionClass = manager.getOWLDataFactory().getOWLClass(OWLUtils.OM2_NS + "Dimension");
		OWLUtils.addSubclassOf(ontology, cfihosDimensionClass, idoInformationObjectClass);
		
		/* om2:Measure as subclass of quality datum*/
		OWLClass idoQualityDatumClass = manager.getOWLDataFactory().getOWLClass(OWLUtils.IDO_NS + "QualityDatum");
		OWLClass cfihosMeasureClass = manager.getOWLDataFactory().getOWLClass(OWLUtils.OM2_MEASURE);
		OWLUtils.addSubclassOf(ontology, cfihosMeasureClass, idoQualityDatumClass);
		
		/* cfihos property group as subclass of IDO information object */
		OWLClass cfihosPropertyGroupClass = manager.getOWLDataFactory().getOWLClass(getPrefixIRI() + "PropertyGroup");
		OWLUtils.addSubclassOf(ontology, cfihosPropertyGroupClass, idoInformationObjectClass);
		
		/* Add equivalent classes */
		/* om2:Unit equivalent to IDO UnitOfMeasure */
		OWLClass idoUnitOfMeasureClass = manager.getOWLDataFactory().getOWLClass(OWLUtils.IDO_NS + "UnitOfMeasure");
		OWLUtils.addEquivalentClass(ontology, cfihosUnitClass, idoUnitOfMeasureClass);
		
		/* om2:Measure equivalent to IDO scalar quantity datum*/
		OWLClass idoScalarQuantityDatumClass = manager.getOWLDataFactory().getOWLClass(OWLUtils.IDO_NS + "ScalarQuantityDatum");
		OWLUtils.addEquivalentClass(ontology, cfihosMeasureClass, idoScalarQuantityDatumClass);
		
		/* Add equivalent properties */
//		OWLObjectProperty idoHasFunction = manager.getOWLDataFactory().getOWLObjectProperty(OWLUtils.IDO_NS + "hasFunction");
//		OWLObjectProperty cfihosHasTag = manager.getOWLDataFactory().getOWLObjectProperty(getPrefixIRI() + "hasTag");
//		OWLUtils.addEquivalentProperties(ontology, cfihosHasTag, idoHasFunction);
		
		/* IDO datumUOM equivalent to om2:hasUnit */
		OWLObjectProperty idoDatumUOM = manager.getOWLDataFactory().getOWLObjectProperty(OWLUtils.IDO_NS + "datumUOM");
		OWLObjectProperty cfihosHasUnit = manager.getOWLDataFactory().getOWLObjectProperty(OWLUtils.OM2_HAS_UNIT);
		OWLUtils.addEquivalentObjectProperties(ontology, cfihosHasUnit, idoDatumUOM);
		
		/* om2:hasUnit as subproperty of IDO hasContentPart */
		OWLObjectProperty idoHasContentPart = manager.getOWLDataFactory().getOWLObjectProperty(OWLUtils.IDO_NS + "hasContentPart");
		OWLUtils.addSubPropertyOf(ontology, cfihosHasUnit, idoHasContentPart);
		
		/* IDO datumValue equivalent to om2:hasNumericalValue */
		OWLDataProperty idoDatumValue = manager.getOWLDataFactory().getOWLDataProperty(OWLUtils.IDO_NS + "datumValue");
		OWLDataProperty cfihosHasNumericalValue = manager.getOWLDataFactory().getOWLDataProperty(OWLUtils.OM2_HAS_NUMERICAL_VALUE);
		OWLUtils.addEquivalentDataProperties(ontology, cfihosHasNumericalValue, idoDatumValue);
		

		/*Qualities do not appear in CFIHOS, but we create them from the properties to make them interoperable with IDO */
		CFIHOSUtils.includeQualitiesForIDO(ontology,cfihosPrefix);
		return ontology;
	}
	
	private static void enrichWithExternalAnnotations(OWLOntology ontology) {
		AnnotationEnricher annotationEnricher = new AnnotationEnricher(ontology);
		annotationEnricher.addExternalOntologyToUse(IRI.create(OWLUtils.OM2_SOURCE_RAW_IRI));
		annotationEnricher.enrichOntology();
	}

}
