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
import net.krotscheck.jersey2.configuration.Jersey2ToolkitConfig;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
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
 * Tests for the abstract file system factory.
 *
 * @author Michael Krotscheck
 */
public final class AbstractFileSystemFactoryTest {

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
     * Test that get/set manager works.
     *
     * @throws Exception Just in case...
     */
    @Test
    public void testGetSetManager() throws Exception {
        FileSystemManager manager = VFS.getManager();
        CustomFileSystemFactory factory = new CustomFileSystemFactory();

        Assert.assertNull(factory.getManager());

        factory.setManager(manager);
        Assert.assertEquals(manager, factory.getManager());

        factory.setManager(null);
        Assert.assertNull(factory.getManager());
    }

    /**
     * Test get/set toolkit configuration.
     */
    @Test
    public void testGetSetToolkitConfig() {
        Jersey2ToolkitConfig config = new Jersey2ToolkitConfig();
        CustomFileSystemFactory factory = new CustomFileSystemFactory();

        Assert.assertNull(factory.getToolkitConfig());

        factory.setToolkitConfig(config);
        Assert.assertEquals(config, factory.getToolkitConfig());

        factory.setToolkitConfig(null);
        Assert.assertNull(factory.getToolkitConfig());
    }

    /**
     * Assert that the filesystem is properly configured.
     */
    @Test
    public void testInjectionPoints() {
        FileSystem fileSystem = locator.getService(FileSystem.class, "custom1");
        Assert.assertNotNull(fileSystem);
    }

    /**
     * Assert nonexistent configuration.
     */
    @Test
    public void testNonexistentInjection() {
        FileSystem fileSystem = locator.getService(FileSystem.class, "custom3");
        Assert.assertNull(fileSystem);
    }

    /**
     * Test actions if the protocol or path is not supported.
     *
     * @throws Exception An exception thrown when things go topsy turvy.
     */
    @Test(expected = RuntimeException.class)
    public void testInvalidInjection() throws Exception {
        locator.getService(FileSystem.class, "custom2");
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
     * A custom invalid file system factory for our test.
     */
    public static final class Custom2FileSystemFactory
            extends AbstractFileSystemFactory {

        /**
         * The name of this file system factory. "custom1.
         *
         * @return "custom2"
         */
        @Override
        public String getFileSystemName() {
            return "custom2";
        }

        /**
         * Test binder.
         */
        public static final class Binder extends AbstractBinder {

            @Override
            protected void configure() {
                bindFactory(Custom2FileSystemFactory.class)
                        .to(FileSystem.class)
                        .named("custom2")
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
            context.register(new CustomFileSystemFactory.Binder());
            context.register(new Custom2FileSystemFactory.Binder());
            return true;
        }
    }
}
