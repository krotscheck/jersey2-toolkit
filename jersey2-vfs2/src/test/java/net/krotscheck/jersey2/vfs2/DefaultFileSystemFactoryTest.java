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

package net.krotscheck.jersey2.vfs2;

import net.krotscheck.jersey2.configuration.ConfigurationFeature;
import org.apache.commons.vfs2.FileSystem;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Singleton;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * Test for the default file system factory.
 *
 * @author Michael Krotscheck
 */
public final class DefaultFileSystemFactoryTest {

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
     * Test that the name works.
     */
    @Test
    public void testName() {
        DefaultFileSystemFactory factory = new DefaultFileSystemFactory();
        Assert.assertEquals("default", factory.getFileSystemName());
    }

    /**
     * A custom file system factory for our test.
     */
    public static final class CustomFileSystemFactory
            extends AbstractFileSystemFactory {

        /**
         * The name of this file system factory. "custom1.
         *
         * @return "custom1"
         */
        @Override
        public String getFileSystemName() {
            return "custom1";
        }

        /**
         * Test binder.
         */
        public static final class Binder extends AbstractBinder {

            @Override
            protected void configure() {
                bindFactory(CustomFileSystemFactory.class)
                        .to(FileSystem.class)
                        .named("custom1")
                        .in(Singleton.class);
            }
        }
    }

    /**
     * A private class to test our feature injection.
     */
    private static class TestFeature implements Feature {

        @Override
        public boolean configure(final FeatureContext context) {
            context.register(ConfigurationFeature.class);
            context.register(new FileSystemManagerFactory.Binder());
            context.register(new DefaultFileSystemFactory.Binder());
            return true;
        }
    }
}
