#!/usr/bin/env python3
"""
validate_ontology_consistency.py

Checks OWL ontologies for logical consistency and class satisfiability using
the HermiT reasoner

USAGE
    pip install owlready2          # one-time; requires a Java runtime on PATH
    python validate_ontology_consistency.py \
        --ontology CFIHOS=CORE-CFIHOS-V2.0.owl \
        --ontology CFIHOS-IDO=CORE-CFIHOS-V2.0_ido.owl \
        --reasoner hermit \
        --with-imports \
        --out-dir ./consistency_reports

    # To check a single file quickly:
    python validate_ontology_consistency.py --ontology CFIHOS=my_ontology.owl

NOTES ON IMPORTS
    If an ontology's owl:imports target is a remote URL (e.g. the IDO core
    ontology at https://rds.posccaesar.org/...), owlready2 will try to fetch
    it over the network the first time and then cache it locally under
    ~/.owlready2 (configurable via owlready2.onto_path). If you are working
    offline, place a local copy of the imported ontology in a folder and add
    that folder with --onto-path so owlready2 resolves the import from disk
    instead of the network.
"""

import argparse
import json
import sys
import time
from pathlib import Path


def eprint(*a, **kw):
    print(*a, file=sys.stderr, **kw)


def parse_ontology_args(pairs):
    """Turn ['CFIHOS=path/to.owl', 'CFIHOS-IDO=other.owl'] into a dict."""
    out = {}
    for p in pairs:
        if "=" not in p:
            eprint(f"--ontology expects NAME=PATH, got: {p!r}")
            sys.exit(2)
        name, path = p.split("=", 1)
        out[name] = path
    return out


def get_label(entity):
    try:
        labels = entity.label
        if labels:
            return str(labels[0])
    except Exception:
        pass
    return None


def run_check(name, path, reasoner, with_imports, onto_paths, out_dir):
    import owlready2
    from owlready2 import (
        get_ontology,
        sync_reasoner_hermit,
        sync_reasoner_pellet,
        onto_path,
        default_world,
        OwlReadyInconsistentOntologyError,
    )

    for p in onto_paths:
        onto_path.append(p)

    print(f"\n{'=' * 70}\nLoading ontology '{name}' from {path}\n{'=' * 70}")
    load_start = time.time()
    onto = get_ontology(Path(path).resolve().as_uri()).load(
        only_local=not with_imports and False  # see note below
    )
  
    load_time = time.time() - load_start

    n_classes = len(list(onto.classes()))
    n_obj_props = len(list(onto.object_properties()))
    n_data_props = len(list(onto.data_properties()))
    n_individuals = len(list(onto.individuals()))
    print(
        f"Loaded in {load_time:.1f}s | classes={n_classes} "
        f"object_properties={n_obj_props} data_properties={n_data_props} "
        f"individuals={n_individuals}"
    )

    reasoner_fn = {
        "hermit": sync_reasoner_hermit,
        "pellet": sync_reasoner_pellet,
    }[reasoner]

    print(f"Running {reasoner} reasoner (this can take a while for large ontologies)...")
    reason_start = time.time()
    consistent = True
    unsatisfiable = []
    error_message = None
    try:
        with onto:
            reasoner_fn(infer_property_values=False, debug=1)
    except OwlReadyInconsistentOntologyError as e:
        consistent = False
        error_message = str(e)
    reason_time = time.time() - reason_start

    if consistent:
        # After a successful classification, unsatisfiable classes are the
        # ones owlready2 marks as equivalent to owl.Nothing.
        Nothing = owlready2.Nothing
        for cls in onto.classes():
            try:
                if Nothing in cls.equivalent_to:
                    unsatisfiable.append(cls)
            except Exception:
                continue

    print(f"Reasoning finished in {reason_time:.1f}s")
    if not consistent:
        print("RESULT: INCONSISTENT ONTOLOGY")
        print(f"Reasoner message: {error_message}")
    elif unsatisfiable:
        print(f"RESULT: consistent, but {len(unsatisfiable)} unsatisfiable class(es) found:")
        for cls in unsatisfiable:
            print(f"  - {cls.iri}  (label: {get_label(cls)})")
    else:
        print("RESULT: consistent, 0 unsatisfiable classes")

    report = {
        "ontology_name": name,
        "path": str(Path(path).resolve()),
        "reasoner": reasoner,
        "with_imports_requested": with_imports,
        "load_time_seconds": round(load_time, 2),
        "reasoning_time_seconds": round(reason_time, 2),
        "n_classes": n_classes,
        "n_object_properties": n_obj_props,
        "n_data_properties": n_data_props,
        "n_individuals": n_individuals,
        "consistent": consistent,
        "error_message": error_message,
        "n_unsatisfiable_classes": len(unsatisfiable),
        "unsatisfiable_classes": [
            {"iri": cls.iri, "label": get_label(cls)} for cls in unsatisfiable
        ],
    }

    if out_dir:
        out_dir = Path(out_dir)
        out_dir.mkdir(parents=True, exist_ok=True)
        out_file = out_dir / f"{name}_consistency_report.json"
        out_file.write_text(json.dumps(report, indent=2))
        print(f"Report written to {out_file}")

    return report


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument(
        "--ontology",
        action="append",
        required=True,
        metavar="NAME=PATH",
        help="Ontology to check, e.g. --ontology CFIHOS=CORE-CFIHOS-V2.0.owl. "
        "Repeat this flag once per ontology variant.",
    )
    ap.add_argument(
        "--reasoner",
        choices=["hermit", "pellet"],
        default="hermit",
        help="Reasoner to use (default: hermit, matching the paper's methodology).",
    )
    ap.add_argument(
        "--with-imports",
        action="store_true",
        default=True,
        help="Resolve owl:imports (default: on). Use --no-imports to disable.",
    )
    ap.add_argument("--no-imports", dest="with_imports", action="store_false")
    ap.add_argument(
        "--onto-path",
        action="append",
        default=[],
        metavar="DIR",
        help="Local directory to search for imported ontologies before hitting "
        "the network (repeatable). Use this for offline runs.",
    )
    ap.add_argument(
        "--out-dir",
        default="consistency_reports",
        help="Directory to write per-ontology JSON reports (default: ./consistency_reports).",
    )
    args = ap.parse_args()

    try:
        import owlready2  # noqa: F401
    except ImportError:
        eprint(
            "owlready2 is not installed. Install it with:\n"
            "    pip install owlready2\n"
            "It requires a Java runtime (java) on PATH to run HermiT/Pellet."
        )
        sys.exit(1)

    ontologies = parse_ontology_args(args.ontology)
    results = []
    for name, path in ontologies.items():
        if not Path(path).exists():
            eprint(f"File not found for '{name}': {path}")
            sys.exit(1)
        results.append(
            run_check(
                name, path, args.reasoner, args.with_imports, args.onto_path, args.out_dir
            )
        )

    print(f"\n{'=' * 70}\nSummary\n{'=' * 70}")
    print(f"{'Ontology':<15}{'Consistent':<12}{'Unsatisfiable':<15}{'Classes':<10}{'Reasoning (s)':<15}")
    for r in results:
        print(
            f"{r['ontology_name']:<15}{str(r['consistent']):<12}"
            f"{r['n_unsatisfiable_classes']:<15}{r['n_classes']:<10}"
            f"{r['reasoning_time_seconds']:<15}"
        )

    if any(not r["consistent"] for r in results):
        sys.exit(1)


if __name__ == "__main__":
    main()
