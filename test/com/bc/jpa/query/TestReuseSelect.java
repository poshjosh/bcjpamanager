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

import com.bc.jpa.dao.BuilderForSelect;
import com.bc.jpa.JpaContext;
import com.bc.jpa.dao.BuilderForDelete;
import com.idisc.pu.References;
import com.idisc.pu.entities.Emailstatus;
import com.idisc.pu.entities.Extractedemail;
import com.idisc.pu.entities.Installation;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.persistence.NoResultException;
import org.junit.Test;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 19, 2016 9:21:10 AM
 */
public class TestReuseSelect extends TestBaseForJpaQuery {

  public TestReuseSelect(String testName) {
    super(testName);
  }

  @Test
  public void test() throws Exception {
      
    Map<String, String> emailsAndUsernames = new HashMap();
    emailsAndUsernames.put("abc@def.com", "abc");
    emailsAndUsernames.put("123@456.com", "123");
    
    JpaContext jpaContext = this.getIdiscJpaContext();
    
    final Installation installation = jpaContext.getEntityManager(Installation.class).find(Installation.class, 2);
    
    final List<Extractedemail> created = new LinkedList();
    
    try (com.bc.jpa.dao.BuilderForSelect<Extractedemail> select = jpaContext.getBuilderForSelect(Extractedemail.class)){
       
      System.out.println("Adding "+emailsAndUsernames.size()+" contacts");
      
      final Emailstatus emailstatus = (Emailstatus)jpaContext.getEnumReferences().getEntity(References.emailstatus.unverified);
      
      select.begin();
      
      for (Object email : emailsAndUsernames.keySet()) {
          
        if (email != null) {

          Object username = emailsAndUsernames.get(email);
          
          select.reset();

          List<Extractedemail> foundList = select
                  .where(Extractedemail.class, "emailAddress", email)
                  .createQuery().setMaxResults(1).getResultList();
          
          Extractedemail found = foundList == null || foundList.isEmpty() ? null : foundList.get(0);
          
          if (found == null) {
              
            found = new Extractedemail();
            
            found.setEmailstatus(emailstatus);
            found.setDatecreated(new Date());
            found.setEmailAddress(email.toString());
            found.setInstallationid(installation);
            found.setUsername(username == null ? null : username.toString());
            
            select.persist(found); 
            
            created.add(found);
          }
        }
      }
      
      select.commit();
      
System.out.println("============================== Created: "+created);
    }
    
    try(BuilderForDelete<Extractedemail> dao = jpaContext.getBuilderForDelete(Extractedemail.class)) {
        
      dao.begin();
        
      for (Object email : emailsAndUsernames.keySet()) {
          
        if (email != null) {
            
System.out.println("============================== Removing: "+email);        

//            dao.remove(created.get(i));    //Entity must be managed to get remove

          dao.reset();
          
          final int updateCount = dao
                  .where(Extractedemail.class, "emailAddress", email)
                  .executeUpdate();
System.out.println("============================== Update count: "+updateCount);        
        }
      }  
        
      dao.commit();
    }

    BuilderForSelect<Extractedemail> select = jpaContext.getBuilderForSelect(Extractedemail.class);
    
    for (Object email : emailsAndUsernames.keySet()) {
          
      if (email != null) {

        select.reset();
          
        try{
          Extractedemail found = select
                  .where(Extractedemail.class, "emailAddress", email)
                  .createQuery().setMaxResults(1).getSingleResult();
System.out.println("============================== Even after deleting, still found: "+found);              
        }catch(NoResultException indicatesSuccess) {
            
        }
      }
    }  
  }
}
