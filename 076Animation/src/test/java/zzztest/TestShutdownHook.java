package zzztest;

import java.util.concurrent.TimeUnit;

public class TestShutdownHook implements Runnable{

	public TestShutdownHook() {
		//正常退出退调用的回调接口
		Runtime.getRuntime().addShutdownHook(new Thread(this));
//		Class<?> clazz = Shutdown.class;
	}
	
	private void hook() {
		System.out.println("hook");
	}
	
	public static void main(String[] args) {
		new TestShutdownHook();
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		hook();
	}
}
