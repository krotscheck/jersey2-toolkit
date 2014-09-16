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

package net.krotscheck.test;

/**
 * A marker interface used for JUnit categorization.
 * <p/>
 * The junit category runner supports inclusion and exclusion of specific test
 * via the <code>@Category(IntegrationTest.class)</code>. By using this
 * annotation, you can then use the 'groups' and 'excludedGroups' parameters in
 * the maven-surefire-plugin to specify which tests should be run during which
 * section of the build.
 * <p/>
 * Only an IntegrationTest interface is provided. Anything that is not an
 * integration test is assumed to be a unit test.
 *
 * @author Michael Krotscheck
 */
public interface IntegrationTest {

}
