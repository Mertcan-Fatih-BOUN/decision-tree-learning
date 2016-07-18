package Runner;

import BuddingTree.BT;
import DataSet.DataSet.DataSet;
import DataSet.Reader.FlickerReader;
import DataSet.Reader.MNISTReader;
import DataSet.Reader.MSDReader;
import DataSet.Reader.PENDATAReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

public class BTRunner {
    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) throws IOException, URISyntaxException {
        DataSet dataSet = MNISTReader.getBoth();
//        DataSet dataSet = MNISTReader.getMNIST();
//        DataSet dataSet = FlickerReader.getGithubDataset();
//        DataSet dataSet = PENDATAReader.getStatic();
//        DataSet dataSet = PENDATAReader.getDynamic();
//        DataSet dataSet = PENDATAReader.getBoth();
//        DataSet dataSet = MSDReader.getBoth();
        System.out.println("File read");


        double learning_rate = 0.01;
        double learning_rate_input_multiplier = 1;
        int epoch = 100;
        double lambda = 0.0001;
        double learning_rate_decay = 0.99;
        boolean use_linear_rho = false;
        boolean use_multi_modal = true;
        boolean use_rms_prop = false;
        double[] rms_prop_factors = new double[]{0.9, 0.1};
        double random_range = 0.001;

        String s = "";
        s += "Dataset: " + dataSet.name + "\n";
        s += "learning_rate: " + learning_rate +
                " learning_rate_input_multiplier: " + learning_rate_input_multiplier +
                " lambda: " + lambda +
                " learning_rate_decay :" + learning_rate_decay + "\n";
        if (use_linear_rho)
            s += "Linear rho\n";
        if (use_multi_modal)
            s += "Multi modal\n";
        if (use_rms_prop)
            s += "RMS PROP " + rms_prop_factors[0] + " " + rms_prop_factors[1] + "\n";
        s += "Special note: " + "\n";

        if (use_multi_modal && dataSet.first_modal_size == -1) {
            s += "Cannot use multi modal algorithm on non-multi-modal data set\n";
            System.out.print(s);
            return;
        }
        System.out.print(s);


        String fileName = "run_results\\BT\\" + dataSet.name + "\\";
        if(use_rms_prop){
            fileName += "rms_prop\\";
        }else{
            fileName += "no_rms_prop\\";
        }
        if(use_linear_rho && !use_multi_modal){
            fileName += "linear_rho\\";
        }else if(!use_linear_rho && use_multi_modal){
            fileName += "multi_modal\\";
        }else if(!use_linear_rho && !use_multi_modal){
            fileName += "base_model\\";
        }else{
            fileName += "linear_and_multimodal\\";
        }
        fileName += dataSet.name + "_" + learning_rate + "_" + lambda + ".txt";

        BufferedWriter output = null;
        try {
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            int i = 0;
            while(file.exists()){
                i++;
                file = new File(fileName.substring(0, fileName.indexOf(".txt")) + "(" + i + ").txt");
            }
            output = new BufferedWriter(new FileWriter(file));
            output.write(s);
            output.flush();
            BT btm = new BT(dataSet, use_linear_rho, use_multi_modal, random_range, use_rms_prop, rms_prop_factors, output);
            btm.learnTree(learning_rate, learning_rate_input_multiplier, epoch, lambda, learning_rate_decay);
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        finally {
            if ( output != null ) {
                output.close();
            }
        }

    }
}
