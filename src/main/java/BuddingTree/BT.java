package BuddingTree;


import Analysis.ClassificationError;
import Analysis.Evaluable;
import Analysis.MAPError;
import DataSet.DataSet.DataSet;
import DataSet.Instance.Instance;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static Analysis.CalculateError.*;

public class BT implements Evaluable {
    BufferedWriter output;

    double bestResult = 0;
    long id;

    boolean user_multi_modal = false;
    boolean use_linear_rho = false;
    boolean use_rms_prop = false;
    double[] rms_prop_factors = new double[]{1, 1};

    final int ATTRIBUTE_COUNT;
    final int CLASS_COUNT;


    double LEARNING_RATE;
    double LEARNING_RATE_INPUT_MULTIPLIER;

    double LAMBDA;

    double RANDOM_RANGE;
    private final ArrayList<Instance> X;
    private final ArrayList<Instance> V;
    final DataSet dataSet;

    protected Node ROOT;
    private final DataSet.TYPE type;


    /**
     * @param dataSet            Data set will be used for training
     * @param use_linear_rho     Whether the BT will use linear leaf responses
     * @param enable_multi_modal Whether the BT will use multi-modal model
     * @param random_range       [-random_range random_range] is the initial value of variables
     */
    public BT(DataSet dataSet, boolean use_linear_rho, boolean enable_multi_modal, double random_range, boolean use_rms_prop, double[] rms_prop_factors, BufferedWriter output) {
        this.use_linear_rho = use_linear_rho;
        this.user_multi_modal = enable_multi_modal;
        this.use_rms_prop = use_rms_prop;
        if (use_rms_prop)
            this.rms_prop_factors = rms_prop_factors;
        this.dataSet = dataSet;
        this.type = dataSet.type;
        this.X = dataSet.TRAINING_INSTANCES;
        this.V = dataSet.VALIDATION_INSTANCES;
        this.ATTRIBUTE_COUNT = X.get(0).x.length;
        this.CLASS_COUNT = X.get(0).r.length;
        this.RANDOM_RANGE = random_range;
        this.output = output;
        this.id = new Date().getTime() + (long)(Math.random() * 100);
        ROOT = new Node(this);
    }

    protected int size() {
        return ROOT.size();
    }

    protected double eff_size() {
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
                String s = "";
                s += String.format("Epoch: %3d, Size: %3d, Effective Size: %.3f\n", e, size(), eff_size());
                s += String.format("%4s %4s %4s %4s\n", "xMAP", "xPRE", "vMAP", "vPRE");
                for (int i = 0; i < CLASS_COUNT; i++) {
                    s += String.format("%.3f %.3f %.3f %.3f\n", mapErrorX.MAP[i], mapErrorX.precision[i], mapErrorV.MAP[i], mapErrorV.precision[i]);
                }
                s += String.format("%4s %4s %4s %4s\n", "----", "----", "----", "----");
                s += String.format("%.3f %.3f %.3f %.3f\n", mapErrorX.getAverageMAP(), mapErrorX.getAveragePrec(), mapErrorV.getAverageMAP(), mapErrorV.getAveragePrec());
                s += String.format("\n");

                System.out.print(s);
                if(output != null) {
                    output.write(s);
                    output.flush();
                    if (bestResult < mapErrorV.getAverageMAP()) {
                        bestResult = mapErrorV.getAverageMAP();
                        String ss = "";
                        ss += String.format("%3d %3d %3d %.3f %.3f %.5f %.3f %.3f %.3f %.3f\n", id, e, size(), eff_size(), LEARNING_RATE, LAMBDA, mapErrorX.getAverageMAP(), mapErrorX.getAveragePrec(), mapErrorV.getAverageMAP(), mapErrorV.getAveragePrec());
                        writeBestResult(ss);
                    }
                }
            } else if (this.type == DataSet.TYPE.MULTI_CLASS_CLASSIFICATION || this.type == DataSet.TYPE.BINARY_CLASSIFICATION) {
                ClassificationError Xerror = getClassificationError(this, X, type);
                ClassificationError Verror = getClassificationError(this, V, type);

                String s = "";
                s += String.format("Epoch : %d Size: %d Effective Size: %.3f X: %.3f V: %.3f\n", e, size(), (eff_size()), Xerror.getAccuracy(), Verror.getAccuracy());
                s += String.format("X Confusion Matrix\n" + Xerror.getStringConfusionMatrix());
                s += String.format("V Confusion Matrix\n" + Verror.getStringConfusionMatrix());
                s += "\n";

                System.out.print(s);
                if(output != null) {
                    output.write(s);
                    output.flush();

                    if (bestResult < Verror.getAccuracy()) {
                        bestResult = Verror.getAccuracy();
                        String ss = "";
                        ss += String.format("%3d %3d %3d %.3f %.3f %.5f %.3f %.3f\n", id, e, size(), eff_size(), LEARNING_RATE, LAMBDA, Xerror.getAccuracy(), Verror.getAccuracy());
                        writeBestResult(ss);
                    }
                }
            } else if (this.type == DataSet.TYPE.REGRESSION) {
                String s = "";
                s += String.format("Epoch: %d Size: %d MSE X: %.3f MSE V: %.3f\n", e, size(), getMeanSquareError(this, X), getMeanSquareError(this, V));
                s += "\n";
                System.out.print(s);
                if(output != null) {
                    output.write(s);
                    output.flush();
                }
            }

            Collections.shuffle(X);
            for (Instance instance : X) {
                ROOT.F(instance);
                ROOT.backPropagate(instance);
                ROOT.update();
            }
            LEARNING_RATE *= learning_rate_decay;
        }
    }

    private void writeBestResult(String ss) {
        String fileName = "run_results\\BT\\" + dataSet.name + "\\";
        if(use_rms_prop){
            fileName += "rms_prop\\";
        }else{
            fileName += "no_rms_prop\\";
        }
        if(use_linear_rho && !user_multi_modal){
            fileName += "linear_rho\\";
        }else if(!use_linear_rho && user_multi_modal){
            fileName += "multi_modal\\";
        }else if(!use_linear_rho && !user_multi_modal){
            fileName += "base_model\\";
        }else{
            fileName += "linear_and_multimodal\\";
        }

        fileName += "bestresults\\" + dataSet.name + ".txt";

        try {
            File file = new File(fileName);
            file.getParentFile().mkdirs();

            if(file.exists()) {
                boolean found = false;

                FileInputStream fstream = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String strLine;
                StringBuilder fileContent = new StringBuilder();
                while ((strLine = br.readLine()) != null) {
                    String tokens[] = strLine.split(" ");
                    if (tokens.length > 0) {
                        if (tokens[0].equals(id + "")) {
                            found = true;
                            fileContent.append(ss);
                            fileContent.append("\n");
                        } else {
                            fileContent.append(strLine);
                            fileContent.append("\n");
                        }
                    }
                }
                // Now fileContent will have updated content , which you can override into file
                if(found) {
                    FileWriter fstreamWrite = new FileWriter(fileName);
                    BufferedWriter out = new BufferedWriter(fstreamWrite);
                    out.write(fileContent.toString());
                    out.close();
                }else{
                    FileWriter fstreamWrite = new FileWriter(fileName, true);
                    BufferedWriter out = new BufferedWriter(fstreamWrite);
                    out.write(ss);
                    out.close();
                }
                //Close the input stream
                fstream.close();
            }else{
                FileWriter fstreamWrite = new FileWriter(fileName);
                BufferedWriter out = new BufferedWriter(fstreamWrite);
                out.write(ss);
                out.close();
            }
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public double[] evaluate(Instance instance) {
        return ROOT.F(instance);
    }
}