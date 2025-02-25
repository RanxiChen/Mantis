.PHONY: test
test:
	sbt 'testOnly core.ALU.ALUSpec'
