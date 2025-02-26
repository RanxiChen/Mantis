.PHONY: test
test:
	sbt 'testOnly core.ALU.ALUSpec'

test_alu:
	( cd unittest/ALU && gcc generateTestVec.c -o generator && ./generator )
	sbt 'testOnly core.ALU.ALUSpec'

