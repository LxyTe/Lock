package com.te.zookeeper;

public interface Lock {

	
	//获取锁
	public void getLock();
	
	//释放锁
	public void unLock();
}
