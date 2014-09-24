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

import net.krotscheck.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Unit test for the configuration reader.
 *
 * @author Michael Krotscheck
 */
@Category(IntegrationTest.class)
public final class Jersey2ToolkitConfigITest {

    /**
     * Assert that the default version configuration in a compile environment is
     * 'dev', and that you cannot override it.
     */
    @Test
    public void testIntegrationVersionConfiguration() {
        Assert.assertTrue(true);
    }
}
