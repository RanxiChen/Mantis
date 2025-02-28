.PHONY: test
test:
	sbt 'testOnly core.ALU.ALUSpec'

test_alu:
	make -C unittest/ALU
	sbt 'testOnly core.ALU.ALUSpec'

SUBDIR = unittest/ALU unittest/RV64I_part

cmp_test:
	$(MAKE) -C unittest/RV64I_part all
	sbt 'testOnly core.PU.ExtInstNoMemPUSpecCmp '

clean:
	@for dir in $(SUBDIR);do \
		echo "Cleaning in $$dir...";\
		$(MAKE) -C $$dir clean; \
	done
