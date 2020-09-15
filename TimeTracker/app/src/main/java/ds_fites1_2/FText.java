package ds_fites1_2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

// Class that implements the Visitor pattern in txt
public class FText implements Format {

    private final PrintWriter writer;
    private final String filename;
    private static final Logger logger = LoggerFactory.getLogger(FText.class);


    public FText(String name, final PrintWriter pw) {
        filename = name;
        writer = pw;
    }

    public final void closeWriter() {
        this.writer.close();
        FText.logger.info(filename + " created succesfully.");
    }

    public final void visitTitle(ETitle t) {
        this.writer.println(t.getText());
    }

    public final void visitSubTitle(ESubTitle st) {
        this.writer.println(st.getText());
    }

    public final void visitSeparator(ESeparator s) {
        this.writer.println("-------------------------------"
                + "-------------------------------------------"
                + "--------------------------------------");
    }

    public final void visitTableInfo(ETableInfo ti) {

        for (int i = 0; i < ti.getTableNRows(); i++) {


            for (int j = 0; j < ti.getTableNColumns(); j++) {

                this.writer.printf("%-33s", ti.getTableInfoValue(i, j));

            }
            this.writer.println();
        }

    }

    public final void visitText(EText text) {
        this.writer.println(text.getText());
    }

}
