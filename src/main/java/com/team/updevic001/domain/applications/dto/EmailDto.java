package com.team.updevic001.domain.applications.dto;

import com.team.updevic001.mail.EmailTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {

    private String to;
    private String subject;
    private String from;
    private Map<String, String> variables;
    private EmailTemplate template;
}
