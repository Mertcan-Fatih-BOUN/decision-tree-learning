package Misc;

import DataSet.Reader.FlickerReader;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;


public class Util {
    public static Random generator = new Random(45645);
    public static double rand(double s, double e) {
        if (e < s) {
            double t = e;
            e = s;
            s = t;
        }
        return (e - s) * generator.nextDouble() + s;
    }

    public static double rand(double s, double e, Random r) {
        if (e < s) {
            double t = e;
            e = s;
            s = t;
        }
        return (e - s) * r.nextDouble() + s;
    }

    public static double[] softmax(double[] input) {
        double[] result = new double[input.length];
        double sum = 0;
        for (int i = 0; i < input.length; i++) {
            result[i] = Math.exp(input[i]);
            sum += result[i];
        }
        for (int i = 0; i < input.length; i++) {
            result[i] = result[i] / sum;
        }
        return result;
    }

    public static double sigmoid(double x) {
        return 1.0 / (1 + Math.exp(-x));
    }

    public static double[] sigmoid(double[] x) {
        double[] tmp = new double[x.length];
        for (int i = 0; i < x.length; i++)
            tmp[i] = sigmoid(x[i]);
        return tmp;
    }

    public static double dotProduct(double[] a, double[] b) {
        double result = 0;
        for (int i = 0; i < a.length; i++)
            result += a[i] * b[i];
        return result;
    }

    public static int argMax(double[] d) {
        double tmp = d[0];
        int tmpIndex = 0;
        for (int i = 1; i < d.length; i++) {
            if (d[i] > tmp) {
                tmp = d[i];
                tmpIndex = i;
            }
        }
        return tmpIndex;
    }

    public static int argMax(int[] d) {
        int tmp = d[0];
        int tmpIndex = 0;
        for (int i = 1; i < d.length; i++) {
            if (d[i] > tmp) {
                tmp = d[i];
                tmpIndex = i;
            }
        }
        return tmpIndex;
    }


}
