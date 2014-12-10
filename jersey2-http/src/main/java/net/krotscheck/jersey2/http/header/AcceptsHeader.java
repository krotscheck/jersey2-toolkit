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

import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;

/**
 * The factory converts the raw HTTP Accept header into a BasicHeader collection
 * of individual header elements, which are sorted based on priority. It is
 * injected into the context as a Named entity using the header as its name.
 *
 * @author Michael Krotscheck
 */
public final class AcceptsHeader extends BasicHeader {

    /**
     * Construct a new instance of the header parameters.
     *
     * @param value The injected content of the Accept: header.
     */
    @Inject
    protected AcceptsHeader(@HeaderParam(HttpHeaders.ACCEPT)
                            @DefaultValue("")
                            final String value) {
        super(HttpHeaders.ACCEPT, value);
    }

    /**
     * HK2 Binder for our injector context.
     */
    public static final class Binder extends AbstractBinder {

        @Override
        protected void configure() {
            bind(AcceptsHeader.class)
                    .to(AcceptsHeader.class)
                    .to(BasicHeader.class)
                    .named(HttpHeaders.ACCEPT)
                    .in(RequestScoped.class);
        }
    }
}
