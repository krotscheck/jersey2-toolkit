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

package net.krotscheck.jersey2.http;

import net.krotscheck.jersey2.http.header.AcceptsHeader;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * This feature builds on HK2 and the apache commons http library to provide
 * injectors for common elements of the HTTP request environment. For instance,
 * rather than manually parsing raw header strings, you may simply inject that
 * header into your request context.
 *
 * @author Michael Krotscheck
 */
public final class HttpFeature implements Feature {

    /**
     * Inject our component factories.
     *
     * @param context Our application context.
     * @return Always returns true.
     */
    @Override
    public boolean configure(final FeatureContext context) {

        context.register(new AcceptsHeader.Binder());
        return true;
    }
}
