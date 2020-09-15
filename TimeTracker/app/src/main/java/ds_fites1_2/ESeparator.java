package ds_fites1_2;

public class ESeparator implements Elements {

    public ESeparator() {
    }

    public final void accept(Format f) {
        f.visitSeparator(this);
    }
}
