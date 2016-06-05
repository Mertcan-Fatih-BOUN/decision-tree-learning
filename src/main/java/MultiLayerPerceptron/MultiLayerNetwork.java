package MultiLayerPerceptron;


import java.util.Random;


class MultiLayerNetwork {
    public Random r = new Random();
    int input_layer;
    int hidden_layer;
    int output_layer;
    double[][] W1;
    double[][] W2;
    Neuron[] input_neurons;
    Neuron[] hidden_neurons;
    Neuron[] output_neurons;
    double learn_rate = 0.006;
    double learn_rate_main = 0.006;

    MultiLayerNetwork(int input_layer, int hidden_layer, int output_layer) {
        this.hidden_layer = hidden_layer;
        this.input_layer = input_layer;
        this.output_layer = output_layer;
        if (hidden_layer != 0) {
            createW1();
            createW2();
            createInputNeurons();
            createHiddenNeurons();
            createOutputNeurons();
        } else {
            createW1();
            createInputNeurons();
            createHiddenNeurons();
            W2 = new double[0][1];
        }
    }

    private void createInputNeurons() {
        input_neurons = new Neuron[input_layer];
        for (int i = 0; i < input_layer; i++) {
            input_neurons[i] = new Neuron(true);
        }
    }

    private void createHiddenNeurons() {
        if (hidden_layer != 0) {
            hidden_neurons = new Neuron[hidden_layer];
            for (int i = 0; i < hidden_layer; i++) {
                hidden_neurons[i] = new Neuron(false);
            }
        } else {
            hidden_neurons = new Neuron[output_layer];
            for (int i = 0; i < output_layer; i++) {
                hidden_neurons[i] = new Neuron(false);
            }
        }
    }

    private void createOutputNeurons() {
        output_neurons = new Neuron[output_layer];
        for (int i = 0; i < output_layer; i++) {
            output_neurons[i] = new Neuron(false);
        }
    }

    private void createW2() {
        W2 = new double[output_layer][hidden_layer];
        for (int i = 0; i < output_layer; i++) {
            for (int j = 0; j < hidden_layer; j++) {
                W2[i][j] = (0.001 - 0.0001) * r.nextDouble() + 0.0001;
            }
        }
    }

    private void createW1() {
        if (hidden_layer != 0)
            W1 = new double[hidden_layer][input_layer];
        else
            W1 = new double[output_layer][input_layer];
        for (int i = 0; i < W1.length; i++) {
            for (int j = 0; j < input_layer; j++) {
                W1[i][j] = (0.001 - 0.0001) * r.nextDouble() + 0.0001;
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class Neuron {
        double row;
        double output;
        boolean isInputLayer = false;

        Neuron(boolean isInputLayer) {
            this.isInputLayer = isInputLayer;
        }

        void feed_neuron(double row) {
            this.row = row;
            if (!isInputLayer)
                output = sigma(row);
            else
                output = row;
        }

        private double sigma(double row) {
            return 1 / (1 + (Math.exp(-row)));
        }

        double derivative_sigma() {
            return output * (1 - output);
        }
    }
}
