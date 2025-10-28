package es.um.dis.cfihos.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFConstants;

import es.um.dis.utils.OWLUtils;

public class Cfihos {
	/* Input parameters */
	private static final String EXCEL_FILE = "CORE-CFIHOS-V2.0-excel-FINAL.xlsx";
	private static final String OUTPUT_FILE = "CORE-CFIHOS-V2.0.owl";
	private static final IRI ONTOLOGY_IRI = IRI.create("http://infohub.siemens-energy.com/CFIHOS");
	
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
	
	
	
	public static void main(String[] args) throws OWLOntologyStorageException, OWLOntologyCreationException, IOException {
		InputStream inputStream = Cfihos.class.getClassLoader().getResourceAsStream(EXCEL_FILE);
		OWLOntology ontology = generateOntology(inputStream);
		ontology.saveOntology(new FileOutputStream(new File(OUTPUT_FILE)));

	}
	
	private static String getPrefixIRI() {
		String prefixIRI = ONTOLOGY_IRI.getIRIString() + "#";
		return prefixIRI;
	}
	
	private static String getPrefixIRIForEquipment() {
		String prefixIRI = ONTOLOGY_IRI.getIRIString() + "/equipment" + "#";
		return prefixIRI;
	}
	
	private static String getPrefixIRIForTags() {
		String prefixIRI = ONTOLOGY_IRI.getIRIString() + "/tag" + "#";
		return prefixIRI;
	}
	
	private static OWLOntology generateOntology(InputStream is) throws OWLOntologyCreationException, IOException {
		OWLOntology ontology = OWLManager.createOWLOntologyManager().createOntology(ONTOLOGY_IRI);
		Workbook workbook = new XSSFWorkbook(is);

		includeBaseEntities(ontology);
		includeProperties(workbook, ontology);
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
		return ontology;
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
			if(row.getCell(7) != null) {
				propertyRange = row.getCell(7).getStringCellValue();
			}
			
			CFIHOSUtils.addProperty(ontology, prefixIRI, propertyCFIHOSCode, propertyName, propertyDefinition, propertyRange);
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
		OWLClass standardClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + "Standard"));
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
	
	private static void includeBaseEntities(OWLOntology ontology) {
		String prefixIRI = getPrefixIRI();
		//OWLClass measureClass = OWLUtils.createClass(ontology, IRI.create("http://www.ontology-of-units-of-measure.org/resource/om-2/Measure"));
		//OWLDataProperty hasValue = OWLUtils.createDataProperty(ontology, IRI.create("http://purl.org/biotop/btl2.owl#hasValue"));
		//OWLObjectProperty hasUnit = OWLUtils.createObjectProperty(ontology, IRI.create("http://www.ontology-of-units-of-measure.org/resource/om-2/hasUnit"));
		OWLClass unitClass = OWLUtils.createClass(ontology, IRI.create("http://www.ontology-of-units-of-measure.org/resource/om-2/Unit"));
		OWLClass internationalSystemUnitClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + "InternationalSystemUnit"));
		OWLClass imperialSystemUnitClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + "ImperialSystemUnit"));
		OWLUtils.addSubclassOf(ontology, internationalSystemUnitClass, unitClass);
		OWLUtils.addSubclassOf(ontology, imperialSystemUnitClass, unitClass);
		//OWLUtils.addObjectSomeValuesFromRestriction(ontology, hasUnit, measureClass, unitClass);
		//OWLUtils.addDataSomeValuesFromRestriction(ontology, hasValue, measureClass, OWL2Datatype.RDFS_LITERAL);
	}
	
	
	private static void includeDisciplines(Workbook workbook, OWLOntology ontology) {
		Sheet disciplineSheet = workbook.getSheet(DISCIPLINE_SHEET_NAME);
		String prefixIRI = getPrefixIRI();
		OWLClass disciplineClass = OWLUtils.createClass(ontology, IRI.create(prefixIRI + "Discipline"));
		OWLUtils.addAnnotation(ontology, disciplineClass, IRI.create(RDFConstants.RDFS_LABEL), "Discipline");
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

}
