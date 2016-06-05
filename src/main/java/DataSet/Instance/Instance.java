package DataSet.Instance;


public class Instance {
    public int[] r;
    public double[] x;
    public double[] y;
    public double[] d;


    public void setY(double[] y) {
        this.y = y;
        d = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            d[i] = y[i] - r[i];
        }
    }
}