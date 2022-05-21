package com.bakholdin.siderealconfluence.mapper;

import com.bakholdin.siderealconfluence.dto.RaceDto;
import com.bakholdin.siderealconfluence.entity.Race;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RaceMapper {
    RaceDto toDto(Race race);

    List<RaceDto> toDto(List<Race> raceList);
}
