package controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ted.TaskExecutingDistributor;

@Controller
@RequestMapping(value="/tasks", method = RequestMethod.GET)
public class TedController
{ 	
	private TaskExecutingDistributor taskExecutingDistributor;
	
	@Autowired
	public TedController(TaskExecutingDistributor taskExecutingDistributor)
	{
		this.taskExecutingDistributor = taskExecutingDistributor;
	}

	@RequestMapping(value="/schedule", consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> schedule(@RequestParam(value="class", required=true) String name)
	{
		TaskExecutingDistributor.TaskStatus status = taskExecutingDistributor.scheduleTask(name);
		HttpStatus httpStatus;
		switch(status)
		{
		case CLASS_CONSTRUCTOR_INACCESSIBLE:
		case CLASS_INVALID:
			httpStatus=HttpStatus.FORBIDDEN;
			break;
		case CLASS_NOT_RUNNABLE:
			httpStatus=HttpStatus.NOT_IMPLEMENTED;
			break;
		case CLASS_NOT_FOUND:
			httpStatus=HttpStatus.NOT_FOUND;
			break;
		case QUEUE_FULL:
			httpStatus=HttpStatus.SERVICE_UNAVAILABLE;
			break;
		case TASK_SCHEDULED:
			httpStatus=HttpStatus.CREATED;
			break;
		case UNKNOWN_EXCEPTION:
			httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
			default:
				httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<String>("", httpStatus);
	}
	
	@RequestMapping(value="/count-scheduled", consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> countScheduled()
	{
		Integer scheduled = taskExecutingDistributor.getScheduled();
		return new ResponseEntity<String>(scheduled.toString(), HttpStatus.OK);
	}
	
	@RequestMapping(value="/count-running", consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> countRunning(ModelMap model)
	{
		Integer running = taskExecutingDistributor.getRunning();
		return new ResponseEntity<String>(running.toString(), HttpStatus.OK);
	}
	
}