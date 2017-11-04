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

package com.bc.jpa.query;

import com.bc.jpa.context.JpaContext;
import com.idisc.pu.entities.Feed;
import org.junit.Test;
import com.bc.jpa.dao.Select;
import com.bc.jpa.dao.Update;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 18, 2016 11:08:27 AM
 */
public class UpdateTest extends TestBaseForJpaQuery {

    public UpdateTest(String testName) {
        super(testName);
    }

    @Test
    public void test() {
        
        final JpaContext jpaContext = this.getIdiscJpaContext();
        
        final Integer ID = 527501;
System.out.println("ID: "+ID); 

        int updateCount = jpaContext.getDaoForUpdate(Feed.class)
                    .begin()
                    .where(Feed.class, "feedid", ID).set("extradetails", "JESUS IS LORD")
                    .finish();
System.out.println("Update count: "+updateCount);            
        
        try(Select<String> b = jpaContext.getDaoForSelect(String.class)) {

            final String extraDetails = b.getCriteria()
                    .where("feedid", ID)
                    .select("extradetails").createQuery().getSingleResult();
System.out.println("Extradetails: "+extraDetails);            
        }

        try(Update<Feed> b = jpaContext.getDaoForUpdate(Feed.class)) {

            updateCount = b.begin()
                    .where(Feed.class, "feedid", ID).set("extradetails", null)
                    .finish();
System.out.println("Update count: "+updateCount);            
        }

        try(Select<String> b = jpaContext.getDaoForSelect(String.class)) {

            final String extraDetails = b
                    .where("feedid", ID)
                    .select("extradetails").createQuery().getSingleResult();
System.out.println("Extradetails: "+extraDetails);            
        }
    }
}
