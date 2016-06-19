package Runner;

import BuddingTree.BT;
import DataSet.DataSet.DataSet;
import DataSet.Reader.FlickerReader;

import java.io.IOException;
import java.net.URISyntaxException;

public class BTRunner {
    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) throws IOException, URISyntaxException {
        DataSet dataSet = FlickerReader.getGithubDataset();
        System.out.println("File read");
        double learning_rate = 0.5;
        double learning_rate_input_multiplier = 0.005;
        int epoch = 100;
        double lambda = 0.0001;
        double learning_rate_decay = 0.99;
        boolean use_linear_rho = false;
        boolean use_multi_modal = true;
        double random_range = 0.001;
        System.out.println("Dataset: " + dataSet.name);
        System.out.println("learning_rate: " + learning_rate +
                " learning_rate_input_multiplier: " + learning_rate_input_multiplier +
                " lambda: " + lambda +
                " learning_rate_decay :" + learning_rate_decay);
        if (use_linear_rho)
            System.out.println("Linear rho");
        if (use_multi_modal)
            System.out.println("Multi modal");
        System.out.println("Special note: ");

        if (use_multi_modal && dataSet.first_modal_size == -1) {
            System.out.println("Cannot use multi modal algorithm on non-multi-modal data set");
            return;

        }
        BT btm = new BT(dataSet, use_linear_rho, use_multi_modal, random_range);
        btm.learnTree(learning_rate, learning_rate_input_multiplier, epoch, lambda, learning_rate_decay);
    }
}
