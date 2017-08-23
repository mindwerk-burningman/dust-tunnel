package dusttunnel;

import oscP5.OscMessage;

import java.util.ArrayList;

public class MuseModel {
    /**
     * the pattern to always check for when getting values
     * example: "muse/elements/alpha_absolute"
     */
    private String _addressPattern;

    // max avg received so far
    protected float _max = 0;

    // min avg received so far
    protected float _min = 0;

    // last received value in range
    protected float _last;

    // most current calculated average
    protected float _curr;

    protected float _userValue;

    // for modifying noisy waves down
    protected float K = 1.0f;

    final float INC_DEC_AMOUNT = 0.00001f;

    // larger values = more limiting
    final float DIFF_LIMITER = 0.45f;

    /**
     * set the signal pattern for this object
     * @param addressPattern for getting correct values
     */
    public MuseModel(String addressPattern) {
        _addressPattern = addressPattern;
    }

    public MuseModel(String addressPattern, float K) {
        _addressPattern = addressPattern;
        this.K = K;
    }

    public String getAddressPattern() {
        return _addressPattern;
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

    public float getLast() {
        return _last;
    }

    protected void setLast(float value) {
        _last = value;
    }

    protected float getCurr() {
        return _curr;
    }

    protected void setCurr(float value) {
        _curr = value;
    }

    public boolean isIncreasing() {
        return getCurr() > getLast();
    }

    public void update(OscMessage msg) {
        float[] values = getValues(msg);
        float averaged = getCleanAverage(values) * K;
        updateRange(averaged);
        setLast(getCurr());
        setCurr(averaged);
        float value = getNextValue(averaged);
        updateUserValue(value);
    }

    // averaged values
    private void updateRange(float value) {
        updateMax(value);
        updateMin(value);
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
            value += INC_DEC_AMOUNT * maxLimiter;
        } else {
            value -= INC_DEC_AMOUNT * minLimiter;
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

    /**
     * get raw values of muse message
     * @param msg from osc event
     * @return float[]
     */
    public float[] getValues(OscMessage msg) {
        float[] values = new float[4];
        if (msg.checkAddrPattern(getAddressPattern())) {
            for (int i = 0; i < 4; i++) {
                values[i] = msg.get(i).floatValue();
            }
        } else {
            throw new IllegalArgumentException("msg did match address pattern for this model");
        }
        return values;
    }

    public boolean isValidMsg(OscMessage msg) {
        return msg.checkAddrPattern(getAddressPattern());
    }

    /**
     * drop outlier values and avg the values
     * @param values values to average
     * @return float
     */
    protected float getCleanAverage(float[] values) {
        float accum = 0;
        int validValuesCount = 0;

        for (int i = 0; i < values.length; i++) {
            if (values[i] != 0) {
                validValuesCount++;
            }
            accum += values[i];
        }

        if (validValuesCount != 0) {
            float mean = accum / validValuesCount;
            return mean;
        }
        return 0;
    }
}
