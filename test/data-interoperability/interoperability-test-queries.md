# Interoperability test queries (IDO vocabulary only)


## CQ1 - Instances of physical artefacts

This CQ retrieves the instances of gate valves from both datasets, since CFIHOS equipment is a subclass of the IDO Physical Artefact class.

```sparql
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX ido:  <http://rds.posccaesar.org/ontology/lis14/rdl/>

SELECT DISTINCT ?asset  WHERE {
  ?asset rdf:type ido:PhysicalArtefact.
}
```


## CQ2 - What are all assets and their qualities, with human-readable labels, expressed using only IDO vocabulary?

This CQ checks that the hasQuality/qualityQuantifiedAs chain is walkable by an agent that knows nothing about either CFIHOS or the specific valve domain.

```sparql
PREFIX ido:  <http://rds.posccaesar.org/ontology/lis14/rdl/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT ?asset ?assetLabel ?quality ?qualityLabel ?datum ?datumLabel WHERE {
  ?asset ido:hasQuality ?quality .
  OPTIONAL { ?asset rdfs:label ?assetLabel }
  OPTIONAL { ?quality rdfs:label ?qualityLabel }
  OPTIONAL {
    ?quality ido:qualityQuantifiedAs ?datum .
    OPTIONAL { ?datum rdfs:label ?datumLabel }
  }
}
ORDER BY ?asset
```

## CQ3 - What are the assets with a "body material" quality quantified as something labeled "SS316", using only IDO vocabulary

This query is written to work regardless of *where* the quality's label lives - asserted directly on the individual (as in the ido-only file) or only on its `rdf:type` class (as in the aligned file, where the label sits on `CFIHOS-40000017Quality` inside the bridge ontology).

```sparql
PREFIX ido:  <http://rds.posccaesar.org/ontology/lis14/rdl/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT DISTINCT ?asset ?assetLabel WHERE {
  ?asset ido:hasQuality ?quality .
  ?quality ido:qualityQuantifiedAs ?datum .
  ?datum rdfs:label ?datumLabel .
  FILTER(CONTAINS(?datumLabel, "SS316"))

  { ?quality rdfs:label ?qualityLabel }
  UNION
  { ?quality a ?qualityClass . ?qualityClass rdfs:label ?qualityLabel }

  FILTER(?qualityLabel = "body material")
  OPTIONAL { ?asset rdfs:label ?assetLabel }
}
```
