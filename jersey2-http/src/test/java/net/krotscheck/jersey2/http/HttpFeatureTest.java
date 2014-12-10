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

package net.krotscheck.jersey2.http;

import net.krotscheck.test.UnitTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This unit test asserts that the configuration is properly injected into a
 * dummy application scope.
 *
 * @author Michael Krotscheck
 */
@Category(UnitTest.class)
public final class HttpFeatureTest extends JerseyTest {

    /**
     * Run a service request.
     */
    @Test
    public void testService() {
        target("/test")
                .request(MediaType.APPLICATION_JSON)
                .get();
    }

    /**
     * Configure the application.
     *
     * @return A properly configured application.
     */
    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        config.register(HttpFeature.class);
        config.register(TestService.class);
        return config;
    }

    /**
     * A test service that asserts our injection scopes.
     */
    @Path("/test")
    public static final class TestService {

        /**
         * Constructor for the test service.
         */
        @Inject
        public TestService() {
        }

        /**
         * Run the test service.
         *
         * @return An OK response.
         */
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response testService() {

            return Response.ok().build();
        }
    }
}
