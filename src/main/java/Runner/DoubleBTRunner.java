package Runner;

import BuddingTree.BT;
import BuddingTree.DoubleBT;
import DataSet.DataSet.DataSet;
import DataSet.Reader.MNISTReader;
import DataSet.Reader.MSDReader;
import DataSet.Reader.PENDATAReader;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by mertcan on 28.6.2016.
 */
public class DoubleBTRunner {
    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("Double BT");
//        DataSet dataSet1 = PENDATAReader.getStatic();
//        DataSet dataSet2 = PENDATAReader.getDynamic();
        DataSet dataSet1 = MSDReader.getSoundOnly();
        DataSet dataSet2 = MSDReader.getLyricsOnly();
        System.out.println("File read");
        double learning_rate = 0.1;
        double learning_rate_input_multiplier = 1;
        int epoch = 100;
        double lambda = 0.0001;
        double learning_rate_decay = 0.99;
        boolean use_linear_rho = true;
        double random_range = 0.001;
        System.out.println("Dataset: " + dataSet1.name);
        System.out.println("learning_rate: " + learning_rate +
                " learning_rate_input_multiplier: " + learning_rate_input_multiplier +
                " lambda: " + lambda +
                " learning_rate_decay :" + learning_rate_decay);
        if (use_linear_rho)
            System.out.println("Linear rho");
        System.out.println("Special note: ");

        DoubleBT btm = new DoubleBT(dataSet1, dataSet2, use_linear_rho, random_range);
        btm.learnTree(learning_rate, learning_rate_input_multiplier, epoch, lambda, learning_rate_decay);
    }
}