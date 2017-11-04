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

package com.bc.jpa.functions;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 14, 2017 10:00:07 PM
 */
public class ExtractSchemaToCatalogMappings implements MetaDataParser<Map<String, String>> {

    @Override
    public Map<String, String> apply(DatabaseMetaData dbMetaData) {
        
        final Map<String, String> output = new LinkedHashMap();
        
        try{
            
            final ResultSet rs = dbMetaData.getSchemas();
            
            while(rs.next()) {
                
                output.put(rs.getString(1), rs.getString(2));
            }
            
        }catch(SQLException e){
            
            throw new RuntimeException(e);
        }

        return output.isEmpty() ? Collections.EMPTY_MAP : Collections.unmodifiableMap(output);
    }
}
