package MultiLayerPerceptron;


import Analysis.ClassificationError;
import Analysis.Evaluable;
import Analysis.MAPError;
import DataSet.DataSet.DataSet;
import DataSet.Instance.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static Analysis.CalculateError.*;

public class MLP implements Evaluable {

    private MultiLayerNetwork multiLayerNetwork;
    public Random r = new Random();
    private int input_number_train = 60000;
    private int hidden_neuron_number = 40;//28*28*2/3;
    private int input_dimension = 28 * 28;
    private int output_dimension = 10;
    private int number_of_epochs = 50;
    private boolean print_each_epoch = false;
    private ArrayList<Instance> train_instances = new ArrayList<>();
    private ArrayList<Instance> test_instances = new ArrayList<>();

    private double[] B2 = new double[output_dimension];
    private double[][] G2 = new double[output_dimension][hidden_neuron_number + 1];
    private double[] B1 = new double[hidden_neuron_number + 1];
    private double[][] G1 = new double[hidden_neuron_number][input_dimension + 1];

    private DataSet.TYPE type;


    public MLP(DataSet dataSet, int hidden_number, int epochs, double learn_rate, boolean print_each) {
        this.type = dataSet.type;
        train_instances = dataSet.TRAINING_INSTANCES;
        test_instances = dataSet.VALIDATION_INSTANCES;
        input_dimension = train_instances.get(0).x.length;
        input_number_train = train_instances.size();
        output_dimension = dataSet.TRAINING_INSTANCES.get(0).r.length;
        multiLayerNetwork = new MultiLayerNetwork(input_dimension, hidden_number, output_dimension);
        multiLayerNetwork.learn_rate_main = learn_rate;
        multiLayerNetwork.learn_rate = learn_rate;
        number_of_epochs = epochs;
        hidden_neuron_number = hidden_number;
        print_each_epoch = print_each;

        B2 = new double[output_dimension];
        G2 = new double[output_dimension][hidden_neuron_number + 1];
        B1 = new double[hidden_neuron_number + 1];
        G1 = new double[hidden_neuron_number][input_dimension + 1];
    }

    public void runPerceptron() {
        if (hidden_neuron_number == 0)
            multiLayerNetwork = new MultiLayerNetwork(input_dimension + 1, hidden_neuron_number, output_dimension);
        else
            multiLayerNetwork = new MultiLayerNetwork(input_dimension + 1, hidden_neuron_number + 1, output_dimension);

        createArrays();

        train_backPropagate();
    }

    private void createArrays() {
        for (Instance T : train_instances) {
            double[] x_ = new double[T.x.length + 1];
            System.arraycopy(T.x, 0, x_, 0, input_dimension);
            x_[input_dimension] = 1;
            T.x = new double[0];
            T.x = x_;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void train_backPropagate() {
        ArrayList<Integer> shuffler = new ArrayList<>();
        for (int i = 0; i < input_number_train; i++) shuffler.add(i);

        for (int trial = 0; trial < number_of_epochs; trial++) {
            Collections.shuffle(shuffler);
            for (int i = 0; i < input_number_train; i++) {
                int theInput = shuffler.get(i);
                double[] output_hat = feed_forward(train_instances.get(theInput).x);
                int[] output = train_instances.get(theInput).r;
                if (multiLayerNetwork.hidden_layer != 0) {
                    for (int j = 0; j < B2.length; j++) {
                        B2[j] = -2 * (output[j] - output_hat[j]) * multiLayerNetwork.output_neurons[j].derivative_sigma();
                    }
                    for (int j = 0; j < G2.length; j++) {
                        for (int t = 0; t < G2[0].length; t++) {
                            G2[j][t] = B2[j] * multiLayerNetwork.hidden_neurons[t].output;
                        }
                    }

                    for (int j = 0; j < B1.length; j++) {
                        double temp = 0;
                        for (int t = 0; t < B2.length; t++) {
                            temp += B2[t] * multiLayerNetwork.W2[t][j];
                        }
                        if (j != B1.length - 1)
                            temp *= multiLayerNetwork.hidden_neurons[j].derivative_sigma();
                        B1[j] = temp;
                    }
                    for (int j = 0; j < G1.length; j++) {
                        for (int t = 0; t < G1[0].length; t++) {
                            G1[j][t] = B1[j] * multiLayerNetwork.input_neurons[t].output;
                        }
                    }


                    for (int j = 0; j < G1.length; j++) {
                        for (int t = 0; t < G1[0].length; t++) {
                            multiLayerNetwork.W1[j][t] -= multiLayerNetwork.learn_rate * G1[j][t];
                        }
                    }
                    for (int j = 0; j < G2.length; j++) {
                        for (int t = 0; t < G2[0].length; t++) {
                            multiLayerNetwork.W2[j][t] -= multiLayerNetwork.learn_rate * G2[j][t];
                        }
                    }
                } else {
                    G2 = new double[output_dimension][input_dimension + 1];
                    for (int j = 0; j < B2.length; j++) {
                        if (j != B2.length - 1)
                            B2[j] = -2 * (output[j] - output_hat[j]) * multiLayerNetwork.hidden_neurons[j].derivative_sigma();
                        else
                            B2[j] = -2 * (output[j] - output_hat[j]);
                    }
                    for (int j = 0; j < G2.length; j++) {
                        for (int t = 0; t < G2[0].length; t++) {
                            G2[j][t] = B2[j] * multiLayerNetwork.input_neurons[t].output;
                        }
                    }
                    for (int j = 0; j < G2.length; j++) {
                        for (int t = 0; t < G2[0].length; t++) {
                            multiLayerNetwork.W1[j][t] -= multiLayerNetwork.learn_rate * G2[j][t];
                        }
                    }
                }

            }
            if (print_each_epoch) {
                if (this.type == DataSet.TYPE.MULTI_LABEL_CLASSIFICATION) {
                    MAPError mapErrorX = getMAP_P50_error(this, train_instances);
                    MAPError mapErrorV = getMAP_P50_error(this, test_instances);
                    System.out.printf("Epoch: %3d\n", trial);
                    System.out.printf("%4s %4s %4s %4s\n", "xMAP", "xPRE", "vMAP", "vPRE");
                    for (int i = 0; i < mapErrorX.MAP.length; i++) {
                        System.out.printf("%.2f %.2f %.2f %.2f\n", mapErrorX.MAP[i], mapErrorX.precision[i], mapErrorV.MAP[i], mapErrorV.precision[i]);
                    }
                    System.out.printf("%4s %4s %4s %4s\n", "----", "----", "----", "----");
                    System.out.printf("%.2f %.2f %.2f %.2f\n", mapErrorX.getAverageMAP(), mapErrorX.getAveragePrec(), mapErrorV.getAverageMAP(), mapErrorV.getAveragePrec());
                    System.out.println();
                } else if (this.type == DataSet.TYPE.MULTI_CLASS_CLASSIFICATION || this.type == DataSet.TYPE.BINARY_CLASSIFICATION) {
                    ClassificationError Xerror = getClassificationError(this, train_instances, type);
                    ClassificationError Verror = getClassificationError(this, test_instances, type);
                    System.out.printf("Epoch : %d X: %.2f V: %.2f\n", trial, Xerror.getAccuracy(), Verror.getAccuracy());
                    System.out.println("X Confusion Matrix\n" + Xerror.getStringConfusionMatrix());
                    System.out.println("V Confusion Matrix\n" + Verror.getStringConfusionMatrix());
                } else if (this.type == DataSet.TYPE.REGRESSION)
                    System.out.printf("Epoch: %d MSE X: %.2f MSE V: %.2f\n", trial, getMeanSquareError(this, train_instances), getMeanSquareError(this, test_instances));

            }
            multiLayerNetwork.learn_rate *= 0.99;
        }
    }

    private double[] feed_forward(double[] input) {
        double[] output_hat = new double[output_dimension];

        for (int i = 0; i < multiLayerNetwork.input_layer; i++) {
            multiLayerNetwork.input_neurons[i].feed_neuron(input[i]);
        }

        if (multiLayerNetwork.hidden_layer != 0) {
            for (int i = 0; i < multiLayerNetwork.hidden_layer - 1; i++) {
                double sum_hidden_neuron_i = 0;
                for (int j = 0; j < multiLayerNetwork.input_layer; j++) {
                    sum_hidden_neuron_i += multiLayerNetwork.input_neurons[j].output * multiLayerNetwork.W1[i][j];
                }
                multiLayerNetwork.hidden_neurons[i].feed_neuron(sum_hidden_neuron_i);
            }
            multiLayerNetwork.hidden_neurons[multiLayerNetwork.hidden_layer - 1].output = 1;
            multiLayerNetwork.hidden_neurons[multiLayerNetwork.hidden_layer - 1].row = 1;

            for (int i = 0; i < multiLayerNetwork.output_layer; i++) {
                double sum_hidden_neuron_i = 0;
                for (int j = 0; j < multiLayerNetwork.hidden_layer; j++) {
                    sum_hidden_neuron_i += multiLayerNetwork.hidden_neurons[j].output * multiLayerNetwork.W2[i][j];
                }
                multiLayerNetwork.output_neurons[i].feed_neuron(sum_hidden_neuron_i);

                output_hat[i] = multiLayerNetwork.output_neurons[i].output;
            }
        } else {
            for (int i = 0; i < multiLayerNetwork.output_layer; i++) {
                double sum_hidden_neuron_i = 0;
                for (int j = 0; j < multiLayerNetwork.input_layer; j++) {
                    sum_hidden_neuron_i += multiLayerNetwork.input_neurons[j].output * multiLayerNetwork.W1[i][j];
                }
                multiLayerNetwork.hidden_neurons[i].feed_neuron(sum_hidden_neuron_i);
                output_hat[i] = multiLayerNetwork.hidden_neurons[i].output;
//                output_hat[i] = sum_hidden_neuron_i;
            }
        }


        return output_hat;
    }


    @Override
    public double[] evaluate(Instance instance) {
        double[] atts = Arrays.copyOf(instance.x, instance.x.length + 1);
        atts[atts.length - 1] = 1;
        instance.y = feed_forward(atts);
        return instance.y;
    }
}
