package com.bc.jpa.util;

import java.util.Map;

/**
 * @author Josh
 */
public interface EntityMapBuilder {

    Map build(Object entity);

    Map build(Object entity, Class entityType);

    void build(Object entity, Map appendTo);

    void build(Object entity, Class entityType, Map appendTo);

    void reset();
}
