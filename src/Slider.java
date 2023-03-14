public class Slider {
    int x;
    int y;
    int w;
    int h;
    float value;
    float start;
    float end;
    String text;
    boolean isOn;
    public Slider(int x, int y, float start, float end, String text, boolean isOn) {
        this.x = x;
        this.y = y;
        this.w = 200;
        this.h = 10;
        this.start = start;
        this.end = end;
        this.value = start;
        this.text = text;
        this.isOn = isOn;
    }
}
