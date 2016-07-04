package com.bc.jpa.fk;

import com.bc.jpa.EntityController;
import java.util.List;
import java.util.Map;

/**
 * @(#)EnumReferences.java   06-Oct-2014 10:43:16
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * A reference table is a table holding references for actual values
 * E.g a reference table for colours may hold 256 colours. The reference
 * table must have at least 2 columns one for the id, and the other for
 * the actual color value. 
 * <b>For example:</b><br/>
 * <pre>
 * Table:colour
 * -------------
 *  id | colour 
 * -------------
 *  1    red
 *  2    green
 *  3    blue
 *  4    navy blue
 * -------------
 * </pre>
 * <br/>
 * This way, in the database any table referencing the color red may simply 
 * reference the id (i.e <tt>1</tt>).
 * <br/><br/>
 * This class requires that an enumeraton be declared for each reference table
 * in the database. In the case of our colour table, the enumeration:
 * <br/><br/>
 * <pre>
 * public static interface References{
 *     public static enum color{red,green,blue,navyblue}
 *     public static enum country{canada,nigeria,germany} 
 * }
 * </pre>
 * <br/> 
 * This way, this class may be used thus:<br/>
 * <pre>
 * // Entity class Red.class
 * EnumReferences&lt;Red&gt; refs = new EnumReferences&lt;Red&gt;(){
 *     ControllerFactory factory; 
 *     public EntityController&lt;Red&gt; getControllerFactory() {
 *         if(factory == null) {
 *             factory = new DefaultControllerFactory("persistence.xml", null);
 *         }
 *         return factory;
 *     } 
 * };
 * 
 * Red red = refs.getEntity(References.color.red)
 * 
 * Object numeral_one = refs.getId(References.color.red);
 * 
 * </pre>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 * @param <E>
 */
public interface EnumReferences<E> {
    
    String getDataColumnName(Enum en);

    List<E> getEntities(Enum en);

    /**
     * Sample reference table:
     * <code><pre>
     * tablename: availability
     * --------------------------
     * | id_column | data_column |
     * ---------------------------
     * |    1      |   InStock   |
     * |    2      |   SoldOut   |
     * ---------------------------
     * </pre></code>
     * Sample Enum reference:
     * <code><pre>
     * public class References extends EnumReferences{
     *     public static enum availability{InStock, SoldOut};
     * }
     * </pre></code>
     * @param enumType
     * @return
     */
    Map<Enum, E> getEntities(Class<E> enumType);

    E getEntity(String key, Object val);

    E getEntity(Enum en);

    EntityController<E, ?> getEntityController(String tablename);

    Enum getEnum(String key, Object val);

    Enum getEnum(String key, int val);

    Class getEnumType(String column);

    Class[] getEnumTypes();

    Object getId(Enum en);

    String getIdColumnName(Enum en);

    /**
     * Sample reference table:
     * <code><pre>
     * tablename: availability
     * --------------------------
     * | id_column | data_column |
     * ---------------------------
     * |    1      |   InStock   |
     * |    2      |   SoldOut   |
     * ---------------------------
     * </pre></code>
     * Sample Enum reference:
     * <code><pre>
     * public class References extends EnumReferences{
     *     public static enum availability{InStock, SoldOut};
     * }
     * </pre></code>
     * @param enumType The type of the enum whose id mappings is to be returned.
     * @param keysNotValues If true, the values of the output Map will be
     * reference <b>id column values</b>, otherwise the values will be reference
     * <b>data column values</b>
     * @return Map with id mappings of the input enum type.
     */
    Map<Enum, Object> getIds(Class<E> enumType, boolean keysNotValues);

    /**
     * @param enumclassname The name of the enum class whose mappings will be returned
     * @return The Mappings of the enum class
     * @see #getMappings(java.lang.Enum[])
     */
    Map getMappings(String enumclassname);

    /**
     * @param enumclass The enum class whose mappings will be returned
     * @return The Mappings of the enum class
     * @see #getMappings(java.lang.Enum[])
     */
    Map getMappings(Class enumclass);

    /**
     * Returns the actual values of each row of a declared reference.
     * E.g of a declared:
     * <code><pre>
     *     public static enum productstatus{new,classic,refurbished,used};
     * </pre></code>
     * @param arr The enum values of a declared reference.
     * @return Map with each entry pair of format {'new' = 1, 'classic' = 2, 'refurbished' = 3, 'used' = 4}
     */
    Map getMappings(Enum[] arr);

    String getTableName(Class enumclass);

    String getTableName(Enum en);

    Enum[] getValues(String enumclassname);

    Enum[] getValues(Class enumclass);

    boolean isReference(String column);

    void validate();

    void validate(Class aClass);

}
