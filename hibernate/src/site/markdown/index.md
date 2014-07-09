# jersey2-hibernate

This library provides HK2 injectors and container lifecycle components for common Hibernate functionality. This feature **does not** contain the jersey dependencies, nor does it contain any hibernate database adapters. We recommend using jersey 2.11 or later, as there are HK2 injection bugs in previous versions that prevent both session scoped and singleton scoped items from being properly destroyed.

## Injectors

* **org.hibernate.Session** <br/><small>*Request scoped*. This library assumes that a session is created per service request, and destroyed afterwards. If you want to create a session outside of a request scope, we recommend you inject the SessionFactory.</small>
* **org.hibernate.SessionFactory** <br/><small>*Singleton scoped*. If you would like to have finer control over when a session is created, you may inject the SessionFactory yourself.</small>
* **org.hibernate.cfg.Configuration** <br/><small>*Singleton scoped*. While available for injection, the injection chain is such that you cannot guarantee that modifications to the configuration will persist to the session factory. Instead, please modify your hibernate configuration.</small>
* **org.hibernate.search.FullTextSession**  <br/><small>*Request scoped*. This hibernate-search component is built off of the session factory. Useful for fulltext searches.</small>
* **org.hibernate.search.SearchFactory**  <br/><small>*Request scoped*. This hibernate-search component is built off of the session factory. It is also frequently needed for fulltext searches using Hibernate Search.</small>


## Lifecycle Containers
* **net.krotscheck.jersey2.hibernate.lifecycle.SearchIndexContextListener**<br/><small>This container lifecycle listener will trigger a rebuild of the search index whenever the servlet container is restarted.</small>

## Getting Started

Here are the basic steps necessary to get this feature working in your jersey2 application.


### 1: Enable the maven repository

The jersey2-hibernate feature is published to a maven repository on github. You can enable the repository with the following in your <code>pom.xml</code>.

	<repositories>
		...
		<repository>
			<id>jersey2-toolkit</id>
            <url>https://krotscheck.github.com/jersey2-toolkit/repo</url>
        </repository>
		...
    </repositories>

### 2: Add the dependency

	<dependencies>
		...
		<dependency>
			<groupId>net.krotscheck.jersey2</groupId>
			<artifactId>jersey2-hibernate</artifactId>
    	    <version>${project.version}</version>
    	</dependency>
		...
	</dependencies>

### 3: Enable the feature in your application

	import net.krotscheck.jersey2.hibernate.HibernateFeature;
	import org.glassfish.jersey.server.ResourceConfig;

	public class YourApplication extends ResourceConfig {
	    public YourApplication() {
	        register(HibernateFeature.class);
	    }
	}

### 4: Configure Hibernate
The configuration of hibernate is beyond the scope of this document, we recommend you read the sections on [hibernate configuration](http://docs.jboss.org/hibernate/core/3.3/reference/en-US/html/session-configuration.html) for both <code>/src/main/resources/hibernate.properties</code> and <code>/src/main/resources/hibernate.cfg.xml</code>.

### 5: Inject hibernate components
At this point, hibernate components are available to inject into your jersey2 services. Here is an example service. 


	@Path("/path")
	public class MyService {
	
		@Inject
    	public Session session;
	
		@Inject
    	public FullTextSession ftSession;

		@Inject
		public SessionFactory sessionFactory;

		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Response getMethod() {
			return Response.ok().build();
		}
	}
