package benchmark;

public class DBGenConfig {
    private int dbSizeMin;
    private int dbSizeMax;
    private int step;

    public int getDbSizeMin() {
        return dbSizeMin;
    }

    public void setDbSizeMin(int dbSizeMin) {
        this.dbSizeMin = dbSizeMin;
    }

    public int getDbSizeMax() {
        return dbSizeMax;
    }

    public void setDbSizeMax(int dbSizeMax) {
        this.dbSizeMax = dbSizeMax;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
