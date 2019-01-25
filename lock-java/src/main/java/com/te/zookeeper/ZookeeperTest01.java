package com.te.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;

public class ZookeeperTest01 {

	// 连接地址
	private static final String ADDRESS = "127.0.0.1:2181";
	// zk会话超时时间
	private static final int SESSION_OUT = 2000;
	//阻塞用户主线程，必须等待连接成功才可以继续后面操作
 	private static final CountDownLatch countDownLatch = new  CountDownLatch (1);
	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		ZooKeeper zk = new ZooKeeper(ADDRESS, SESSION_OUT, new Watcher() {

			// 事件通知
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				// 1.获取事件状态
				KeeperState state = event.getState();
				// 2.判断为连接状态
				if (state == KeeperState.SyncConnected) {
					// 3.获取事件类型
					EventType type = event.getType();
					if (type == EventType.None) {
						countDownLatch.countDown();									
                       System.out.println("开始启动连接了");
					}
				}
			}
		});
		//阻塞主线程，等待countDownLatch的结果为0
		countDownLatch.await();
		//持久节点
	// String result =zk.create("/lxymm2/OneTest8", "OneTest8".getBytes(),Ids.OPEN_ACL_UNSAFE , CreateMode.PERSISTENT);
	 //临时节点
	 String result =zk.create("/lxymm3", "OneTest8".getBytes(),Ids.OPEN_ACL_UNSAFE , CreateMode.EPHEMERAL);
	System.out.println("新增节点为:"+result);
	  try {
		Thread.sleep(5000);
		zk.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
}
