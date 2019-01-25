package com.te.zookeeper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author 刘新杨
 *   菩提本无树，
 *   明镜亦非台。
 *   
 *   分布式环境下生产全局唯一id
 *   保证接口的幂等性操作
 */
public class UuidNumber {

	//业务id
	private  static int count=0;
	ReentrantLock lock = new ReentrantLock();
	//生成订单号
	public String getNumber(){
//		synchronized (UuidNumber.class) {
//			SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			  return simpleDateFormat.format(new Date())+"--"+ ++count;
//		}
		
		try {
			lock.lock();
			SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    lock.unlock();
			  return simpleDateFormat.format(new Date())+"--"+ ++count;
		} catch (Exception e) {
				 e.printStackTrace();
		}finally {
			if(lock.isLocked()) {
				System.err.println(Thread.currentThread().getName()+"有异常锁");	
					lock.unlock();
				}
		}
		return null;
	}
}
