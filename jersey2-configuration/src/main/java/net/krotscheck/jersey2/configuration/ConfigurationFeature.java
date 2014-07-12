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


import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * This is the central configuration feature for the jersey2 toolkit. It reads
 * any jersey2-toolkit.properties configuration file found in your war file.
 * The contents of this file should contain whatever configuration options
 * are appropriate for the features you've enabled.
 *
 * @author Michael Krotscheck
 */
public final class ConfigurationFeature implements Feature {

    /**
     * Register the ConfigurationFeature with the current application context.
     *
     * @param context The application context.
     * @return Always true.
     */
    @Override
    public boolean configure(final FeatureContext context) {
        context.register(new Jersey2ToolkitConfig.Binder());
        return true;
    }
}
