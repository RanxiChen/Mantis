#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

// ALU操作类型枚举
typedef enum {
    ALU_ADD = 0,
    ALU_SUB = 1,
    ALU_AND = 2,
    ALU_OR  = 3,
    ALU_XOR = 4,
    ALU_SLL = 5,
    ALU_SRL = 6,
    ALU_SRA = 7,
    ALU_SLT = 8,
    ALU_SLTU = 9,
    ALU_OP_COUNT
} ALUOp;

// 计算ALU结果
int64_t compute_result(int64_t a, int64_t b, ALUOp op) {
    switch (op) {
        case ALU_ADD: return a + b;
        case ALU_SUB: return a - b;
        case ALU_AND: return a & b;
        case ALU_OR:  return a | b;
        case ALU_XOR: return a ^ b;
        case ALU_SLT: return (a < b) ? 1 : 0;
        case ALU_SLTU:return ( (uint64_t)a < (uint64_t)b ) ? 1 :0;
        case ALU_SLL: return a << (b & 0x3F); // 只使用低6位进行移位
        case ALU_SRL: return (uint64_t)a >> (b & 0x3F);
        case ALU_SRA: return a >> (b & 0x3F);
        default:      return 0;
    }
}

// 生成随机64位整数
int64_t random_int64() {
    int64_t value = 0;
    for (int i = 0; i < 4; i++) {
        value = (value << 16) | (rand() & 0xFFFF);
    }
    return value;
}

// 生成特殊测试用例
void generate_special_testcases(FILE* file) {
    // 边界情况
    int64_t special_values[] = {
        0, 1, -1, INT64_MAX, INT64_MIN,
        0x7FFFFFFFFFFFFFFF, 0x8000000000000000,
        0xFFFFFFFFFFFFFFFF, 0x5555555555555555, 0xAAAAAAAAAAAAAAAA
    };
    
    int num_special = sizeof(special_values) / sizeof(special_values[0]);
    
    for (int i = 0; i < num_special; i++) {
        for (int j = 0; j < num_special; j++) {
            for (int op = 0; op < ALU_OP_COUNT; op++) {
                int64_t a = special_values[i];
                int64_t b = special_values[j];
                int64_t result = compute_result(a, b, op);
                
                fprintf(file, "0x%016llx,0x%016llx,%d,0x%016llx\n", 
                        (unsigned long long)a, 
                        (unsigned long long)b, 
                        op, 
                        (unsigned long long)result);
            }
        }
    }
}

// 生成随机测试用例
void generate_random_testcases(FILE* file, int count) {
    for (int i = 0; i < count; i++) {
        int64_t a = random_int64();
        int64_t b = random_int64();
        ALUOp op = rand() % ALU_OP_COUNT;
        int64_t result = compute_result(a, b, op);
        
        fprintf(file, "0x%016llx,0x%016llx,%d,0x%016llx\n", 
                (unsigned long long)a, 
                (unsigned long long)b, 
                op, 
                (unsigned long long)result);
    }
}

int main() {
    // 初始化随机数生成器
    srand(42); // 固定种子以获得可重复的结果
    
    // 打开文件
    FILE* file = fopen("alu_testcases.csv", "w");
    if (!file) {
        perror("无法创建文件");
        return 1;
    }
    
    // 写入CSV标题
    fprintf(file, "operand_a,operand_b,operation,expected_result\n");
    
    // 生成特殊测试用例
    generate_special_testcases(file);
    
    // 生成随机测试用例
    generate_random_testcases(file, 1000);
    
    // 关闭文件
    fclose(file);
    printf("ALU测试集已生成到 alu_testcases.csv\n");
    
    return 0;
}

