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

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * This factory injects a singleton instance of the file system manager into the
 * jersey context.
 *
 * @author Michael Krotscheck
 */
public final class FileSystemManagerFactory
        implements Factory<FileSystemManager> {

    /**
     * Logger instance.
     */
    private static Logger logger =
            LoggerFactory.getLogger(FileSystemManagerFactory.class);

    /**
     * Provide the file system manager.
     *
     * @return A file system manager instance.
     */
    @Override
    public FileSystemManager provide() {
        try {
            return VFS.getManager();
        } catch (FileSystemException fse) {
            logger.error("Cannot create FileSystemManager", fse);
            throw new RuntimeException("Cannot create FileSystemManager", fse);
        }
    }

    /**
     * Dispose of the filesystem.
     *
     * @param manager The manager to dispose of.
     */
    @Override
    public void dispose(final FileSystemManager manager) {
        // Do nothing.
    }

    /**
     * HK2 Binder for our injector context.
     */
    public static final class Binder extends AbstractBinder {

        @Override
        protected void configure() {
            bindFactory(FileSystemManagerFactory.class)
                    .to(FileSystemManager.class)
                    .in(Singleton.class);
        }
    }
}
