package point2.point1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Testcase {

	public static void main(String[] args) {
		Manager m = new Manager();
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		try {
			executorService.execute(new ThreadA("A2", 1000, m));
			Thread.sleep(100);
			executorService.execute(new ThreadA("A1", 1000, m));
			Thread.sleep(100);
			executorService.execute(new ThreadB("B1", 1000, m));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			executorService.awaitTermination(100, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		executorService.shutdown();
		if (!executorService.isShutdown()) {
			System.out.println("Forcing shutdown");
			executorService.shutdownNow();
		}
		System.exit(0);
	}
}

class ThreadA extends Thread {

	private final Manager m;
	private final String ID;
	private final long workTime;

	ThreadA(String name, long worktime, Manager m) {
		this.ID = name;
		this.workTime = worktime;
		this.m = m;
	}

	@Override
	public void run() {
		try {
			System.out.printf("Thread %s requestingA\n", ID);
			m.requestA();
			System.out.printf("Thread %s getA\n", ID);
			System.out.printf("Thread %s doing some work...\n", ID);
			sleep(workTime);
			System.out.printf("Thread %s releasingA\n", ID);
			m.releaseA();
			System.out.printf("Thread %s releasedA\n", ID);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class ThreadB extends Thread {

	private final Manager m;
	private final String ID;
	private final long workTime;

	ThreadB(String name, long worktime, Manager m) {
		this.ID = name;
		this.workTime = worktime;
		this.m = m;
	}

	@Override
	public void run() {
		// try to get the resource
		try {
			System.out.printf("Thread %s requestingB\n", ID);
			m.requestB();
			System.out.printf("Thread %s getB\n", ID);
			System.out.printf("Thread %s doing some work...\n", ID);
			sleep(workTime);
			System.out.printf("Thread %s releasingB\n", ID);
			m.releaseB();
			System.out.printf("Thread %s releasedB\n", ID);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
