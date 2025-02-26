alu_testcases.csv:
	gcc generateTestVec.c -o generator.out
	./generator.out

clean:
	find . -type f -executable -exec rm '{}' \;
	rm -rf *.csv