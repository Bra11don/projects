# CS450 — Computer Architecture

## 8088 Breadboard Computer

This project documents the progressive construction of a working computer around the Intel 80C88 processor.

The build began with power distribution, an 8 MHz clock, processor support logic, 32 KB of ROM, and 512 KB of SRAM. It then added an LCD through an 82C55A programmable peripheral interface and custom assembly output routines. The final hardware integrated an 8259A interrupt controller, 8253 timer, 16C550D serial controller, CompactFlash storage, and a speaker before successfully executing POST and BIOS boot messages.

The included `breadboard-computer/lab1-source` directory contains the available bootloader and kernel experiments in x86 assembly and C. Later stages were primarily physical hardware work, so their implementation is summarized here rather than represented by assignment-report files.
