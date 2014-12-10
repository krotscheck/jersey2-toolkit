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

package net.krotscheck.jersey2.http.header;

import net.krotscheck.test.UnitTest;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHeaders;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Unit tests for the accepts header class.
 *
 * @author Michael Krotscheck
 */
@Category(UnitTest.class)
public final class AcceptsHeaderTest {

    /**
     * Assert that a basic header can be assembled.
     */
    @Test
    public void testSingleAcceptsHeader() {
        String headerValue = "application/xml";
        AcceptsHeader header = new AcceptsHeader(headerValue);

        Assert.assertEquals(HttpHeaders.ACCEPT, header.getName());
        Assert.assertEquals(headerValue, header.getValue());

        HeaderElement[] elements = header.getElements();

        Assert.assertEquals(1, elements.length);

        for (HeaderElement element : elements) {
            Assert.assertEquals(headerValue, element.getName());
            Assert.assertEquals(0, element.getParameterCount());
        }
    }

    /**
     * Assert that complex header values are properly decoded.
     */
    @Test
    public void testPrioritizedHeader() {
        String headerValue = "application/xml,"
                + " text/plain;p=0.9,"
                + " application/json";
        AcceptsHeader header = new AcceptsHeader(headerValue);

        Assert.assertEquals(HttpHeaders.ACCEPT, header.getName());
        Assert.assertEquals(headerValue, header.getValue());

        HeaderElement[] elements = header.getElements();

        Assert.assertEquals(3, elements.length);

        Assert.assertEquals("application/xml", elements[0].getName());
        Assert.assertEquals(0, elements[0].getParameterCount());

        Assert.assertEquals("text/plain", elements[1].getName());
        Assert.assertEquals(1, elements[1].getParameterCount());
        Assert.assertEquals("p", elements[1].getParameter(0).getName());
        Assert.assertEquals("0.9", elements[1].getParameter(0).getValue());

        Assert.assertEquals("application/json", elements[2].getName());
        Assert.assertEquals(0, elements[2].getParameterCount());
    }

}
