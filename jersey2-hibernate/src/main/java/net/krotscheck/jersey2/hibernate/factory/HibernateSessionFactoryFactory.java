/*
 * Copyright (c) 2014 Michael Krotscheck
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.krotscheck.jersey2.hibernate.factory;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.PerThread;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import javax.inject.Inject;

/**
 * This factory provides a singleton Hibernate SessionFactory for your
 * application context. You may use this to generate your own sessions if you
 * are not in an explicit request context, however if you do so please make sure
 * to clean up after yourself and call session.close().
 * <p/>
 * Furthermore, this injector will search through the ServiceLocator context to
 * determine if there are any Hibernate Event Listeners injected from other
 * features. It will gather those and automatically register them against the
 * hibernate factory.
 *
 * @author Michael Krotscheck
 */
public final class HibernateSessionFactoryFactory
        implements Factory<SessionFactory> {

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory
            .getLogger(HibernateSessionFactoryFactory.class);

    /**
     * Create a new factory factory.
     *
     * @param hibernateConfig The Hibernate Config
     * @param serviceLocator  The service locator from which to resolve event
     *                        handlers.
     */
    @Inject
    public HibernateSessionFactoryFactory(final Configuration hibernateConfig,
                                          final ServiceLocator serviceLocator) {
        this.configuration = hibernateConfig;
        this.locator = serviceLocator;
    }

    /**
     * Injected hibernate configuration.
     */
    private Configuration configuration;

    /**
     * The service locator.
     */
    private ServiceLocator locator;

    /**
     * Provide a singleton instance of the hibernate session factory.
     *
     * @return A session factory.
     */
    @Override
    public SessionFactory provide() {
        logger.trace("Creating hibernate session factory.");

        // Build the service registry.
        ServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        SessionFactory factory = configuration.buildSessionFactory(registry);

        // Register our event listeners.
        injectEventListeners(((SessionFactoryImpl) factory)
                .getServiceRegistry());

        return factory;
    }

    /**
     * Dispose of the hibernate session.
     *
     * @param sessionFactory The session to dispose.
     */
    @Override
    public void dispose(final SessionFactory sessionFactory) {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            logger.info("Disposing of hibernate session factory.");
            sessionFactory.close();
        }
    }

    /**
     * This method automatically adds discovered hibernate event listeners into
     * the hibernate service registry.
     *
     * @param registry The service registry.
     */
    private void injectEventListeners(final ServiceRegistry registry) {

        EventListenerRegistry eventRegistry = registry
                .getService(EventListenerRegistry.class);

        List<PostInsertEventListener> postInsertEvents = locator
                .getAllServices(PostInsertEventListener.class);
        if (postInsertEvents != null) {
            for (PostInsertEventListener piEventListener : postInsertEvents) {
                logger.trace("Registering PostInsert: " + piEventListener
                        .getClass().getCanonicalName());
                eventRegistry.appendListeners(EventType.POST_INSERT,
                        piEventListener);
            }
        }

        List<PostUpdateEventListener> postUpdateEvents = locator
                .getAllServices(PostUpdateEventListener.class);
        if (postUpdateEvents != null) {
            for (PostUpdateEventListener puEventListener : postUpdateEvents) {
                logger.trace("Registering PostUpdate: " + puEventListener
                        .getClass().getCanonicalName());
                eventRegistry.appendListeners(EventType.POST_UPDATE,
                        puEventListener);
            }
        }

        List<PostDeleteEventListener> postDeleteEvents = locator
                .getAllServices(PostDeleteEventListener.class);
        if (postDeleteEvents != null) {
            for (PostDeleteEventListener pdEventListener : postDeleteEvents) {
                logger.trace("Registering PostDelete: " + pdEventListener
                        .getClass().getCanonicalName());
                eventRegistry.appendListeners(EventType.POST_DELETE,
                        pdEventListener);
            }
        }

        List<PreInsertEventListener> preInsertEvents = locator
                .getAllServices(PreInsertEventListener.class);
        if (preInsertEvents != null) {
            for (PreInsertEventListener piEventListener : preInsertEvents) {
                logger.trace("Registering PreInsert: " + piEventListener
                        .getClass().getCanonicalName());
                eventRegistry.appendListeners(EventType.PRE_INSERT,
                        piEventListener);
            }
        }

        List<PreUpdateEventListener> preUpdateEvents = locator
                .getAllServices(PreUpdateEventListener.class);
        if (preUpdateEvents != null) {
            for (PreUpdateEventListener puEventListener : preUpdateEvents) {
                logger.trace("Registering PreUpdate: " + puEventListener
                        .getClass().getCanonicalName());
                eventRegistry.appendListeners(EventType.PRE_UPDATE,
                        puEventListener);
            }
        }

        List<PreDeleteEventListener> preDeleteEvents = locator
                .getAllServices(PreDeleteEventListener.class);
        if (preDeleteEvents != null) {
            for (PreDeleteEventListener pdEventListener : preDeleteEvents) {
                logger.trace("Registering PreDelete: " + pdEventListener
                        .getClass().getCanonicalName());
                eventRegistry.appendListeners(EventType.PRE_DELETE,
                        pdEventListener);
            }
        }
    }

    /**
     * HK2 Binder for our injector context.
     */
    public static final class Binder extends AbstractBinder {

        @Override
        protected void configure() {
            bindFactory(HibernateSessionFactoryFactory.class)
                    .to(SessionFactory.class)
                    .in(PerThread.class);
        }
    }
}
