package com.te.zookeeper;

import org.ietf.jgss.Oid;

/**
 * 模拟并发用户生成订单号 
 * @author 刘新杨
 *   菩提本无树，
 *   明镜亦非台。
 */
public class UuidService  implements Runnable {

	UuidNumber uuidNumber =	new UuidNumber();
	public void run() {
	  //模拟生成订单号
		this.getNumber();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getNumber(){
		String number= uuidNumber.getNumber();
		System.out.println(Thread.currentThread().getName()+"当前唯一订单号:"+number);
	}
	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
		  new Thread(new UuidService()).start();	
		}
	}
}
