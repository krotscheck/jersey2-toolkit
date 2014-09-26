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

import net.krotscheck.jersey2.configuration.Jersey2ToolkitConfig;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.glassfish.hk2.api.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * This factory will inject a VFS file system instance into the jersey scope, as
 * a singleton instance that you may resolve your own FileObjects from. Due to
 * limitations in HK2, we currently only support a default factory 'out of the
 * box'.
 *
 * @author Michael Krotscheck
 */
public abstract class AbstractFileSystemFactory implements Factory<FileSystem> {

    /**
     * Logger instance.
     */
    private static Logger logger =
            LoggerFactory.getLogger(AbstractFileSystemFactory.class);

    /**
     * The file system manager.
     */
    private FileSystemManager manager;


    /**
     * The jersey 2 configuration.
     */
    private Jersey2ToolkitConfig toolkitConfig;

    /**
     * Get the file system manager.
     *
     * @return The filesystem manager.
     */
    public final FileSystemManager getManager() {
        return manager;
    }

    /**
     * Set the filesystem manager.
     *
     * @param fsManager The filesystem manager to use.
     */
    @Inject
    public final void setManager(final FileSystemManager fsManager) {
        this.manager = fsManager;
    }

    /**
     * Get the toolkit configuration instance from which this factory reads its
     * configuration.
     *
     * @return The config.
     */
    public final Jersey2ToolkitConfig getToolkitConfig() {
        return toolkitConfig;
    }

    /**
     * Set the toolkit configuration from which this component should read the
     * details of its VFS paths.
     *
     * @param config The toolkit configuration.
     */
    @Inject
    public final void setToolkitConfig(final Jersey2ToolkitConfig config) {
        this.toolkitConfig = config;
    }


    /**
     * Return the name of the filesystem to generate. This is mapped in the
     * configuration to vfs2.[YourFileSystemName]
     *
     * @return The name of the filesystem.
     */
    public abstract String getFileSystemName();

    /**
     * Provide a configured instance of the filesystem.
     *
     * @return A new filesystem instance.
     */
    @Override
    public final FileSystem provide() {
        String configName = String.format("vfs2.%s",
                getFileSystemName());
        String vfsPath = toolkitConfig.getString(configName);
        logger.debug(String.format("Provisioning: [%s]", vfsPath));
        try {
            FileObject object = manager.resolveFile(vfsPath);
            return object.getFileSystem();
        } catch (FileSystemException fse) {
            throw new RuntimeException(
                    String.format("Cannot resolve filesystem with name [%s] "
                            + "at path [%s]", configName, vfsPath), fse);
        }
    }

    /**
     * Close the filesystem.
     *
     * @param fileSystem The filesystem to close.
     */
    @Override
    public final void dispose(final FileSystem fileSystem) {
        logger.debug(String.format("Disposing: [%s]",
                fileSystem.getRootURI()));
        manager.closeFileSystem(fileSystem);
    }
}
