package com.te.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class LockService {
	private static JedisPool pool = null;
	static {
		JedisPoolConfig config = new JedisPoolConfig();
		// 设置最大连接数
		config.setMaxTotal(200);
		// 设置最大空闲数
		config.setMaxIdle(8);
		// 设置最大等待时间
		config.setMaxWaitMillis(1000 * 100);
		// 在borrow一个jedis实例时，是否需要验证，若为true，则所有jedis实例均是可用的
		config.setTestOnBorrow(true);
		pool = new JedisPool(config, "127.0.0.1", 6379, 3000);
	}
	private LockRedis lockRedis = new LockRedis(pool);

   
	  public void test(){
	String 	result=  lockRedis.getRedisLock(5000L, 6000L);

	if(result == null){
		 System.out.println(Thread.currentThread().getName()+"获取失败，获取时间超时");
	  return;
	 }
	 System.out.println(Thread.currentThread().getName()+"获取成功，此时锁的id值为"+result);
	  
	 //业务逻辑执行完成之后，要进行解锁
	 
	 lockRedis.unRedisLock(result);
	  }


}
