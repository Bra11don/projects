# Kevin Angstadt, 2025
# A simple makefile for creating a bootable floppy image

all:
	dd if=/dev/zero of=disk0.img bs=512 count=720

	nasm bootload.asm
	dd if=bootload of=disk0.img bs=512 count=1 conv=notrunc

	bcc -ansi -c -o kernel.o kernel.c
	nasm -f as86 -o kernel_asm.o kernel.asm
	ld86 -o kernel -d kernel.o kernel_asm.o

	dd if=kernel of=disk0.img bs=512 conv=notrunc seek=3

clean:
	rm -f *.o
	rm -f bootload
	rm -f kernel
	rm -f disk0.img

.PHONY: all clean
