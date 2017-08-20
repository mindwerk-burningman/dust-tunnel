package dusttunnel;

import oscP5.OscMessage;

import java.util.ArrayList;

public class MuseModel {
    /**
     * the pattern to always check for when getting values
     * example: "muse/elements/alpha_absolute"
     */
    private String _addressPattern;

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

    /**
     * get the value for the band after sanitation / smoothing
     * @param msg from osc event
     * @return float
     */
    public float getValue(OscMessage msg) {
        float[] values = getValues(msg);
        float value = sanitize(values);
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
        ArrayList<Float> goodValues = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (values[i] > 0) {
                goodValues.add(values[i]);
            }
        }

        float accum = 0;
        for (Float val : goodValues) {
                accum += val;
        }
        return accum / goodValues.size();
    }
}
