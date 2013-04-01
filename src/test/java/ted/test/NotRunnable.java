package ted.test;

public class NotRunnable {

	public static int TASK_TIME = 2000;
	
	public void run() {
		try {
			while(true)
				Thread.sleep(TASK_TIME);
		} catch (InterruptedException e) {
			return;
			//throw new RuntimeException("Thread interrupted.", e);
		}
	}
}
