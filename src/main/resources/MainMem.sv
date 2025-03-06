module MainMem#(
	parameter nkb = 2
)(
	input logic [63:0] addr,
	input logic [2:0]  widthsel,//64,32,16,or default 8 bits
	output logic [63:0] readdata,//async read
	input logic [63:0] writedata,
	input logic WE,//sync write,when WE=high
	input logic clk,
	input logic reset
);

logic [7:0] main_mem [0:nkb*1024-1];
//async read
logic valid_addr=(addr <= nkb*1024-1) ? addr:0;
assign readdata[7:0] = main_mem[valid_addr];
assign readdata[15:8] = widthsel[0] ? main_mem[valid_addr+1] : 8'b0;
assign readdata[31:16] = widthsel[1] ? main_mem[valid_addr+3:valid_addr+2] : 16'b0;
assign readdata[63:32] = widthsel[2] ? main_mem[valid_addr+7:valid_addr+4] : 32'b0;

always_ff @(posedge clk, posedge reset )
begin
	if(reset)
		main_mem <= 0;
	else if(WE)
		main_mem[valid_addr+7:addr]<=writedata;
end

endmodule
