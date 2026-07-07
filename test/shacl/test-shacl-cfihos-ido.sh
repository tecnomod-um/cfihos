pyshacl -d data-examples/example-CFIHOS-IDO.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl -e CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl --inference rdfs -o CFIHOS-IDO/report-example-CFIHOS-IDO-astrea.txt
pyshacl -d data-examples/valve-instances-compliant.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl -e CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl --inference rdfs -o CFIHOS-IDO/report-valve-instances-compliant-astrea.txt
pyshacl -d data-examples/valve-instances-noncompliant.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl -e CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl --inference rdfs -o CFIHOS-IDO/report-valve-instances-noncompliant-astrea.txt
pyshacl -d data-examples/valve-compliant-with-cfihos-only.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl -e CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl --inference rdfs -o CFIHOS-IDO/report-valve-compliant-with-cfihos-only-astrea.txt
pyshacl -d data-examples/valve-compliant-with-ido-only.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl -e CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl --inference rdfs -o CFIHOS-IDO/report-valve-compliant-with-ido-only-astrea.txt
pyshacl -d data-examples/cfihos_ido_valid_data_shapes_test.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl -e CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl --inference rdfs -o CFIHOS-IDO/report-cfihos_ido-valid_data_shapes_test-astrea.txt
pyshacl -d data-examples/cfihos_ido_invalid_data_shapes_test.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl -e CFIHOS-IDO/cfihos-ido-shapes-astrea.ttl --inference rdfs -o CFIHOS-IDO/report-cfihos_ido-invalid_data_shapes_test-astrea.txt

pyshacl -d data-examples/example-CFIHOS-IDO.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-data.ttl -e CFIHOS-IDO/cfihos-ido-shapes-data.ttl --inference rdfs -o CFIHOS-IDO/report-example-CFIHOS-IDO-data.txt
pyshacl -d data-examples/valve-instances-compliant.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-data.ttl -e CFIHOS-IDO/cfihos-ido-shapes-data.ttl --inference rdfs -o CFIHOS-IDO/report-valve-instances-compliant-data.txt
pyshacl -d data-examples/valve-instances-noncompliant.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-data.ttl -e CFIHOS-IDO/cfihos-ido-shapes-data.ttl --inference rdfs -o CFIHOS-IDO/report-valve-instances-noncompliant-data.txt
pyshacl -d data-examples/valve-compliant-with-cfihos-only.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-data.ttl -e CFIHOS-IDO/cfihos-ido-shapes-data.ttl --inference rdfs -o CFIHOS-IDO/report-valve-compliant-with-cfihos-only-data.txt
pyshacl -d data-examples/valve-compliant-with-ido-only.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-data.ttl -e CFIHOS-IDO/cfihos-ido-shapes-data.ttl --inference rdfs -o CFIHOS-IDO/report-valve-compliant-with-ido-only-data.txt
pyshacl -d data-examples/cfihos_ido_valid_data_shapes_test.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-data.ttl -e CFIHOS-IDO/cfihos-ido-shapes-data.ttl --inference rdfs -o CFIHOS-IDO/report-cfihos_ido-valid_data_shapes_test-data.txt
pyshacl -d data-examples/cfihos_ido_invalid_data_shapes_test.ttl  -s CFIHOS-IDO/cfihos-ido-shapes-data.ttl -e CFIHOS-IDO/cfihos-ido-shapes-data.ttl --inference rdfs -o CFIHOS-IDO/report-cfihos_ido-invalid_data_shapes_test-data.txt

