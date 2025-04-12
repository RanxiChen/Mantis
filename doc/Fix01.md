# Fix jalr jump to error location
```
auipc x22,0
addi x22,x22,16
jalr x23,x22,0
```
In my bypass network, when run jalr, if stage will find reg22 in wb stage first,
will the result of addi is not written back
I want to reverse the check order