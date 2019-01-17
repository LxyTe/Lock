package com.te.reentrantlock;

import java.util.concurrent.locks.Lock;

/**
 * 可重入锁
 * @author 刘新杨
 *   菩提本无树，
 *   明镜亦非台。
 */
public class ReentrantLock  extends Thread{
  Lock lock = new java.util.concurrent.locks.ReentrantLock();
	
  @Override
  public void run(){
	set();  
  }
  
  public void set(){
	  try {
		lock.lock();//在set中加入锁
		System.out.println("set锁");
		get();
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		lock.unlock();
	}
  }

  public void get(){
	  try {
		lock.lock();
		System.out.println("Lock的可重入性");
		
	} catch (Exception e) {
	e.printStackTrace();
	}finally {
		lock.unlock();
	}
  }
  
	public static void main(String[] args) {
		new ReentrantLock().start();
	}
}
