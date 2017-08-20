package dusttunnel;

import oscP5.OscMessage;

import java.util.ArrayList;

public class MuseModel {
    /**
     * the pattern to always check for when getting values
     * example: "muse/elements/alpha_absolute"
     */
    private String _addressPattern;

    // max received so far
    private float _max;

    // min received so far
    private float _min;

    // last received value
    private float _last;

    private float MAX_VALUE_DIFF_ALLOWED = 0.2f;

    /**
     * set the signal pattern for this object
     * @param addressPattern for getting correct values
     */
    public MuseModel(String addressPattern) {
        _addressPattern = addressPattern;
    }

    public String getAddressPattern() {
        return _addressPattern;
    }

    private float getMax() {
        return _max;
    }

    private void setMax(float value) {
        _max = value;
    }

    private void updateMax(float value) {
        if (value > getMax()) {
            setMax(value);
        }
    }

    private float getMin() {
        return _min;
    }

    private void setMin(float value) {
        _min = value;
    }

    private void updateMin(float value) {
        if (value < getMin()) {
            setMin(value);
        }
    }

    private float getLast() {
        return _last;
    }

    private void setLast(float value) {
        _last = value;
    }

    private void updateLast(float value) {
        setLast(value);
    }

    private void update(float value) {
        updateLast(value);
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
        float value = smooth(sanitize(values));
        update(value);
        return value;
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
     * drop outlier values then add smoothing and avg the values
     * @param values values to sanitize
     * @return float
     */
    private float sanitize(float[] values) {
        float accum = 0;

        for (int i = 0; i < values.length; i++) {
            if (values[i] <= 0) {
                accum += 0;
            } else if (values[i] >= 1) {
                accum += 1;
            } else {
                accum += values[i];
            }
        }

        return accum / values.length;
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
