package ru.ballo.projects.email;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EmailContext {
    private String to;
    private String from;
    private String subject;
    private String text;
    private String template;
    private Map<String, Object> properties;
}
