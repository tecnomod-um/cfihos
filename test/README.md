
# Testing the CFIHOS ontology

The ontology is tested and evaluated along four complementary lines:

* **Competency questions** — a set of natural-language questions with the corresponding SPARQL queries that answer them, covering asset classification, property/data validation, documentation and handover, traceability, and IDO interoperability. See [`competency-questions/`](competency-questions/README.md). Queries can be run against the ontology in Protégé using the SPARQL tab.

* **SHACL validation** — auto-generated and hand-authored SHACL shapes that validate example asset data against both the CFIHOS and CFIHOS-IDO ontologies. See [`shacl/`](shacl/README.md).

* **Data interoperability** — queries demonstrating that data built on the CFIHOS-IDO ontology interoperates with data built on plain IDO. See [`data-interoperability/`](data-interoperability/README.md).

* **Use case** — the ontology applied to a real industrial valve data sheet, modeled with both the standalone CFIHOS ontology ([`use-case/example-CFIHOS.ttl`](use-case/example-CFIHOS.ttl)) and the CFIHOS-IDO ontology ([`use-case/example-CFIHOS-IDO.ttl`](use-case/example-CFIHOS-IDO.ttl)).
