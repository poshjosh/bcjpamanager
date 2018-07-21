/*
 * Copyright 2016 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.jpa.classloaders;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 25, 2016 9:15:27 PM
 */
public class AlternativeResourceContext implements ResourceContext, Serializable {

    private transient static final Logger LOG = Logger.getLogger(AlternativeResourceContext.class.getName());

    private final Map<String, URL> replacements;
    
    public AlternativeResourceContext(Map<String, URL> replacements) {
       this.replacements = Objects.requireNonNull(replacements);
    }
    
    @Override
    public URL getResource(String name) {
        final URL output = replacements.get(name);
        if (output != null) {
            LOG.fine(() -> "For resource: "+name+", returning: "+output);
        }
        return output;
    }
    
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        final URL output = replacements.get(name);
        if (output != null) {
            LOG.fine(() -> "For resource: "+name+", returning: "+output);
        }
        return output == null ? null : Collections.enumeration(Arrays.asList(output));
    }
}
