package es.um.dis.utils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnrichmentTask implements Runnable{
	private final static Logger LOGGER = Logger.getLogger(EnrichmentTask.class.getName());

	private File inputOWLFile;
	private File outputOWLFile;
	
	public EnrichmentTask(File inputOWLFile, File outputOWLFile) {
		super();
		this.inputOWLFile = inputOWLFile;
		this.outputOWLFile = outputOWLFile;
	}

	@Override
	public void run() {
		AnnotationEnricher enricher;
		try {
			enricher = new AnnotationEnricher(inputOWLFile);
			enricher.enrichOntology();
			enricher.saveOntology(outputOWLFile);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, String.format("Error processing %s", inputOWLFile.getName()), e);
		} 
		
	}

}
