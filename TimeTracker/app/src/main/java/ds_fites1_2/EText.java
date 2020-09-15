package ds_fites1_2;

public class EText implements Elements {

    private final String text;

    public EText(String t) {
        text = t;
    }

    public final String getText() {
        return this.text;
    }

    public final void accept(Format f) {
        f.visitText(this);
    }

}
