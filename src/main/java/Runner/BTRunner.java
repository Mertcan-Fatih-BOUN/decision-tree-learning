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
        DataSet dataSet = null;

        int dataset_id = 9;
        double learning_rate = 0.1;
        double learning_rate_input_multiplier = 1;
        int epoch = 15;
        double lambda = 0.0001;
        double learning_rate_decay = 0.99;
        boolean use_linear_rho = false;
        boolean use_multi_modal = false;
        boolean user_weighted_alpha = false;
        boolean use_rms_prop = false;
        double[] rms_prop_factors = new double[]{0.9, 0.1};
        double random_range = 0.001;
        int frequency = 2500;

        if (args.length >= 13) {
            dataset_id = Integer.parseInt(args[0]);
            learning_rate = Double.parseDouble(args[1]);
            learning_rate_input_multiplier = Double.parseDouble(args[2]);
            epoch = Integer.parseInt(args[3]);
            lambda = Double.parseDouble(args[4]);
            learning_rate_decay = Double.parseDouble(args[5]);
            use_linear_rho = Boolean.parseBoolean(args[6]);
            use_multi_modal = Boolean.parseBoolean(args[7]);
            user_weighted_alpha = Boolean.parseBoolean(args[8]);
            use_rms_prop = Boolean.parseBoolean(args[9]);
            rms_prop_factors = new double[]{Double.parseDouble(args[10]), Double.parseDouble(args[11])};
            random_range = Double.parseDouble(args[12]);
            if(args.length > 13){
                frequency = Integer.parseInt(args[13]);
            }
        }

        switch(dataset_id){
            case 0:
                dataSet = MNISTReader.getMNIST();
                break;
            case 1:
                dataSet = MNISTReader.getMNIST_TOP();
                break;
            case 2:
                dataSet = MNISTReader.getMNIST_BOTTOM();
                break;
            case 3:
                dataSet = MNISTReader.getBoth();
                break;
            case 4:
                dataSet = MSDReader.getSoundOnly();
                break;
            case 5:
                dataSet = MSDReader.getLyricsOnly();
                break;
            case 6:
                dataSet = MSDReader.getBoth();
                break;
            case 7:
                dataSet = PENDATAReader.getStatic();
                break;
            case 8:
                dataSet = PENDATAReader.getDynamic();
                break;
            case 9:
                dataSet = PENDATAReader.getBoth();
                break;
            case 10:
                dataSet = MSDReader.getLyricsOnly(frequency);
                break;
            case 11:
                dataSet = MSDReader.getBoth(frequency);
                break;
            default:
                dataSet = MNISTReader.getMNIST();
        }

//        dataSet = MNISTReader.getMNIST();           0
//        dataSet = MNISTReader.getMNIST_TOP();       1
//        dataSet = MNISTReader.getMNIST_BOTTOM();    2
//        dataSet = MNISTReader.getBoth();            3
//
//        dataSet = MSDReader.getSoundOnly();         4
//        dataSet = MSDReader.getLyricsOnly();        5
//        dataSet = MSDReader.getBoth();              6
//
//        dataSet = PENDATAReader.getStatic();        7
//        dataSet = PENDATAReader.getDynamic();       8
//        dataSet = PENDATAReader.getBoth();          9


        System.out.println("File read");


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
        if (user_weighted_alpha)
            s += "Weighted alpha\n";
        if (use_rms_prop)
            s += "RMS PROP " + rms_prop_factors[0] + " " + rms_prop_factors[1] + "\n";
        s += "Special note: " + "\n";

        if (use_multi_modal && dataSet.first_modal_size == -1) {
            s += "Cannot use multi modal algorithm on non-multi-modal data set\n";
            System.out.print(s);
            return;
        }

        if(user_weighted_alpha && !use_multi_modal){
            s += "Cannot use weighted alpha on non-multi modal run\n";
            System.out.print(s);
            return;
        }
        System.out.print(s);

        String fileName = "run_results\\BT\\" + dataSet.name + "\\";
        if (use_rms_prop) {
            fileName += "rms_prop\\";
        } else {
            fileName += "no_rms_prop\\";
        }

        if(user_weighted_alpha){
            fileName += "weighted_average_and_";
        }

        if (use_linear_rho && !use_multi_modal) {
            fileName += "linear_rho\\";
        } else if (!use_linear_rho && use_multi_modal) {
            fileName += "multi_modal\\";
        } else if (!use_linear_rho && !use_multi_modal) {
            fileName += "base_model\\";
        } else {
            fileName += "linear_and_multimodal\\";
        }
        fileName += dataSet.name + "_" + learning_rate + "_" + lambda + ".txt";

        BufferedWriter output = null;
        try {
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            int i = 0;
            while (file.exists()) {
                i++;
                file = new File(fileName.substring(0, fileName.indexOf(".txt")) + "(" + i + ").txt");
            }
            output = new BufferedWriter(new FileWriter(file));
            output.write(s);
            output.flush();
            BT btm = new BT(dataSet, use_linear_rho, use_multi_modal, user_weighted_alpha, random_range, use_rms_prop, rms_prop_factors, output);
            btm.learnTree(learning_rate, learning_rate_input_multiplier, epoch, lambda, learning_rate_decay);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                output.close();
            }
        }

    }

    public static DataSet getMSDGenreWithFrequency(int mode, int frequency) throws IOException, URISyntaxException {
        switch (mode){
            case 1:
                return MSDReader.getLyricsOnly(frequency);
            case 2:
                return MSDReader.getBoth(frequency);
            case 3:
                return MSDReader.getSoundOnlyFilled0(frequency);
            case 4:
                return MSDReader.getLyricsOnlyFilled0(frequency);
            default:
                return null;
        }
    }
}
