package dbdr.domain.careworker.dto;

import dbdr.domain.careworker.dto.request.CareworkerRequest;
import dbdr.domain.careworker.dto.response.CareworkerResponse;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.institution.service.InstitutionService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CareworkerMapper {
    @Autowired
    private InstitutionService institutionService;

    @Mappings({
            @Mapping(target = "institutionId", source = "institution.id"),
            @Mapping(target = "id", source = "id")})
    public abstract CareworkerResponse toResponse(Careworker careworker);

    @Mapping(target = "institution", source = "institutionId")
    public abstract Careworker toEntity(CareworkerRequest request);

    protected Institution mapInstitution(Long institutionId) {
        return institutionService.getInstitutionById(institutionId);
    }


}