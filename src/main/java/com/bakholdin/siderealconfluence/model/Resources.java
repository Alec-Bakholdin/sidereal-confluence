package com.bakholdin.siderealconfluence.model;

import lombok.Data;

@Data
public class Resources {
    private int greenCubes;
    private int whiteCubes;
    private int brownCubes;

    private int blackCubes;
    private int yellowCubes;
    private int blueCubes;

    private int octagons;

    private int points;

    public boolean hasMoreThan(Resources resources) {
        return  this.greenCubes     >= resources.getGreenCubes() &&
                this.whiteCubes     >= resources.getWhiteCubes() &&
                this.brownCubes     >= resources.getBrownCubes() &&
                this.blackCubes     >= resources.getBlackCubes() &&
                this.yellowCubes    >= resources.getYellowCubes() &&
                this.blueCubes      >= resources.getBlueCubes() &&
                this.octagons       >= resources.getOctagons() &&
                this.points         >= resources.getPoints();
    }

    public boolean add(Resources resources) {
        this.greenCubes     += resources.getGreenCubes();
        this.whiteCubes     += resources.getWhiteCubes();
        this.brownCubes     += resources.getBrownCubes();
        this.blackCubes     += resources.getBlackCubes();
        this.yellowCubes    += resources.getYellowCubes();
        this.blueCubes      += resources.getBlueCubes();
        this.octagons       += resources.getOctagons();
        this.points         += resources.getPoints();
        return true;
    }

    public boolean subtract(Resources resources) {
        if(!hasMoreThan(resources)) {
            return false;
        }
        this.greenCubes     -= resources.getGreenCubes();
        this.whiteCubes     -= resources.getWhiteCubes();
        this.brownCubes     -= resources.getBrownCubes();
        this.blackCubes     -= resources.getBlackCubes();
        this.yellowCubes    -= resources.getYellowCubes();
        this.blueCubes      -= resources.getBlueCubes();
        this.octagons       -= resources.getOctagons();
        this.points         -= resources.getPoints();
        return true;
    }
}
