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

import org.apache.commons.vfs2.FileSystem;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

/**
 * This factory will inject a VFS file system instance into the jersey scope, as
 * a singleton instance that you may resolve your own FileObjects from. Due to
 * limitations in HK2, we currently only support a default factory 'out of the
 * box'.
 *
 * @author Michael Krotscheck
 */
public final class DefaultFileSystemFactory extends AbstractFileSystemFactory {

    /**
     * Name of the file system.
     */
    private static final String NAME = "default";

    /**
     * Name of the default file system.
     *
     * @return "default"
     */
    @Override
    public String getFileSystemName() {
        return NAME;
    }        // Do nothing.

    /**
     * HK2 Binder for our injector context.
     */
    public static final class Binder extends AbstractBinder {

        @Override
        protected void configure() {
            bindFactory(DefaultFileSystemFactory.class)
                    .to(FileSystem.class)
                    .named(NAME)
                    .in(RequestScoped.class)
                    .ranked(999);
        }
    }
}
