package com.bakholdin.siderealconfluence.entity;

import com.bakholdin.siderealconfluence.enums.GamePhase;
import com.bakholdin.siderealconfluence.enums.GameState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameState state = GameState.Lobby;

    @Enumerated(EnumType.STRING)
    private GamePhase phase;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Set<User> users = new HashSet<>();
}
