/*
 * Copyright 2017 NUROX Ltd.
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

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 2:46:54 PM
 */
public class ContextClassLoaderAccessor implements Serializable {

    private transient static final Logger LOG = Logger.getLogger(ContextClassLoaderAccessor.class.getName());

    /**
     * Wraps <code>Thread.currentThread().setContextClassLoader(ClassLoader)</code> 
     * into a doPrivileged block if security manager is present
     * @param classLoader
     */
    public void set(final ClassLoader classLoader) {
        
        LOG.fine(() -> "Setting Current Thread ContextClassLoader to: " + classLoader);
        
        if (System.getSecurityManager() == null) {
            Thread.currentThread().setContextClassLoader(classLoader);
        }else {
            java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                        @Override
                        public java.lang.Object run() {
                            Thread.currentThread().setContextClassLoader(classLoader);
                            return classLoader;
                        }
                    }
            );
        }
    }

    /**
     * Wraps <code>Thread.currentThread().getContextClassLoader()</code> 
     * into a doPrivileged block if security manager is present
     * @return 
     */
    public ClassLoader get() {
        final ClassLoader classLoader;
        if (System.getSecurityManager() == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }else {
            classLoader = (ClassLoader) java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                        @Override
                        public java.lang.Object run() {
                            return Thread.currentThread().getContextClassLoader();
                        }
                    }
            );
        }
        LOG.finer(() -> "Current Thread ContextClassLoader: " + classLoader);
        return classLoader;
    }
}
