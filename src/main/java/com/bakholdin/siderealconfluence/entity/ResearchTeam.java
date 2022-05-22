package com.bakholdin.siderealconfluence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResearchTeam {
    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;
    private String name;

    private Integer era;
    private Integer points;
    private String resultingTechnology;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Resources researchOptions;
}
