package controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ted.TaskExecutingDistributor;

public class TedControllerTest
{
	private MockMvc mockMvc;
	private TaskExecutingDistributor ted;
	int nThreads;
	int nQueue;
	
	private static int waitTime=50;
	
	private void wait(int timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			throw new RuntimeException("Thread interrupted.", e);
		}
	}
	
	@Before
	public void setup()
	{
		nThreads=4;
		nQueue=4;
		ted = new TaskExecutingDistributor(nThreads,nThreads,nQueue,0);
		mockMvc=MockMvcBuilders.standaloneSetup(new TedController(ted)).build();
	}
	
	@After
	public void cleanup()
	{
		ted.shutdownNow();
		ted=null;
		mockMvc=null;
	}
	
	@Test
    public void testCountRunning() throws Exception
    {
		
		mockMvc.perform(get("/tasks/count-running").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string("0"));
	}
	
	@Test
	public void testCountScheduled() throws Exception
	{
		mockMvc.perform(get("/tasks/count-scheduled").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string("0"));
	}
	
	@Test
	public void testImpossibleTaskHttpStatus() throws Exception
	{
		mockMvc.perform(get("/tasks/schedule?class=ted.test.AbstractRunnable").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden())
			.andExpect(content().string(""));
		mockMvc.perform(get("/tasks/schedule?class=ted.test.AnInterface").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden())
			.andExpect(content().string(""));
		mockMvc.perform(get("/tasks/schedule?class=ted.test.NotRunnable").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotImplemented())
			.andExpect(content().string(""));
		mockMvc.perform(get("/tasks/schedule?class=ted.test.PrivateConstructor").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden())
			.andExpect(content().string(""));
		mockMvc.perform(get("/tasks/schedule?class=ted.test.NonExistentClass").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(content().string(""));
	}
	
	@Test
	public void testPossibleTaskHttpStatus() throws Exception
	{
		for(int i=0;i<nThreads;i++)
		{
			mockMvc.perform(get("/tasks/schedule?class=ted.test.InfiniteTask").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string(""));
		}
		//give threads some time to start
		wait(waitTime);
		mockMvc.perform(get("/tasks/count-running").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string(""+nThreads));
		for(int i=0;i<nQueue;i++)
		{
			mockMvc.perform(get("/tasks/schedule?class=ted.test.InfiniteTask").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string(""));
		}
		//give threads some time to start
		wait(waitTime);
		mockMvc.perform(get("/tasks/count-scheduled").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string(""+nQueue));
		//queue should be full now
		this.mockMvc.perform(get("/tasks/schedule?class=ted.test.InfiniteTask").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isServiceUnavailable())
			.andExpect(content().string(""));
	}
}
