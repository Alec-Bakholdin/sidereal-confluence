package com.bakholdin.siderealconfluence.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Resources implements Serializable {
    private int green;
    private int white;
    private int brown;

    private int black;
    private int yellow;
    private int blue;

    private int octagon;
    private int ships;
    private int points;

    public void add(Resources resources) {
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

    public void subtract(Resources resources) {
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

    public boolean canPayFor(Resources cost) {
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

    public int resourceTotal() {
        return green + white + brown + black + yellow + blue + octagon + ships + points;
    }
}
