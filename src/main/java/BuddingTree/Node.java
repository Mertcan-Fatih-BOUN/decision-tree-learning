package BuddingTree;


import DataSet.Instance.Instance;

import java.util.Arrays;

import static Misc.Util.*;


@SuppressWarnings("Duplicates")
class Node {

    private Node parent = null;
    private Node leftNode = null;
    private Node rightNode = null;


    /**
     * Leaf response for linear_rho variation
     */
    private double[][] rho;
    private double[][] gradient_rho;
    private double[][] sum_grad_rho;

    /**
     * Leaf response for standard version and constant term for linear_rho version
     */
    private double[] rho0;
    private double[] gradient_rho0;
    private double[] sum_grad_rho0;

    /**
     * Leafness parameter
     */
    private double gamma = 1;
    private double gradient_gamma = 0;
    private double sum_grad_gamma;

    /**
     * Node weight
     */
    private double[] w;
    private double[] gradient_w;
    private double[] sum_grad_w;

    private double w0;
    private double gradient_w0;
    private double sum_grad_w0;

    private double w00;
    private double gradient_w00;
    private double sum_grad_w00;

    private double w01;
    private double gradient_w01;
    private double sum_grad_w01;

    /**
     * Weight of modalities for classes
     */
    private double[] P1;
    private double[] P2;

    private double[] gradient_P1;
    private double[] gradient_P2;

    private double[] sum_grad_P1;
    private double[] sum_grad_P2;

    private double g1;
    private double g2;


    private double[] g;
    private double[] y;
    private double[] delta;

    private BT tree;

    Node(BT tree) {
        this.tree = tree;

        w = new double[tree.ATTRIBUTE_COUNT];
        gradient_w = new double[w.length];
        sum_grad_w = new double[w.length];
        for (int i = 0; i < w.length; i++) {
            w[i] = rand(-tree.RANDOM_RANGE, tree.RANDOM_RANGE);
            gradient_w[i] = 0;
            sum_grad_w[i] = 0;
        }

        w0 = rand(-tree.RANDOM_RANGE, tree.RANDOM_RANGE);
        w00 = rand(-tree.RANDOM_RANGE, tree.RANDOM_RANGE);
        w01 = rand(-tree.RANDOM_RANGE, tree.RANDOM_RANGE);

        sum_grad_w0 = 0;
        sum_grad_w00 = 0;
        sum_grad_w01 = 0;

        gradient_w0 = 0;
        gradient_w00 = 0;
        gradient_w01 = 0;

        P1 = new double[tree.CLASS_COUNT];
        P2 = new double[tree.CLASS_COUNT];
        gradient_P1 = new double[tree.CLASS_COUNT];
        gradient_P2 = new double[tree.CLASS_COUNT];
        sum_grad_P1 = new double[tree.CLASS_COUNT];
        sum_grad_P2 = new double[tree.CLASS_COUNT];


        Arrays.fill(P1, 0.5);
        Arrays.fill(P2, 0.5);
        Arrays.fill(gradient_P1, 0);
        Arrays.fill(gradient_P2, 0);
        Arrays.fill(sum_grad_P1, 0);
        Arrays.fill(sum_grad_P2, 0);


        if (tree.use_linear_rho) {
            rho = new double[tree.CLASS_COUNT][tree.ATTRIBUTE_COUNT];
            sum_grad_rho = new double[tree.CLASS_COUNT][tree.ATTRIBUTE_COUNT];
            gradient_rho = new double[tree.CLASS_COUNT][tree.ATTRIBUTE_COUNT];
            for (int i = 0; i < rho.length; i++) {
                for (int j = 0; j < rho[i].length; j++) {
                    rho[i][j] = rand(-tree.RANDOM_RANGE, tree.RANDOM_RANGE);
                    sum_grad_rho[i][j] = 0;
                    gradient_rho[i][j] = 0;
                }
            }
        }

        rho0 = new double[tree.CLASS_COUNT];
        sum_grad_rho0 = new double[tree.CLASS_COUNT];
        gradient_rho0 = new double[tree.CLASS_COUNT];

        for (int i = 0; i < rho0.length; i++) {
            rho0[i] = rand(-tree.RANDOM_RANGE, tree.RANDOM_RANGE);
            sum_grad_rho0[i] = 0;
            gradient_rho0[i] = 0;
        }

        y = new double[tree.CLASS_COUNT];
        g = new double[tree.CLASS_COUNT];
        delta = new double[tree.CLASS_COUNT];
        Arrays.fill(y, 0);
        Arrays.fill(g, 0);
        gamma = 1;
    }


    private double[] g(Instance instance) {
        if (tree.user_multi_modal) {
            double sum1 = 0;
            double sum2 = 0;
            int lenght = instance.x.length;
            int firstmodal_size = tree.dataSet.first_modal_size;
            for (int j = 0; j < lenght; j++) {
                if (j < firstmodal_size) {
                    sum1 += w[j] * instance.x[j];
                } else {
                    sum2 += w[j] * instance.x[j];
                }
            }
            sum1 += w00;
            sum2 += w01;
            g1 = sigmoid(sum1);
            g2 = sigmoid(sum2);
            for (int i = 0; i < g.length; i++) {
                g[i] = (P1[i] * g1 + P2[i] * g2) / (P1[i] + P2[i]);
            }
        } else {
            double gg = sigmoid(dotProduct(w, instance.x) + w0);
            for (int i = 0; i < g.length; i++)
                g[i] = gg;
        }
        return g;
    }

    double[] F(Instance instance) {
        if (leftNode == null) {
            for (int i = 0; i < y.length; i++) {
                if (tree.use_linear_rho)
                    y[i] = this.gamma * (dotProduct(instance.x, rho[i]) + rho0[i]);
                else
                    y[i] = this.gamma * rho0[i];
            }
        } else {
            leftNode.F(instance);
            rightNode.F(instance);
            for (int i = 0; i < y.length; i++) {
                g(instance);
                if (tree.use_linear_rho)
                    y[i] = (1 - gamma) * ((g[i] * leftNode.y[i]) + ((1 - g[i]) * rightNode.y[i])) + gamma * (dotProduct(instance.x, rho[i]) + rho0[i]);
                else
                    y[i] = (1 - gamma) * ((g[i] * leftNode.y[i]) + ((1 - g[i]) * rightNode.y[i])) + gamma * rho0[i];
            }
        }
        return y;
    }

    private double[] delta(Instance instance) {
        if (this.parent == null) {
            for (int i = 0; i < delta.length; i++) {
                delta[i] = y[i] - instance.r[i];
            }
        } else {
            if (this == this.parent.leftNode) {
                for (int i = 0; i < delta.length; i++) {
                    delta[i] = parent.delta[i] * (1 - this.parent.gamma) * this.parent.g[i];
                }
            } else {
                for (int i = 0; i < delta.length; i++) {
                    delta[i] = parent.delta[i] * (1 - this.parent.gamma) * (1 - this.parent.g[i]);
                }
            }
        }
        return delta;
    }

    void backPropagate(Instance instance) {
        calculateGradient(instance);
        if (leftNode != null) {
            leftNode.backPropagate(instance);
            rightNode.backPropagate(instance);
        }
    }

    void update() {
        learnParameters();
        if (leftNode != null) {
            leftNode.update();
            rightNode.update();
        }

        if (gamma < 1 && leftNode == null) {
            splitNode();
        }
    }

    private void setGamma(double f) {
        if (f < 0) {
            gamma = 0;
        } else if (f > 1) {
            gamma = 1;
        } else
            gamma = f;
    }

    private void calculateGradient(Instance instance) {
        delta(instance);
        double[] left_y;
        double[] right_y;

        if (leftNode != null) {
            left_y = leftNode.y;
        } else {
            left_y = new double[tree.CLASS_COUNT];
            Arrays.fill(left_y, 0);
        }

        if (rightNode != null) {
            right_y = rightNode.y;
        } else {
            right_y = new double[tree.CLASS_COUNT];
            Arrays.fill(right_y, 0);
        }

        Arrays.fill(gradient_w, 0);
        for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++) {
            for (int j = 0; j < tree.CLASS_COUNT; j++) {
                if (tree.user_multi_modal) {
                    int firstmodal_size = tree.dataSet.first_modal_size;
                    //Project report equation 10
                    if (i < firstmodal_size)
                        gradient_w[i] += delta[j] * (1 - gamma) * g1 * (P1[j] / (P1[j] + P2[j])) * (1 - g1) * (left_y[j] - right_y[j]) * instance.x[i];
                    else
                        gradient_w[i] += delta[j] * (1 - gamma) * g2 * (P2[j] / (P1[j] + P2[j])) * (1 - g2) * (left_y[j] - right_y[j]) * instance.x[i];
                } else {
                    //Budding Trees paper derivative of J respect to w
                    gradient_w[i] += delta[j] * (1 - gamma) * g[j] * (1 - g[j]) * (left_y[j] - right_y[j]) * instance.x[i];
                }
            }
        }


        //Project report equation 11
        Arrays.fill(gradient_P1, 0);
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_P1[i] += delta[i] * (1 - gamma) * (left_y[i] - right_y[i]) * ((g1 * (P1[i] + P2[i]) - (P1[i] * g1 + P2[i] * g2)) / ((P1[i] + P2[i]) * (P1[i] + P2[i])));

        Arrays.fill(gradient_P2, 0);
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_P2[i] += delta[i] * (1 - gamma) * (left_y[i] - right_y[i]) * ((g2 * (P1[i] + P2[i]) - (P1[i] * g1 + P2[i] * g2)) / ((P1[i] + P2[i]) * (P1[i] + P2[i])));

        //Budding Trees paper derivative of J respect to w
        gradient_w0 = 0;
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_w0 += delta[i] * (1 - gamma) * g[i] * (1 - g[i]) * (left_y[i] - right_y[i]);

        //Project report equation 11
        gradient_w00 = 0;
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_w00 += delta[i] * (1 - gamma) * g1 * (P1[i] / (P1[i] + P2[i])) * (1 - g1) * (left_y[i] - right_y[i]);

        //Project report equation 11
        gradient_w01 = 0;
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_w01 += delta[i] * (1 - gamma) * g2 * (P2[i] / (P1[i] + P2[i])) * (1 - g2) * (left_y[i] - right_y[i]);


        //Project report equation 13
        if (tree.use_linear_rho) {
            for (int i = 0; i < tree.CLASS_COUNT; i++) {
                for (int j = 0; j < tree.ATTRIBUTE_COUNT; j++) {
                    gradient_rho[i][j] = delta[i] * gamma * instance.x[j];
                }
            }
        }

        //Budding tree paper, derivative of J respect to rho
        for (int i = 0; i < tree.CLASS_COUNT; i++) {
            gradient_rho0[i] = delta[i] * gamma;
        }

        //Budding tree paper, derivatinve of J respect to gamma
        gradient_gamma = 0;
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_gamma += delta[i] * ((-g[i] * left_y[i]) - (1 - g[i]) * right_y[i] + rho0[i]) - tree.LAMBDA;
    }

    int size() {
        if (leftNode == null)
            return 1;
        else
            return 1 + leftNode.size() + rightNode.size();
    }

    double myEffSize() {
        if (leftNode == null || gamma == 1)
            return 1;
        else
            return gamma + (1 - gamma) * (leftNode.myEffSize() + rightNode.myEffSize());
    }


    private void learnParameters() {
        for (int i = 0; i < sum_grad_w.length; i++) {
            sum_grad_w[i] = tree.rms_prop_factors[0] * sum_grad_w[i] + tree.rms_prop_factors[1] * gradient_w[i] * gradient_w[i];
        }

        if (tree.user_multi_modal) {
            for (int i = 0; i < sum_grad_P1.length; i++) {
                sum_grad_P1[i] = tree.rms_prop_factors[0] * sum_grad_P1[i] + tree.rms_prop_factors[1] * gradient_P1[i] * gradient_P1[i];
            }

            for (int i = 0; i < sum_grad_P2.length; i++) {
                sum_grad_P2[i] = tree.rms_prop_factors[0] * sum_grad_P2[i] + tree.rms_prop_factors[1] * gradient_P2[i] * gradient_P2[i];
            }

            sum_grad_w00 = tree.rms_prop_factors[0] *  sum_grad_w00 + tree.rms_prop_factors[1] * gradient_w00 * gradient_w00;

            sum_grad_w01 = tree.rms_prop_factors[0] * sum_grad_w01 + tree.rms_prop_factors[1] * gradient_w01 * gradient_w01;
        }

        sum_grad_w0 = tree.rms_prop_factors[0] * sum_grad_w0 + tree.rms_prop_factors[1] * gradient_w0 * gradient_w0;

        if (tree.use_linear_rho) {
            for (int i = 0; i < tree.CLASS_COUNT; i++) {
                for (int j = 0; j < tree.ATTRIBUTE_COUNT; j++) {
                    sum_grad_rho[i][j] = tree.rms_prop_factors[0] * sum_grad_rho[i][j] + tree.rms_prop_factors[1] * gradient_rho[i][j] * gradient_rho[i][j];
                }
            }
        }

        for (int i = 0; i < sum_grad_rho0.length; i++) {
            sum_grad_rho0[i] = tree.rms_prop_factors[0] * sum_grad_rho0[i] + tree.rms_prop_factors[1] * gradient_rho0[i] * gradient_rho0[i];
        }

        sum_grad_gamma = tree.rms_prop_factors[0] * sum_grad_gamma + tree.rms_prop_factors[1] * gradient_gamma * gradient_gamma;


        //Budding tree paper, adaptive leaning rate, end of the third page
        for (int i = 0; i < sum_grad_w.length; i++) {
            if (sum_grad_w[i] != 0)
                w[i] = w[i] - tree.LEARNING_RATE * tree.LEARNING_RATE_INPUT_MULTIPLIER * gradient_w[i] / Math.sqrt(sum_grad_w[i]);
        }

        if (sum_grad_w0 != 0)
            w0 = w0 - tree.LEARNING_RATE * gradient_w0 / Math.sqrt(sum_grad_w0);

        if (tree.user_multi_modal) {

            if (sum_grad_w00 != 0)
                w00 = w00 - tree.LEARNING_RATE * gradient_w00 / Math.sqrt(sum_grad_w00);

            if (sum_grad_w01 != 0)
                w01 = w01 - tree.LEARNING_RATE * gradient_w01 / Math.sqrt(sum_grad_w01);

            for (int i = 0; i < sum_grad_P1.length; i++) {
                if (sum_grad_P1[i] != 0)
                    P1[i] = P1[i] - tree.LEARNING_RATE * gradient_P1[i] / Math.sqrt(sum_grad_P1[i]);
            }

            for (int i = 0; i < sum_grad_P2.length; i++) {
                if (sum_grad_P2[i] != 0)
                    P2[i] = P2[i] - tree.LEARNING_RATE * gradient_P2[i] / Math.sqrt(sum_grad_P2[i]);
            }
        }
        if (sum_grad_gamma != 0)
            setGamma(gamma - tree.LEARNING_RATE * gradient_gamma / Math.sqrt(sum_grad_gamma));

        if (tree.use_linear_rho) {
            for (int i = 0; i < tree.CLASS_COUNT; i++) {
                for (int j = 0; j < tree.ATTRIBUTE_COUNT; j++) {
                    if (sum_grad_rho[i][j] != 0) {
                        rho[i][j] = (rho[i][j] - tree.LEARNING_RATE * tree.LEARNING_RATE_INPUT_MULTIPLIER * gradient_rho[i][j] / Math.sqrt(sum_grad_rho[i][j]));
                    }
                }
            }
        }

        for (int i = 0; i < sum_grad_rho0.length; i++) {
            if (sum_grad_rho0[i] != 0)
                rho0[i] = rho0[i] - tree.LEARNING_RATE * gradient_rho0[i] / Math.sqrt(sum_grad_rho0[i]);
        }


    }

    private void splitNode() {
        leftNode = new Node(tree);
        leftNode.parent = this;

        rightNode = new Node(tree);
        rightNode.parent = this;
    }
}