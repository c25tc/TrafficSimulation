public class Button {
    int x;
    int y;
    int w;
    int h;
    int color;
    boolean isActive;
    String text;
    boolean isEdit;
    public Button(int x, int y, int w, int h, int color, String text, boolean isEdit) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.text = text;
        this.color = color;
        this.isActive = false;
        this.isEdit = isEdit;
    }
    public boolean isClicked(int x, int y) {
        return x > this.x && x < (this.x + this.w) && y > this.y && y < this.y + this.h;
    }


}
