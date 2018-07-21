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
package com.bc.jpa.util;

import com.bc.jpa.context.JpaContext;
import com.bc.jpa.TestApp;
import com.bc.util.JsonBuilder;
import com.bc.util.MapBuilder;
import com.bc.util.MapBuilderImpl;
import com.idisc.pu.entities.Archivedfeed;
import com.idisc.pu.entities.Feed;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import com.bc.jpa.dao.Select;
import org.junit.Test;

/**
 *
 * @author Josh
 */
public class MapBuilderImplTest extends TestCase {
    
    public MapBuilderImplTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testAll() {
        
        final JpaContext jpaContext = TestApp.getInstance().getIdiscJpaContext();
        
        final Select<Feed> dao = jpaContext.getDaoForSelect(Feed.class);
        
        final List<Feed> found = dao.descOrder(Feed.class, "feeddate").getResultsAndClose(100, 3);
        
        System.out.println("Found " + found.size() + " feeds. " + found);
        
//        @todo remove and build
        if(true) {
            return;
        }
        
        final Set<Class> ignore = new HashSet(Arrays.asList(Archivedfeed.class));
        final MapBuilder mapBuilder = new MapBuilderImpl()
                .recursionFilter(new EntityRecursionFilter())
                .sourceType(Feed.class)
                .nullsAllowed(false)
                .maxCollectionSize(10)
                .typesToIgnore(ignore);
        
        for(Feed feed : found) {
            System.out.println("\nBuilding: " + feed);
            final Map map = mapBuilder.source(feed).build();
            map.put("content", "[DUMMY CONTENT]");
        
            try{
                JsonBuilder jsonBuilder = new JsonBuilder(true);
                jsonBuilder.appendJSONString(map, System.out);
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
