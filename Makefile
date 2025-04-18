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
	cp unittest/PipelinedRV64/Hazard01Text.hex conf/rom.hex
	echo "ffffffff" >> conf/rom.hex
	cp unittest/PipelinedRV64/Hazard01.ref conf/pipelined.ref
	sbt 'testOnly core.pipelined.TinySocSpec'

test01:
	@echo "Running test01..."
	cp unittest/PipelinedRV64/test01.hex conf/rom.hex
	sbt 'testOnly core.pipelined.TinySocSpec'