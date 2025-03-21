
module cpu (
    input clk,
    input rst,

    input [15:0] hartid;        // 常量，该核心的编号


    // 指令流控制接口：

    input pause;                // 高电平时核心内所有时钟暂停
    input halt;                 // 高电平时核心内所有时钟暂停，同时清空流水线，重置所有输出位
    input redir_vld;            // 高电平时，在本周期内将核心重定向到指定pc
    input [46:0] redir_pc;      // 重定向目标pc，省略最低一位的0
    input [55:0] redir_pgtable; // 重定向后新指令流的页表地址
    input [7:0] redir_asid;     // 重定向后新指令流的asid（与TLB索引相关，用于隔离不同进程的TLB）

    // TLB接口：
    
    /**
    * 页表规则：
    * 使用SV48地址空间配置，虚拟地址48bits，物理地址56bits
    * 页长度4KB，对应12bits虚拟地址；页表使用四级页表，每级对应9bits虚拟地址，索引2^9个64bits页表项；共4*9+12=48bits
    * 
    * 虚拟地址格式：
    * 47 -------- 39 | 38 -------- 30 | 29 -------- 21 | 20 -------- 12 | 11 -------- 0
    *      VPN3             VPN2             VPN1             VPN0            Offset 
    * 
    * 物理地址格式：
    * 55 ------------------ 12 | 11 -------- 0
    *            PPN                 Offset 
    * 
    * 页表项格式：
    * 53 ------------------ 10 | 9 8 | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0
    *            PPN                   D   A   G   U   X   W   R   V
    * 当[7:0]全为0时，该表项的[53:10]指向下一级页表
    */

    input tlb_flush_vld;
    input tlb_flush_all;
    input [35:0] tlb_flush_vpage;

    // 中断事件接口：

    output interrupt_pagefault;         // 当核心出现缺页异常时，暂停流水线并将该输出位拉高，并维持到核心解除暂停或重定向
    output interrupt_ecall;             // 当核心提交一个ecall指令时，暂停流水线并将该输出位拉高，并维持到核心解除暂停或重定向
    output interrupt_ebreak;            // 当核心提交一个ebreak指令时，暂停流水线并将该输出位拉高，并维持到核心解除暂停或重定向
    input interrupt_resp_continue;      // 当核心由于上述情况暂停时，如果该输入为高电平，则下一周期核心解除暂停并保持暂停前的状态继续执行后续指令


    // 寄存器直接访问接口：

    input regacc_vld;                   // 核心在pause或interrupt状态下，高电平时，在本周期内核心响应对逻辑寄存器的直接操作。在下一次regacc_resp_vld被置高后的下一周期前不会再次发起
    input [7:0] regacc_idx;             // 该操作的逻辑寄存器号
    input regacc_write;                 // 高电平时，该操作为寄存器写；低电平时，该操作为寄存器读
    input [63:0] regacc_data;           // 该操作为寄存器写时，待写入的寄存器数据

    output regacc_resp_vld;             // 当核心完成一个直接寄存器操作时将该输出位拉高，不维持到下一周期
    output [63:0] regacc_resp_data;     // 该操作为寄存器读时，读出的寄存器数据


    // Non-cache内存段访问接口：

    input [15:0] noncache_haddr_bottom; // 常量，无缓存内存段的起始地址高16位，即虚拟地址[47:32]
    input [15:0] noncache_haddr_top;    // 常量，无缓存内存段的结束地址高16位，即虚拟地址[47:32]，核心对此区间内的内存load/store不经过页表翻译且必须通过noncache接口发出

    input noncache_req_rdy;                 // 
    output reg noncache_req_vld;            // 发起一次noncache请求，在收到下一次noncache回复前不会再次发起
    output reg [47:0] noncache_req_addr;    // 本次请求的内存地址
    output reg [3:0] noncache_req_len;      // 本次请求的内存长度，b0001: 1byte, b0010: 2bytes, b0100: 4bytes, b1000: 8bytes
    output reg noncache_req_store;          // 高电平时，该操作为store；低电平时，该操作为load
    output reg [63:0] noncache_req_data;    // 该操作为store时，待写入的数据

    output reg noncache_resp_rdy;       // 
    input noncache_resp_vld;            // 
    input [7:0] noncache_resp_expt;     // 本次noncache请求出现的异常，1: 无效地址, 2:错误对齐
    input [63:0] noncache_resp_data;    // 本次请求操作为load时，读出的数据


    // L1I cache接口：

    /**
    * Fetch规则：
    * 每次从ICache取出若干条连续指令，要求：
    * 1. 指令数量不大于4
    * 2. 指令总长度不大于128bits
    * 3. 最多包含一个 分支/跳转/AMO/FENCE/SYSTEM 指令
    * 4. 如果存在一个3.中所述指令，则其一定是本次取出的最后一条指令
    */

    output icache_req_vld;              // 发起一次对icache的访问，本周期内结束
    output [54:0] icache_req_vpc;       // 本次请求pc的虚拟地址，省略最低一位的0
    output [54:0] icache_req_ppc;       // 本次请求pc的物理地址，省略最低一位的0
    input icache_resp_hit;              // 本次icache访问是否命中
    input [127:0] icache_resp_data;     // 从pc开始的128位数据，可能包含多个指令
    input [7:0] icache_resp_mask;       // 本次fetch结果中哪些位置是有效的，每一位对应data中的16bits
    input [7:0] icache_resp_expt;       // 请求出现的异常, 1: 无效地址


    // L1D cache接口：

    input dcache_req_rdy;               //
    output reg dcache_req_vld;          // 发起一次dcache请求，可连续发起
    output reg [15:0] dcache_req_id;    // 本次请求的id，由核心生成并指定，仅用于乱序dcache流水线
    output reg [55:0] dcache_req_addr;  // 本次请求的物理地址
    output reg [3:0] dcache_req_len;    // 本次请求的内存长度，b0001: 1byte, b0010: 2bytes, b0100: 4bytes, b1000: 8bytes
    output reg [8:0] dcache_req_op;     // 本次请求的操作，h00: load, h01: store, h80: amoadd, h81: amoswap, h82: amolr, h83: amosc, h84: amoxor, h88: amoor, h8c: amoand, h90: amomin, h94: amomax, h98: amominu, h9c: amomaxu
    output reg [63:0] dcache_req_data;  // 请求为store或amo时，给出的操作数

    output reg dcache_resp_rdy;         //
    input dcache_resp_vld;              //
    input [15:0] dcache_resp_id;        // 本次请求的id
    input [7:0] dcache_resp_expt;       // 本次请求出现的异常，1: 无效地址, 2:错误对齐
    input [63:0] dcache_resp_data;      // 请求为load或amo时，返回的操作数
);
    
endmodule
