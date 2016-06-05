package Runner;


import DataSet.DataSet.DataSet;
import DataSet.Reader.FlickerReader;
import DataSet.Reader.MNISTReader;
import SoftDecisionTree.SDT;

import java.io.IOException;
import java.net.URISyntaxException;

public class SDTRunner {
    public static void main(String[] args) throws IOException, URISyntaxException {
        DataSet dataSet = MNISTReader.getMNIST();
        System.out.println("File read");
        double learning_rate = 0.1;
        int epoch = 3;
        int max_step = 5;
        double learning_rate_decay = 0.99;
        System.out.println("Dataset: " + dataSet.name);
        System.out.println("learning_rate: " + learning_rate + " max_step: " + max_step + " learning_rate_decay :" + learning_rate_decay);
        System.out.println("Special note: ");
        SDT sdt = new SDT(dataSet);
        sdt.learnTree(epoch, max_step, learning_rate, learning_rate_decay);
    }
}
