package com.te.redis;

import java.util.Collections;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 基于redis实现分布式锁代码思路
 * 
 * @author 刘新杨 菩提本无树， 明镜亦非台。
 */
public class LockRedis {

	// redis线程池
	private JedisPool jedisPool;

	// key值为下面的，value的值为一个不会重复的随机数 uuid
	private String redisLockKey = "redis_lock";
	// 意思是SET IF NOT EXIST，即当key不存在时，我们进行set操作；若key已经存在，则不做任何操作；
	private static final String SET_IF_NOT_EXIST = "NX";
	// 这个参数我们传的是PX，意思是我们要给这个key加一个过期的设置，具体时间由第五个参数决定。
	private static final String SET_WITH_EXPIRE_TIME = "PX";

	public LockRedis(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	/**
	 * redis的两个超时时间 含义
	 * 1.获取锁的超时时间之前(acquireTimeout)-----在尝试获取锁的时候，如果在规定的时间内没有获取锁，直接放弃
	 * 2.获取锁的超时时间之后(timeOut)----- 在获取锁成功之后，对应的key要有超时时间 ,防止死锁
	 */

	// 加锁,此方法可以成功加锁，但是有一定的风险性，如果47行，突然宕机了，那么就有可能产生死锁
	public String getRedisLock(Long acquireTimeout, Long timeOut) {

		Jedis conn = null;

		try {
			// 1.建立redis连接
			conn = jedisPool.getResource();

			// 2.定义key值对应的value值
			String identfierValue = UUID.randomUUID().toString();
			// 3.获取锁之后的超时时间，以秒为单位
			int expireLock = (int) (timeOut / 1000);

			// 4.获取锁之前的超时时间
			// 5.循环机制保障重复获取锁(类似于乐观锁)
			Long endTime = System.currentTimeMillis() + acquireTimeout;
			while (System.currentTimeMillis() < endTime) {
				// 获取锁
				// //6.使用setnx放入key，返回为1，则加锁成功
				// if(conn.setnx(redisLockKey, identfierValue)==1){
				// //设置key的有效期
				// conn.expire(redisLockKey, expireLock);
				// return identfierValue;
				// }
				// 下面的方法可避免出现死锁,上面的方法是经典错法之一
				String result = conn.set(redisLockKey, identfierValue, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME,
						expireLock);

				if ("OK".equals(result)) {
					return identfierValue;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}

		return null;

	}

	// 解锁，
	public void unRedisLock(String lockValue) {

		Jedis conn = null;

		try {
			// 1.建立redis连接
			conn = jedisPool.getResource();
			/**
			 * 这种解锁方法也是经典错法 问题在于如果调用jedis.del()方法的时候，这把锁已经不属于当前客户端的时候会解除他人加的锁。
			 * 那么是否真的有这种场景？答案是肯定的， 比如客户端A加锁，一段时间之后客户端A解锁，在执行jedis.del()之前
			 * ，锁突然过期了，此时客户端B尝试加锁成功，然后客户端A再执行del()方法， 则将客户端B的锁给解除了。
			 */
			// if(conn.get(redisLockKey).equals(lockValue)){
			// conn.del(redisLockKey);
			// }
			String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
			Object result = conn.eval(script, Collections.singletonList(redisLockKey),
					Collections.singletonList(lockValue));
			String reString = result.toString();

			if (("1").equals(reString)) {
				System.out.println(redisLockKey + " , " + lockValue + "解锁成功");
			} else {
				//失败原因，key对应的值和传入的值不一样了
				/**
				 * 业务场景:
				 * 锁A的过期时间为5s，但是由于网络等原因，执行时间一共执行了，10s， 那么就会出现
				 * A还没执行完成，而B又获取了锁，那么由于判断的时候的value和取出来的value不一样，所以
				 * A的执行操作就会出现问题。在秒杀等极端场景，可以废除A的所有操作或者再加上DB的乐观锁来联合
				 * 解决这一场景问题
				 */
				System.out.println(redisLockKey + " , " + lockValue + "解锁失败,回滚所有操作");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
	}
}
