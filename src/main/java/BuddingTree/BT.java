package BuddingTree;


import Analysis.ClassificationError;
import Analysis.Evaluable;
import Analysis.MAPError;
import DataSet.DataSet.DataSet;
import DataSet.Instance.Instance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static Analysis.CalculateError.*;

public class BT implements Evaluable {
    boolean user_multi_modal = false;
    boolean use_linear_rho = false;

    final int ATTRIBUTE_COUNT;
    final int CLASS_COUNT;


    double LEARNING_RATE;
    double LEARNING_RATE_INPUT_MULTIPLIER;

    double LAMBDA;

    double RANDOM_RANGE;
    private final ArrayList<Instance> X;
    private final ArrayList<Instance> V;
    final DataSet dataSet;

    private Node ROOT;
    private final DataSet.TYPE type;


    /**
     * @param dataSet            Data set will be used for training
     * @param use_linear_rho     Whether the BT will use linear leaf responses
     * @param enable_multi_modal Whether the BT will use multi-modal model
     * @param random_range       [-random_range random_range] is the initial value of variables
     */
    public BT(DataSet dataSet, boolean use_linear_rho, boolean enable_multi_modal, double random_range) {
        this.use_linear_rho = use_linear_rho;
        this.user_multi_modal = enable_multi_modal;
        this.dataSet = dataSet;
        this.type = dataSet.type;
        this.X = dataSet.TRAINING_INSTANCES;
        this.V = dataSet.VALIDATION_INSTANCES;
        this.ATTRIBUTE_COUNT = X.get(0).x.length;
        this.CLASS_COUNT = X.get(0).r.length;
        this.RANDOM_RANGE = random_range;
        ROOT = new Node(this);
    }

    private int size() {
        return ROOT.size();
    }

    private double eff_size() {
        return ROOT.myEffSize();
    }

    /**
     * Starts the learning process of BT
     *
     * @param learning_rate                  Learning rate of update process
     * @param learning_rate_input_multiplier Learning rate multiplier which is used where the gradient contains input vector directly
     * @param epoch                          Total epoch
     * @param lambda                         Complexity cost weight, smaller lambda, smaller tree
     * @param learning_rate_decay            Multiplier of learning rate after each epoch
     * @throws IOException
     */
    public void learnTree(double learning_rate, double learning_rate_input_multiplier, int epoch, double lambda, double learning_rate_decay) throws IOException {
        this.LEARNING_RATE = learning_rate;
        this.LEARNING_RATE_INPUT_MULTIPLIER = learning_rate_input_multiplier;
        this.LAMBDA = lambda;


        for (int e = 0; e <= epoch; e++) {
            if (this.type == DataSet.TYPE.MULTI_LABEL_CLASSIFICATION) {
                MAPError mapErrorX = getMAP_P50_error(this, X);
                MAPError mapErrorV = getMAP_P50_error(this, V);
                System.out.printf("Epoch: %3d, Size: %3d, Effective Size: %3d\n", e, size(), (int) eff_size());
                System.out.printf("%4s %4s %4s %4s\n", "xMAP", "xPRE", "vMAP", "vPRE");
                for (int i = 0; i < CLASS_COUNT; i++) {
                    System.out.printf("%.2f %.2f %.2f %.2f\n", mapErrorX.MAP[i], mapErrorX.precision[i], mapErrorV.MAP[i], mapErrorV.precision[i]);
                }
                System.out.printf("%4s %4s %4s %4s\n", "----", "----", "----", "----");
                System.out.printf("%.2f %.2f %.2f %.2f\n", mapErrorX.getAverageMAP(), mapErrorX.getAveragePrec(), mapErrorV.getAverageMAP(), mapErrorV.getAveragePrec());
                System.out.println();
            } else if (this.type == DataSet.TYPE.MULTI_CLASS_CLASSIFICATION || this.type == DataSet.TYPE.BINARY_CLASSIFICATION) {
                ClassificationError Xerror = getClassificationError(this, X, type);
                ClassificationError Verror = getClassificationError(this, V, type);
                System.out.printf("Epoch : %d Size: %d X: %.2f V: %.2f\n", e, size(), Xerror.getAccuracy(), Verror.getAccuracy());
                System.out.println("X Confusion Matrix\n" + Xerror.getStringConfusionMatrix());
                System.out.println("V Confusion Matrix\n" + Verror.getStringConfusionMatrix());
            } else if (this.type == DataSet.TYPE.REGRESSION)
                System.out.printf("Epoch: %d Size: %d MSE X: %.2f MSE V: %.2f\n", e, size(), getMeanSquareError(this, X), getMeanSquareError(this, V));

            Collections.shuffle(X);
            for (Instance instance : X) {
                ROOT.F(instance);
                ROOT.backPropagate(instance);
                ROOT.update();
            }
            LEARNING_RATE *= learning_rate_decay;
        }
    }

    @Override
    public double[] evaluate(Instance instance) {
        return ROOT.F(instance);
    }
}