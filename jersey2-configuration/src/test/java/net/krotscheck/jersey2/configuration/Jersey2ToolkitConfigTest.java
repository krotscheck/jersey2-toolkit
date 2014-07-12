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
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

/**
 * Unit test for the configuration reader.
 *
 * @author Michael Krotscheck
 */
public final class Jersey2ToolkitConfigTest {

    /**
     * Assert that the system properties override the text properties.
     */
    @Test
    public void testSystemOverride() {
        // Override a property in jersey2-toolkit.properties
        System.setProperty("property1", "override1");
        // Add a new system property.
        System.setProperty("property3", "override3");

        Configuration config = new Jersey2ToolkitConfig();

        Assert.assertEquals("override1", config.getProperty("property1"));
        Assert.assertEquals("value2", config.getProperty("property2"));
        Assert.assertEquals("override3", config.getProperty("property3"));

        System.clearProperty("property1");
        System.clearProperty("property3");
    }

    /**
     * Assert that a property may be read.
     */
    @Test
    public void testReadFileProperty() {
        Configuration config = new Jersey2ToolkitConfig();
        Assert.assertEquals("value1", config.getProperty("property1"));
        Assert.assertEquals("value2", config.getProperty("property2"));
    }

    /**
     * Assert that a missing file does not throw an error.
     *
     * @throws java.io.IOException File operation errors.
     */
    @Test
    public void testMissingFile() throws IOException {
        // Move the properties file out of the way.
        File propFile = ResourceUtil
                .getFileForResource("jersey2-toolkit.properties");
        File newPropFile = ResourceUtil
                .getFileForResource("jersey2-toolkit-mv.properties");
        FileUtils.moveFile(propFile, newPropFile);

        Assert.assertFalse(propFile.exists());
        Assert.assertTrue(newPropFile.exists());

        // Add something to check
        System.setProperty("property3", "override3");

        // If this throws an error, we've got a problem.
        Configuration config = new Jersey2ToolkitConfig();
        Assert.assertFalse(config.containsKey("property1"));
        Assert.assertFalse(config.containsKey("property2"));
        Assert.assertTrue(config.containsKey("property3"));

        System.clearProperty("property3");

        // Move the file back.
        FileUtils.moveFile(newPropFile, propFile);

        Assert.assertTrue(propFile.exists());
        Assert.assertFalse(newPropFile.exists());

    }

    /**
     * Assert that an unreadable file does not throw an error.
     *
     * @throws java.io.IOException File operation errors.
     */
    @Test
    public void testUnreadableFile() throws IOException {
        // Get the prop file and store the old permissions.
        File propFile =
                ResourceUtil.getFileForResource("jersey2-toolkit.properties");

        Set<PosixFilePermission> oldPerms =
                Files.getPosixFilePermissions(propFile.toPath());

        // Apply writeonly permission set.
        Set<PosixFilePermission> writeonly = new HashSet<>();
        writeonly.add(PosixFilePermission.OWNER_WRITE);
        writeonly.add(PosixFilePermission.GROUP_WRITE);
        Files.setPosixFilePermissions(propFile.toPath(), writeonly);

        // Add something to check
        System.setProperty("property3", "override3");

        // If this throws an error, we've got a problem.
        Configuration config = new Jersey2ToolkitConfig();
        Assert.assertFalse(config.containsKey("property1"));
        Assert.assertFalse(config.containsKey("property2"));
        Assert.assertTrue(config.containsKey("property3"));

        // Apply the correct permissions again
        Files.setPosixFilePermissions(propFile.toPath(), oldPerms);
    }
}
