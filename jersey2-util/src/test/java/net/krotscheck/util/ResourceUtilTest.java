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

package net.krotscheck.util;

import org.apache.commons.io.input.NullInputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for ResourceUtil.
 *
 * @author Michael Krotscheck
 */
public final class ResourceUtilTest {

    /**
     * Assert that we can resolve a path in the resource directory.
     */
    @Test
    public void testGetPathForResource() {
        String name = "/valid-resource-file.txt";
        String path = ResourceUtil.getPathForResource(name);

        Assert.assertTrue(path.length() > 0);
        Assert.assertTrue(path.indexOf(name) > 0);

        String invalidName = "/invalid-resource-file.txt";
        String invalidPath = ResourceUtil.getPathForResource(invalidName);

        Assert.assertTrue(invalidPath.length() > 0);
        Assert.assertTrue(invalidPath.indexOf(invalidName) > 0);
    }

    /**
     * Assert that we can convert a resource path to a file.
     */
    @Test
    public void testGetFileForResource() {
        String name = "/valid-resource-file.txt";
        File resource = ResourceUtil.getFileForResource(name);

        Assert.assertEquals("valid-resource-file.txt", resource.getName());
        Assert.assertTrue(resource.exists());
        Assert.assertTrue(resource.isFile());

        String invalidName = "/invalid-resource-file.txt";
        File invalidResource = ResourceUtil.getFileForResource(invalidName);

        Assert.assertEquals("invalid-resource-file.txt",
                invalidResource.getName());
        Assert.assertFalse(invalidResource.exists());
        Assert.assertFalse(invalidResource.isFile());
    }

    /**
     * Assert that we can read a resource as a string.
     *
     * @throws java.lang.Exception Should not be thrown.
     */
    @Test
    public void testGetResourceAsString() throws Exception {
        String name = "/valid-resource-file.txt";
        String content = ResourceUtil.getResourceAsString(name);

        Assert.assertEquals("valid resource content", content);

        String invalidName = "/invalid-resource-file.txt";
        String invalidContent = ResourceUtil.getResourceAsString(invalidName);

        Assert.assertEquals("", invalidContent);


        // Make the file write only
        File resource = ResourceUtil.getFileForResource(name);
        Set<PosixFilePermission> oldPerms = Files
                .getPosixFilePermissions(resource.toPath());
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        perms.add(PosixFilePermission.OTHERS_WRITE);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);

        // Write only...
        Files.setPosixFilePermissions(resource.toPath(), perms);
        String writeOnlyName = "/valid-resource-file.txt";
        String writeOnlyContent = ResourceUtil
                .getResourceAsString(writeOnlyName);
        Assert.assertEquals("", writeOnlyContent);

        Files.setPosixFilePermissions(resource.toPath(), oldPerms);

    }

    /**
     * Assert that we can read a resource as a stream.
     *
     * @throws java.lang.Exception Should not be thrown.
     */
    @Test
    public void testGetResourceAsStream() throws Exception {
        String name = "/valid-resource-file.txt";
        InputStream stream = ResourceUtil.getResourceAsStream(name);
        Assert.assertTrue(stream.available() > 0);

        String invalidName = "/invalid-resource-file.txt";
        InputStream invalidContent = ResourceUtil
                .getResourceAsStream(invalidName);

        Assert.assertTrue(invalidContent instanceof NullInputStream);
        Assert.assertTrue(invalidContent.available() == 0);

        // Make the file write only
        File resource = ResourceUtil.getFileForResource(name);
        Set<PosixFilePermission> oldPerms = Files
                .getPosixFilePermissions(resource.toPath());
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        perms.add(PosixFilePermission.OTHERS_WRITE);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);

        // Write only...
        Files.setPosixFilePermissions(resource.toPath(), perms);
        String writeOnlyName = "/valid-resource-file.txt";
        InputStream writeOnlyContent = ResourceUtil
                .getResourceAsStream(writeOnlyName);
        Assert.assertTrue(writeOnlyContent instanceof NullInputStream);
        Assert.assertTrue(writeOnlyContent.available() == 0);

        Files.setPosixFilePermissions(resource.toPath(), oldPerms);
    }

    /**
     * Ensure the constructor is private.
     *
     * @throws java.lang.Exception Tests throw exceptions.
     */
    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<ResourceUtil> constructor = ResourceUtil.class
                .getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        // Override the private constructor and create an instance
        constructor.setAccessible(true);
        ResourceUtil util = constructor.newInstance();
        Assert.assertNotNull(util);
    }
}
