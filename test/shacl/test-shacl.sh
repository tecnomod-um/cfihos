python3 validate_shacl.py -d data-examples/example.ttl  -s CFIHOS/cfihos-shapes.ttl --report CFIHOS/report-example.txt
python3 validate_shacl.py -d data-examples/example-A.ttl  -s CFIHOS-IDO/cfihos-ido-shapes.ttl --report CFIHOS-IDO/report-example-A.txt
python3 validate_shacl.py -d data-examples/valve-instances-compliant.ttl  -s CFIHOS/cfihos-shapes.ttl --report CFIHOS/report-valve-instances-compliant.txt
python3 validate_shacl.py -d data-examples/valve-instances-compliant.ttl  -s CFIHOS-IDO/cfihos-ido-shapes.ttl --report CFIHOS-IDO/report-valve-instances-compliant.txt
python3 validate_shacl.py -d data-examples/valve-instances-noncompliant.ttl  -s CFIHOS/cfihos-shapes.ttl --report CFIHOS/report-valve-instances-noncompliant.txt
python3 validate_shacl.py -d data-examples/valve-instances-noncompliant.ttl  -s CFIHOS-IDO/cfihos-ido-shapes.ttl --report CFIHOS-IDO/report-valve-instances-noncompliant.txt
