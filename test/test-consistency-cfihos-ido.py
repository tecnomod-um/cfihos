 python3 validate_ontology_consistency.py \
        --ontology CFIHOS=../ontology/CORE-CFIHOS-V2.0_ido.owl \
        --reasoner hermit \
        --with-imports \
        --onto-path ../ontology/ \
        --out-dir ./consistency_reports 
