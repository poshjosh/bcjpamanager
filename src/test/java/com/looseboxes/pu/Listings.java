/**
 * @(#)Listings.java   08-Apr-2010 12:37:54
 *
 * Copyright 2009 BC Enterprise, Inc. All rights reserved.
 * BCE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.looseboxes.pu;

import java.util.Map;

/**
 * Listins are/contains columns for which we want to keep an index.
 */
public interface Listings {

    /**
     * @param joinColName The join column name (e.g <tt>availabilityid</tt> or <tt>productstatusid</tt>)
     * @param joinColValue Example for a join column name of <tt>availabilityid</tt> would be <tt>InStock</tt> or <tt>1</tt>
     * @return The updated values
     */
    Map<Enum, Integer> decrement(String joinColName, Object joinColValue);
    
    /**
     * @param params
     * @return The updated values
     */
    Map<String, Map<Enum, Integer>> decrement(Map params);

    /**
     * @param joinColName The join ccolumn name (e.g <tt>availabilityid</tt> or <tt>productstatusid</tt>)
     * @param joinColValue Example for a join column name of <tt>availabilityid</tt> would be <tt>InStock</tt> or <tt>1</tt>
     * @return The updated values
     */
    Map<Enum, Integer> increment(String joinColName, Object joinColValue);
    
    /**
     * @param params
     * @return The updated values
     */
    Map<String, Map<Enum, Integer>> increment(Map params);

    /**
     * <b><u>Some Examples</u>:</b><br/>
     * <code>
     * {productstatus={new=[n],used=[n],refurbished=[n]}}
     * {availability={InStock=[n],LimitedAvailability=[n]}}
     * Where [n] is the id, usually an <tt>int</tt>
     * </code>
     * @author  chinomso bassey ikwuagwu
     * @version 1.0
     * @return 
     * @since   1.0
     */
    Map<String, Map<Enum, Integer>> getValues();
            
}//~END
