package ds_fites1_2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

// Class that implements the Visitor pattern in HTML
public class FHTML implements Format {

    private final PrintWriter writer;
    private final String filename;
    private final ContainerTag html;
    private final ContainerTag body;
    private static final Logger logger = LoggerFactory.getLogger(FHTML.class);


    public FHTML(String name, final PrintWriter pw) {
        filename = name;
        writer = pw;
        html = html();
        final ContainerTag head = head();
        head.with(title("Informe"));
        this.html.with(head);
        body = body();
        this.html.with(this.body);
    }

    public final void closeWriter() {
        writer.println(this.html.render());
        writer.close();
        FHTML.logger.info(filename + " created succesfully.");

    }

    public final void visitTitle(ETitle t) {
        this.body.with(h1(t.getText()));
    }

    public final void visitSubTitle(ESubTitle st) {
        this.body.with(h2(st.getText()));
    }

    public final void visitSeparator(ESeparator s) {
        this.body.with(hr());
    }

    public final void visitTableInfo(ETableInfo ti) {

        final ContainerTag table = table();
        table.attr("border", 1);

        final String[] headers = ti.getTableInfo()[0];

        for (int i = 0; i < headers.length; i++) {
            table.with(th(headers[i]));
        }

        for (int i = 1; i < ti.getTableNRows(); i++) {

            final ContainerTag tr = tr();

            for (int j = 0; j < ti.getTableNColumns(); j++) {

                tr.with(td(ti.getTableInfoValue(i, j)));

            }

            table.with(tr);
        }

        this.body.with(table);
        this.body.with(br());
    }

    public final void visitText(EText text) {
        this.body.with(p(text.getText()));

    }

}
