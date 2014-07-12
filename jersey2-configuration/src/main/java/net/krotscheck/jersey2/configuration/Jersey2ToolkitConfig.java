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

package net.krotscheck.jersey2.configuration;

import net.krotscheck.util.ResourceUtil;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import javax.inject.Singleton;

/**
 * The toolkit configuration object.
 */
public final class Jersey2ToolkitConfig extends CompositeConfiguration {

    /**
     * Logger instance.
     */
    private static Logger
            logger = LoggerFactory.getLogger(Jersey2ToolkitConfig.class);

    /**
     * Create an instance of the configuration provider.
     */
    public Jersey2ToolkitConfig() {
        logger.debug("Adding system configuration");
        addConfiguration(new SystemConfiguration());
        logger.debug("Adding jersey2-toolkit configuration");
        addConfiguration(buildToolkitConfig());
    }

    /**
     * Builds the configuration object for our toolkit config.
     *
     * @return A Configuration object.
     */
    private Configuration buildToolkitConfig() {

        // The toolkit configuration file.
        try {
            File configFile = ResourceUtil
                    .getFileForResource("jersey2-toolkit.properties");
            return new PropertiesConfiguration(configFile);
        } catch (ConfigurationException ce) {
            logger.error("jersey2-toolkit.properties not readable,"
                    + " some features may be misconfigured.");

            // Return a new, empty map configuration so we don't error out.
            return new MapConfiguration(new HashMap<String, String>());
        }
    }

    /**
     * HK2 Binder for our injector context.
     */
    public static final class Binder extends AbstractBinder {

        @Override
        protected void configure() {
            bind(Jersey2ToolkitConfig.class)
                    .to(Jersey2ToolkitConfig.class)
                    .to(Configuration.class)
                    .named("jersey2-toolkit")
                    .in(Singleton.class);
        }
    }
}
