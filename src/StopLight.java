import processing.core.PVector;

public class StopLight {
    PVector pos;
    float onDuration;
    float offDuration;
    boolean isOn;

    public StopLight(int x, int y, float onDuration, float offDuration, boolean isOn) {
        this.pos = new PVector(x, y);
        this.onDuration = onDuration;
        this.offDuration = offDuration;
        this.isOn = isOn;
    }

    public void update() {
        if (isOn) {
            if (TrafficSimulation.systemTime % (onDuration * 60) == 0) {
                isOn = false;
            }
        } else {
            if (TrafficSimulation.systemTime % (offDuration * 60) == 0) {
                isOn = true;
            }
        }
    }
}
