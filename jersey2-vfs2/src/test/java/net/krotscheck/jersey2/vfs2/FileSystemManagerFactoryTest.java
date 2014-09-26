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

import net.krotscheck.test.UnitTest;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Unit test for the File System Manager factory.
 *
 * @author Michael Krotscheck
 */
@PrepareForTest(VFS.class)
@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
@Category(UnitTest.class)
public final class FileSystemManagerFactoryTest {

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
        FileSystemManager manager = locator.getService(FileSystemManager.class);
        Assert.assertNotNull(manager);

        FileSystemManagerFactory factory = new FileSystemManagerFactory();

        // Make sure that we can create a search factory.
        FileSystemManager factoryManager = factory.provide();
        Assert.assertNotNull(factoryManager);

        // Make sure we can dispose of the factory (does nothing, sadly).
        factory.dispose(factoryManager);
    }

    /**
     * Test provide and dispose with exceptions.
     *
     * @throws Exception A runtime exception thrown when fs provisioning fails.
     */
    @Test(expected = RuntimeException.class)
    @SuppressWarnings({"unchecked" })
    public void testProvideDisposeExceptions() throws Exception {

        mockStatic(VFS.class);
        when(VFS.getManager()).thenThrow(FileSystemException.class);

        FileSystemManagerFactory factory = new FileSystemManagerFactory();

        // This should throw a runtime exception.
        factory.provide();
    }

    /**
     * A private class to test our feature injection.
     */
    private static class TestFeature implements Feature {

        @Override
        public boolean configure(final FeatureContext context) {
            context.register(new FileSystemManagerFactory.Binder());
            return true;
        }
    }

}
