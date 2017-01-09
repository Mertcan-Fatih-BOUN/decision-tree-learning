package Runner;

import BuddingTree.BT;
import com.sun.javafx.binding.StringFormatter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by mertcan on 8.01.2017.
 */
public class BatchGenerator {
    static int dataset_id = 1;
    static double learning_rate = 0.1;
    static double learning_rate_input_multiplier = 1;
    static int epoch = 15;
    static double lambda = 0.0001;
    static double learning_rate_decay = 0.99;
    static boolean use_linear_rho = false;
    static boolean use_multi_modal = false;
    static boolean user_weighted_alpha = false;
    static boolean use_rms_prop = false;
    static double[] rms_prop_factors = new double[]{0.9, 0.1};
    static double random_range = 0.001;
    static int[] frequency = new int[]{250, 500, 1000, 2500, 5000};
    static int frequency_ = 250;

    static String fullContent = "";
    static boolean writeLock = true;

    public static void main(String[] args) throws IOException {
        writeLock = true;
        generateOneBatFile();

        /*writeLock = false;
        generateBatchesForNormalBT();
        generateBatchesForNormalLinearBT();
        generateBatchesForNormalMutliGatingBT();
        generateBatchesForLinearMutliGatingBT();
        generateBatchesForNormalWeightedMutliGatingBT();
        generateBatchesForLinearWeightedMutliGatingBT();
        generateBatchesForNormalDoubleBT();
        generateBatchesForLinearDoubleBT();*/
    }

    private static void generateOneBatFile() throws IOException {
        generateBatchesForNormalBT();
        generateBatchesForNormalLinearBT();
        generateBatchesForNormalMutliGatingBT();
        generateBatchesForLinearMutliGatingBT();
        generateBatchesForNormalWeightedMutliGatingBT();
        generateBatchesForLinearWeightedMutliGatingBT();
        generateBatchesForNormalDoubleBT();
        generateBatchesForLinearDoubleBT();

        writeLock = false;
        writeToFile("AllinOne", "", fullContent);
    }

    public static String generateStringForRun(){
        String run = "";
        if(writeLock){
            run = "start \"dt\" /b /WAIT java -cp log Runner.BTRunner %d %.4f %.4f %d %.4f %.4f %s %s %s %s %.4f %.4f %.4f %d> nul";
        }else{
            run = "start \"dt\" /b java -cp log Runner.BTRunner %d %.4f %.4f %d %.4f %.4f %s %s %s %s %.4f %.4f %.4f %d> nul";
        }
        return String.format(Locale.ENGLISH, run, dataset_id, learning_rate, learning_rate_input_multiplier, epoch, lambda, learning_rate_decay, use_linear_rho, use_multi_modal, user_weighted_alpha, use_rms_prop, rms_prop_factors[0], rms_prop_factors[1], random_range, frequency_);
    }

    public static String generateStringForRunDoubleBT(){
        String run = "";
        if(writeLock){
            run = "start \"dt\" /b /WAIT java -cp log Runner.DoubleBTRunner %d %.4f %.4f %d %.4f %.4f %s %s %.4f %.4f %.4f %d> nul";
        }else{
            run = "start \"dt\" /b java -cp log Runner.DoubleBTRunner %d %.4f %.4f %d %.4f %.4f %s %s %.4f %.4f %.4f %d> nul";
        }
        return String.format(Locale.ENGLISH, run, dataset_id, learning_rate, learning_rate_input_multiplier, epoch, lambda, learning_rate_decay, use_linear_rho, use_rms_prop, rms_prop_factors[0], rms_prop_factors[1], random_range, frequency_);
    }

    public static void generateBatchesForNormalBT() throws IOException {
        dataset_id = 1;
        learning_rate = 0.1;
        epoch = 15;
        use_linear_rho = false;
        use_multi_modal = false;
        user_weighted_alpha = false;
        String filename_suffix = "bt_normal";
        generateWithCurrentSettings(filename_suffix);
    }

    public static void generateBatchesForNormalLinearBT() throws IOException {
        dataset_id = 1;
        learning_rate = 0.1;
        epoch = 15;
        use_linear_rho = true;
        use_multi_modal = false;
        user_weighted_alpha = false;
        String filename_suffix = "bt_normal_linear";
        generateWithCurrentSettings(filename_suffix);

    }

    public static void generateBatchesForNormalMutliGatingBT() throws IOException {
        dataset_id = 1;
        learning_rate = 0.1;
        epoch = 15;
        use_linear_rho = false;
        use_multi_modal = true;
        user_weighted_alpha = false;
        String filename_suffix = "bt_normal_multi_gating";
        generateWithCurrentSettings(filename_suffix);
    }

    public static void generateBatchesForLinearMutliGatingBT() throws IOException {
        dataset_id = 1;
        learning_rate = 0.1;
        epoch = 15;
        use_linear_rho = true;
        use_multi_modal = true;
        user_weighted_alpha = false;
        String filename_suffix = "bt_linear_multi_gating";
        generateWithCurrentSettings(filename_suffix);
    }

    public static void generateBatchesForNormalWeightedMutliGatingBT() throws IOException {
        dataset_id = 1;
        learning_rate = 0.1;
        epoch = 15;
        use_linear_rho = false;
        use_multi_modal = true;
        user_weighted_alpha = true;
        String filename_suffix = "bt_normal_weighted_multi_gating";
        generateWithCurrentSettings(filename_suffix);
    }

    public static void generateBatchesForLinearWeightedMutliGatingBT() throws IOException {
        dataset_id = 1;
        learning_rate = 0.1;
        epoch = 15;
        use_linear_rho = true;
        use_multi_modal = true;
        user_weighted_alpha = true;
        String filename_suffix = "bt_linear_weighted_multi_gating";
        generateWithCurrentSettings(filename_suffix);
    }

    public static void generateBatchesForNormalDoubleBT() throws IOException {
        dataset_id = 1;
        learning_rate = 0.1;
        epoch = 15;
        use_linear_rho = false;
        use_multi_modal = false;
        String filename_suffix = "doublebt_normal";
        generateWithCurrentSettingsDoubleBT(filename_suffix);
    }

    public static void generateBatchesForLinearDoubleBT() throws IOException {
        dataset_id = 1;
        learning_rate = 0.1;
        epoch = 15;
        use_linear_rho = true;
        use_multi_modal = false;
        String filename_suffix = "doublebt_linear";
        generateWithCurrentSettingsDoubleBT(filename_suffix);
    }

    private static void generateWithCurrentSettings(String filename_suffix) throws IOException {

        for(int i = 1; i < 12; i++){
            if(i > 4 && i < 7)
                continue;
            if((i == 1 || i == 2 || i== 4 || i == 5 || i == 7 || i == 8 || i == 10) && use_multi_modal){
                continue;
            }
            boolean generate_frequecy = false;
            if(i > 9){
                generate_frequecy = true;
            }
            dataset_id = i;;
            learning_rate = 0.1;
            String batch_script = "";
            if(generate_frequecy){
                for(int k = 0; k < frequency.length; k++){
                    batch_script = "";
                    frequency_ = frequency[k];
                    learning_rate = 0.1;
                    for(int j = 0; j < 4; j++){
                        batch_script += generateStringForRun() + "\n";
                        learning_rate = learning_rate * Math.pow(10, -1);
                    }
                    writeToFile(filename_suffix, "freq_" + frequency_ + "_" + i, batch_script);
                }
            }else{
                for(int j = 0; j < 4; j++){
                    batch_script += generateStringForRun() + "\n";
                    learning_rate = learning_rate * Math.pow(10, -1);
                }
                writeToFile(filename_suffix, i + "", batch_script);
            }


        }
    }



    private static void generateWithCurrentSettingsDoubleBT(String filename_suffix) throws IOException {
        for(int i = 0; i < 4; i++){
            if(i == 1)
                continue;
            boolean generate_frequecy = false;
            if(i == 3){
                generate_frequecy = true;
            }
            dataset_id = i;;
            learning_rate = 0.1;
            String batch_script = "";
            if(generate_frequecy){
                for(int k = 0; k < frequency.length; k++){
                    batch_script = "";
                    frequency_ = frequency[k];
                    learning_rate = 0.1;
                    for(int j = 0; j < 4; j++){
                        batch_script += generateStringForRunDoubleBT() + "\n";
                        learning_rate = learning_rate * Math.pow(10, -1);
                    }
                    writeToFile(filename_suffix, "freq_" + frequency_ + "_" + i, batch_script);
                }
            }else{
                for(int j = 0; j < 4; j++){
                    batch_script += generateStringForRunDoubleBT() + "\n";
                    learning_rate = learning_rate * Math.pow(10, -1);
                }
                writeToFile(filename_suffix, i + "", batch_script);
            }


        }
    }

    public static void writeToFile(String filename_suffix, String filename, String content) throws IOException {
        if(writeLock){
            fullContent += content + "\n\n";
        }else {
            String fileName = "batches\\" + filename_suffix + "\\batch" + filename + ".bat";
            BufferedWriter output = null;
            try {
                File file = new File(fileName);
                file.getParentFile().mkdirs();
                output = new BufferedWriter(new FileWriter(file));
                output.write(content);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (output != null) {
                    output.close();
                }
            }
        }
    }
}
