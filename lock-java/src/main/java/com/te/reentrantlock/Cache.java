package com.te.reentrantlock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁，和lock锁的区别是，读写有两把锁，而lock只有一把锁
 * 优点是可以简单的实现，一些读和写的时候会产生的一些脏读问题，加锁的时候，一定要注意解锁 
 *双读是支持的，一读一些不支持(只有全部写完的时候，才会支持读),写写 不支持(写的时候，只支持，一个一个的写。)
 *上面的这种方式，可以解决写入不多的情况下使用
 * @author 刘新杨
 *   菩提本无树，
 *   明镜亦非台。
 */
public class Cache {
  
	
	static Map<String, Object> map = new HashMap <String , Object>();
	static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	static Lock r = rwl.readLock();
	static Lock w = rwl.writeLock();

	// 获取一个key对应的value
	public static final Object get(String key) {
		r.lock();
		try {
			System.out.println("正在做读的操作,key:" + key + " 开始");
			Thread.sleep(100);
			Object object = map.get(key);
			System.out.println("正在做读的操作,key:" + key + " 结束");
			System.out.println();
			return object;
		} catch (InterruptedException e) {

		} finally {
			r.unlock();
		}
		return key;
	}

	// 设置key对应的value，并返回旧有的value
	public static final Object put(String key, Object value) {
		w.lock();
		try {

			System.out.println("正在做写的操作,key:" + key + ",value:" + value + "开始.");
			Thread.sleep(100);
			Object object = map.put(key, value);
			System.out.println("正在做写的操作,key:" + key + ",value:" + value + "结束.");
			System.out.println();
			return object;
		} catch (InterruptedException e) {

		} finally {
			w.unlock();
		}
		return value;
	}

	// 清空所有的内容,也相当于一个写的功能，要加锁。
	public static final void clear() {
		w.lock();
		try {
			map.clear();
		} finally {
			w.unlock();
		}
	}

	public static void main(String[] args) {
		new Thread(new Runnable() {

		
			public void run() { //写
				for (int i = 0; i < 10; i++) {
					Cache.put(i + "", i + "");
				}

			}
		}).start();
		new Thread(new Runnable() {

	
			public void run() { //读
				for (int i = 0; i < 10; i++) {
					Cache.get(i + "");
				}

			}
		}).start();
	}
}
