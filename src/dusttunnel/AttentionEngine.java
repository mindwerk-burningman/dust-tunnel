package dusttunnel;

public class AttentionEngine {
    private MuseModel[] _models;
    private boolean _isAttentionGrowing = false;
    private float _last;
    private float _curr;

    public AttentionEngine(MuseModel[] models) {
        _models = models;
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

    public boolean isAttentionGrowing() {
        return _isAttentionGrowing;
    }

    public void update() {
        float accum = 0;
        for (MuseModel model : getModels()) {
            accum += model.getLast();
        }

        if (accum > getLast()) {
            _isAttentionGrowing = true;
        } else {
            _isAttentionGrowing = false;
        }

        setLast(accum);
    }
}
