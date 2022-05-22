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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ConverterCard {
    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;
    private Integer era;
    private Boolean starting;
    private String upgradeTech1;
    private String upgradeTech2;
    @Enumerated(EnumType.STRING)
    private RaceType race;

    private String frontName;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn
    private Set<Converter> frontConverters;

    private String backName;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn
    private Set<Converter> backConverters;

}
