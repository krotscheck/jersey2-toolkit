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

package net.krotscheck.jersey2.hibernate.lifecycle;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This context listener will kick off a full index refresh whenever the
 * application restarts, and will set up hibernate search index optimization.
 *
 * @author Michael Krotscheck
 */
public final class SearchIndexContextListener
        implements ContainerLifecycleListener {

    /**
     * Logger instance.
     */
    private static Logger logger =
            LoggerFactory.getLogger(SearchIndexContextListener.class);

    /**
     * The injected hibernate session.
     */
    private SessionFactory factory;

    /**
     * Create a new instance of this context listener.
     *
     * @param sessionFactory The hibernate session factory from which to draw
     *                       our configuration.
     */
    @Inject
    public SearchIndexContextListener(final SessionFactory sessionFactory) {
        this.factory = sessionFactory;
    }

    /**
     * When the container starts up, rebuild the search index.
     *
     * @param container The application container.
     */
    @Override
    public void onStartup(final Container container) {
        logger.info("Rebuilding Search Index...");
        Session session = factory.openSession();
        FullTextSession fullTextSession = Search.getFullTextSession(session);

        try {
            fullTextSession
                    .createIndexer()
                    .startAndWait();
        } catch (InterruptedException e) {
            logger.warn("Search reindex interrupted. Good luck!");
        } finally {
            session.close();
        }
    }

    /**
     * Reload the container.
     *
     * @param container The application container.
     */
    @Override
    public void onReload(final Container container) {
        // Do nothing, no reload is required.
    }

    /**
     * Shut down the container.
     *
     * @param container The container.
     */
    @Override
    public void onShutdown(final Container container) {

    }

    /**
     * HK2 Binder for our injector context.
     */
    public static final class Binder extends AbstractBinder {

        @Override
        protected void configure() {
            bind(SearchIndexContextListener.class)
                    .to(ContainerLifecycleListener.class)
                    .in(Singleton.class);
        }
    }
}
