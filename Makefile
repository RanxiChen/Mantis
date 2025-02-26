.PHONY: test
test:
	sbt 'testOnly core.ALU.ALUSpec'

test_alu:
	make -C unittest/ALU
	sbt 'testOnly core.ALU.ALUSpec'

SUBDIR = unittest/ALU

clean:
	@for dir in $(SUBDIR);do \
		echo "Cleaning in $$dir...";\
		$(MAKE) -C $$dir clean; \
	done
