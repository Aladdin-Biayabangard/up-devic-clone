package com.team.updevic001.model.dtos.request;

import com.team.updevic001.model.enums.RecipientsGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailDraftRequest {
    private String subject;
    private String message;
    private Set<String> recipients;
    private RecipientsGroup recipientsGroup;
}
