   0:	fd010113          	addi	x2,x2,-48
   4:	02113423          	sd	x1,40(x2)
   8:	02813023          	sd	x8,32(x2)
   c:	03010413          	addi	x8,x2,48
  10:	fe042023          	sw	x0,-32(x8)
  14:	00a00793          	addi	x15,x0,10
  18:	fcf42e23          	sw	x15,-36(x8)
  1c:	fe042783          	lw	x15,-32(x8)
  20:	4057d79b          	sraiw	x15,x15,0x5
  24:	fcf42c23          	sw	x15,-40(x8)
  28:	01400793          	addi	x15,x0,20
  2c:	fef42623          	sw	x15,-20(x8)
  30:	ff600793          	addi	x15,x0,-10
  34:	fef42423          	sw	x15,-24(x8)
  38:	fd842783          	lw	x15,-40(x8)
  3c:	00078713          	addi	x14,x15,0
  40:	fe042783          	lw	x15,-32(x8)
  44:	0007071b          	addiw	x14,x14,0
  48:	0007879b          	addiw	x15,x15,0
  4c:	00f71863          	bne	x14,x15,5c <main+0x5c>
  50:	00300793          	addi	x15,x0,3
  54:	fef42623          	sw	x15,-20(x8)
  58:	00c0006f          	jal	x0,64 <main+0x64>
  5c:	00700793          	addi	x15,x0,7
  60:	fef42623          	sw	x15,-20(x8)
  64:	fe042223          	sw	x0,-28(x8)
  68:	0240006f          	jal	x0,8c <main+0x8c>
  6c:	fe842783          	lw	x15,-24(x8)
  70:	00078713          	addi	x14,x15,0
  74:	fe442783          	lw	x15,-28(x8)
  78:	00f707bb          	addw	x15,x14,x15
  7c:	fef42423          	sw	x15,-24(x8)
  80:	fe442783          	lw	x15,-28(x8)
  84:	0017879b          	addiw	x15,x15,1
  88:	fef42223          	sw	x15,-28(x8)
  8c:	fe442783          	lw	x15,-28(x8)
  90:	fec42703          	lw	x14,-20(x8)
  94:	0007071b          	addiw	x14,x14,0
  98:	fcf77ae3          	bgeu	x14,x15,6c <main+0x6c>
  9c:	00000793          	addi	x15,x0,0
  a0:	00078513          	addi	x10,x15,0
  a4:	02813083          	ld	x1,40(x2)
  a8:	02013403          	ld	x8,32(x2)
  ac:	03010113          	addi	x2,x2,48
  b0:	00008067          	jalr	x0,0(x1)
