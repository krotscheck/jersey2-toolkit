# jersey2-configuration

This library provides a configuration injector for the jersey2-toolkit
features, using Apache's commons-configuration. All of the toolkit's 
configuration is handled via one file, with individual properties namespaced 
by feature.

    -- /src/main/resources/jersey2-toolkit.properties
    
    featurename1.propertyname1=value
    featurename1.propertyname2=value
    
    featurename3.propertyname1=value

For specific configuration values, please see the corresponding feature.

## Injectors

* **net.krotscheck.jersey2.configuration.Jersey2ToolkitConfiguration** 
<br/><small>*Singleton scoped*. The configuration object that is injected 
into the scope. In most cases you won't have to access it directly, 
however it could be useful if you want to consolidate configuration for your 
own features.</small>

## Getting Started

Here are the basic steps necessary to get this feature working in your jersey2 
application.

### 1: Enable the maven repository

The jersey2-hibernate feature is published to a maven repository on github. 
You can enable the repository with the following in your <code>pom.xml</code>.

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
			<artifactId>jersey2-configuration</artifactId>
    	</dependency>
		...
	</dependencies>

### 3: Enable the feature in your application

	import net.krotscheck.jersey2.hibernate.ConfigurationFeature;
	import org.glassfish.jersey.server.ResourceConfig;

	public class YourApplication extends ResourceConfig {
	    public YourApplication() {
	        register(ConfigurationFeature.class);
	    }
	}

### 4: Add a configuration file
Create a new file: /src/main/resources/jersey2-toolkit.properties. It should 
look like this:

    -- /src/main/resources/jersey2-toolkit.properties
    
    featurename1.propertyname1=value
    featurename1.propertyname2=value
    
    featurename3.propertyname1=value

### 5: Inject and use

The configuration is injected both explicitly and with its own name. 
Therefore, both of the following examples are valid:

	@Path("/path")
	public class MyService {
	
		@Inject
		@Named("jersey2-toolkit")
    	public Configuration namedConfiguration;
	
		@Inject
    	public Jersey2ToolkitConfig explicitConfiguration;

		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Response getMethod() {
		
			return Response.ok().build();
		}
	}

