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

package net.krotscheck.jersey2.hibernate;

import net.krotscheck.jersey2.hibernate.lifecycle.SearchIndexContextListener;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.SearchFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Test that all expected classes are in the hibernate feature.
 *
 * @author Michael Krotscheck
 */
public final class HibernateFeatureTest extends JerseyTest {

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
        config.register(HibernateFeature.class);
        config.register(TestService.class);

        return config;
    }

    /**
     * A test service that asserts our injection scopes.
     */
    @Path("/test")
    public static final class TestService {

        /**
         * Hibernate configuration.
         */
        @Inject
        private SearchIndexContextListener contextListener;

        /**
         * Hibernate configuration.
         */
        @Inject
        private Configuration config;

        /**
         * Session factory.
         */
        @Inject
        private SessionFactory factory;

        /**
         * Search factory injection.
         */
        @Inject
        private SearchFactory searchFactory;

        /**
         * FullText session injector.
         */
        @Inject
        private FullTextSession ftSession;

        /**
         * Session injector.
         */
        @Inject
        private Session session;

        /**
         * Run the test service.
         *
         * @return An OK response.
         */
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response testService() {
            Assert.assertNotNull(config);
            Assert.assertNotNull(searchFactory);
            Assert.assertNotNull(factory);
            Assert.assertNotNull(ftSession);
            Assert.assertNotNull(session);
            Assert.assertNotNull(contextListener);

            return Response.ok().build();
        }
    }

}
