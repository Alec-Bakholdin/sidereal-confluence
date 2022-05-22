package com.bakholdin.siderealconfluence.entity;

import com.bakholdin.siderealconfluence.enums.ColonyType;
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
public class Colony {
    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;
    private String name;

    @Enumerated(EnumType.STRING)
    private ColonyType frontType;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Converter frontConverter;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Converter upgradeConverter;

    @Enumerated(EnumType.STRING)
    private ColonyType backType;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Converter backConverter;
}
