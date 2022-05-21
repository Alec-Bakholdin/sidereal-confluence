package com.bakholdin.siderealconfluence.entity;

import com.bakholdin.siderealconfluence.enums.RaceType;
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
public class Race {
    @Id
    @Enumerated(EnumType.STRING)
    private RaceType name;

    private Integer colonySupport;
    private Integer tiebreaker;
    private Integer startingColonies;
    private Integer startingResearchTeams;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Resources startingResources;
}
