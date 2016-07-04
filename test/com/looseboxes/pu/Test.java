package com.looseboxes.pu;

import com.looseboxes.pu.entities.Address;
import com.looseboxes.pu.entities.Country;
import com.looseboxes.pu.entities.Currency;
import com.looseboxes.pu.entities.Region;
import com.looseboxes.pu.entities.Siteuser;
import com.bc.jpa.EntityController;
import com.bc.jpa.fk.EnumReferences;
import java.io.File;
import java.util.Date;


/**
 * @(#)Bcecommercepu.java   10-Apr-2015 17:35:44
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try{
            
            PuInstaller installer = new PuInstaller(){
                @Override
                protected File getFile(String fname) {
                    // In development mode the files are stored by Netbeans IDE
                    // in the src folder
                    return new File("src", fname);
                }
            };
            
            //@todo The database, username, password should be gotten from the properties file
            if(installer.isInstalled()) {
                installer.uninstall("ivyfash_db0", "ivyfash_root", "7345xT-eeSw");
            }
            
            if(!installer.isInstalled()) {
                installer.install("ivyfash_db0", "ivyfash_root", "7345xT-eeSw");
            }
            
            final LbJpaContext jpaContext = new LbJpaContext();
            
            EnumReferences refs = jpaContext.getEnumReferences();
            Country country = (Country)refs.getEntity(References.country.Nigeria);
            Region region = (Region)refs.getEntity(References.region.AbujaFederalCapitalTerritory);
            Currency currency = (Currency)refs.getEntity(References.currency.NGN);
            
            EntityController<Address, Integer> addressec = jpaContext.getEntityController(Address.class, Integer.class);
            Address address = new Address();
            address.setCity("Abuja");
            address.setCountryid(country);
            address.setCounty("Garki II");
            address.setDatecreated(new Date());
            address.setProductList(null);
            address.setRegionid(region);
            address.setSiteuserList(null);
            address.setStreetAddress("18 Yawuri Street");
            address.setUserpaymentmethodList(null);
//            addressec.create(address);
                    
            Siteuser siteuser = new Siteuser();
            siteuser.setAddressid(addressec.selectById(1));
            siteuser.setCurrencyid(currency);
            siteuser.setDatecreated(new Date());
            siteuser.setEmailAddress("posh.bc@gmail.com");
            siteuser.setFirstName("Chinomso");
//            siteuser.setGenderid(country);
            siteuser.setLastName("Ikwuagwu");
            siteuser.setUsername("Nonny");
            
            EntityController<Siteuser, Integer> siteuserec = jpaContext.getEntityController(Siteuser.class, Integer.class);
            siteuserec.create(siteuser);
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
