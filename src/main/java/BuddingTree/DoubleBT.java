package BuddingTree;

import Analysis.ClassificationError;
import Analysis.Evaluable;
import Analysis.MAPError;
import Analysis.MultiEvaluable;
import DataSet.DataSet.DataSet;
import DataSet.Instance.Instance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static Analysis.CalculateError.getClassificationError;
import static Analysis.CalculateError.getMAP_P50_error;
import static Analysis.CalculateError.getMeanSquareError;

/**
 * Created by mertcan on 28.6.2016.
 */
public class DoubleBT implements MultiEvaluable {
    boolean use_linear_rho = false;

    double LEARNING_RATE;
    double LEARNING_RATE_INPUT_MULTIPLIER;

    double LAMBDA;

    double RANDOM_RANGE;
    final DataSet dataSet1;
    final DataSet dataSet2;
    private final DataSet.TYPE type;

    BT bt1;
    BT bt2;


    /**
     * @param dataSet1       Data set will be used for training first tree
     * @param dataSet2       Data set will be used for training second tree
     * @param use_linear_rho Whether the BT will use linear leaf responses
     * @param random_range   [-random_range random_range] is the initial value of variables
     */
    public DoubleBT(DataSet dataSet1, DataSet dataSet2, boolean use_linear_rho, double random_range) {
        this.use_linear_rho = use_linear_rho;
        this.dataSet1 = dataSet1;
        this.dataSet2 = dataSet2;
        this.type = dataSet1.type;
        if(dataSet1.TRAINING_INSTANCES.size() != dataSet2.TRAINING_INSTANCES.size()){
            System.out.println("Error in datasets.");
        }else {
            bt1 = new BT(dataSet1, use_linear_rho, false, random_range);
            bt2 = new BT(dataSet2, use_linear_rho, false, random_range);
        }
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
        this.bt1.LEARNING_RATE = learning_rate;
        this.bt1.LEARNING_RATE_INPUT_MULTIPLIER = learning_rate_input_multiplier;
        this.bt1.LAMBDA = lambda;

        this.bt2.LEARNING_RATE = learning_rate;
        this.bt2.LEARNING_RATE_INPUT_MULTIPLIER = learning_rate_input_multiplier;
        this.bt2.LAMBDA = lambda;

        for (int e = 0; e <= epoch; e++) {
            if (this.type == DataSet.TYPE.MULTI_LABEL_CLASSIFICATION) {
                MAPError mapErrorX = getMAP_P50_error(this, dataSet1.TRAINING_INSTANCES, dataSet2.TRAINING_INSTANCES);
                MAPError mapErrorV = getMAP_P50_error(this, dataSet1.VALIDATION_INSTANCES, dataSet2.VALIDATION_INSTANCES);
                System.out.printf("Epoch: %3d, Size: %3d, Effective Size: %3d\n", e, bt1.size() + bt2.size(), (int) (bt1.eff_size() + bt2.eff_size()));
                System.out.printf("%4s %4s %4s %4s\n", "xMAP", "xPRE", "vMAP", "vPRE");
                for (int i = 0; i < bt1.CLASS_COUNT; i++) {
                    System.out.printf("%.3f %.3f %.3f %.3f\n", mapErrorX.MAP[i], mapErrorX.precision[i], mapErrorV.MAP[i], mapErrorV.precision[i]);
                }
                System.out.printf("%4s %4s %4s %4s\n", "----", "----", "----", "----");
                System.out.printf("%.3f %.3f %.3f %.3f\n", mapErrorX.getAverageMAP(), mapErrorX.getAveragePrec(), mapErrorV.getAverageMAP(), mapErrorV.getAveragePrec());
                System.out.println();
            } else if (this.type == DataSet.TYPE.MULTI_CLASS_CLASSIFICATION || this.type == DataSet.TYPE.BINARY_CLASSIFICATION) {
                ClassificationError Xerror = getClassificationError(this, dataSet1.TRAINING_INSTANCES, dataSet2.TRAINING_INSTANCES, type);
                ClassificationError Verror = getClassificationError(this, dataSet1.VALIDATION_INSTANCES, dataSet2.VALIDATION_INSTANCES, type);
                System.out.printf("Epoch : %d Size: %d Effective Size: %3d X: %.3f V: %.3f\n", e, bt1.size() + bt2.size(), (int) (bt1.eff_size() + bt2.eff_size()), Xerror.getAccuracy(), Verror.getAccuracy());
                System.out.println("X Confusion Matrix\n" + Xerror.getStringConfusionMatrix());
                System.out.println("V Confusion Matrix\n" + Verror.getStringConfusionMatrix());
            } else if (this.type == DataSet.TYPE.REGRESSION)
                System.out.printf("TO-DO");



            Collections.shuffle(dataSet1.TRAINING_INSTANCES);
            Collections.shuffle(dataSet2.TRAINING_INSTANCES);
            for (int i = 0; i < dataSet1.TRAINING_INSTANCES.size(); i++) {
                Instance instance1 = dataSet1.TRAINING_INSTANCES.get(i);
                Instance instance2 = dataSet2.TRAINING_INSTANCES.get(i);
                bt1.ROOT.F(instance1);
                bt1.ROOT.backPropagate(instance1);
                bt1.ROOT.update();
                bt2.ROOT.F(instance2);
                bt2.ROOT.backPropagate(instance2);
                bt2.ROOT.update();
            }
            LEARNING_RATE *= learning_rate_decay;
        }
    }

    @Override
    public double[] evaluate(Instance instance1, Instance instance2) {
        double[] responses = new double[bt1.CLASS_COUNT];
        double[] f1 = bt1.ROOT.F(instance1);
        double[] f2 = bt2.ROOT.F(instance2);
        for(int i = 0; i < f1.length; i++){
            responses[i] = (f1[i] + f2[i]) / 2;
        }
        return responses;
    }
}