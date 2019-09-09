package edu.bcm.dldcc.big.nursa.model.omics.dto;

import java.util.Objects;

public class FoldChangeMinMax {

     private double min=0D;
     private double max=0D;

    public FoldChangeMinMax() {
    }

    public FoldChangeMinMax(double min, double max) {
        this.min = min;
        this.max = max;
    }


    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoldChangeMinMax that = (FoldChangeMinMax) o;
        return Objects.equals(min, that.min) &&
                Objects.equals(max, that.max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }
}
