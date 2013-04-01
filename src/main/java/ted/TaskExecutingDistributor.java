package ted;

import java.beans.ConstructorProperties;
import java.lang.reflect.Modifier;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskExecutingDistributor
{
	public enum TaskStatus
	{
		CLASS_CONSTRUCTOR_INACCESSIBLE, CLASS_INVALID, CLASS_NOT_FOUND, CLASS_NOT_RUNNABLE, QUEUE_FULL, TASK_SCHEDULED, UNKNOWN_EXCEPTION;
	}
	
	private ThreadPoolExecutor executorPool;
	private int maximumQueueSize;
	
	@ConstructorProperties({"corePoolSize", "maximumPoolSize", "maximumQueueSize", "keepAliveTime"})
	public TaskExecutingDistributor(int corePoolSize, int maximumPoolSize, int maximumQueueSize, long keepAliveTime)
	{
		if(corePoolSize < 0)
			throw new IllegalArgumentException("corePoolSize must be greater than or equal 0");
		if(maximumPoolSize < 0)
			throw new IllegalArgumentException("maximumPoolSize must be greater than 0");
		if(maximumQueueSize <= 0)
			throw new IllegalArgumentException("maximumQueueSize must be greater than 0");
		if(keepAliveTime < 0)
			throw new IllegalArgumentException("keepAliveTime must be greater than or equal 0");
		
		this.maximumQueueSize=maximumQueueSize;
		ArrayBlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(this.maximumQueueSize);
		this.executorPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, taskQueue, new ThreadPoolExecutor.AbortPolicy());
	}
	
	public TaskStatus scheduleTask(String name)
	{
		try
		{
			Class<?> aClass = Class.forName(name);
			//Class<?> aClass = Class.forName(name, false, ClassLoader.getSystemClassLoader());
			if(isInterface(aClass))
				return TaskStatus.CLASS_INVALID;
			if(!isRunnable(aClass))
				return TaskStatus.CLASS_NOT_RUNNABLE;
			Runnable task = (Runnable) aClass.newInstance();
			executorPool.execute(task);
		}
		catch(ClassNotFoundException e)
		{
			return TaskStatus.CLASS_NOT_FOUND;
		}
		catch (IllegalAccessException e)
		{
			return TaskStatus.CLASS_CONSTRUCTOR_INACCESSIBLE;
		}
		catch (InstantiationException e)
		{
			return TaskStatus.CLASS_INVALID;
		}
		catch (RejectedExecutionException e)
		{
			return TaskStatus.QUEUE_FULL;
		}
		catch (Exception e)
		{
			System.err.println("Unknown exception - "+e.toString()+" in:\n TaskExecutingDistributor method scheduleTask("+name+")");
			return TaskStatus.UNKNOWN_EXCEPTION;
		}
		return TaskStatus.TASK_SCHEDULED;
	}
	
	public int getRunning()
	{
		return executorPool.getActiveCount();
	}
	
	public int getScheduled()
	{
		return executorPool.getQueue().size();
	}
	
	public void shutdown()
	{
		executorPool.shutdown();
	}
	
	public void shutdownNow()
	{
		executorPool.shutdownNow();
	}
	
	public void restart()
	{
		int corePoolSize = executorPool.getCorePoolSize();
		int maximumPoolSize = executorPool.getMaximumPoolSize();
		long keepAliveTime = executorPool.getKeepAliveTime(TimeUnit.MILLISECONDS);
		if(!executorPool.isShutdown())
			executorPool.shutdownNow();
		
		ArrayBlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(maximumQueueSize);
		executorPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, taskQueue, new ThreadPoolExecutor.AbortPolicy());
	}
	
	private boolean isInterface(Class<?> argClass)
	{
		int modifiers = argClass.getModifiers();
		if(Modifier.isInterface(modifiers))
			return true;
		else
			return false;
	}
	
	private boolean isRunnable(Class<?> argClass)
	{
		Class<?> aClass = argClass;
		while(aClass != null && !(aClass.getSimpleName().equals("Object")))
		{
			Class<?>[] interfaces = aClass.getInterfaces();
			for (Class<?> interface1 : interfaces)
			{
				if(interface1.getSimpleName().equals("Runnable"));
					return true;
			}
			aClass = aClass.getSuperclass();
		}
		return false;
	}
}
