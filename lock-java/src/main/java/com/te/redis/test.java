package com.te.redis;


public class test {

	public static void main(String[] args) {
	LockService lockService=new LockService();
	for(int i=0; i<=100;i++){
		new ThreadRedis(lockService).start();
	}
		
	}
}
