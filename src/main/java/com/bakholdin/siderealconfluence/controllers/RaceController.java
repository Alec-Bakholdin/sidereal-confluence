package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.dto.RaceDto;
import com.bakholdin.siderealconfluence.mapper.RaceMapper;
import com.bakholdin.siderealconfluence.repository.RaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/races")
@RequiredArgsConstructor
public class RaceController {
    private final RaceRepository raceRepository;
    private final RaceMapper raceMapper;


    @GetMapping("")
    public List<RaceDto> getRaces() {
        return raceMapper.toDto(raceRepository.findAll());
    }
}
