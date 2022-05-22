package com.bakholdin.siderealconfluence.entity;

import com.bakholdin.siderealconfluence.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private ConverterCard converterCard;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Colony colony;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private ResearchTeam researchTeam;
}
