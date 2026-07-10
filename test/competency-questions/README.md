# Selected Competency Questions — SPARQL Queries

This page shows a set of competency questions desgined for testing the CFIHOS ontology developed.

The next table shows the competency questions and what it is evaluating from the ontology

| ID | Category | Competency question |
|----|----------|----------------------|
| CQ1  | Asset classification     | What properties are applicable to the "ball valve" class? |
| CQ2 | Asset classification | What are the direct and inferred subclasses of "manual valve," and how many equipment classes exist at each level of the hierarchy? 
| CQ3  | Asset classification     | How many "tag or equipment class" instances apply to both tags and equipment? Please count only active (not TERMINATED) instances. |
| CQ4  | Asset classification     | Which equipment classes have no associated tag class, or vice versa? |
| CQ5  | Data/property validation | What are the name and definition of the property CFIHOS-40000002? |
| CQ6 | Data/property validation | For a given equipment class, what quantitative properties are defined, and what unit/dimension is each restricted to? |
| CQ7 | Data/property validation | What are the properties of class “discipline document type”? |
| CQ8  | Documentation/handover   | Which documents are required for commissioning a pump? |
| CQ9  | Documentation/handover   | What discipline is responsible for document type 0505? |
| C10 | Documentation/handover | Which discipline is responsible for a given document type, and are there document types with no assigned discipline? |
| C11 | Documentation/handover | What are the subclasses of “discipline” covered by CFIHOS? List them with their codes|
| CQ12  | Traceability/provenance  | Which CFIHOS entities are traceable to standards? |
| CQ13 | Interoperability (IDO)   | What are the assets and their properties, expressed using only IDO vocabulary? |
| CQ14 | Interoperability (IDO)   | Which CFIHOS classes are `subClassOf` vs. `equivalentClass` to an IDO class? |



## CQ1 — What properties are applicable to the "ball valve" class?

```sparql
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX cfihos: <http://infohub.siemens-energy.com/CFIHOS/tag#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
SELECT ?property ?label WHERE {
  cfihos:CFIHOS-30000807 rdfs:subClassOf ?r .        # ball valve
  ?r a owl:Restriction ;
     owl:onProperty ?property ;
     owl:someValuesFrom ?domain .
  OPTIONAL { ?property rdfs:label ?label }
}
```

## CQ2 — subclasses of "valve" (transitive closure)**
```sparql
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX cfihos-eq: <http://infohub.siemens-energy.com/CFIHOS/equipment#>
SELECT ?class ?label WHERE {
  ?class rdfs:subClassOf+ cfihos-eq:CFIHOS-30000649 .
  ?class rdfs:label ?label .
}
```

### CQ3 — How many "tag or equipment class" instances apply to both tags and equipment (active only)?

```sparql
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX cfihos: <http://infohub.siemens-energy.com/CFIHOS#>
PREFIX equipment: <http://infohub.siemens-energy.com/CFIHOS/equipment#>
PREFIX tag: <http://infohub.siemens-energy.com/CFIHOS/tag#>
SELECT (count (distinct ?tag) as ?uniqueTagCount) WHERE {
?tag rdfs:subClassOf* tag:CFIHOS-30000311 .
?tag cfihos:hasCFIHOSCode ?tagCode .
FILTER EXISTS {
?equipment rdfs:subClassOf* equipment:CFIHOS-30000311 .
?equipment cfihos:hasCFIHOSCode ?tagCode .
}
}
```


### CQ4 — Which equipment classes have no associated tag class, or vice versa?
```sparql
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX cfihos: <http://infohub.siemens-energy.com/CFIHOS#>
PREFIX cfihos-eq: <http://infohub.siemens-energy.com/CFIHOS/equipment#>
SELECT ?eq ?label WHERE {
  ?eq rdfs:subClassOf* cfihos-eq:CFIHOS-30000311 .   # equipment root
  ?eq rdfs:label ?label .
  FILTER NOT EXISTS {
    ?eq rdfs:subClassOf [
      a owl:Restriction ;
      owl:onProperty cfihos:hasTag ;
      owl:someValuesFrom ?tag
    ]
  }
}
```

### CQ5 — What are the name and definition of the property CFIHOS-40000002?

```sparql
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX iao: <http://purl.obolibrary.org/obo/IAO_>
PREFIX cfihos: <http://infohub.siemens-energy.com/CFIHOS#>
SELECT * WHERE {
  cfihos:CFIHOS-40000002 rdfs:label ?name .
  cfihos:CFIHOS-40000002 iao:0000115 ?definition .
} 
```

## CQ6 — What units of measure (with their symbols) are available for a given dimension?
```sparql
PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>
PREFIX om-2:  <http://www.ontology-of-units-of-measure.org/resource/om-2/>
PREFIX cfihos: <http://infohub.siemens-energy.com/CFIHOS#>

SELECT ?unit ?unitLabel ?symbol WHERE {
  ?dimension a cfihos:CFIHOS-00000072 ;
             rdfs:label "pressure" .
  ?unit a cfihos:CFIHOS-00000073 ;
        om-2:hasDimension ?dimension ;
        rdfs:label ?unitLabel .
  OPTIONAL { ?unit om-2:symbol ?symbol }
}
ORDER BY ?unitLabel
```

### CQ7 — Which documents are required for commissioning a pump?

```sparql
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX cfihos: <http://infohub.siemens-energy.com/CFIHOS#>
SELECT distinct ?property WHERE {
?disciplineDocument rdfs:subClassOf cfihos:CFIHOS-00000027 .
?disciplineDocument ?property ?value .
}
```

### CQ8 — What are the properties of class “discipline document type”??

```sparql
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX cfihos: <http://infohub.siemens-energy.com/CFIHOS#>
SELECT ?document ?documentLabel WHERE {
  ?requirements rdfs:subClassOf cfihos:CFIHOS-00000132.
  ?requirements rdfs:subClassOf [
    rdf:type owl:Restriction ;
    owl:onProperty cfihos:hasTag ;
    # vacuum pump. With pump there is no data
    owl:someValuesFrom <http://infohub.siemens-energy.com/CFIHOS/tag#CFIHOS-30001032> 
  ] .
  ?requirements  rdfs:subClassOf [
    rdf:type owl:Restriction ;
    owl:onProperty cfihos:hasDocumentType ;
    owl:someValuesFrom ?document
  ] .
  ?document rdfs:label ?documentLabel .

}

```

### CQ9 — What discipline is responsible for document type 0505?
*(= paper's CQ13)*
```sparql
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX cfihos: <http://infohub.siemens-energy.com/CFIHOS#>
SELECT ?discipline ?disciplineLabel WHERE {
  ?ddt rdfs:subClassOf cfihos:CFIHOS-00000027 ;                    # discipline document type
       rdfs:subClassOf [ owl:onProperty cfihos:hasDocumentType ;
                          owl:someValuesFrom ?doctype ] ;
       rdfs:subClassOf [ owl:onProperty cfihos:hasDiscipline ;     # ASSUMED property name
                          owl:someValuesFrom ?discipline ] .
  ?doctype cfihos:hasShortCode "0505" .                            # ASSUMED annotation property name
  ?discipline rdfs:label ?disciplineLabel .
}
```

## CQ10 — discipline responsible for a document type**
```sparql
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX cfihos: <http://infohub.siemens-energy.com/CFIHOS#>
SELECT ?doctype ?discipline ?disciplineLabel WHERE {
  ?ddt rdfs:subClassOf cfihos:CFIHOS-00000027 ;           # discipline document type
       rdfs:subClassOf [ owl:onProperty cfihos:hasDocumentType ; owl:someValuesFrom ?doctype ] ;
       rdfs:subClassOf [ owl:onProperty cfihos:hasDiscipline ; owl:someValuesFrom ?discipline ] .
  ?discipline rdfs:label ?disciplineLabel .
}
```
### CQ11 —  What are the subclassas of “discipline” covered by CFIHOS? List them with their codes
```sparql
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX cfihos: <http://infohub.siemens-energy.com/CFIHOS#>
PREFIX schema: <http://schema.org/>
SELECT * WHERE {
?discipline rdfs:subClassOf cfihos:CFIHOS-00000021 .
?discipline schema:identifier ?disciplineCode .
?discipline rdfs:label ?disciplineName .
}
```


### CQ12 — Which CFIHOS entities are traceable to standards?
```sparql
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX cfihos: <http://infohub.siemens-energy.com/CFIHOS#>
SELECT ?entity ?entityLabel ?stdLabel WHERE {
  ?entity cfihos:hasSourceStandard ?standard .
  ?standard rdfs:label ?stdLabel .
  ?entityLabel rdfs:label ?entityLabel .
}

```
### CQ13 — What are the assets and their properties, expressed using only IDO vocabulary?

```sparql
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX ido: <http://rds.posccaesar.org/ontology/lis14/rdl/>
SELECT ?asset ?asset_label ?quality ?quality_label WHERE {
  ?asset rdfs:subClassOf [
    rdf:type owl:Restriction ;
    owl:onProperty ido:hasQuality ;
    owl:someValuesFrom ?quality
  ] .
  ?quality rdfs:label ?quality_label .
  ?asset rdfs:label ?asset_label .
}
```

### CQ14 — Which CFIHOS classes are subClassOf vs. equivalentClass to an IDO class?
```sparql
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX ido: <http://rds.posccaesar.org/ontology/lis14/rdl/>
SELECT ?cfihosClass ?idoClass ?relation WHERE {
  { ?cfihosClass rdfs:subClassOf ?idoClass . BIND("subClassOf" AS ?relation) }
  UNION
  { ?cfihosClass owl:equivalentClass ?idoClass . BIND("equivalentClass" AS ?relation) }
  FILTER(STRSTARTS(STR(?idoClass), STR(ido:)))
}
```

