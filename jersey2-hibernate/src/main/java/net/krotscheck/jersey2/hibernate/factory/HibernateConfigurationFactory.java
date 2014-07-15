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
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * This factory creates a singleton hibernate configuration object, using either
 * your application's hibernate.properties or hibernate.cfg.xml file. To ensure
 * all of your entities are properly registered, add both of those files to your
 * resources directory and ensure they reflect the correct settings for your
 * application.
 *
 * @author Michael Krotscheck
 */
public final class HibernateConfigurationFactory
        implements Factory<Configuration> {

    /**
     * Logger instance.
     */
    private static Logger logger =
            LoggerFactory.getLogger(HibernateConfigurationFactory.class);

    /**
     * Provide a Hibernate Configuration object.
     *
     * @return The hibernate configuration.
     */
    @Override
    public Configuration provide() {
        logger.trace("Configuration provide");

        // Get the hibernate configuration;
        Configuration configuration = new Configuration();
        configuration.configure();

        return configuration;
    }

    /**
     * Dispose of the hibernate configuration.
     *
     * @param configuration The configuration to dispose of.
     */
    @Override
    public void dispose(final Configuration configuration) {
        // This instance doesn't need to be disposed of.
    }

    /**
     * HK2 Binder for our injector context.
     */
    public static final class Binder extends AbstractBinder {

        @Override
        protected void configure() {
            bindFactory(HibernateConfigurationFactory.class)
                    .to(Configuration.class)
                    .in(Singleton.class);
        }
    }
}
