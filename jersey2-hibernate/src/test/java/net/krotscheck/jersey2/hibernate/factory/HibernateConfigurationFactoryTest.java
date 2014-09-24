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
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.hibernate.cfg.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * Unit test for the hibernate configuration and its binder.
 *
 * @author Michael Krotscheck
 */
@Category(UnitTest.class)
public final class HibernateConfigurationFactoryTest {

    /**
     * Test provide and dispose.
     */
    @Test
    public void testProvideDispose() {
        HibernateConfigurationFactory factory = new
                HibernateConfigurationFactory();

        Configuration config = factory.provide();

        Assert.assertEquals(
                "org.hibernate.dialect.H2Dialect",
                config.getProperty("hibernate.dialect")
        );

        // This shouldn't actually do anything, but is included here for
        // coverage.
        factory.dispose(config);

    }

    /**
     * Test the application binder.
     */
    @Test
    public void testBinder() {

        ResourceConfig config = new ResourceConfig();
        config.register(TestFeature.class);

        // Make sure it's registered
        Assert.assertTrue(config.isRegistered(TestFeature.class));

        // Create a fake application.
        ApplicationHandler handler = new ApplicationHandler(config);
        Configuration appConfig = handler
                .getServiceLocator().getService(Configuration.class);
        Assert.assertNotNull(appConfig);

        // Make sure it's reading from the same place.
        Assert.assertEquals(
                "org.hibernate.dialect.H2Dialect",
                appConfig.getProperty("hibernate.dialect")
        );

        // Make sure it's a singleton...
        Configuration appConfig2 = handler
                .getServiceLocator().getService(Configuration.class);
        Assert.assertSame(appConfig, appConfig2);
    }

    /**
     * A private class to test our feature injection.
     */
    private static class TestFeature implements Feature {

        @Override
        public boolean configure(final FeatureContext context) {
            context.register(new HibernateConfigurationFactory.Binder());
            return true;
        }
    }
}
