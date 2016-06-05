package Runner;


import DataSet.DataSet.DataSet;
import DataSet.Reader.MNISTReader;
import MultiLayerPerceptron.MLP;

import java.io.IOException;
import java.net.URISyntaxException;

public class MLPRunner {
    public static void main(String[] args) throws IOException, URISyntaxException {
        //DataSet dataSet = FlickerReader.getGithubDatasetNoTag();
        //DataSet dataSet = Readers.MSDReader.getBoth();
        DataSet dataSet = MNISTReader.getMNIST();
        System.out.println("File read");

        int neuron_number = 15;
        int epoch = 50;
        double learn_rate = 0.01;
        boolean print_each_epoch = true;

        System.out.println("Dataset: " + dataSet.name);
        System.out.println("learn_rate: " + learn_rate +
                " neuron_number: " + neuron_number +
                " epoch: " + epoch);
        System.out.println("Special note: ");

        //noinspection ConstantConditions
        MLP b = new MLP(dataSet, neuron_number, epoch, learn_rate, print_each_epoch);
        b.runPerceptron();
    }
}
