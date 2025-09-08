package com.team.updevic001.model.dtos.certificate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CertificateViewPageResponse {
    List<CertificateViewDto> views;
    private int lastPageNumber;
    private long totalElements;
    private boolean hasNext;
}