package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.dto.ResourcesDto;
import com.bakholdin.siderealconfluence.entity.Resources;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResourcesMapper {
    ResourcesDto toDto(Resources resources);
}
