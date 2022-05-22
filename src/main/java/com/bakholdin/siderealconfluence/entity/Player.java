package com.bakholdin.siderealconfluence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Player implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Boolean ready = false;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(nullable = false)
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Resources resources;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Resources donations;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn
    private Set<ActiveCard> activeCards;

    @ManyToOne
    @JoinColumn
    private Race race;

    @ManyToOne
    private Game game;
}
