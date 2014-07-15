# jersey2-toolkit

This project contains a collection of mix-and-match Jersey2 Features which
can enhance and accelerate your web service development. Features can be 
loaded wholesale, or can be assembled on an as-needed basis. Available features are as follows:

* [jersey2-util](http://krotscheck.github.io/jersey2-toolkit/jersey2-util)<br/>Utilities that simplify development in a Jersey2 environment.
* [jersey2-hibernate](http://krotscheck.github.io/jersey2-toolkit/jersey2-hibernate)<br/>Jersey2 Feature and Binders for the Hibernate ORM.

## Basic Usage
Each feature is a distinct submodule, to isolate dependencies. Each submodule 
contains one feature class which includes all components of that feature, which
may be used as follows:

	import net.krotscheck.jersey2.hibernate.HibernateFeature;
	import org.glassfish.jersey.server.ResourceConfig;

	public class YourApplication extends ResourceConfig {
	    public YourApplication() {
	        register(HibernateFeature.class);
	    }
	}

## Advanced Usage
In all cases, each component is dependent on an injection chain from the other 
components, and is kept relatively isolated. Furthermore, every component 
injector contains a public internal class called <code>Binder</code> which 
may be used to construct your own feature. Therefore, if you have a custom 
implementation of a particular component that you would rather use, or you 
would like to exclude some feature, you may construct your own as follows:

    public final class CustomHibernateFeature implements Feature {
    
        @Override
        public boolean configure(final FeatureContext context) {
            // Custom Binders
            context.register(new MyCustomSessionFactoryFactory.Binder());
    
            // Library Provided Binders
            context.register(new HibernateSessionFactory.Binder());
            context.register(new HibernateConfigurationFactory.Binder());
    
            // Disable search indexing
            // context.register(new FulltextSearchFactoryFactory.Binder());
            // context.register(new FulltextSessionFactory.Binder());
            // context.register(new SearchIndexContextListener.Binder());
    
            return true;
        }
    }