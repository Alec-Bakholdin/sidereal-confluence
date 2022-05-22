package com.bakholdin.siderealconfluence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.UUID;


@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ActiveCard {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;
    private Boolean upgraded;

    @ManyToOne
    @JoinColumn
    private Card card;
}
