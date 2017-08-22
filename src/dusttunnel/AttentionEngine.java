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
    private float K = 1.25f;

    public AttentionEngine(MuseModel[] models) {
        _models = models;
    }

    protected MuseModel[] getModels() {
        return _models;
    }

    public float getLast() {
        return _last;
    }

    protected void setLast(float curr) {
        _last = curr;
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
        float value = getValue();

        setLast(value);
    }

    protected void updateRange(float value) {
        updateMax(value);
        updateMin(value);
    }

    public float getValue() {
        float value = 0;
        for (MuseModel model : getModels()) {
            value += model.getCurr(); // values in range
        }
        float averaged = value / getModels().length;
        updateRange(averaged);
        float valueInRange = calculateValueInRange(averaged);
        return valueInRange;
    }

    protected float calculateValueInRange(float value) {
        float max = getMax();
        float min = getMin();
        float range = max - min;
        float valueInRange = (value - min) / range;
        return valueInRange;
    }
}
