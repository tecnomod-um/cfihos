#!/usr/bin/env python3
"""
RDF data validator against SHACL shapes.

Requires the `pyshacl` library:
    pip install pyshacl rdflib

Usage:
    python validate_shacl.py --data data.ttl --shapes shapes.ttl
    python validate_shacl.py -d data.ttl -s shapes.ttl -f turtle --report report.txt
    python validate_shacl.py -d data.ttl -s shapes.ttl --inference rdfs

Output:
    Prints whether validation is conformant (CONFORMS / DOES NOT CONFORM)
    and the detailed violation report. Exit code 0 if conformant,
    1 if there are violations, 2 if an error occurred while running.
"""

import argparse
import sys
from pathlib import Path

try:
    from pyshacl import validate
except ImportError:
    print(
        "Error: missing 'pyshacl' library.\n"
        "Install it with: pip install pyshacl rdflib",
        file=sys.stderr,
    )
    sys.exit(2)


# Supported RDF formats and their typical extensions
RDF_FORMATS = {
    "turtle": [".ttl"],
    "xml": [".rdf", ".xml"],
    "json-ld": [".jsonld", ".json"],
    "n3": [".n3"],
    "nt": [".nt"],
    "trig": [".trig"],
}


def detect_format(path: Path) -> str:
    """Tries to infer the RDF format from the file extension."""
    ext = path.suffix.lower()
    for fmt, extensions in RDF_FORMATS.items():
        if ext in extensions:
            return fmt
    # 'turtle' is a reasonable default for rdflib
    return "turtle"


def validate_rdf(
    data_path: str,
    shapes_path: str,
    data_format: str = None,
    shapes_format: str = None,
    inference: str = "none",
    report_path: str = None,
    ont_graph: str = None,
):
    """
    Validates an RDF data graph against a SHACL shapes graph.

    Returns a tuple (conforms: bool, results_text: str, results_graph)
    """
    p_data = Path(data_path)
    p_shapes = Path(shapes_path)

    if not p_data.exists():
        raise FileNotFoundError(f"Data file not found: {data_path}")
    if not p_shapes.exists():
        raise FileNotFoundError(f"Shapes file not found: {shapes_path}")

    data_format = data_format or detect_format(p_data)
    shapes_format = shapes_format or detect_format(p_shapes)

    conforms, results_graph, results_text = validate(
        data_graph=str(p_data),
        shacl_graph=str(p_shapes),
        data_graph_format=data_format,
        shacl_graph_format=shapes_format,
        ont_graph=ont_graph,
        inference=inference,  # 'none', 'rdfs', 'owlrl', 'both'
        abort_on_first=False,
        allow_infos=True,
        allow_warnings=True,
        meta_shacl=False,
        advanced=True,
        debug=False,
    )

    if report_path:
        Path(report_path).write_text(results_text, encoding="utf-8")

    return conforms, results_text, results_graph


def main():
    parser = argparse.ArgumentParser(
        description="Validates an RDF data file against a SHACL shapes file.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument(
        "-d", "--data", required=True, help="Path to the RDF data file to validate"
    )
    parser.add_argument(
        "-s", "--shapes", required=True, help="Path to the SHACL shapes file with validation rules"
    )
    parser.add_argument(
        "--data-format",
        choices=list(RDF_FORMATS.keys()),
        default=None,
        help="Format of the data file (auto-detected from extension by default)",
    )
    parser.add_argument(
        "--shapes-format",
        choices=list(RDF_FORMATS.keys()),
        default=None,
        help="Format of the shapes file (auto-detected from extension by default)",
    )
    parser.add_argument(
        "--inference",
        choices=["none", "rdfs", "owlrl", "both"],
        default="none",
        help="Type of inference to apply before validating",
    )
    parser.add_argument(
        "--ontology",
        default=None,
        help="Optional path to an additional ontology graph (ont_graph)",
    )
    parser.add_argument(
        "--report",
        default=None,
        help="Path to save the validation report as plain text",
    )
    parser.add_argument(
        "-q", "--quiet",
        action="store_true",
        help="Only print the result (CONFORMS/DOES NOT CONFORM), not the full report",
    )

    args = parser.parse_args()

    try:
        conforms, results_text, _ = validate_rdf(
            data_path=args.data,
            shapes_path=args.shapes,
            data_format=args.data_format,
            shapes_format=args.shapes_format,
            inference=args.inference,
            report_path=args.report,
            ont_graph=args.ontology,
        )
    except Exception as e:
        print(f"Error during validation: {e}", file=sys.stderr)
        sys.exit(2)

    status = "CONFORMS" if conforms else "DOES NOT CONFORM"
    print(f"\nSHACL validation result: {status}\n")

    if not args.quiet:
        print(results_text)

    if args.report:
        print(f"\nReport saved to: {args.report}")

    sys.exit(0 if conforms else 1)


if __name__ == "__main__":
    main()
