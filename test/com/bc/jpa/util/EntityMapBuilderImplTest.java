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

import com.bc.jpa.JpaContext;
import com.bc.jpa.TestApp;
import com.bc.jpa.dao.BuilderForSelect;
import com.bc.util.JsonFormat;
import com.idisc.pu.entities.Feed;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author Josh
 */
public class EntityMapBuilderImplTest extends TestCase {
    
    public EntityMapBuilderImplTest(String testName) {
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
        JpaContext jpaContext = TestApp.getInstance().getIdiscJpaContext();
        BuilderForSelect<Feed> dao = jpaContext.getBuilderForSelect(Feed.class);
        List<Feed> found = dao.descOrder(Feed.class, "feeddate").getResultsAndClose(0, 3);
        EntityMapBuilderImpl instance = new EntityMapBuilderImpl(false, 0, 0);
        for(Feed feed : found) {
            
            feed.setContent(null);
            Map map = instance.build(Feed.class, feed);
            map.remove("content");
            
            JsonFormat jsonFmt = new JsonFormat(true);

            String json = jsonFmt.toJSONString(map);
            
System.out.println(json);            
        }
    }
}
/**
 * 
    public void testBuild() {
        
        System.out.println("build");
        
        Sitetype sitetype = new Sitetype();
        sitetype.setSitetype("web");
        sitetype.setSitetypeid((short)1);
        
        Country country = new Country();
        country.setCountry("Nigeria");
        country.setCountryid((short)566);
        country.setCodeIso2("NG");
        country.setCodeIso3("NGA");
        
        Site site = new Site();
        site.setSiteid(1);
        site.setIconurl("http://www.looseboxes.com/images/appicon.jpg");
        site.setDatecreated(new Date());
        site.setSitetypeid(sitetype);
        site.setCountryid(country);
        
        EntityMapBuilderImpl instance = new EntityMapBuilderImpl();
        
        Map map = instance.build(Site.class, site);

//System.out.println(map.toString().replace(", ", "\n, "));        

        JsonFormat jsonFmt = new JsonFormat(true);
        
        String json = jsonFmt.toJSONString(map);
        
System.out.println(json);
    }
 * 
 */