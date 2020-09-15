package ds_fites1_2;

// Class that implements the Visitor pattern for the format
public interface Format {

    void visitTitle(ETitle t);

    void visitSubTitle(ESubTitle st);

    void visitSeparator(ESeparator s);

    void visitTableInfo(ETableInfo ti);

    void visitText(EText text);

    void closeWriter();


}
