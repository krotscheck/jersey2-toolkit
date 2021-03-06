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
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.SearchFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * This factory will generate a Hibernate Search SearchFactory from the current
 * hibernate session. It is request-scoped, since it depends on a valid
 * session.
 *
 * @author Michael Krotscheck
 */
public final class FulltextSearchFactoryFactory
        implements Factory<SearchFactory> {

    /**
     * Logger instance.
     */
    private static Logger logger =
            LoggerFactory.getLogger(FulltextSearchFactoryFactory.class);

    /**
     * The current hibernate session injected from the application container.
     */
    private FullTextSession session;

    /**
     * Create a new fulltext search factory.
     *
     * @param fullTextSession The fulltext session to use.
     */
    @Inject
    public FulltextSearchFactoryFactory(final FullTextSession fullTextSession) {
        this.session = fullTextSession;
    }

    /**
     * Create a request-scoped SearchFactory.
     *
     * @return A new SearchFactory
     */
    @Override
    public SearchFactory provide() {
        logger.trace("Creating hibernate search factory.");
        return session.getSearchFactory();
    }

    /**
     * Dispose of the search factory.
     *
     * @param factory The factory to dispose of.
     */
    @Override
    public void dispose(final SearchFactory factory) {
        // Do nothing- the factory has no disposal requirements.
    }

    /**
     * HK2 Binder for our injector context.
     */
    public static final class Binder extends AbstractBinder {

        @Override
        protected void configure() {
            bindFactory(FulltextSearchFactoryFactory.class)
                    .to(SearchFactory.class)
                    .in(RequestScoped.class);
        }
    }
}
