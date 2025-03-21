
module cpu (

    input   clk, rst
    
    // CPU控制器接口CCTR

    ,input  [15:0]  s_cctr_hartid               // Hart ID，常量

    // CPU有三种状态：
    // Normal:      流水线正常运行；当CPU收到外部redirect或外部continue后进入
    // Halt:        流水线被保持复位；当CPU复位后或收到外部halt指令后进入
    // Interrupted: 流水线暂停，但保持流水线状态；当CPU流水线产生内部中断或收到外部唤起中断后进入
    // 与常规CPU不同，测试用的CPU复位后处于Halt状态，需要外部控制器手动通过redirect接口启动

    ,output         s_cctr_halted               // CPU是否处于Halt状态（停止状态）
    ,output         s_cctr_interrupted          // CPU是否处于Interrupted状态（中断状态）
    ,output [15:0]  s_cctr_itr_idx              // CPU当前的中断号
    ,output [63:0]  s_cctr_itr_arg              // CPU当前的中断参数

    ,input          s_cctr_halt                 // 停止CPU **仅持续单周期，需随时缓存**

    ,input          s_cctr_raise_itr            // 中断CPU **仅持续单周期，需随时缓存**
    ,input  [15:0]  s_cctr_raise_itr_idx        // 中断号（外部中断参数默认为0） **仅持续单周期，需随时缓存**

    ,input          s_cctr_redir                // 重定向CPU **仅持续单周期，需随时缓存**
    ,input  [63:0]  s_cctr_redir_pc             // 重定向的目标PC（虚拟地址） **仅持续单周期，需随时缓存**
    ,input  [63:0]  s_cctr_redir_pgtable        // 重定向后的页表基址（物理地址） **仅持续单周期，需随时缓存**
    ,input  [7:0]   s_cctr_redir_asid           // 重定向后的ASID **仅持续单周期，需随时缓存**

    // 下面所有CCTR接口只在CPU处于interrupted状态时有效

    ,input          s_cctr_continue             // 继续CPU **仅持续单周期，需随时缓存**

    ,input          s_cctr_flush_tlb            // 清空单个TLB页表项
    ,input          s_cctr_flush_tlb_all        // 清空所有TLB页表项
    ,input          s_cctr_flush_tlb_asid       // 清空ASID对应的所有TLB页表项
    ,input  [63:0]  s_cctr_flush_tlb_idx        // 清空TLB的参数，低12位[11:0]为ASID，高52位[63:12]为虚拟页号
    ,output         s_cctr_flush_busy

    ,input          s_cctr_regacc               // 访问一个通用寄存器
    ,input  [7:0]   s_cctr_regacc_idx           // 寄存器编号
    ,input          s_cctr_regacc_write         // 写使能
    ,input  [63:0]  s_cctr_regacc_wdata         // 写数据
    ,output         s_cctr_regacc_busy
    ,output [63:0]  s_cctr_regacc_rdata         // 读数据

    ,input          s_cctr_pxymem               // 访问一个64bits宽的物理内存地址
    ,input  [63:0]  s_cctr_pxymem_addr          // 物理地址，对齐到8bytes
    ,input          s_cctr_pxymem_write         // 写使能
    ,input  [63:0]  s_cctr_pxymem_wdata         // 写数据
    ,output         s_cctr_pxymem_busy
    ,output [63:0]  s_cctr_pxymem_rdata         // 读数据


    // CPU-L1ICache接口

    ,output         m_ici_req_vld
    ,output [63:0]  m_ici_req_vpc               // PC虚拟地址
    ,output [63:0]  m_ici_req_ppc               // PC物理地址
    ,output [63:0]  m_ici_req_nppc              // PC的下一个CacheLine的物理地址
    ,output         m_ici_req_nvld              // 是否需要索引下一个CacheLine
    ,input          m_ici_resp_hit              // **单周期内响应**
    ,input  [127:0] m_ici_resp_data
    ,input  [7:0]   m_ici_resp_mask             // Data掩码，每1bit对应Data的16bits

    // CPU-L1DCache接口

    ,input          m_dci_req_rdy
    ,output         m_dci_req_vld
    ,output [7:0]   m_dci_req_id                // CPU分配的ID，用于DCache与核心流水线的同步
    ,output [63:0]  m_dci_req_addr              // 物理地址
    ,output [3:0]   m_dci_req_len               // 访存长度，单位byte：8/4/2/1
    ,output [7:0]   m_dci_req_op                // 操作符。[7:5]: 0 Load, 1 LoadU, 2 Store, 3 AMO。[4:0]：AMO-OP5
    ,output [63:0]  m_dci_req_data              // 源操作数
    ,output         m_dci_resp_rdy
    ,input          m_dci_resp_vld
    ,input  [7:0]   m_dci_resp_id               // ID
    ,input  [63:0]  m_dci_resp_data             // 结果操作数

    // CPU-L1ITLB接口
    
    ,output [7:0]   m_tlbi_0_asid               // ASID
    ,output [63:0]  m_tlbi_0_pgtable            // 页表基址

    ,output         m_tlbi_0_vld
    ,output [63:0]  m_tlbi_0_vaddr              // 虚拟地址
    ,input          m_tlbi_0_hit
    ,input  [63:0]  m_tlbi_0_entry              // 页表项：[53:10]物理页编号，[7:0] D|A|G|U|X|W|R|V

    ,output         m_tlbi_0_flush_all
    ,output         m_tlbi_0_flush_asid
    ,output         m_tlbi_0_flush_vpg
    ,output [63:0]  m_tlbi_0_flush_idx          // 清空TLB的参数，低12位[11:0]为ASID，高52位[63:12]为虚拟页号
    ,input          m_tlbi_0_flush_busy

    // CPU-L1DTLB接口
    
    ,output [7:0]   m_tlbi_1_asid
    ,output [63:0]  m_tlbi_1_pgtable
    
    ,output         m_tlbi_1_vld
    ,output [63:0]  m_tlbi_1_vaddr
    ,input          m_tlbi_1_hit
    ,input  [63:0]  m_tlbi_1_entry

    ,output         m_tlbi_1_flush_all
    ,output         m_tlbi_1_flush_asid
    ,output         m_tlbi_1_flush_vpg
    ,output [63:0]  m_tlbi_1_flush_idx
    ,input          m_tlbi_1_flush_busy
    
);
    
endmodule

