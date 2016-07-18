package Runner;

import BuddingTree.BT;
import BuddingTree.DoubleBT;
import DataSet.DataSet.DataSet;
import DataSet.Reader.MNISTReader;
import DataSet.Reader.MSDReader;
import DataSet.Reader.PENDATAReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by mertcan on 28.6.2016.
 */
public class DoubleBTRunner {
    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("Double BT");
        DataSet dataSet1 = PENDATAReader.getStatic();
        DataSet dataSet2 = PENDATAReader.getDynamic();
//        DataSet dataSet1 = MSDReader.getSoundOnly();
//        DataSet dataSet2 = MSDReader.getLyricsOnly();
        System.out.println("File read");

        double learning_rate = 0.1;
        double learning_rate_input_multiplier = 1;
        int epoch = 100;
        double lambda = 0.0001;
        double learning_rate_decay = 0.99;
        boolean use_linear_rho = false;
        boolean use_rms_prop = true;
        double[] rms_prop_factors = new double[]{0.9, 0.1};
        double random_range = 0.001;
        String s = "";
        s += "Dataset: " + dataSet1.name + "\n";
        s += "learning_rate: " + learning_rate +
                " learning_rate_input_multiplier: " + learning_rate_input_multiplier +
                " lambda: " + lambda +
                " learning_rate_decay :" + learning_rate_decay + "\n";
        if (use_linear_rho)
            s += "Linear rho\n";
        if (use_rms_prop)
            s += "RMS PROP " + rms_prop_factors[0] + " " + rms_prop_factors[1] + "\n";
        s += "Special note: " + "\n";
        System.out.print(s);


        String fileName = "run_results\\DobuleBT\\" + dataSet1.name + "_" + dataSet2.name + "\\";
        if(use_rms_prop){
            fileName += "rms_prop\\";
        }else{
            fileName += "no_rms_prop\\";
        }
        if(use_linear_rho){
            fileName += "linear_rho\\";
        }else if(!use_linear_rho){
            fileName += "base_model\\";
        }

        fileName += dataSet1.name + "_" + dataSet2.name + "_" + learning_rate + "_" + lambda + ".txt";

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

            DoubleBT btm = new DoubleBT(dataSet1, dataSet2, use_linear_rho, random_range, use_rms_prop, rms_prop_factors, output);
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