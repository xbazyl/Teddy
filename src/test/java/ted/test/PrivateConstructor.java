package ted.test;

public class PrivateConstructor implements Runnable {
	
	private PrivateConstructor(){}
	
	public static int TASK_TIME = 2000;
	
	@Override
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
