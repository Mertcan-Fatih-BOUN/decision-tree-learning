package BuddingTree;

import Analysis.ClassificationError;
import Analysis.Evaluable;
import Analysis.MAPError;
import Analysis.MultiEvaluable;
import DataSet.DataSet.DataSet;
import DataSet.Instance.Instance;
import Misc.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static Analysis.CalculateError.getClassificationError;
import static Analysis.CalculateError.getMAP_P50_error;
import static Analysis.CalculateError.getMeanSquareError;

/**
 * Created by mertcan on 28.6.2016.
 */
public class DoubleBT implements MultiEvaluable {
    boolean use_linear_rho = false;
    BufferedWriter output;

    long id;

    double bestResult = 0;

    double LEARNING_RATE;
    double LEARNING_RATE_INPUT_MULTIPLIER;

    double LAMBDA;

    double RANDOM_RANGE;
    final DataSet dataSet1;
    final DataSet dataSet2;
    private final DataSet.TYPE type;

    BT bt1;
    BT bt2;

    boolean use_rms_prop = false;
    double[] rms_prop_factors = new double[]{1, 1};

    /**
     * @param dataSet1       Data set will be used for training first tree
     * @param dataSet2       Data set will be used for training second tree
     * @param use_linear_rho Whether the BT will use linear leaf responses
     * @param random_range   [-random_range random_range] is the initial value of variables
     */
    public DoubleBT(DataSet dataSet1, DataSet dataSet2, boolean use_linear_rho, double random_range, boolean use_rms_prop, double[] rms_prop_factors, BufferedWriter output) {
        this.use_linear_rho = use_linear_rho;
        this.dataSet1 = dataSet1;
        this.dataSet2 = dataSet2;
        this.type = dataSet1.type;
        this.use_rms_prop = use_rms_prop;
        this.rms_prop_factors = rms_prop_factors;
        this.output = output;
        this.id = new Date().getTime() + (long)(Math.random() * 100);
        if(dataSet1.TRAINING_INSTANCES.size() != dataSet2.TRAINING_INSTANCES.size()){
            System.out.println("Error in datasets.");
        }else {
            bt1 = new BT(dataSet1, use_linear_rho, false, random_range, use_rms_prop, rms_prop_factors, null);
            bt2 = new BT(dataSet2, use_linear_rho, false, random_range, use_rms_prop, rms_prop_factors, null);
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
        this.LEARNING_RATE = learning_rate;
        this.LAMBDA = lambda;

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

                String s = "";
                s += String.format("Epoch: %3d, Size: %3d, Effective Size: %.3f\n", e, bt1.size() + bt2.size(), (bt1.eff_size() + bt2.eff_size()));
                s += String.format("%4s %4s %4s %4s\n", "xMAP", "xPRE", "vMAP", "vPRE");
                for (int i = 0; i < bt1.CLASS_COUNT; i++) {
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
                        ss += String.format("%3d %3d %3d %.3f %.3f %.5f %.3f %.3f %.3f %.3f\n", id, e, bt1.size() + bt2.size(), (bt1.eff_size() + bt2.eff_size()), LEARNING_RATE, LAMBDA, mapErrorX.getAverageMAP(), mapErrorX.getAveragePrec(), mapErrorV.getAverageMAP(), mapErrorV.getAveragePrec());
                        writeBestResult(ss);
                    }
                }
            } else if (this.type == DataSet.TYPE.MULTI_CLASS_CLASSIFICATION || this.type == DataSet.TYPE.BINARY_CLASSIFICATION) {
                ClassificationError Xerror = getClassificationError(this, dataSet1.TRAINING_INSTANCES, dataSet2.TRAINING_INSTANCES, type);
                ClassificationError Verror = getClassificationError(this, dataSet1.VALIDATION_INSTANCES, dataSet2.VALIDATION_INSTANCES, type);

                String s = "";
                s += String.format("Epoch : %d Size: %d Effective Size: %.3f X: %.3f V: %.3f\n", e, bt1.size() + bt2.size(), (bt1.eff_size() + bt2.eff_size()), Xerror.getAccuracy(), Verror.getAccuracy());
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
                        ss += String.format("%3d %3d %3d %.3f %.3f %.5f %.3f %.3f\n", id, e, bt1.size() + bt2.size(), (bt1.eff_size() + bt2.eff_size()), LEARNING_RATE, LAMBDA, Xerror.getAccuracy(), Verror.getAccuracy());
                        writeBestResult(ss);
                    }
                }
            } else if (this.type == DataSet.TYPE.REGRESSION)
                System.out.printf("TO-DO");



            Collections.shuffle(dataSet1.TRAINING_INSTANCES, Util.generator);
            Collections.shuffle(dataSet2.TRAINING_INSTANCES, Util.generator);
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

    private void writeBestResult(String ss) {
        String fileName = "run_results\\DobuleBT\\" + dataSet1.name + "_" + dataSet2.name + "\\";
        if(use_rms_prop){
            fileName += "rms_prop\\";
        }else{
            fileName += "no_rms_prop\\";
        }
        if(use_linear_rho){
            fileName += "linear_rho\\";
        }else{
            fileName += "base_model\\";
        }

        fileName += "bestresults\\" + dataSet1.name + "_" + dataSet2.name + ".txt";

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