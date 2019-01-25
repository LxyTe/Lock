package com.te.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.I0Itec.zkclient.IZkDataListener;

public class ZookeeperAbstractLockImpl extends ZookeeperAbstractLock {

	@Override
	boolean isLock() {
		try {
			zkClient.createEphemeral(PATH);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override // 重点
	void waitLock() {

		// 使用事件监听，节点是否被删除
		IZkDataListener iZkDataListener = new IZkDataListener() {

			// 节点删除。。。
			public void handleDataDeleted(String dataPath) throws Exception {
				// TODO 如果节点被删除，那么就唤醒等待

				if (countDownLatch != null) {
					countDownLatch.countDown();
				}
			}

			public void handleDataChange(String dataPath, Object data) throws Exception {
				// TODO Auto-generated method stub

			}
		};
		//开启监听，某个临时节点
		zkClient.subscribeDataChanges(PATH, iZkDataListener);
		if (zkClient.exists(PATH)) {
           //每次自己new，可以解决多线程环境下的线程安全问题
			countDownLatch = new CountDownLatch(1);
			try {
				countDownLatch.await();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		zkClient.unsubscribeDataChanges(PATH, iZkDataListener);
		
	}

}
