package com.bakholdin.siderealconfluence.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Resources {
    private int green;
    private int white;
    private int brown;

    private int black;
    private int yellow;
    private int blue;

    private int octagon;

    private int points;

    public boolean hasMoreThan(Resources resources) {
        return this.green >= resources.getGreen() &&
                this.white >= resources.getWhite() &&
                this.brown >= resources.getBrown() &&
                this.black >= resources.getBlack() &&
                this.yellow >= resources.getYellow() &&
                this.blue >= resources.getBlue() &&
                this.octagon >= resources.getOctagon() &&
                this.points >= resources.getPoints();
    }

    public boolean add(Resources resources) {
        this.green += resources.getGreen();
        this.white += resources.getWhite();
        this.brown += resources.getBrown();
        this.black += resources.getBlack();
        this.yellow += resources.getYellow();
        this.blue += resources.getBlue();
        this.octagon += resources.getOctagon();
        this.points += resources.getPoints();
        return true;
    }

    public boolean subtract(Resources resources) {
        if (!hasMoreThan(resources)) {
            return false;
        }
        this.green -= resources.getGreen();
        this.white -= resources.getWhite();
        this.brown -= resources.getBrown();
        this.black -= resources.getBlack();
        this.yellow -= resources.getYellow();
        this.blue -= resources.getBlue();
        this.octagon -= resources.getOctagon();
        this.points -= resources.getPoints();
        return true;
    }
}
