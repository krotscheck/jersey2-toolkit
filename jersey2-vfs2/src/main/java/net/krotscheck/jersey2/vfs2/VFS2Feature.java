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


import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * This jersey2 feature provides various helper classes that may be used to
 * access a virtual file system instance (as per apache commons-vfs2). It may be
 * used explicitly by injecting the FileSystemManager and resolving your files
 * from there. Please make sure that any file opened is closed in the scope of
 * your request.
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
        context.register(new FileSystemManagerFactory.Binder());

        return true;
    }
}
