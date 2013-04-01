package ted;

import static org.junit.Assert.*;

import org.junit.Test;

public class TaskExecutingDistributorArgumentTest
{
	private TaskExecutingDistributor ted;
	
	@Test
	public void testTaskExecutingDistributor()
	{
		assertNull("reference ted should be null",ted);
		ted = new TaskExecutingDistributor(2,3,4,2000);
		assertNotNull("reference ted should be initialized",ted);
		assertEquals("there should be no running tasks",0,ted.getRunning());
		assertEquals("there should be no scheduled tasks",0,ted.getScheduled());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArguments1()
	{
		ted = new TaskExecutingDistributor(-2,2,4,2000);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArguments2_1()
	{
		ted = new TaskExecutingDistributor(2,0,4,2000);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArguments2_2()
	{
		ted = new TaskExecutingDistributor(2,-2,4,2000);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArguments3_1()
	{
		ted = new TaskExecutingDistributor(2,2,0,2000);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArguments3_2()
	{
		ted = new TaskExecutingDistributor(2,2,-2,2000);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArguments4()
	{
		ted = new TaskExecutingDistributor(2,2,4,-2000);
	}
	
	@Test
	public void testBoundaryArgument()
	{
		try
		{
			ted = new TaskExecutingDistributor(0,1,1,0);
		}
		catch(Exception e)
		{
			System.err.println("Exception: "+e.toString());
			fail();
		}
	}

}
