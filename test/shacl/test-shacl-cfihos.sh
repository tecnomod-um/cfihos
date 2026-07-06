pyshacl -d data-examples/example-A.ttl  -s CFIHOS/cfihos-shapes-astrea.ttl -e CFIHOS/cfihos-shapes-astrea.ttl --inference rdfs -o CFIHOS/report-example-A-astrea.txt
pyshacl -d data-examples/valve-instances-compliant.ttl  -s CFIHOS/cfihos-shapes-astrea.ttl -e CFIHOS/cfihos-shapes-astrea.ttl --inference rdfs -o CFIHOS/report-valve-instances-compliant-astrea.txt
pyshacl -d data-examples/valve-instances-noncompliant.ttl  -s CFIHOS/cfihos-shapes-astrea.ttl -e CFIHOS/cfihos-shapes-astrea.ttl --inference rdfs -o CFIHOS/report-valve-instances-noncompliant-astrea.txt
pyshacl -d data-examples/valve-compliant-with-cfihos-only.ttl  -s CFIHOS/cfihos-shapes-astrea.ttl -e CFIHOS/cfihos-shapes-astrea.ttl --inference rdfs -o CFIHOS/report-valve-compliant-with-cfihos-only-astrea.txt
pyshacl -d data-examples/valve-compliant-with-ido-only.ttl  -s CFIHOS/cfihos-shapes-astrea.ttl -e CFIHOS/cfihos-shapes-astrea.ttl --inference rdfs -o CFIHOS/report-valve-compliant-with-ido-only-astrea.txt
pyshacl -d data-examples/cfihos_valid_data_shapes_test.ttl  -s CFIHOS/cfihos-shapes-astrea.ttl -e CFIHOS/cfihos-shapes-astrea.ttl --inference rdfs -o CFIHOS/report-cfihos_valid_data_shapes_test-astrea.txt
pyshacl -d data-examples/cfihos_invalid_data_shapes_test.ttl  -s CFIHOS/cfihos-shapes-astrea.ttl -e CFIHOS/cfihos-shapes-astrea.ttl --inference rdfs -o CFIHOS/report-cfihos_invalid_data_shapes_test-astrea.txt

pyshacl -d data-examples/example-A.ttl  -s CFIHOS/cfihos-shapes-data.ttl -e CFIHOS/cfihos-shapes-data.ttl --inference rdfs -o CFIHOS/report-example-A-data.txt
pyshacl -d data-examples/valve-instances-compliant.ttl  -s CFIHOS/cfihos-shapes-data.ttl -e CFIHOS/cfihos-shapes-data.ttl --inference rdfs -o CFIHOS/report-valve-instances-compliant-data.txt
pyshacl -d data-examples/valve-instances-noncompliant.ttl  -s CFIHOS/cfihos-shapes-data.ttl -e CFIHOS/cfihos-shapes-data.ttl --inference rdfs -o CFIHOS/report-valve-instances-noncompliant-data.txt
pyshacl -d data-examples/valve-compliant-with-cfihos-only.ttl  -s CFIHOS/cfihos-shapes-data.ttl -e CFIHOS/cfihos-shapes-data.ttl --inference rdfs -o CFIHOS/report-valve-compliant-with-cfihos-only-data.txt
pyshacl -d data-examples/valve-compliant-with-ido-only.ttl  -s CFIHOS/cfihos-shapes-data.ttl -e CFIHOS/cfihos-shapes-data.ttl --inference rdfs -o CFIHOS/report-valve-compliant-with-ido-only-data.txt
pyshacl -d data-examples/cfihos_valid_data_shapes_test.ttl  -s CFIHOS/cfihos-shapes-data.ttl -e CFIHOS/cfihos-shapes-data.ttl --inference rdfs -o CFIHOS/report-cfihos_valid_data_shapes_test-data.txt
pyshacl -d data-examples/cfihos_invalid_data_shapes_test.ttl  -s CFIHOS/cfihos-shapes-data.ttl -e CFIHOS/cfihos-shapes-data.ttl --inference rdfs -o CFIHOS/report-cfihos_invalid_data_shapes_test-data.txt

