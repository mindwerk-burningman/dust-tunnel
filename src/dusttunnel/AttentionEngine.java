package dusttunnel;

import themidibus.ControlChange;

import java.util.Timer;
import java.util.TimerTask;

public class AttentionEngine {
    private MuseModel[] _models;
    private float _last;
    private float _curr;
    private float _min = 0;
    private float _max = 1;
    private float K = 1.5f;
    private float _userValue;
    final float INC_DEC_AMOUNT = 0.00001f;
    // larger values = more limiting
    final float DIFF_LIMITER = 0.45f;

    public AttentionEngine(MuseModel[] models) {
        _models = models;
    }

    protected MuseModel[] getModels() {
        return _models;
    }

    public float getLast() {
        return _last;
    }

    protected void setLast(float value) {
        _last = value;
    }

    protected void setCurr(float value) {
        _curr = value;
    }

    protected float getCurr() {
        return _curr;
    }

    public float getMax() {
        return _max;
    }

    protected void setMax(float value) {
        _max = value;
    }

    protected void updateMax(float value) {
        if (value > getMax()) {
            setMax(value);
        }
    }

    public float getMin() {
        return _min;
    }

    protected void setMin(float value) {
        _min = value;
    }

    protected void updateMin(float value) {
        if (value < getMin()) {
            setMin(value);
        }
    }

    public void update() {
        float value = 0;
        for (MuseModel model : getModels()) {
            value += model.getUserValue();
        }
        float mean = value / getModels().length;
        updateRange(mean);
        setLast(getCurr());
        setCurr(mean);
        float userValue = getNextValue(mean);
        updateUserValue(userValue * K);
    }

    /**
     * closer it gets to max, the smaller the increment amounts
     * and vice versa
     */
    // averaged value
    private float getMaxLimiter(float value) {
        float max = getMax();
        float diff = max - value;
        return diff * DIFF_LIMITER;
    }

    // averaged value
    private float getMinLimiter(float value) {
        float min = getMin();
        float diff = value - min;
        return diff * DIFF_LIMITER;
    }

    // averaged value into display/CC values between 0 & 1
    private float getNextValue(float value) {
        float last = getLast();
        float maxLimiter = getMaxLimiter(value);
        float minLimiter = getMinLimiter(value);
        if (value > last) {
            value += INC_DEC_AMOUNT; //  * maxLimiter;
        } else {
            value -= INC_DEC_AMOUNT; // * minLimiter;
        }

        if (value >= 1) {
            value = 1;
        }

        if (value <= 0) {
            value = 0;
        }

        return value;
    }

    public void updateUserValue(float value) {
        _userValue = value;
    }

    public float getUserValue() {
        return _userValue;
    }

    protected void updateRange(float value) {
        updateMax(value);
        updateMin(value);
    }
}
