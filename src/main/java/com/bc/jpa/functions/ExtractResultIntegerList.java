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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 14, 2017 10:41:27 PM
 */
public class ExtractResultIntegerList implements BiFunction<ResultSet, Integer, List<Integer>> {

    @Override
    public List<Integer> apply(ResultSet resultSet, Integer dataIndex) {
        
        final List<Integer> output = new ArrayList();

        try{
            while(resultSet.next()) {

                output.add(resultSet.getInt(dataIndex));
            }
        }catch(SQLException e) {
            throw new RuntimeException(e);
        }
        
        return output.isEmpty() ? Collections.EMPTY_LIST : Collections.unmodifiableList(output);
    }
}
