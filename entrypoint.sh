#!/bin/sh

# Redis 서버 백그라운드 실행
redis-server --protected-mode no &

# 서버 준비 확인
until redis-cli ping | grep -q PONG; do
  sleep 0.1
done

# Lua 초기화 스크립트 실행
redis-cli --eval /init-level-exp.lua

# 포그라운드 유지
wait