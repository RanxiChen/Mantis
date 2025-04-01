.PHONY: test

test_alu:
	make -C unittest/ALU
	sbt 'testOnly core.ALU.ALUSpec'

SUBDIR = unittest/ALU unittest/RV64I_part build/CtrlU build/RV64

cmp_test:
	$(MAKE) -C unittest/RV64I_part all
	sbt 'testOnly core.PU.ExtInstNoMemPUSpecCmp '

clean:
	@for dir in $(SUBDIR);do \
		echo "Cleaning in $$dir...";\
		$(MAKE) -C $$dir clean; \
	done

test:
	cp unittest/PipelinedRV64/JustCompute_rom.hex conf/rom.hex
	cp unittest/PipelinedRV64/JustCompute.ref conf/pipelined.ref
	sbt 'testOnly core.pipelined.TinySocSpec'