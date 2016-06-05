package SoftDecisionTree;

import Analysis.ClassificationError;
import Analysis.MAPError;
import DataSet.DataSet.DataSet;
import DataSet.Instance.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static Analysis.CalculateError.*;
import static Misc.Util.*;

@SuppressWarnings("Duplicates")
class Node {
    private Node parent = null;
    private Node leftNode = null;
    private Node rightNode = null;
    double[] rho;
    private double[] w;
    private double w0;

    private double[] y;
    private double g;
    private SDT tree;

    Node(SDT tree) {
        this.tree = tree;
    }


    double[] F(Instance instance) {
        if (leftNode == null) {
            y = Arrays.copyOf(rho, rho.length);
        } else {
            g = sigmoid(dotProduct(w, instance.x) + w0);
            double[] y_left = leftNode.F(instance);
            double[] y_right = rightNode.F(instance);
            for (int i = 0; i < y.length; i++)
                y[i] = g * y_left[i] + (1 - g) * y_right[i];
        }
        return y;

    }


    int size() {
        if (leftNode == null)
            return 0;
        else
            return 1 + leftNode.size() + rightNode.size();
    }

    private void learnParameters(ArrayList<Instance> X, double alpha, SDT tree, int MAX_EPOCH) {
        double u = 0.1;

        double[] dw = new double[tree.ATTRIBUTE_COUNT];
        double[] dwp = new double[tree.ATTRIBUTE_COUNT];
        Arrays.fill(dw, 0);
        Arrays.fill(dwp, 0);
        double[] dwleftp = new double[rho.length];
        double[] dwrightp = new double[rho.length];
        double[] dwleft = new double[rho.length];
        double[] dwright = new double[rho.length];

        double dw0p = 0;
        double dw0;


        for (int e = 0; e < MAX_EPOCH; e++) {
            ArrayList<Integer> indices = new ArrayList<>();
            for (int i = 0; i < X.size(); i++) indices.add(i);
            Collections.shuffle(indices);
            for (int i = 0; i < X.size(); i++) {
                int j = indices.get(i);
                double[] x = X.get(j).x;
                int[] r = X.get(j).r;
                double[] y = tree.ROOT.F(X.get(j));
                double[] d = new double[y.length];
                for (int c = 0; c < y.length; c++)
                    d[c] = y[c] - r[c];

                double t = alpha;
                Node m = this;
                Node p;

                while (m.parent != null) {
                    p = m.parent;
                    if (m == m.parent.leftNode)
                        t *= p.g;
                    else
                        t *= (1 - p.g);
                    m = m.parent;
                }

                Arrays.fill(dw, 0);
                dw0 = 0;
                for (int k = 0; k < y.length; k++) {

                    for (int count = 0; count < tree.ATTRIBUTE_COUNT; count++)
                        dw[count] += (-t * d[k] * (leftNode.y[k] - rightNode.y[k]) * g * (1 - g)) * x[count];

                    dw0 += (-t * d[k] * (leftNode.y[k] - rightNode.y[k]) * g * (1 - g));

                    dwleft[k] = -t * d[k] * g;
                    dwright[k] = -t * d[k] * (1 - g);
                }

                for (int count = 0; count < tree.ATTRIBUTE_COUNT; count++)
                    w[count] += dw[count] + u * dwp[count];


                w0 += dw0 + u * dw0p;

                for (int k = 0; k < rho.length; k++) {
                    leftNode.rho[k] += dwleft[k] + u * dwleftp[k];
                    rightNode.rho[k] += dwright[k] + u * dwrightp[k];
                }

                dwp = Arrays.copyOf(dw, dw.length);
                dw0p = dw0;
                dwleftp = Arrays.copyOf(dwleft, rho.length);
                dwrightp = Arrays.copyOf(dwright, dwright.length);
                alpha *= tree.LEARNING_RATE_DECAY;
            }
        }
    }

    void splitNode() {

        double[] oldw0 = Arrays.copyOf(rho, rho.length);


        w = new double[tree.ATTRIBUTE_COUNT];

        double err = tree.error(tree.V);

        leftNode = new Node(tree);
        leftNode.parent = this;
        leftNode.rho = new double[rho.length];

        rightNode = new Node(tree);
        rightNode.parent = this;
        rightNode.rho = new double[rho.length];


        double[] bestw = new double[tree.ATTRIBUTE_COUNT];
        double bestw0 = 0;
        double[] bestw0l = new double[rho.length];
        double[] bestw0r = new double[rho.length];
        double bestErr = 1e10;
        double newErr;


        double alpha;
        for (int t = 0; t < tree.MAX_STEP; t++) {
            for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++)
                w[i] = rand(-0.005, 0.005);
            w0 = rand(-0.005, 0.005);

            for (int i = 0; i < rho.length; i++) {
                rho[i] = rand(-0.005, 0.005);
                leftNode.rho[i] = rand(-0.005, 0.005);
                rightNode.rho[i] = rand(-0.005, 0.005);
            }

            alpha = (tree.LEARNING_RATE + 0.0) / Math.pow(2, t + 1);
            learnParameters(tree.X, alpha, tree, tree.EPOCH);

            newErr = tree.error(tree.V);

            System.out.printf("Step: %d New Error: %.3f\n", t, newErr);

            if (newErr < bestErr) {

                bestw = Arrays.copyOf(w, w.length);
                bestw0 = w0;
                bestw0l = Arrays.copyOf(leftNode.rho, rho.length);
                bestw0r = Arrays.copyOf(rightNode.rho, rho.length);
                bestErr = newErr;
            }
        }


        w = bestw;
        w0 = bestw0;
        leftNode.rho = bestw0l;
        rightNode.rho = bestw0r;

        if (bestErr < err) {
            if (tree.type == DataSet.TYPE.BINARY_CLASSIFICATION || tree.type == DataSet.TYPE.MULTI_CLASS_CLASSIFICATION) {
                ClassificationError errorX = getClassificationError(tree, tree.X, tree.type);
                ClassificationError errorV = getClassificationError(tree, tree.V, tree.type);
                System.out.printf("Size: %d\t Accuracy X: %.2f\t Accuracy V: %.2f", tree.size(), errorX.getAccuracy(), errorV.getAccuracy());
            } else if (tree.type == DataSet.TYPE.REGRESSION) {
                System.out.printf("Size: %d\t MSE X: %.2f\t MSE V: %.2f", tree.size(), getMeanSquareError(tree, tree.X), getMeanSquareError(tree, tree.V));
            } else if (tree.type == DataSet.TYPE.MULTI_LABEL_CLASSIFICATION) {
                MAPError errorX = getMAP_P50_error(tree, tree.X);
                MAPError errorV = getMAP_P50_error(tree, tree.V);
                System.out.printf("Size: %d Average MAP X: %.2f Average P@50 X: %.2f Average MAP V: %.2f Average P@50 V: %.2f\n",
                        tree.size(), errorX.getAverageMAP(), errorX.getAveragePrec(), errorV.getAverageMAP(), errorV.getAveragePrec());
            }
            leftNode.splitNode();
            rightNode.splitNode();
        } else {
            leftNode = null;
            rightNode = null;
            rho = oldw0;
        }
    }

    String toString(int tab) {
        String s = "";
        for (int i = 0; i < tab; i++) {
            s += "\t";
        }
        if (leftNode == null)
            s += "LEAF";
        else {
            s += "NODE" + "\n";
            s += this.leftNode.toString(tab + 1) + "\n";
            s += this.rightNode.toString(tab + 1);
        }
        return s;
    }
}