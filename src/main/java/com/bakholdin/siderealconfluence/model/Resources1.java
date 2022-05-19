package com.bakholdin.siderealconfluence.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Resources1 implements Serializable {
    private int green;
    private int white;
    private int brown;

    private int black;
    private int yellow;
    private int blue;

    private int octagon;
    private int ships;
    private int points;

    public void add(Resources1 resources) {
        if (resources == null) return;

        this.green += resources.getGreen();
        this.white += resources.getWhite();
        this.brown += resources.getBrown();

        this.black += resources.getBlack();
        this.yellow += resources.getYellow();
        this.blue += resources.getBlue();

        this.octagon += resources.getOctagon();
        this.ships += resources.getShips();
        this.points += resources.getPoints();
    }

    public void subtract(Resources1 resources) {
        if (resources == null) return;

        this.green -= resources.getGreen();
        this.white -= resources.getWhite();
        this.brown -= resources.getBrown();

        this.black -= resources.getBlack();
        this.yellow -= resources.getYellow();
        this.blue -= resources.getBlue();

        this.octagon -= resources.getOctagon();
        this.ships -= resources.getShips();
        this.points -= resources.getPoints();
    }

    public boolean canPayFor(Resources1 cost) {
        if (cost == null) return true;

        return this.green >= cost.getGreen() &&
                this.white >= cost.getWhite() &&
                this.brown >= cost.getBrown() &&

                this.black >= cost.getBlack() &&
                this.yellow >= cost.getYellow() &&
                this.blue >= cost.getBlue() &&

                this.octagon >= cost.getOctagon() &&
                this.ships >= cost.getShips() &&
                this.points >= cost.getPoints();
    }


    /**
     * @param parent resources containing this set
     * @return true if this is a combination of the parent's resources, false otherwise
     * For example, {1 green} is an option of {1 green, 1 white} but not {1 black, 1 octagon}
     */
    public boolean isAnOptionOf(Resources1 parent) {
        if (parent == null) return false;

        return this.green == parent.getGreen() ||
                this.white == parent.getWhite() ||
                this.brown == parent.getBrown() ||

                this.black == parent.getBlack() ||
                this.yellow == parent.getYellow() ||
                this.blue == parent.getBlue() ||

                this.octagon == parent.getOctagon() ||
                this.ships == parent.getShips() ||
                this.points == parent.getPoints();
    }

    public int resourceTotal() {
        return green + white + brown + black + yellow + blue + octagon + ships + points;
    }
}
