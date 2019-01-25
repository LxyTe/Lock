package com.te.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * zookeeper事件通知机制
 * 
 * @author 刘新杨 菩提本无树， 明镜亦非台。
 */
public class ZookeeperWatcher implements Watcher {

	// 连接地址
	private static final String ADDRESS = "127.0.0.1:2181";
	// zk会话超时时间
	private static final int SESSION_OUT = 2000;
	// 阻塞用户主线程，必须等待连接成功才可以继续后面操作
	private static final CountDownLatch countDownLatch = new CountDownLatch(1);
	ZooKeeper zk = null;

	// 创建连接
	public void connection() {
		try {
			zk = new ZooKeeper(ADDRESS, SESSION_OUT, this);
			countDownLatch.await();
			System.out.println("zookeeper连接成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 创建持久化几点
	public boolean createNode(String path, String data) {
		try {
			exists(path, true);
			String result = zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			System.out.println("新增节点成功:" + result);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 监听方法
	public void process(WatchedEvent event) {
		// 1.获取事件状态
		KeeperState state = event.getState();
		String path = event.getPath();
		// 2.判断为连接状态
		if (state == KeeperState.SyncConnected) {
			// 3.获取事件类型
			EventType type = event.getType();
			if (type == EventType.None) {
				countDownLatch.countDown();
				System.out.println("开始启动连接了****");

			}
			if (type == EventType.NodeCreated) {
				System.out.println("新增了某个节点：" + path);
			}
			if (type == EventType.NodeDataChanged) {
				System.out.println("修改了某个节点" + path);
			}

		}
		System.out.println("事件通知已经结束");
	}

	/**
	 * 
	 * 表示监听输入的参数路径，是否存在，或者值被改变
	 * @param 路径
	 * @param 是否监测
	 * @return
	 */
	public Stat exists(String path, boolean isWatch) {
		try {
			return zk.exists(path, isWatch);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void close() {
		if (zk != null) {
			try {
				zk.close();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		ZookeeperWatcher zookeeperWatcher = new ZookeeperWatcher();
		zookeeperWatcher.connection();
		zookeeperWatcher.createNode("/lxylovemm/9527", "250");
	}

}
