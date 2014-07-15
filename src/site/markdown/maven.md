# Maven Repository
To include these features in your project, you must first enable the maven 
repository by including the following in your pom.xml or in settings.xml 
(please see appropriate documentation for specific syntax).

	<repository>
		<id>jersey2-toolkit</id>
		<url>https://krotscheck.github.com/jersey2-toolkit/repo</url>
	</repository>
	
After that, you may include any dependency on an as-needed basis.

	<dependencies>
		...
		<dependency>
			<groupId>net.krotscheck.jersey2</groupId>
			<artifactId>jersey2-hibernate</artifactId>
    	</dependency>
		...
	</dependencies>
