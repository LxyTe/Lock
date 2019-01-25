package com.te.zookeeper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ZookeeperLockTest implements Runnable {

	public void run() {

		String number = this.getNumber();
		System.out.println(Thread.currentThread().getName() + "当前唯一订单号:" + number);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 业务id
	private static int count = 100;
	ZookeeperAbstractLockImpl zkLock = new ZookeeperAbstractLockImpl();

	// 生成订单号
	public String getNumber() {

		try {
			zkLock.getLock();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return simpleDateFormat.format(new Date()) + "--" + --count;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			zkLock.unLock();
		}
		return null;
	}

	public static void main(String[] args) {
		for (int i = 100; i > 0; i--) {
			new Thread(new ZookeeperLockTest()).start();
		}
	}
}
