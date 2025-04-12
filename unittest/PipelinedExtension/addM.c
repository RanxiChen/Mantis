#include<stdio.h>
#include<stdint.h>
int main(void){
	uint64_t r1,r2,r3,r4,r5,r6;
	asm volatile (
			"add a1,x0,2\n\t"
			"add a2,x0,46\n\t"
			"add a3,x0,10\n\t"
			"mul a4,a1,a2\n\t"
			"mul a5,a4,a4\n\t"
			"mul a6,a5,a3\n\t"
			"addi %0,a1,0\n\t"
			"addi %1,a2,0\n\t"
			"addi %2,a3,0\n\t"
			"addi %3,a4,0\n\t"
			"addi %4,a5,0\n\t"
			"addi %5,a6,0\n\t"
			:"=r"(r1),"=r"(r2),"=r"(r3),"=r"(r4),"=r"(r5),"=r"(r6)
		     );
	printf("r1=%016x\n",r1);
	printf("r2=%016x\n",r2);
	printf("r3=%016x\n",r3);
	printf("r4=%016x\n",r4);
	printf("r5=%016x\n",r5);
	printf("r6=%016x\n",r6);
	return 0;
}
