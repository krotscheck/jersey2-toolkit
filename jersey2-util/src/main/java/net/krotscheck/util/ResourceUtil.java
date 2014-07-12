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


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.NullInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * A utility class that simplifies reading resource files.
 *
 * @author Michael Krotscheck
 */
public final class ResourceUtil {

    /**
     * Private constructor - utility class only.
     */
    private ResourceUtil() {
    }

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(ResourceUtil.class);

    /**
     * Convert a resource path to an absolute file path.
     *
     * @param resourcePath The resource-relative path to resolve.
     * @return The absolute path to this resource.
     */
    public static String getPathForResource(final String resourcePath) {
        URL path = ResourceUtil.class.getResource(resourcePath);
        if (path == null) {
            path = ResourceUtil.class.getResource("/");
            File tmpFile = new File(path.getPath(), resourcePath);
            return tmpFile.getPath();
        }

        return path.getPath();
    }

    /**
     * Convert a resource path to a File object.
     *
     * @param resourcePath The resource-relative path to resolve.
     * @return A file instance representing the resource in question.
     */
    public static File getFileForResource(final String resourcePath) {
        URL path = ResourceUtil.class.getResource(resourcePath);
        if (path == null) {
            path = ResourceUtil.class.getResource("/");
            return new File(new File(path.getPath()), resourcePath);
        }

        return new File(path.getPath());
    }

    /**
     * Read the resource found at a specific path into a string.
     *
     * @param resourcePath The path to the resource.
     * @return The resource as a string, or an empty string if the resource was
     * not found.
     */
    public static String getResourceAsString(final String resourcePath) {
        File resource = getFileForResource(resourcePath);

        if (!resource.exists() || resource.isDirectory()) {
            logger.error("Cannot read resource, does not exist"
                    + " (or is a directory).");
            return "";
        }

        try {
            return FileUtils.readFileToString(resource);
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
            return "";
        }
    }

    /**
     * Read the resource found at the given path into a stream.
     *
     * @param resourcePath The path to the resource.
     * @return The resource as an input stream. If the resource was not found,
     * this will be the NullInputStream.
     */
    public static InputStream getResourceAsStream(final String resourcePath) {
        File resource = getFileForResource(resourcePath);

        if (!resource.exists() || resource.isDirectory()) {
            logger.error("Cannot read resource, does not exist"
                    + " (or is a directory).");
            return new NullInputStream(0);
        }

        try {
            return new FileInputStream(resource);
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
            return new NullInputStream(0);
        }
    }
}
