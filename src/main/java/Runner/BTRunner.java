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
        double learning_rate_w = 0.5 * 0.003;
        double learning_rate_gamma = 0.5;
        double learning_rate_rho = 0.5;
        double learning_rate_p = 0.5;
        int epoch = 100;
        double lambda = 0.0001;
        double learning_rate_decay = 0.99;
        boolean use_linear_rho = false;
        boolean use_multi_modal = true;
        System.out.println("Dataset: " + dataSet.name);
        System.out.println("learning_rate_w: " + learning_rate_w +
                " learning_rate_gamma: " + learning_rate_gamma +
                " learning_rate_rho: " + learning_rate_rho +
                " learning_rate_p: " + learning_rate_p +
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
        BT btm = new BT(dataSet, use_linear_rho, use_multi_modal);
        btm.learnTree(learning_rate_w, learning_rate_gamma, learning_rate_rho, learning_rate_p, epoch, lambda, learning_rate_decay);
    }
}
