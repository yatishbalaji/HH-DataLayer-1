package com.headhonchos.JobRcmdWeights;

/**
 * Created by aman on 29/5/14.
 */
public class WeightsCalculation implements Weights {
    @Override
    public double getAlpha1() {
        return 0.5;
    }

    @Override
    public double getAlpha2() {
        return 0.3;
    }

    @Override
    public double getAlpha3() {
        return 0.2;
    }
}
