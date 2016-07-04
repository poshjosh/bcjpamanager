package com.bc.jpa.fk;

import java.util.Map;

/**
 * @(#)Keywords.java   31-May-2014 12:10:44
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 * @deprecated
 */
@Deprecated
public interface Keywords {
    
    /**
     * @deprecated
     */
    @Deprecated
    void reset();
   
    /**
     * @deprecated
     */
    @Deprecated
    Integer findMatchingKey(String column, String value, boolean wholeWords);
    
    /**
     * @deprecated
     */
    @Deprecated
    Map<String, Integer> extractKeys(String text, boolean wholeWords);

    /**
     * @deprecated
     */
    @Deprecated
    String getRemainderText();
    
    /**
     * @deprecated
     */
    @Deprecated
    boolean isSplitWords();

    /**
     * @deprecated
     */
    @Deprecated
    void setSplitWords(boolean splitWords);

    /**
     * @deprecated
     */
    @Deprecated
    String getDatabaseName();

    /**
     * @deprecated
     */
    @Deprecated
    void setDatabaseName(String databaseName);

    /**
     * @deprecated
     */
    @Deprecated
    String getTableName();

    /**
     * @deprecated
     */
    @Deprecated
    void setTableName(String tableName);
}
