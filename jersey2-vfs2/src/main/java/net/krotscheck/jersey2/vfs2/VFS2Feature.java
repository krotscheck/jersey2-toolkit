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

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * This jersey2 feature provides various helper classes that may be used to
 * access a virtual file system instance (as per apache commons-vfs2). It may be
 * used either explicitly by injecting the FileSystemManager, or by configuring
 * multiple different file systems by name in the jersey2-toolkit.configuration
 * file. For more information, please see the documentation.
 *
 * @author Michael Krotscheck
 */
public final class VFS2Feature implements Feature {

    /**
     * Register the VFS2Feature with the current application context.
     *
     * @param context The application context.
     * @return Always true.
     */
    @Override
    public boolean configure(final FeatureContext context) {

        // First make sure the jersey2-configuration feature is included.
        if (!context.getConfiguration()
                .isRegistered(ConfigurationFeature.class)) {
            context.register(ConfigurationFeature.class);
        }

        context.register(new FileSystemManagerFactory.Binder());
        context.register(new DefaultFileSystemFactory.Binder());

        return true;
    }
}
