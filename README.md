# cfihos
Java script to convert CFIHOS excel file into an OWL ontology.

The intended use of this script is through an IDE editor, such as Eclipse. It is needed to import this project as a Maven project.

The main class is located at [src/main/java/es/um/dis/cfihos/main/Cfihos.java](src/main/java/es/um/dis/cfihos/main/Cfihos.java). This class reads the CFIHOS excel file at [src/main/resources/CORE-CFIHOS-V2.0-excel-FINAL.xlsx](src/main/resources/CORE-CFIHOS-V2.0-excel-FINAL.xlsx) and creates and OWL ontology called *CORE-CFIHOS-V2.0.owl* in the root folder of the project.