package ted.test;

public class InfiniteTask implements Runnable {

	@Override
	public void run() {
		try {
			while(true)
				Thread.sleep(2000);
		} catch (InterruptedException e) {
			return;
			//throw new RuntimeException("Thread interrupted.", e);
		}

	}

}
