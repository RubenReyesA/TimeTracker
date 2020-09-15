package ds_fites1_2;

public class ETableInfo implements Elements {

    private final int tableNRows;
    private final int tableNColumns;
    private final String[][] tableInfo;

    public ETableInfo(int nrows, int ncolumns) {
        tableNRows = nrows;
        tableNColumns = ncolumns;
        tableInfo = new String[nrows][ncolumns];
    }

    public final int getTableNRows() {
        return this.tableNRows;
    }

    public final int getTableNColumns() {
        return this.tableNColumns;
    }

    public final String[][] getTableInfo() {
        return this.tableInfo;
    }

    public final String getTableInfoValue(int i, int j) {
        return tableInfo[i][j];
    }

    public final void accept(Format f) {
        f.visitTableInfo(this);
    }

    public final void setContent(int f, int c, String d) {
        tableInfo[f][c] = d;
    }

}
