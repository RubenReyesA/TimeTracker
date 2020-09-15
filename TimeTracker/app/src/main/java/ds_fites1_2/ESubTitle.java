package ds_fites1_2;

public class ESubTitle implements Elements {

    private final String text;

    public ESubTitle(String t) {
        text = t;
    }

    public final String getText() {
        return this.text;
    }

    public final void accept(Format f) {
        f.visitSubTitle(this);
    }

}
