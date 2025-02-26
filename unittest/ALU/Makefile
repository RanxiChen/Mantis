alu_testcases.csv:
	gcc generateTestVec.c -o generator
	./generator

clean:
	find . -type f -executable -exec rm '{}' \;
	rm -rf *.csv