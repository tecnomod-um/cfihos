# Data interoperability test queries 

This page describes the queries that demonstrate how data based on our CFIHOS-IDO ontology can interoperate with content based on the IDO. For this purpose two small datasets consisting on ten instances of valves each have been generated:
* [Dataset CFIHOS-IDO](dataset-chfios-ido.ttl)
* [Dataset IDO](dataset-ido.ttl)

Once these two files are loaded into a triple store repository including the 
[CFIHOS-IDO ontology](../../ontology/CORE-CFIHOS-V2.0_ido.owl), the [CFIHOS ontology](../../ontology/CORE-CFIHOS-V2.0.owl) and the [IDO ontology](../../ontology/ido.owl), the following queries can be executed and obtain the expected answers.

## CQ1 - Instances of physical artefacts

This CQ retrieves the instances of the IDO Physical Artefact class.

```sparql
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX ido:  <http://rds.posccaesar.org/ontology/lis14/rdl/>

SELECT DISTINCT ?asset  WHERE {
  ?asset rdf:type ido:PhysicalArtefact.
}
```
**Results**
```
?asset
<http://example.org/cfhios#Valve101>
<http://example.org/cfhios#Valve002>
<http://example.org/cfhios#Valve003>
<http://example.org/cfhios#Valve004>
<http://example.org/cfhios#Valve005>
<http://example.org/cfhios#Valve006>
<http://example.org/cfhios#Valve007>
<http://example.org/cfhios#Valve008>
<http://example.org/cfhios#Valve009>
<http://example.org/cfhios#Valve010>
<http://example.org/ido#Valve001>
<http://example.org/ido#Valve002>
<http://example.org/ido#Valve003>
<http://example.org/ido#Valve004>
<http://example.org/ido#Valve005>
<http://example.org/ido#Valve006>
<http://example.org/ido#Valve007>
<http://example.org/ido#Valve008>
<http://example.org/ido#Valve009>
<http://example.org/ido#Valve010>
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

**Results**
```
?asset
<http://example.org/cfhios#Valve101>
<http://example.org/cfhios#Valve002>
<http://example.org/cfhios#Valve003>
<http://example.org/cfhios#Valve004>
<http://example.org/cfhios#Valve005>
<http://example.org/cfhios#Valve006>
<http://example.org/cfhios#Valve007>
<http://example.org/cfhios#Valve008>
<http://example.org/cfhios#Valve009>
<http://example.org/cfhios#Valve010>
<http://example.org/ido#Valve001>
<http://example.org/ido#Valve002>
<http://example.org/ido#Valve003>
<http://example.org/ido#Valve004>
<http://example.org/ido#Valve005>
<http://example.org/ido#Valve006>
<http://example.org/ido#Valve007>
<http://example.org/ido#Valve008>
<http://example.org/ido#Valve009>
<http://example.org/ido#Valve010>
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



**Results**
| asset | assetLabel | quality | qualityLabel | datum | datumLabel |
|---|---|---|---|---|---|
| cfhios:Valve002 | gate valve | cfhios:Valve002_BodyMaterialQ | — | cfhios:Material_CarbonSteel | carbon steel |
| cfhios:Valve002 | gate valve | cfhios:Valve002_InletConnQ | — | cfhios:Conn_Flanged300 | flanged, Class 300 |
| cfhios:Valve002 | gate valve | cfhios:Valve002_OutletConnQ | — | cfhios:Conn_Flanged300 | flanged, Class 300 |
| cfhios:Valve002 | gate valve | cfhios:Valve002_SeatDesignQ | — | cfhios:SeatDesign_Metal | metal-to-metal seated |
| cfhios:Valve002 | gate valve | cfhios:Valve002_StemSealQ | — | cfhios:StemSeal_PTFEPacking | PTFE packing |
| cfhios:Valve002 | gate valve | cfhios:Valve002_OperatorTypeQ | — | cfhios:Operator_Gearbox | gearbox operated |
| cfhios:Valve002 | gate valve | cfhios:Valve002_PressureRatingQ | — | cfhios:PressureRating_300 | Class 300 |
| cfhios:Valve002 | gate valve | cfhios:Valve002_SeatLeakageQ | — | cfhios:SeatLeakage_ClassIV | seat leakage Class IV |
| cfhios:Valve002 | gate valve | cfhios:Valve002_TempRatingQ | — | cfhios:TempRating_Minus46to200 | -46 C to 200 C |
| cfhios:Valve003 | gate valve | cfhios:Valve003_BodyMaterialQ | — | cfhios:Material_DuplexSS | duplex stainless steel |
| cfhios:Valve003 | gate valve | cfhios:Valve003_InletConnQ | — | cfhios:Conn_ButtWeld | butt-weld |
| cfhios:Valve003 | gate valve | cfhios:Valve003_OutletConnQ | — | cfhios:Conn_ButtWeld | butt-weld |
| cfhios:Valve003 | gate valve | cfhios:Valve003_SeatDesignQ | — | cfhios:SeatDesign_Soft | soft-seated |
| cfhios:Valve003 | gate valve | cfhios:Valve003_OperatorTypeQ | — | cfhios:Operator_Pneumatic | pneumatic actuator |
| cfhios:Valve003 | gate valve | cfhios:Valve003_SeatLeakageQ | — | cfhios:SeatLeakage_ClassVI | seat leakage Class VI |
| cfhios:Valve004 | gate valve | cfhios:Valve004_BodyMaterialQ | — | cfhios:Material_SS316 | SS316 (austenitic stainless steel) |
| cfhios:Valve005 | gate valve | cfhios:Valve005_BodyMaterialQ | — | cfhios:Material_CarbonSteel | carbon steel |
| cfhios:Valve005 | gate valve | cfhios:Valve005_InletConnQ | — | cfhios:Conn_Threaded | threaded |
| cfhios:Valve005 | gate valve | cfhios:Valve005_OutletConnQ | — | cfhios:Conn_Threaded | threaded |
| cfhios:Valve005 | gate valve | cfhios:Valve005_SeatDesignQ | — | cfhios:SeatDesign_Metal | metal-to-metal seated |
| cfhios:Valve005 | gate valve | cfhios:Valve005_StemSealQ | — | cfhios:StemSeal_Bellows | bellows |
| cfhios:Valve005 | gate valve | cfhios:Valve005_OperatorTypeQ | — | cfhios:Operator_Manual | manual (hand wheel) |
| cfhios:Valve005 | gate valve | cfhios:Valve005_PressureRatingQ | — | cfhios:PressureRating_150 | Class 150 |
| cfhios:Valve005 | gate valve | cfhios:Valve005_SeatLeakageQ | — | cfhios:SeatLeakage_ClassIV | seat leakage Class IV |
| cfhios:Valve005 | gate valve | cfhios:Valve005_TempRatingQ | — | cfhios:TempRating_Minus29to400 | -29 C to 400 C |
| cfhios:Valve006 | gate valve | cfhios:Valve006_BodyMaterialQ | — | cfhios:Material_SS316 | SS316 (austenitic stainless steel) |
| cfhios:Valve006 | gate valve | cfhios:Valve006_InletConnQ | — | cfhios:Conn_Flanged150 | flanged, Class 150 |
| cfhios:Valve006 | gate valve | cfhios:Valve006_OutletConnQ | — | cfhios:Conn_Flanged150 | flanged, Class 150 |
| cfhios:Valve006 | gate valve | cfhios:Valve006_PressureRatingQ | — | cfhios:PressureRating_150 | Class 150 |
| cfhios:Valve006 | gate valve | cfhios:Valve006_TempRatingQ | — | cfhios:TempRating_Minus46to200 | -46 C to 200 C |
| cfhios:Valve007 | gate valve | cfhios:Valve007_BodyMaterialQ | — | cfhios:Material_DuplexSS | duplex stainless steel |
| cfhios:Valve007 | gate valve | cfhios:Valve007_InletConnQ | — | cfhios:Conn_Flanged300 | flanged, Class 300 |
| cfhios:Valve007 | gate valve | cfhios:Valve007_OutletConnQ | — | cfhios:Conn_Flanged300 | flanged, Class 300 |
| cfhios:Valve007 | gate valve | cfhios:Valve007_SeatDesignQ | — | cfhios:SeatDesign_Soft | soft-seated |
| cfhios:Valve007 | gate valve | cfhios:Valve007_StemSealQ | — | cfhios:StemSeal_PTFEPacking | PTFE packing |
| cfhios:Valve007 | gate valve | cfhios:Valve007_OperatorTypeQ | — | cfhios:Operator_Gearbox | gearbox operated |
| cfhios:Valve007 | gate valve | cfhios:Valve007_PressureRatingQ | — | cfhios:PressureRating_300 | Class 300 |
| cfhios:Valve007 | gate valve | cfhios:Valve007_SeatLeakageQ | — | cfhios:SeatLeakage_ClassVI | seat leakage Class VI |
| cfhios:Valve007 | gate valve | cfhios:Valve007_TempRatingQ | — | cfhios:TempRating_Minus29to400 | -29 C to 400 C |
| cfhios:Valve008 | gate valve | cfhios:Valve008_BodyMaterialQ | — | cfhios:Material_CarbonSteel | carbon steel |
| cfhios:Valve008 | gate valve | cfhios:Valve008_SeatDesignQ | — | cfhios:SeatDesign_Metal | metal-to-metal seated |
| cfhios:Valve008 | gate valve | cfhios:Valve008_StemSealQ | — | cfhios:StemSeal_Bellows | bellows |
| cfhios:Valve008 | gate valve | cfhios:Valve008_OperatorTypeQ | — | cfhios:Operator_Manual | manual (hand wheel) |
| cfhios:Valve009 | gate valve | cfhios:Valve009_BodyMaterialQ | — | cfhios:Material_SS316 | SS316 (austenitic stainless steel) |
| cfhios:Valve009 | gate valve | cfhios:Valve009_InletConnQ | — | cfhios:Conn_ButtWeld | butt-weld |
| cfhios:Valve009 | gate valve | cfhios:Valve009_OutletConnQ | — | cfhios:Conn_ButtWeld | butt-weld |
| cfhios:Valve009 | gate valve | cfhios:Valve009_SeatDesignQ | — | cfhios:SeatDesign_Soft | soft-seated |
| cfhios:Valve009 | gate valve | cfhios:Valve009_StemSealQ | — | cfhios:StemSeal_Bellows | bellows |
| cfhios:Valve009 | gate valve | cfhios:Valve009_OperatorTypeQ | — | cfhios:Operator_Pneumatic | pneumatic actuator |
| cfhios:Valve009 | gate valve | cfhios:Valve009_PressureRatingQ | — | cfhios:PressureRating_600 | Class 600 |
| cfhios:Valve009 | gate valve | cfhios:Valve009_SeatLeakageQ | — | cfhios:SeatLeakage_ClassIV | seat leakage Class IV |
| cfhios:Valve009 | gate valve | cfhios:Valve009_TempRatingQ | — | cfhios:TempRating_Minus46to200 | -46 C to 200 C |
| cfhios:Valve010 | gate valve | cfhios:Valve010_BodyMaterialQ | — | cfhios:Material_DuplexSS | duplex stainless steel |
| cfhios:Valve010 | gate valve | cfhios:Valve010_InletConnQ | — | cfhios:Conn_Flanged150 | flanged, Class 150 |
| cfhios:Valve010 | gate valve | cfhios:Valve010_OutletConnQ | — | cfhios:Conn_Flanged150 | flanged, Class 150 |
| cfhios:Valve010 | gate valve | cfhios:Valve010_SeatDesignQ | — | cfhios:SeatDesign_Metal | metal-to-metal seated |
| cfhios:Valve010 | gate valve | cfhios:Valve010_StemSealQ | — | cfhios:StemSeal_PTFEPacking | PTFE packing |
| cfhios:Valve010 | gate valve | cfhios:Valve010_OperatorTypeQ | — | cfhios:Operator_Manual | manual (hand wheel) |
| cfhios:Valve010 | gate valve | cfhios:Valve010_PressureRatingQ | — | cfhios:PressureRating_150 | Class 150 |
| cfhios:Valve010 | gate valve | cfhios:Valve010_SeatLeakageQ | — | cfhios:SeatLeakage_ClassVI | seat leakage Class VI |
| cfhios:Valve010 | gate valve | cfhios:Valve010_TempRatingQ | — | cfhios:TempRating_Minus29to400 | -29 C to 400 C |
| cfhios:Valve101 | gate valve | cfhios:Valve101_BodyMaterialQ | — | cfhios:Material_SS316 | SS316 (austenitic stainless steel) |
| cfhios:Valve101 | gate valve | cfhios:Valve101_InletConnQ | — | cfhios:Conn_Flanged150 | flanged, Class 150 |
| cfhios:Valve101 | gate valve | cfhios:Valve101_OutletConnQ | — | cfhios:Conn_Flanged150 | flanged, Class 150 |
| cfhios:Valve101 | gate valve | cfhios:Valve101_SeatDesignQ | — | cfhios:SeatDesign_Soft | soft-seated |
| cfhios:Valve101 | gate valve | cfhios:Valve101_StemSealQ | — | cfhios:StemSeal_Bellows | bellows |
| cfhios:Valve101 | gate valve | cfhios:Valve101_OperatorTypeQ | — | cfhios:Operator_Manual | manual (hand wheel) |
| cfhios:Valve101 | gate valve | cfhios:Valve101_PressureRatingQ | — | cfhios:PressureRating_150 | Class 150 |
| cfhios:Valve101 | gate valve | cfhios:Valve101_SeatLeakageQ | — | cfhios:SeatLeakage_ClassVI | seat leakage Class VI |
| cfhios:Valve101 | gate valve | cfhios:Valve101_TempRatingQ | — | cfhios:TempRating_Minus29to400 | -29 C to 400 C |
| ido:Valve001 | gate valve | ido:Valve001_BodyMaterialQ | body material | ido:Material_SS316 | SS316 (austenitic stainless steel) |
| ido:Valve001 | gate valve | ido:Valve001_InletConnQ | inlet connection type | ido:Conn_Flanged150 | flanged, Class 150 |
| ido:Valve001 | gate valve | ido:Valve001_OutletConnQ | outlet connection type | ido:Conn_Flanged150 | flanged, Class 150 |
| ido:Valve001 | gate valve | ido:Valve001_SeatDesignQ | seat design type | ido:SeatDesign_Soft | soft-seated |
| ido:Valve001 | gate valve | ido:Valve001_StemSealQ | stem sealing type | ido:StemSeal_Bellows | bellows |
| ido:Valve001 | gate valve | ido:Valve001_OperatorTypeQ | valve operator type | ido:Operator_Manual | manual (hand wheel) |
| ido:Valve001 | gate valve | ido:Valve001_PressureRatingQ | pressure rating | ido:PressureRating_150 | Class 150 |
| ido:Valve001 | gate valve | ido:Valve001_SeatLeakageQ | seat leakage class | ido:SeatLeakage_ClassVI | seat leakage Class VI |
| ido:Valve001 | gate valve | ido:Valve001_TempRatingQ | temperature rating | ido:TempRating_Minus29to400 | -29 C to 400 C |
| ido:Valve002 | gate valve | ido:Valve002_BodyMaterialQ | body material | ido:Material_CarbonSteel | carbon steel |
| ido:Valve002 | gate valve | ido:Valve002_InletConnQ | inlet connection type | ido:Conn_Flanged300 | flanged, Class 300 |
| ido:Valve002 | gate valve | ido:Valve002_OutletConnQ | outlet connection type | ido:Conn_Flanged300 | flanged, Class 300 |
| ido:Valve002 | gate valve | ido:Valve002_SeatDesignQ | seat design type | ido:SeatDesign_Metal | metal-to-metal seated |
| ido:Valve002 | gate valve | ido:Valve002_StemSealQ | stem sealing type | ido:StemSeal_PTFEPacking | PTFE packing |
| ido:Valve002 | gate valve | ido:Valve002_OperatorTypeQ | valve operator type | ido:Operator_Gearbox | gearbox operated |
| ido:Valve002 | gate valve | ido:Valve002_PressureRatingQ | pressure rating | ido:PressureRating_300 | Class 300 |
| ido:Valve002 | gate valve | ido:Valve002_SeatLeakageQ | seat leakage class | ido:SeatLeakage_ClassIV | seat leakage Class IV |
| ido:Valve002 | gate valve | ido:Valve002_TempRatingQ | temperature rating | ido:TempRating_Minus46to200 | -46 C to 200 C |
| ido:Valve003 | gate valve | ido:Valve003_BodyMaterialQ | body material | ido:Material_DuplexSS | duplex stainless steel |
| ido:Valve003 | gate valve | ido:Valve003_InletConnQ | inlet connection type | ido:Conn_ButtWeld | butt-weld |
| ido:Valve003 | gate valve | ido:Valve003_OutletConnQ | outlet connection type | ido:Conn_ButtWeld | butt-weld |
| ido:Valve003 | gate valve | ido:Valve003_SeatDesignQ | seat design type | ido:SeatDesign_Soft | soft-seated |
| ido:Valve003 | gate valve | ido:Valve003_OperatorTypeQ | valve operator type | ido:Operator_Pneumatic | pneumatic actuator |
| ido:Valve003 | gate valve | ido:Valve003_SeatLeakageQ | seat leakage class | ido:SeatLeakage_ClassVI | seat leakage Class VI |
| ido:Valve004 | gate valve | ido:Valve004_BodyMaterialQ | body material | ido:Material_SS316 | SS316 (austenitic stainless steel) |
| ido:Valve005 | gate valve | ido:Valve005_BodyMaterialQ | body material | ido:Material_CarbonSteel | carbon steel |
| ido:Valve005 | gate valve | ido:Valve005_InletConnQ | inlet connection type | ido:Conn_Threaded | threaded |
| ido:Valve005 | gate valve | ido:Valve005_OutletConnQ | outlet connection type | ido:Conn_Threaded | threaded |
| ido:Valve005 | gate valve | ido:Valve005_SeatDesignQ | seat design type | ido:SeatDesign_Metal | metal-to-metal seated |
| ido:Valve005 | gate valve | ido:Valve005_StemSealQ | stem sealing type | ido:StemSeal_Bellows | bellows |
| ido:Valve005 | gate valve | ido:Valve005_OperatorTypeQ | valve operator type | ido:Operator_Manual | manual (hand wheel) |
| ido:Valve005 | gate valve | ido:Valve005_PressureRatingQ | pressure rating | ido:PressureRating_150 | Class 150 |
| ido:Valve005 | gate valve | ido:Valve005_SeatLeakageQ | seat leakage class | ido:SeatLeakage_ClassIV | seat leakage Class IV |
| ido:Valve005 | gate valve | ido:Valve005_TempRatingQ | temperature rating | ido:TempRating_Minus29to400 | -29 C to 400 C |
| ido:Valve006 | gate valve | ido:Valve006_BodyMaterialQ | body material | ido:Material_SS316 | SS316 (austenitic stainless steel) |
| ido:Valve006 | gate valve | ido:Valve006_InletConnQ | inlet connection type | ido:Conn_Flanged150 | flanged, Class 150 |
| ido:Valve006 | gate valve | ido:Valve006_OutletConnQ | outlet connection type | ido:Conn_Flanged150 | flanged, Class 150 |
| ido:Valve006 | gate valve | ido:Valve006_PressureRatingQ | pressure rating | ido:PressureRating_150 | Class 150 |
| ido:Valve006 | gate valve | ido:Valve006_TempRatingQ | temperature rating | ido:TempRating_Minus46to200 | -46 C to 200 C |
| ido:Valve007 | gate valve | ido:Valve007_BodyMaterialQ | body material | ido:Material_DuplexSS | duplex stainless steel |
| ido:Valve007 | gate valve | ido:Valve007_InletConnQ | inlet connection type | ido:Conn_Flanged300 | flanged, Class 300 |
| ido:Valve007 | gate valve | ido:Valve007_OutletConnQ | outlet connection type | ido:Conn_Flanged300 | flanged, Class 300 |
| ido:Valve007 | gate valve | ido:Valve007_SeatDesignQ | seat design type | ido:SeatDesign_Soft | soft-seated |
| ido:Valve007 | gate valve | ido:Valve007_StemSealQ | stem sealing type | ido:StemSeal_PTFEPacking | PTFE packing |
| ido:Valve007 | gate valve | ido:Valve007_OperatorTypeQ | valve operator type | ido:Operator_Gearbox | gearbox operated |
| ido:Valve007 | gate valve | ido:Valve007_PressureRatingQ | pressure rating | ido:PressureRating_300 | Class 300 |
| ido:Valve007 | gate valve | ido:Valve007_SeatLeakageQ | seat leakage class | ido:SeatLeakage_ClassVI | seat leakage Class VI |
| ido:Valve007 | gate valve | ido:Valve007_TempRatingQ | temperature rating | ido:TempRating_Minus29to400 | -29 C to 400 C |
| ido:Valve008 | gate valve | ido:Valve008_BodyMaterialQ | body material | ido:Material_CarbonSteel | carbon steel |
| ido:Valve008 | gate valve | ido:Valve008_SeatDesignQ | seat design type | ido:SeatDesign_Metal | metal-to-metal seated |
| ido:Valve008 | gate valve | ido:Valve008_StemSealQ | stem sealing type | ido:StemSeal_Bellows | bellows |
| ido:Valve008 | gate valve | ido:Valve008_OperatorTypeQ | valve operator type | ido:Operator_Manual | manual (hand wheel) |
| ido:Valve009 | gate valve | ido:Valve009_BodyMaterialQ | body material | ido:Material_SS316 | SS316 (austenitic stainless steel) |
| ido:Valve009 | gate valve | ido:Valve009_InletConnQ | inlet connection type | ido:Conn_ButtWeld | butt-weld |
| ido:Valve009 | gate valve | ido:Valve009_OutletConnQ | outlet connection type | ido:Conn_ButtWeld | butt-weld |
| ido:Valve009 | gate valve | ido:Valve009_SeatDesignQ | seat design type | ido:SeatDesign_Soft | soft-seated |
| ido:Valve009 | gate valve | ido:Valve009_StemSealQ | stem sealing type | ido:StemSeal_Bellows | bellows |
| ido:Valve009 | gate valve | ido:Valve009_OperatorTypeQ | valve operator type | ido:Operator_Pneumatic | pneumatic actuator |
| ido:Valve009 | gate valve | ido:Valve009_PressureRatingQ | pressure rating | ido:PressureRating_600 | Class 600 |
| ido:Valve009 | gate valve | ido:Valve009_SeatLeakageQ | seat leakage class | ido:SeatLeakage_ClassIV | seat leakage Class IV |
| ido:Valve009 | gate valve | ido:Valve009_TempRatingQ | temperature rating | ido:TempRating_Minus46to200 | -46 C to 200 C |
| ido:Valve010 | gate valve | ido:Valve010_BodyMaterialQ | body material | ido:Material_DuplexSS | duplex stainless steel |
| ido:Valve010 | gate valve | ido:Valve010_InletConnQ | inlet connection type | ido:Conn_Flanged150 | flanged, Class 150 |
| ido:Valve010 | gate valve | ido:Valve010_OutletConnQ | outlet connection type | ido:Conn_Flanged150 | flanged, Class 150 |
| ido:Valve010 | gate valve | ido:Valve010_SeatDesignQ | seat design type | ido:SeatDesign_Metal | metal-to-metal seated |
| ido:Valve010 | gate valve | ido:Valve010_StemSealQ | stem sealing type | ido:StemSeal_PTFEPacking | PTFE packing |
| ido:Valve010 | gate valve | ido:Valve010_OperatorTypeQ | valve operator type | ido:Operator_Manual | manual (hand wheel) |
| ido:Valve010 | gate valve | ido:Valve010_PressureRatingQ | pressure rating | ido:PressureRating_150 | Class 150 |
| ido:Valve010 | gate valve | ido:Valve010_SeatLeakageQ | seat leakage class | ido:SeatLeakage_ClassVI | seat leakage Class VI |
| ido:Valve010 | gate valve | ido:Valve010_TempRatingQ | temperature rating | ido:TempRating_Minus29to400 | -29 C to 400 C |
