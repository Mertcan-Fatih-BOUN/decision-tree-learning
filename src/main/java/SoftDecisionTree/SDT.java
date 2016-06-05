package SoftDecisionTree;


import Analysis.Evaluable;
import DataSet.DataSet.DataSet;
import DataSet.Instance.Instance;

import java.io.IOException;
import java.util.ArrayList;

import static Analysis.CalculateError.getClassificationError;
import static Analysis.CalculateError.getMAP_P50_error;
import static Analysis.CalculateError.getMeanSquareError;
import static Misc.Util.argMax;

public class SDT implements Evaluable {
    double LEARNING_RATE_DECAY;
    double LEARNING_RATE;
    int MAX_STEP;
    int EPOCH;

    ArrayList<Instance> X;
    ArrayList<Instance> V;

    Node ROOT;

    DataSet.TYPE type;

    private final int CLASS_COUNT;
    final int ATTRIBUTE_COUNT;

    public SDT(DataSet dataSet) throws IOException {
        X = dataSet.TRAINING_INSTANCES;
        V = dataSet.VALIDATION_INSTANCES;
        CLASS_COUNT = X.get(0).r.length;
        ATTRIBUTE_COUNT = X.get(0).x.length;
        type = dataSet.type;
    }


    int size() {
        return ROOT.size();
    }

    public void learnTree(int epoch, int max_step, double learning_rate, double learning_rate_decay) {
        LEARNING_RATE = learning_rate;
        EPOCH = epoch;
        MAX_STEP = max_step;
        LEARNING_RATE_DECAY = learning_rate_decay;

        ROOT = new Node(this);

        ROOT.rho = new double[CLASS_COUNT];

        if (CLASS_COUNT == 1) {
            for (Instance i : X)
                ROOT.rho[0] += argMax(i.r);
            ROOT.rho[0] /= X.size();
        } else {
            double inc = 1.0 / X.size();
            for (Instance i : X)
                ROOT.rho[argMax(i.r)] += inc;
        }

        ROOT.splitNode();
    }


    /**
     * This error should be used for split control.
     * For reporting purposes user Analysis package directly.
     *
     * @param list The Instance list for error calculation
     * @return Error value
     */
    double error(ArrayList<Instance> list) {
        if (type == DataSet.TYPE.BINARY_CLASSIFICATION || type == DataSet.TYPE.MULTI_CLASS_CLASSIFICATION)
            return getClassificationError(this, list, type).getMissClassificationError();
        else if (type == DataSet.TYPE.REGRESSION)
            return getMeanSquareError(this, list);
        else if (type == DataSet.TYPE.MULTI_LABEL_CLASSIFICATION)
            return 1 - getMAP_P50_error(this, list).getAverageMAP();
        else
            return 0;
    }

    public String toString() {
        return ROOT.toString(1);
    }


    @Override
    public double[] evaluate(Instance instance) {
        return ROOT.F(instance);
    }
}