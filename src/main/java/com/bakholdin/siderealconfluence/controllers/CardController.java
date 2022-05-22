package com.bakholdin.siderealconfluence.controllers;

import com.bakholdin.siderealconfluence.dto.CardDto;
import com.bakholdin.siderealconfluence.entity.Card;
import com.bakholdin.siderealconfluence.mapper.CardMapper;
import com.bakholdin.siderealconfluence.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @GetMapping
    public Set<CardDto> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return cardMapper.toDto(cards);
    }
}
