package es.um.dis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;


public class AnnotationEnricher {
	private final static Logger LOGGER = Logger.getLogger(AnnotationEnricher.class.getName());
	
	private OWLOntologyManager ontologyManager;
	private OWLOntology ontology;

	public AnnotationEnricher(OWLOntology ontology) {
		this.ontology = ontology;
		this.ontologyManager = ontology.getOWLOntologyManager();
	}
	
	public AnnotationEnricher(InputStream inputStream) throws OWLOntologyCreationException {
		this.ontologyManager = OWLManager.createOWLOntologyManager();
		this.ontology = this.ontologyManager.loadOntologyFromOntologyDocument(inputStream);
	}
	
	public AnnotationEnricher(File ontologyFile) throws OWLOntologyCreationException, FileNotFoundException {
		this(new FileInputStream(ontologyFile));
	}
	
	public AnnotationEnricher(IRI ontologyIRI) throws OWLOntologyCreationException {
		this.ontologyManager = OWLManager.createOWLOntologyManager();
		this.ontology = this.ontologyManager.loadOntologyFromOntologyDocument(ontologyIRI);
	}
	
	public void enrichOntology() {
		LOGGER.log(Level.INFO, String.format("Enriching %s", this.ontology.getOntologyID().toString()));
		
		Set<OWLAxiom> classAxioms = this.enrichOntologyClasses();
		this.ontology.addAxioms(classAxioms);
		
		Set<OWLAxiom> objectPropertyAxioms = this.enrichOntologyObjectProperties();
		this.ontology.addAxioms(objectPropertyAxioms);
		
		Set<OWLAxiom> dataPropertyAxioms = this.enrichOntologyDataProperties();
		this.ontology.addAxioms(dataPropertyAxioms);
		
		Set<OWLAxiom> individualAxioms = this.enrichOntologyIndividuals();
		this.ontology.addAxioms(individualAxioms);
		
		Set<OWLAxiom> annotationPropertyAxiomsFirstRound = this.enrichOntologyAnnotationProperties();
		this.ontology.addAxioms(annotationPropertyAxiomsFirstRound);
		
		Set<OWLAxiom> annotationPropertyAxiomsSecondRound = this.enrichOntologyAnnotationProperties();
		this.ontology.addAxioms(annotationPropertyAxiomsSecondRound);
		
		
		int totalAnnotationPropertyAxioms = annotationPropertyAxiomsFirstRound.size() + annotationPropertyAxiomsSecondRound.size();
		int totalAxioms = classAxioms.size() + objectPropertyAxioms.size() + dataPropertyAxioms.size() + individualAxioms.size() + totalAnnotationPropertyAxioms;
		
		LOGGER.log(Level.INFO, String.format("%d new axioms added to the ontology", totalAxioms));
		LOGGER.log(Level.INFO, String.format("%d new class axioms added to the ontology", classAxioms.size()));
		LOGGER.log(Level.INFO, String.format("%d new object property axioms added to the ontology", objectPropertyAxioms.size()));
		LOGGER.log(Level.INFO, String.format("%d new data property axioms added to the ontology", dataPropertyAxioms.size()));
		LOGGER.log(Level.INFO, String.format("%d new annotation property axioms added to the ontology", totalAnnotationPropertyAxioms));
		LOGGER.log(Level.INFO, String.format("%d new individual axioms added to the ontology", individualAxioms.size()));
	}
	
	public Set<OWLAxiom> enrichOntologyEntities(Stream <? extends OWLEntity> entities) {
		Set<OWLAxiom> axiomsToAdd = ConcurrentHashMap.newKeySet();
		entities.forEach(entity -> {
			OWLOntologyManager externalOntologyManager = OWLManager.createOWLOntologyManager();
			try {
				OWLOntology externalOntologyPortion = externalOntologyManager.loadOntology(entity.getIRI());
				externalOntologyPortion.annotationAssertionAxioms(entity.getIRI()).forEach(annotationAssertionAxiom -> {
					if (!this.ontology.containsAxiom(annotationAssertionAxiom)) {
						axiomsToAdd.add(annotationAssertionAxiom);
					}
				});
				
				
			} catch (OWLOntologyCreationException ontologyCreationException) {
				LOGGER.log(Level.WARNING, String.format("Error obtanining data for %s", entity.getIRI().toQuotedString()));
			} catch(Exception e) {
				LOGGER.log(Level.SEVERE, "Not controlled exception", e);
			}
		});
		return axiomsToAdd;
	}
	
	public Set<OWLAxiom> enrichOntologyClasses(){
		return this.enrichOntologyEntities(this.ontology.classesInSignature());
	}
	public Set<OWLAxiom> enrichOntologyObjectProperties(){
		return this.enrichOntologyEntities(this.ontology.objectPropertiesInSignature());
	}
	
	public Set<OWLAxiom> enrichOntologyDataProperties(){
		return this.enrichOntologyEntities(this.ontology.dataPropertiesInSignature());
	}
	
	public Set<OWLAxiom> enrichOntologyAnnotationProperties(){
		return this.enrichOntologyEntities(this.ontology.annotationPropertiesInSignature());
	}
	
	public Set<OWLAxiom> enrichOntologyIndividuals(){
		return this.enrichOntologyEntities(this.ontology.individualsInSignature());
	}
	
	public void saveOntology(File outputFile) throws OWLOntologyStorageException, FileNotFoundException {
		this.ontology.saveOntology(new FileOutputStream(outputFile));
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}
	
	
}
