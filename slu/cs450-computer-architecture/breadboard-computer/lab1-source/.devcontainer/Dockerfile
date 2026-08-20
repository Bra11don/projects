FROM ubuntu:latest

RUN apt-get update && export DEBIAN_FRONTEND=noninteractive && \
  apt-get install -y \
    bcc \
    coreutils \
    curl \
    git \
    hexedit \
    make \
    nasm \
    tar \
    unzip \
    xxd \
    zip && \
  apt-get clean && \
  rm -rf /var/lib/apt/lists/*
