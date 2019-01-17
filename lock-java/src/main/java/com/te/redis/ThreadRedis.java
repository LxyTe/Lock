package com.te.redis;

public class ThreadRedis extends Thread {

	private LockService lockService;
	
	public ThreadRedis(LockService lockService){
		this.lockService=lockService;
	}
	
	public void run(){
		lockService.test();
	}
}
