#Teddy  

This is a small project that was assigned to me as a "homework". Its description is something like this:

TED (task executing distributor) is a system, which performs tasks. Task's template is a class implementing *java.lang.Runnable* interface.
TED communicates through HTTP/JSON web interface on port 8330. These are the possible HTTP queries:  

- GET /tasks/schedule?class=fully.qualified.class.Name  
Orders execution of task implemented by class *fully.qualified.class.Name* (where *fully.qualified.class* is the package's name and *Name* is the class's name). It returns empty body with different HTTP codes  
- GET /tasks/count-scheduled  
Returns the number of tasks in the queue (tasks that have been commissioned but did not started executing), HTTP code 200.  
* GET /tasks/count-running  
Returns the number of tasks which are currently executing, HTTP code 200.

##Requirements
This project was made using  

*  Eclipse 4.2
*  Spring MVC 3.2.2
*  JUnit 4.11
*  jdk 1.7 (probably 1.6 would be sufficient)
*  Maven 3.0.5
*  Tomcat 7.0.37
  

But to run it you just need the last two - I assume you have these installed and know how to use them on basic level.

##Running Teddy
After cloning the repository  

    $ cd Teddy  
	$ mvn install  

Copy Teddy.war from "target" directory to your ${CATALINA_HOME}/webapps and run Tomcat. Tomcat's standard port is 8080, so you can try  

    http://localhost:8080/Teddy/tasks/schedule?class=ted.example.Itemize
    http://localhost:8080/Teddy/tasks/count-scheduled

to see if it's working. To stay true to project's description you'll have to change the port to 8330 in server.xml of your Tomcat installation or perhaps run another Tomcat instance, to not mess up your other web applications.

##Running with Eclipse
If you have m2eclipse plugin, you can import this project as "Maven/Existing Maven Projects". In eclipse project's directory, type in the following  

    $ mvn eclipse:eclipse -Dwtpversion=2.0  

Refresh your project in Eclipse and then Run as -> Maven install, Run as -> Run on Server. If you didn't have server specified, just follow the creation wizard and point him to your Tomcat installation directory.
Eclipse will automatically copy settings from Tomcat and run another instance of the server. You can change these settings in Eclipse, without changing your base Tomcat settings.

##Adding task templates
TED has just one example task (ted.example.Itemize), which prints names of JVM languages to standard output. You can try it with:  

    http://localhost:8080/Teddy/schedule?class=ted.example.Itemize  

if you didn't change standard port and the server is running. You can add more task templates by adding .java files to src/main/java in any package and building project again with maven (mvn install).
Keep in mind, that there is no way to make TED to stop running tasks through http query, so you should probably avoid adding infinite tasks. Tasks with System.exit() will also kill whole web app.

##Settings
You can change the number of maximum running threads and queue size in  

    /src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml  

file's bean definition. TED uses *java.util.concurrent.ThreadExecutorPool* to perform tasks,
and its arguments correspond to those of ThreadExecutorPool. Although there is no limit, these should be set to reasonable values. On default, there are 4 threads and a queue of size 16.

##Running tests
You can review and add test in  

* /src/test/java/ted
* /src/test/java/controler

And /src/test/java/ted/test holds classes used to test TED with. Maven performs tests on install, but you can also run them explicitly in project directory with  

    $ mvn test


##Other info
This code is far from perfect, use at your own risk.