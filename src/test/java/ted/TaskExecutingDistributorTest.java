package ted;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TaskExecutingDistributorTest
{
	private TaskExecutingDistributor ted;
	private int nThreads;
	private int nQueue;
	
	private static int waitTime=50;
	
	private void wait(int timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			throw new RuntimeException("Thread interrupted.", e);
		}
	}
	
	@Before
	public void beforeEachTest()
	{
		nThreads = 4; //must be >= 1
		nQueue = 4; //must be >= 1
		ted = new TaskExecutingDistributor(nThreads,nThreads,nQueue,0);
	}
	
	@After
	public void afterEachTest()
	{
		ted.shutdownNow();
	}
	
	@Test
	public void testScheduleTask()
	{
		for(int i=0;i<nThreads;i++)
		{
			ted.scheduleTask("ted.test.InfiniteTask");
		}
		//give threads some time to start
		wait(waitTime);
		assertEquals("there should be nThreads running tasks",nThreads,ted.getRunning());
		assertEquals("there should be no scheduled tasks",0,ted.getScheduled());
		
		for(int i=0;i<nQueue;i++)
		{
			ted.scheduleTask("ted.test.InfiniteTask");
		}
		//give threads some time to start
		wait(waitTime);
		assertEquals("max number of threads was used, there should still be nThreads running tasks",nThreads,ted.getRunning());
		assertEquals("there should be nQueue scheduled tasks",nQueue,ted.getScheduled());
		
		ted.scheduleTask("ted.test.InfiniteTask");
		wait(waitTime);
		assertEquals("Queue was full, there should be nQueue scheduled tasks",nQueue,ted.getScheduled());
	}

	@Test
	public void testShutdownNow()
	{
		for(int i=0;i<nThreads;i++)
		{
			ted.scheduleTask("ted.test.InfiniteTask");
		}
		for(int i=0;i<nQueue;i++)
		{
			ted.scheduleTask("ted.test.InfiniteTask");
		}
		//give threads some time to start
		wait(waitTime);
		//this test depends on task's class ability to handle interrupt
		ted.shutdownNow();
		//give some time for threads to close
		wait(waitTime);
		assertEquals("there should be no running tasks",0,ted.getRunning());
		assertEquals("there should be no scheduled tasks",0,ted.getScheduled());		
	}
	
	@Test
	public void testImpossibleTasks()
	{
		ted.scheduleTask("ted.test.PrivateConstructor");
		ted.scheduleTask("ted.test.AbstractRunnable");
		ted.scheduleTask("ted.test.NotRunnable");
		wait(waitTime);
		assertEquals("there should be no running tasks",0,ted.getRunning());
		assertEquals("there should be no scheduled tasks",0,ted.getScheduled());
	}
	
	@Test
	public void testInheritedRunnable()
	{
		ted.scheduleTask("ted.test.InheritedRunnable");
		//give threads some time to start
		wait(waitTime);
		assertEquals("running inherited runnable should be possible ",1,ted.getRunning());
		assertEquals("there should be no scheduled tasks",0,ted.getScheduled());
	}
	
	@Test
	public void testTaskStatusReturn()
	{
		TaskExecutingDistributor.TaskStatus status = ted.scheduleTask("ted.test.PrivateConstructor");
		assertEquals("status for private constructor", TaskExecutingDistributor.TaskStatus.CLASS_CONSTRUCTOR_INACCESSIBLE,status);
		
		status = ted.scheduleTask("ted.test.AbstractRunnable");
		assertEquals("status for abstract class", TaskExecutingDistributor.TaskStatus.CLASS_INVALID, status );
		
		status = ted.scheduleTask("ted.test.AnInterface");
		assertEquals("status for interface", TaskExecutingDistributor.TaskStatus.CLASS_INVALID, status );
		
		status = ted.scheduleTask("ted.test.NotRunnable");
		assertEquals("status for not runnable class", TaskExecutingDistributor.TaskStatus.CLASS_NOT_RUNNABLE, status );
		
		status = ted.scheduleTask("ted.test.NonExistentClass");
		assertEquals("status for non-existent class", TaskExecutingDistributor.TaskStatus.CLASS_NOT_FOUND, status );
		
		wait(waitTime);
		assertEquals("there should be no running tasks",0,ted.getRunning());
		assertEquals("there should be no scheduled tasks",0,ted.getScheduled());
		
		status = ted.scheduleTask("ted.test.InfiniteTask");
		assertEquals("status for scheduling or running a task", TaskExecutingDistributor.TaskStatus.TASK_SCHEDULED, status );
		//give threads some time to start
		wait(waitTime);
		assertEquals("there should be 1 running task",1,ted.getRunning());
		for(int i=1;i<nThreads;i++)
			ted.scheduleTask("ted.test.InfiniteTask");
		for(int i=0;i<nQueue;i++)
			ted.scheduleTask("ted.test.InfiniteTask");
		//give threads some time to start
		wait(waitTime);
		assertEquals("there should be nThreads running tasks",nThreads,ted.getRunning());
		assertEquals("there should be nQueue scheduled tasks",nQueue,ted.getScheduled());
		
		status = ted.scheduleTask("ted.test.InfiniteTask");
		//give threads some time to start
		wait(waitTime);
		assertEquals("status for queue full", TaskExecutingDistributor.TaskStatus.QUEUE_FULL, status );
	}
	
	//tasks with System.exit will shutdown ted (and also junit), for now it is ignored
	@Ignore
	public void testRunnableWithExit()
	{
		ted.scheduleTask("ted.test.RunnableWithExit");
	}
}
