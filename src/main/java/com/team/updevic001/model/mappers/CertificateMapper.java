package com.team.updevic001.model.mappers;


import com.team.updevic001.dao.entities.CertificateEntity;
import com.team.updevic001.model.dtos.certificate.CertificateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CertificateMapper {
    CertificateDto toDto(CertificateEntity entity);

    CertificateEntity toEntity(CertificateDto dto);

    void updateEntityFromDto(CertificateDto dto, @MappingTarget CertificateEntity entity);
}
