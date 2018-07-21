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

package com.bc.jpa.predicates;

import com.bc.functions.FindExceptionInHeirarchy;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.eclipse.persistence.exceptions.DatabaseException;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 9, 2017 3:11:54 PM
 */
public class DatabaseCommunicationsFailureTest 
        implements Serializable, Predicate<Throwable> {

    private transient static final Logger LOG = 
            Logger.getLogger(DatabaseCommunicationsFailureTest.class.getName());

    private final BiFunction<Throwable, Predicate<Throwable>, Optional<Throwable>> findExceptionInHeirarchy;

    private final Predicate<Throwable> persistenceCommsExceptionTest;
    
    private final Predicate<Throwable> jdbcCommsExceptionTest;
    
    public DatabaseCommunicationsFailureTest() {
        
        this.findExceptionInHeirarchy = new FindExceptionInHeirarchy();
        
        this.persistenceCommsExceptionTest = (t) -> 
                t instanceof DatabaseException && ((DatabaseException)t)
                        .isCommunicationFailure();
        
//        com.mysql.jdbc.exceptions.jdbc4.CommunicationsException
        this.jdbcCommsExceptionTest = (t) -> {
            final String name = t.getClass().getName();
            return name.contains(".jdbc.") && name.contains("CommunicationsException"); 
        };
    }
    
    @Override
    public boolean test(Throwable exception) {
        
        final boolean result = findExceptionInHeirarchy.apply(exception, this.persistenceCommsExceptionTest).isPresent()
                || findExceptionInHeirarchy.apply(exception, this.jdbcCommsExceptionTest).isPresent();
        
        LOG.finer(() -> "Is Comms Failure: " + result + ", " + exception);
        
        return result;
    }
}
