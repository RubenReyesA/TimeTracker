package ds_fites1_2;

public class ETitle implements Elements {

    private final String text;

    public ETitle(String t) {
        text = t;
    }

    public final String getText() {
        return this.text;
    }

    public final void accept(Format f) {
        f.visitTitle(this);
    }
}
