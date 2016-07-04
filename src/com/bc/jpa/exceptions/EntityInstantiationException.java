package com.bc.jpa.exceptions;

/**
 * @(#)EntityInstantiationException.java   09-May-2014 14:29:33
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
public class EntityInstantiationException extends Exception {
    
    public EntityInstantiationException() {
	super();
    }

    public EntityInstantiationException(String message) {
	super(message);
    }

    public EntityInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityInstantiationException(Throwable cause) {
        super(cause);
    }
}
