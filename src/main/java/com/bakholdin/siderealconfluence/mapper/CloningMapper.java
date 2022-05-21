package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.entity.Resources;
import org.mapstruct.Mapper;
import org.mapstruct.control.DeepClone;

@Mapper(componentModel = "spring", mappingControl = DeepClone.class)
public interface CloningMapper {

    Resources clone(Resources in);

}