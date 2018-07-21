package com.bc.jpa.exceptions;

import java.util.List;

public class IllegalOrphanException extends Exception {
    
    private final List<String> messages;
    
    public IllegalOrphanException(List<String> messages) {
        super(condense(messages, "\n"));
        this.messages = messages;
    }
    
    public static final String condense(List<String> messages, String separator) {
        if(messages == null || messages.isEmpty()) {
            return null;
        }else{
            StringBuilder builder = new StringBuilder();
            for(String message:messages) {
                builder.append(message);
                if(separator != null) {
                    builder.append(separator);
                }
            }
            return builder.toString();
        }
    }
    
    public List<String> getMessages() {
        return messages;
    }
}
