package com.bakholdin.siderealconfluence.entity;

import com.bakholdin.siderealconfluence.enums.GamePhase;
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
public class Converter {
    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;
    @Enumerated(EnumType.STRING)
    private GamePhase phase;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Resources input;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Resources output;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Resources donations;

}
