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
    protected float _max = 1;

    // min avg received so far
    protected float _min = 0;

    // last received value in range
    protected float _last;

    // for modifying noisy waves down
    protected float _valueK = 1;

    protected float MAX_VALUE_DIFF_ALLOWED = 0.2f;

    /**
     * set the signal pattern for this object
     * @param addressPattern for getting correct values
     */
    public MuseModel(String addressPattern) {
        _addressPattern = addressPattern;
    }

    public MuseModel(String addressPattern, float valueK) {
        _addressPattern = addressPattern;
        _valueK= valueK;
    }

    protected float getValueK() {
        return _valueK;
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

    protected void updateLast(float value) {
        setLast(value);
    }

    private void update(float value) {
        updateLast(value);
    }

    private void updateRange(float value) {
        updateMax(value);
        updateMin(value);
    }

    /**
     * get the value for the band after sanitation / smoothing
     * @param msg from osc event
     * @return float
     */
    public float getValue(OscMessage msg) {
        float[] values = getValues(msg);
        float averaged = getCleanAverage(values);
        updateRange(averaged);
        float value = calculateValueInRange(averaged);
        update(value);
        return value;
    }

    // (v - min) / r
    protected float calculateValueInRange(float value) {
        float max = getMax();
        float min = getMin();
        float range = max - min;
        float valueInRange = (value - min) / range;
        return valueInRange;//  * getValueK();
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


    /**
     * smooth the value jumps, split the difference
     * @param value already santized
     * @return
     */
    private float smooth(float value) {
        if (getLast() > 0.5f && Math.abs(value - getLast()) > MAX_VALUE_DIFF_ALLOWED) {
            return (getLast() - value) / 2.0f;
        }
        return value;
    }
}
