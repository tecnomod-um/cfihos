# The CFIHOS Ontology

![OQuaRE evaluation](https://github.com/tecnomod-um/cfihos/actions/workflows/oquare.yml/badge.svg)

This repository provides an OWL ontology for **CFIHOS** (Capital Facilities Information Handover Specification), the industry standard for asset information handover used in capital-intensive sectors such as oil and gas. The ontology is generated automatically from the official [CFIHOS specification in Excel format](./data/CORE-CFIHOS-V2.0-excel-FINAL.xlsx), making CFIHOS's classes, properties, and controlled vocabularies machine-interpretable for tasks such as automated reasoning, semantic validation, and data interoperability.

Two ontology variants are provided:

* a **standalone CFIHOS ontology**, formalizing the specification as-is;
* a **CFIHOS-IDO ontology**, which additionally aligns CFIHOS with the [Industrial Data Ontology (IDO)](https://rds.posccaesar.org/ontology/lis14/ont/core/), grounding CFIHOS-based asset data in a broader, standardization-track industrial semantic ecosystem.

## Repository structure

| Path | Contents |
|---|---|
| [`data/`](./data) | The source CFIHOS specification, in Excel format |
| [`code/`](./code/README.md) | Java/Maven project that converts the Excel specification into the two OWL ontologies |
| [`ontology/`](./ontology/README.md) | The generated ontologies: `CORE-CFIHOS-V2.0.owl` (CFIHOS-only) and `CORE-CFIHOS-V2.0_ido.owl` (CFIHOS-IDO aligned), plus a local copy of IDO |
| [`test/`](./test/README.md) | Competency questions, SHACL validation, data-interoperability tests, and a real-world use case |
| [`oquare-evaluation/`](./oquare-evaluation) | Structural quality reports generated automatically by the [OQuaRE](https://github.com/tecnomod-um/oquare-metrics) GitHub Action on every push |
| [`docs/`](./docs) | Slide deck (`cfihos-ontology.pptx`) and a supporting Word document (`cfihos.docx`) describing the ontology |
| `catalog-v001.xml` | Protégé XML catalog, so that ontology imports resolve to the local files in this repository instead of the network |

## Getting started

To browse the ontologies in [Protégé](https://protege.stanford.edu/), open the repository root as the working directory so that `catalog-v001.xml` can resolve the `owl:imports` (CFIHOS-IDO, IDO) to the local files rather than fetching them from the network.

To regenerate the ontologies from the source Excel file, see [`code/README.md`](./code/README.md) — this requires Java 17 and Maven.

To run the validation and interoperability tests, see [`test/README.md`](./test/README.md) — SHACL validation additionally requires Python 3 and [pySHACL](https://github.com/RDFLib/pySHACL).

## Ontologies

* [`CORE-CFIHOS-V2.0.owl`](./ontology/CORE-CFIHOS-V2.0.owl) — the standalone CFIHOS ontology.
* [`CORE-CFIHOS-V2.0_ido.owl`](./ontology/CORE-CFIHOS-V2.0_ido.owl) — the CFIHOS ontology aligned with IDO.
* [`ido.owl`](./ontology/ido.owl) — a local copy of the Industrial Data Ontology, imported by the aligned variant.

See [`ontology/README.md`](./ontology/README.md) for details.

## Testing and evaluation

The ontology is evaluated along several complementary dimensions, all documented in [`test/`](./test/README.md):

* **Competency questions** — a set of natural-language questions with corresponding SPARQL queries, verifying that the ontology supports the intended use cases ([`test/competency-questions/`](./test/competency-questions/README.md)).
* **SHACL validation** — automatically generated and hand-authored SHACL shapes validate example asset data against both ontology variants ([`test/shacl/`](./test/shacl/README.md)).
* **Data interoperability** — queries demonstrating that data built on the CFIHOS-IDO ontology interoperates with data built on plain IDO ([`test/data-interoperability/`](./test/data-interoperability/README.md)).
* **Use case** — a real industrial valve data sheet modeled with both ontology variants ([`test/use-case/`](./test/use-case)).
* **Structural quality (OQuaRE)** — computed automatically on every push via GitHub Actions, with results published to [`oquare-evaluation/`](./oquare-evaluation).

## Documentation

The [`docs/`](./docs) folder contains a slide deck (`cfihos-ontology.pptx`) and a Word document (`cfihos.docx`) with additional background on the ontology's design.

## Citation

A paper describing the ontology, its IDO alignment, and its evaluation is currently in preparation.

Please check back here for a full citation once the paper is published.

## Contact

* Francisco Abad-Navarro — Universidad de Murcia [francisco.abad@um.es](mailto:francisco.abad@um.es)
* Jesualdo Tomás Fernández-Breis — Universidad de Murcia (corresponding author, [jfernand@um.es](mailto:jfernand@um.es))
* Alexander García-Castro — Siemens Energy [alexandergarcia-castro@siemens-energy.com](mailto:alexandergarcia-castro@siemens-energy.com)
