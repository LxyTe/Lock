package com.te.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.I0Itec.zkclient.ZkClient;

public abstract class ZookeeperAbstractLock implements Lock{

	private static final String CONNECTIONS = "127.0.0.1:2181";
    protected  ZkClient zkClient = new ZkClient(CONNECTIONS);
    protected static final String PATH = "/lock";
    protected CountDownLatch countDownLatch = null;
    
    public void getLock(){
    	if(isLock()){
    		System.out.println("获取锁成功###");
    	}else {
			//如果没获取应该等待
    		waitLock();
    		//重新尝试获取锁
    		getLock();
		}
    }
    
    abstract boolean isLock();
    abstract void waitLock();

	public void unLock(){
    	if(zkClient != null){
    		zkClient.close();
    		System.out.println("释放锁成功###");
    	}
    }
}
