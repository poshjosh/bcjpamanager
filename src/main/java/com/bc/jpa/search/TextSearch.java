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

package com.bc.jpa.search;

import com.bc.jpa.dao.Criteria;
import java.util.List;
import java.util.function.Function;
import javax.persistence.Query;

/**
 * @author Chinomso Bassey Ikwuagwu on May 20, 2017 4:15:21 AM
 */
public interface TextSearch {

    /**
     * @return The list of entities matching the text that was searched
     * @see #search(java.lang.Class, java.lang.Object, int, float, java.util.function.Function) 
     */
    default <T> List<T> search(Class<T> entityType, Object value, int minimumParts, float factor) {
        return this.search(entityType, value, minimumParts, factor, (query) -> query);
    }
    
    /**
     * <p>Part count of the input value is reduced by the supplied factor before
 being used as searchEntity text</p>
     * <table>
     *   <tr>
     *     <th>Value</th><th>Minimum parts</th><th>Factor</th><th>Actual text to find</th>
     *   </tr>
     *   <tr>
     *     <td>SOME FAKE VALUE</td><td>2</td><td>0.67</td><td>SOME FAKE</td>
     *   </tr>
     *   <tr>
     *     <td>SOME FAKE VALUE</td><td>3</td><td>0.67</td><td>SOME FAKE VALUE</td>
     *   </tr>
     *   <tr>
     *     <td>SOME FAKE VALUE I COOKED UP</td><td>4</td><td>0.67</td><td>SOME FAKE VALUE I</td>
     *   </tr>
     * </table>
     * @param <T> The generic type of the entity 
     * @param entityType The type of the entity
     * @param value The value to be resolved into the text to be searched for
     * @param minimumParts The minimum parts the input value must have to be eligible for reduction
     * @param factor The factor by which part count of the input value is reduced.
     * @param queryFormatter A function to format the query before it is executed
     * @return The list of entities matching the text that was searched
     * @see #getSearchText(java.lang.Object, float) 
     * @see #search(java.lang.Class, java.lang.String) 
     */
    <T> List<T> search(Class<T> entityType, Object value, int minimumParts, 
            float factor, Function<Query, Query> queryFormatter);

    /**
     * @return The list of entities matching the text that was searched
     * @see #search(java.lang.Class, java.lang.String, com.bc.jpa.dao.Criteria.ComparisonOperator, java.util.function.Function) 
     */
    default <T> List<T> search(Class<T> entityType, String text) {
        return this.search(entityType, text, (query) -> query);
    }
    
    /**
     * Search all <code>text type</code> columns of the specified entity type
     * for the specified text using the SQL <code>LIKE</code> keyword.
     * @param <T> The generic type of the entity 
     * @param entityType The type of the entity
     * @param text The text to searchEntity for
     * @param queryFormatter A function to format the query before it is executed
     * @return The list of entities matching the text that was searched
     */
    <T> List<T> search(Class<T> entityType, String text, Function<Query, Query> queryFormatter);
    
    /**
     * @return The list of entities matching the text that was searched
     * @see #search(java.lang.Class, java.lang.String, com.bc.jpa.dao.Criteria.ComparisonOperator, java.util.function.Function) 
     */
    default <T> List<T> search(Class<T> entityType, String text, Criteria.ComparisonOperator c) {
        return this.search(entityType, text, c, (query) -> query);
    }
    
    /**
     * Search all <code>text type</code> columns of the specified entity type
     * for the specified text.
     * @param <T> The generic type of the entity 
     * @param entityType The type of the entity
     * @param text The text to searchEntity for
     * @param c The SQL comparison operator e.g {@link com.bc.jpa.dao.Criteria.ComparisonOperator#EQUALS EQUALS}
     * OR {@link com.bc.jpa.dao.Criteria.ComparisonOperator#LIKE LIKE} etc
     * @param queryFormatter A function to format the query before it is executed
     * @return The list of entities matching the text that was searched
     */
    <T> List<T> search(Class<T> entityType, String text, 
            Criteria.ComparisonOperator c, Function<Query, Query> queryFormatter);

    boolean searchEntity(Object searchIn, Object searchFor, boolean textTypesOnly);
    
    String getSearchText(Object value, float factor);
    
    List<String> getParts(Object value);
}
