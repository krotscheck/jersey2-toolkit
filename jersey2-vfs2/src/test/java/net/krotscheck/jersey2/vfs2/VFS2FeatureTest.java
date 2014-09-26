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
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemManager;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Test that all expected classes are in the vfs2 feature.
 *
 * @author Michael Krotscheck
 */
@Category(UnitTest.class)
public final class VFS2FeatureTest extends JerseyTest {

    /**
     * Run a service request.
     */
    @Test
    public void testService() {
        Response r = target("/test")
                .request(MediaType.APPLICATION_JSON)
                .get();
        Assert.assertEquals(200, r.getStatus());
    }

    /**
     * Configure the application.
     *
     * @return A properly configured application.
     */
    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        config.register(VFS2Feature.class);
        config.register(TestService.class);

        return config;
    }

    /**
     * A test service that asserts our injection scopes.
     */
    @Path("/test")
    public static final class TestService {

        /**
         * Default Filesystem.
         */
        private FileSystem defaultFilesystem;

        /**
         * Custom Filesystem 2.
         */
        private FileSystemManager fsManager;

        /**
         * Create a new instance of our test service.
         *
         * @param manager   The filesystem manager.
         * @param defaultFS Default Filesystemo
         */
        @Inject
        public TestService(final FileSystemManager manager,
                           @Named("default") final FileSystem defaultFS
        ) {
            this.fsManager = manager;
            this.defaultFilesystem = defaultFS;
        }

        /**
         * Run the test service.
         *
         * @return An OK response.
         */
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response testService() {

            Assert.assertNotNull(fsManager);
            Assert.assertNotNull(defaultFilesystem);

            return Response.ok().build();
        }
    }
}
