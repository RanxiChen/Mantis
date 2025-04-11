module PU (
    input clk,
    input rst,
    output [63:0] fetch_inst_addr,
    input [63:0] fetch_inst_inst,
);
reg [63:0] pcGen;