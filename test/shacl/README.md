# SHACL validation experiments — CFIHOS and CFIHOS‑IDO

This folder contains the SHACL validation experiments for the CFIHOS OWL ontologies. The goal is to demonstrate that CFIHOS asset data can be validated automatically — something the original spreadsheet
specification cannot support — and to compare two families of shapes across the
two ontology variants (standalone **CFIHOS** and the IDO‑aligned **CFIHOS‑IDO**).

## What is being tested

For each ontology variant we validate a set of RDF data examples against **two
independent SHACL shape sources**:

| Shapes file | Origin | What it checks |
|-------------|--------|----------------|
| `*-shapes-astrea.ttl` | Auto‑generated with [Astrea](https://astrea.linkeddata.es/) from the OWL ontology | Broad, structural constraints derived from the axioms: class membership of picklist values and units, node kinds, datatypes, cardinalities |
| `*-shapes-data.ttl` | Hand‑authored (data‑quality shapes) | Targeted domain constraints that axiom‑driven translation does not derive: picklist membership, **unit ↔ dimension** conformance, and **documentation completeness** |

The two sources are complementary: the Astrea shapes give comprehensive
coverage that stays in sync with the ontology, while the hand‑authored shapes
add the closed‑world / cross‑entity data‑quality rules.


## Data model reference

The examples follow the value model of the ontologies:

- **Qualitative + picklist** — the property points directly at a picklist
  member IRI: `:valve CFIHOS:CFIHOS-40000017 CFIHOS:CFIHOS-60000209`.
- **Quantitative** — an Ontology of Units of Measure (OM) measure:
  `[ a om-2:Measure ; om-2:hasUnit <unit> ; om-2:hasNumericalValue 13.7 ]`
  (numeric literals must be `xsd:decimal`).
- **Free text / boolean** — a wrapper individual with `qudt:value`:
  `[ a CFIHOS:TextValue ; qudt:value "CL150" ]`,
  `[ a CFIHOS:BooleanValue ; qudt:value "true"^^xsd:boolean ]`.
- **CFIHOS‑IDO only** — each characteristic is an `ido:hasQuality` whose value
  is attached with `ido:qualityQuantifiedAs`; the value nodes above are unchanged.

## Current results

`Conforms` for each data example × shape source (T = conforms, F = violations,
– = not part of that suite):

| Data example | CFIHOS / Astrea | CFIHOS / hand | IDO / Astrea | IDO / hand |
|--------------|:---:|:---:|:---:|:---:|
| `example-A` (standalone valve)        | T | T | – | – |
| `example-B` (IDO valve)               | – | – | T | T |
| `cfihos_valid_data_shapes_test`       | T | T | – | – |
| `cfihos_invalid_data_shapes_test`     | F | F | – | – |
| `cfihos_ido_valid_data_shapes_test`   | – | – | T | T |
| `cfihos_ido_invalid_data_shapes_test` | – | – | T | F |
| `valve-instances-compliant`           | T | T | T | T |
| `valve-instances-noncompliant`        | F | T | T | T |
| `valve-compliant-with-cfihos-only`    | T | T | F | T |
| `valve-compliant-with-ido-only`       | F | T | T | T |

### How to read the matrix

- **Positive cases conform** as expected: the standalone valve conforms to the
  CFIHOS shapes, the IDO valve to the CFIHOS‑IDO shapes, and the batches of
  compliant instances conform to their respective shapes.
- **Cross‑model rows** show the two representations are distinguishable:
  a CFIHOS‑only valve fails the IDO Astrea shapes, and an IDO‑only valve fails
  the CFIHOS Astrea shapes — each Astrea shape set expects its own serialization.
- **The two shape sources are complementary.** Neither catches everything on
  its own: `valve-instances-noncompliant` is flagged by the CFIHOS Astrea shapes
  but not by the hand‑authored ones, whereas `cfihos_ido_invalid_data_shapes_test`
  is flagged by the hand‑authored shapes but not by the IDO Astrea shapes. This
  motivates running both, and is the empirical basis for combining
  automatically generated structural shapes with hand‑authored data‑quality
  shapes.


## Folder structure

```
shacl/
├── README.md                     ← this file
├── test-shacl-cfihos.sh          ← runs all CFIHOS validations
├── test-shacl-cfihos-ido.sh      ← runs all CFIHOS‑IDO validations
│
├── CFIHOS/                        ← standalone variant
│   ├── cfihos-shapes-astrea.ttl  ← Astrea‑generated shapes
│   ├── cfihos-shapes-data.ttl    ← hand‑authored shapes
│   └── report-*.txt              ← one validation report per (data × shape source)
│
├── CFIHOS-IDO/                   ← IDO‑aligned variant
│   ├── cfihos-ido-shapes-astrea.ttl
│   ├── cfihos-ido-shapes-data.ttl
│   └── report-*.txt
│
└── data-examples/                ← RDF data validated in both suites
    ├── example-A.ttl                        ← valve, standalone (direct‑property) model
    ├── example-B.ttl                        ← same valve, CFIHOS‑IDO (quality/datum) model
    ├── cfihos_valid_data_shapes_test.ttl    ← minimal conformant fixture (CFIHOS)
    ├── cfihos_invalid_data_shapes_test.ttl  ← fixture with seeded violations (CFIHOS)
    ├── cfihos_ido_valid_data_shapes_test.ttl
    ├── cfihos_ido_invalid_data_shapes_test.ttl
    ├── valve-compliant-with-cfihos-only.ttl ← interoperability cross‑checks
    ├── valve-compliant-with-ido-only.ttl
    ├── valve-instances-compliant.ttl        ← batch of conformant valves
    └── valve-instances-noncompliant.ttl     ← batch with violations
```



## Requirements

- Python 3 with [pySHACL](https://github.com/RDFLib/pySHACL): `pip install pyshacl`

## Running the experiments

From this folder:

```bash
./test-shacl-cfihos.sh        # writes reports into CFIHOS/
./test-shacl-cfihos-ido.sh    # writes reports into CFIHOS-IDO/
```

Each line runs, for one data file and one shape source:

```bash
pyshacl -d <data-example>.ttl \
        -s <shapes>.ttl \
        -e <shapes>.ttl \
        --inference rdfs \
        -o <report>.txt
```

Notes on the flags:

- `-e <shapes>.ttl --inference rdfs` loads the shapes file **also as an
  ontology graph** and propagates `rdf:type` / `rdfs:subClassOf`. This is
  required: the `sh:class` checks (picklist members, units) rely on type
  triples that come from the ontology. pySHACL does **not** resolve
  `owl:imports` automatically, so the types must be supplied this way (or the
  data example must assert them inline).
- Reports are named `report-<data-example>-<astrea|data>.txt`, i.e. one per
  shape source, so the two views can be compared side by side.

## Reproducing / extending

To validate your own data against the real ontology instead of the bundled
stubs, load the ontology explicitly and keep RDFS inference on:

```bash
pyshacl -d your-data.ttl -s CFIHOS/cfihos-shapes-astrea.ttl \
        -e path/to/cfihos.ttl --inference rdfs -o report.txt
```

When the ontology is loaded this way, the inline `rdf:type` assertions in the
example files (picklist members and units) become redundant and can be removed.
