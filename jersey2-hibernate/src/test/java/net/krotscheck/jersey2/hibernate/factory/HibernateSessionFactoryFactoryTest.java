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

import net.krotscheck.test.UnitTest;
import org.glassfish.hk2.api.PerThread;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostCommitDeleteEventListener;
import org.hibernate.event.spi.PostCommitInsertEventListener;
import org.hibernate.event.spi.PostCommitUpdateEventListener;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * Unit test for our hibernate session factory factory.
 *
 * @author Michael Krotscheck
 */
@Category(UnitTest.class)
public final class HibernateSessionFactoryFactoryTest {

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
     * Test provide and dispose.
     */
    @Test
    public void testProvideDispose() {
        ServiceRegistry serviceRegistry =
                locator.getService(ServiceRegistry.class);
        HibernateSessionFactoryFactory factoryFactory =
                new HibernateSessionFactoryFactory(serviceRegistry, locator);
        SessionFactory factory = locator.getService(SessionFactory.class);

        // Assert that the factory is open.
        Assert.assertFalse(factory.isClosed());

        // Make sure that we can create a session.
        Session session = factory.openSession();
        Assert.assertNotNull(session);
        Assert.assertTrue(session.isOpen());

        // Make sure we can dispose of the factory.
        factoryFactory.dispose(factory);
        Assert.assertTrue(factory.isClosed());
        Assert.assertFalse(session.isOpen());

        // Make sure doing it twice won't fail.
        factoryFactory.dispose(factory);

        // Make sure passing null doesn't fail
        factoryFactory.dispose(null);
    }

    /**
     * Test the application binder.
     */
    @Test
    public void testBinder() {

        // Create a fake application.
        SessionFactory factoryFactory = locator
                .getService(SessionFactory.class);
        Assert.assertNotNull(factoryFactory);

        // Make sure it's reading from the same place.
        Assert.assertFalse(factoryFactory.isClosed());

        // Make sure it's a singleton...
        SessionFactory factoryFactory2 = handler
                .getServiceLocator().getService(SessionFactory.class);
        Assert.assertSame(factoryFactory, factoryFactory2);
    }

    /**
     * Test the application event injectors.
     */
    @Test
    public void testEventInjection() {

        // Create a fake application.
        SessionFactory factoryFactory = locator
                .getService(SessionFactory.class);
        Assert.assertNotNull(factoryFactory);


        ServiceRegistry serviceRegistry = ((SessionFactoryImpl) factoryFactory)
                .getServiceRegistry();
        EventListenerRegistry eventRegistry = serviceRegistry
                .getService(EventListenerRegistry.class);

        EventListenerGroup<PreInsertEventListener> priGroup = eventRegistry
                .getEventListenerGroup(EventType.PRE_INSERT);
        assertContainsListener(priGroup);

        EventListenerGroup<PostInsertEventListener> poiGroup = eventRegistry
                .getEventListenerGroup(EventType.POST_INSERT);
        assertContainsListener(poiGroup);

        EventListenerGroup<PreUpdateEventListener> pruGroup = eventRegistry
                .getEventListenerGroup(EventType.PRE_UPDATE);
        assertContainsListener(pruGroup);

        EventListenerGroup<PostUpdateEventListener> pouGroup = eventRegistry
                .getEventListenerGroup(EventType.POST_UPDATE);
        assertContainsListener(pouGroup);

        EventListenerGroup<PreDeleteEventListener> prdGroup = eventRegistry
                .getEventListenerGroup(EventType.PRE_DELETE);
        assertContainsListener(prdGroup);

        EventListenerGroup<PostDeleteEventListener> podGroup = eventRegistry
                .getEventListenerGroup(EventType.POST_DELETE);
        assertContainsListener(podGroup);

        EventListenerGroup<PostInsertEventListener> pciGroup = eventRegistry
                .getEventListenerGroup(EventType.POST_COMMIT_INSERT);
        assertContainsListener(pciGroup);

        EventListenerGroup<PostUpdateEventListener> pcuGroup = eventRegistry
                .getEventListenerGroup(EventType.POST_COMMIT_UPDATE);
        assertContainsListener(pcuGroup);

        EventListenerGroup<PostDeleteEventListener> pcdGroup = eventRegistry
                .getEventListenerGroup(EventType.POST_COMMIT_DELETE);
        assertContainsListener(pcdGroup);
    }

    /**
     * Helper method that asserts that our event listener is in the passed
     * group.
     *
     * @param group The event listener group in question.
     */
    private void assertContainsListener(final EventListenerGroup group) {

        for (Object listener : group.listeners()) {
            if (listener instanceof TestEventListener) {
                return;
            }
        }

        Assert.assertFalse(true);
    }

    /**
     * A private class to test our feature injection.
     */
    private static class TestFeature implements Feature {

        @Override
        public boolean configure(final FeatureContext context) {
            context.register(new HibernateServiceRegistryFactory.Binder());
            context.register(new HibernateSessionFactoryFactory.Binder());
            context.register(new TestEventListener.Binder());
            return true;
        }
    }

    /**
     * A test event listener to ensure that the session factory can read them
     * from the injection context.
     */
    public static final class TestEventListener
            implements PreInsertEventListener, PostInsertEventListener,
            PreUpdateEventListener, PostUpdateEventListener,
            PreDeleteEventListener, PostDeleteEventListener,
            PostCommitInsertEventListener, PostCommitUpdateEventListener,
            PostCommitDeleteEventListener {

        @Override
        public void onPostDelete(final PostDeleteEvent event) {

        }

        @Override
        public void onPostInsert(final PostInsertEvent event) {

        }

        @Override
        public void onPostUpdate(final PostUpdateEvent event) {

        }

        @Override
        public boolean requiresPostCommitHanding(
                final EntityPersister persister) {
            return false;
        }

        @Override
        public boolean onPreDelete(final PreDeleteEvent event) {
            return false;
        }

        @Override
        public boolean onPreInsert(final PreInsertEvent event) {
            return false;
        }

        @Override
        public boolean onPreUpdate(final PreUpdateEvent event) {
            return false;
        }

        @Override
        public void onPostDeleteCommitFailed(final PostDeleteEvent event) {

        }

        @Override
        public void onPostInsertCommitFailed(final PostInsertEvent event) {

        }

        @Override
        public void onPostUpdateCommitFailed(final PostUpdateEvent event) {

        }

        /**
         * HK2 Binder for our injector context.
         */
        public static final class Binder extends AbstractBinder {

            @Override
            protected void configure() {
                bind(TestEventListener.class)
                        .to(PostDeleteEventListener.class)
                        .to(PreDeleteEventListener.class)
                        .to(PreInsertEventListener.class)
                        .to(PostInsertEventListener.class)
                        .to(PreUpdateEventListener.class)
                        .to(PostUpdateEventListener.class)
                        .to(PostCommitInsertEventListener.class)
                        .to(PostCommitUpdateEventListener.class)
                        .to(PostCommitDeleteEventListener.class)
                        .in(PerThread.class);
            }
        }
    }
}
