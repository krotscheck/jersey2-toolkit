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

import net.krotscheck.jersey2.hibernate.factory.HibernateServiceRegistryFactory;
import net.krotscheck.jersey2.hibernate.factory.HibernateSessionFactory;
import net.krotscheck.jersey2.hibernate.factory.HibernateSessionFactoryFactory;
import net.krotscheck.test.UnitTest;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.Container;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.Search;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


/**
 * Unit test for our lucene indexer.
 *
 * @author Michael Krotscheck
 */
@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
@Category(UnitTest.class)
public final class SearchIndexContextListenerTest {

    /**
     * The jersey application handler.
     */
    private ApplicationHandler handler;

    /**
     * The jersey application service locator.
     */
    private ServiceLocator locator;

    /**
     * Setup the application handler for this test.
     */
    @Before
    public void setup() {
        ResourceConfig config = new ResourceConfig();
        config.register(TestFeature.class);
        handler = new ApplicationHandler(config);
        locator = handler.getServiceLocator();
    }

    /**
     * Teardown the application handler.
     */
    @After
    public void teardown() {
        locator.shutdown();
        locator = null;
        handler = null;
    }

    /**
     * Assert that the index is created on startup.
     *
     * @throws java.lang.Exception Any unexpected exceptions.
     */
    @PrepareForTest(Search.class)
    @Test
    public void testOnStartup() throws Exception {
        // Set up our container
        Container mockContainer = mock(Container.class);

        // Set up a fake session factory
        SessionFactory mockFactory = mock(SessionFactory.class);
        Session mockSession = mock(Session.class);
        when(mockFactory.openSession()).thenReturn(mockSession);

        // Set up a fake indexer
        MassIndexer mockIndexer = mock(MassIndexer.class);

        // Set up our fulltext session.
        FullTextSession mockFtSession = mock(FullTextSession.class);
        when(mockFtSession.createIndexer())
                .thenReturn(mockIndexer);

        // This is the way to tell PowerMock to mock all static methods of a
        // given class
        mockStatic(Search.class);
        when(Search.getFullTextSession(mockSession))
                .thenReturn(mockFtSession);

        SearchIndexContextListener listener =
                new SearchIndexContextListener(mockFactory);
        listener.onStartup(mockContainer);

        verify(mockIndexer).startAndWait();

        // Verify that the session was closed.
        verify(mockSession).close();

        verifyZeroInteractions(mockContainer);
    }

    /**
     * Assert that an interrupted exception exits cleanly.
     *
     * @throws Exception An exception that might be thrown.
     */
    @PrepareForTest(Search.class)
    @Test
    public void testInterruptedIndex() throws Exception {
        // Set up our container
        Container mockContainer = mock(Container.class);

        // Set up a fake session factory
        SessionFactory mockFactory = mock(SessionFactory.class);
        Session mockSession = mock(Session.class);
        when(mockFactory.openSession()).thenReturn(mockSession);

        // Set up a fake indexer
        MassIndexer mockIndexer = mock(MassIndexer.class);

        // Set up our fulltext session.
        FullTextSession mockFtSession = mock(FullTextSession.class);
        when(mockFtSession.createIndexer())
                .thenReturn(mockIndexer);
        doThrow(new InterruptedException())
                .when(mockIndexer)
                .startAndWait();

        // This is the way to tell PowerMock to mock all static methods of a
        // given class
        mockStatic(Search.class);
        when(Search.getFullTextSession(mockSession))
                .thenReturn(mockFtSession);

        SearchIndexContextListener listener =
                new SearchIndexContextListener(mockFactory);
        listener.onStartup(mockContainer);

        // Verify that the session was closed.
        verify(mockSession).close();

        verifyZeroInteractions(mockContainer);
    }

    /**
     * Assert that reloading does nothing.
     */
    @Test
    public void testOnReload() {
        SessionFactory sessionFactory =
                locator.getService(SessionFactory.class);
        SearchIndexContextListener listener =
                new SearchIndexContextListener(sessionFactory);

        Container mockContainer = mock(Container.class);
        listener.onReload(mockContainer);
        verifyZeroInteractions(mockContainer);
    }

    /**
     * Assert that shutting down does nothing.
     */
    @Test
    public void testOnShutdown() {
        SessionFactory sessionFactory =
                locator.getService(SessionFactory.class);
        SearchIndexContextListener listener =
                new SearchIndexContextListener(sessionFactory);

        Container mockContainer = mock(Container.class);
        listener.onShutdown(mockContainer);
        verifyZeroInteractions(mockContainer);
    }

    /**
     * A private class to test our feature injection.
     */
    private static class TestFeature implements Feature {

        @Override
        public boolean configure(final FeatureContext context) {
            context.register(new HibernateServiceRegistryFactory.Binder());
            context.register(new HibernateSessionFactoryFactory.Binder());
            context.register(new HibernateSessionFactory.Binder());
            return true;
        }
    }
}
