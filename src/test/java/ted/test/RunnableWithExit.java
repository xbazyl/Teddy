package ted.test;

public class RunnableWithExit implements Runnable
{

	@Override
	public void run()
	{
		System.exit(0);
	}

}
