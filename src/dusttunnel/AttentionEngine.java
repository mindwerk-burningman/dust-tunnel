package dusttunnel;

import themidibus.ControlChange;

import java.util.Timer;
import java.util.TimerTask;

public class AttentionEngine {
    private MuseModel[] _models;
    private boolean _isAttentionGrowing = false;
    private float _last;
    private float _curr;
    private float _min = 0;
    private float _max = 1;
    private float _valueK = 1;

    public AttentionEngine(MuseModel[] models) {
        _models = models;
    }

    public AttentionEngine(MuseModel model) {
        _models = new MuseModel[1];
        _models[0] = model;
    }

    private MuseModel[] getModels() {
        return _models;
    }

    public float getLast() {
        return _last;
    }

    private void setLast(float curr) {
        _last = curr;
    }

    public float getMax() {
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

    public float getMin() {
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

    private float getValueK() {
        return _valueK;
    }

    public boolean isAttentionGrowing() {
        return _isAttentionGrowing;
    }

    public void update() {
        float value = getValue();

        if (value > getLast()) {
            _isAttentionGrowing = true;
        } else {
            _isAttentionGrowing = false;
        }
        setLast(value);
    }

    private void updateRange(float value) {
        updateMax(value);
        updateMin(value);
    }

    public float getValue() {
        float value = 0;
        for (MuseModel model : getModels()) {
            value += model.getLast(); // values in range
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
