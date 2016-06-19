package DataSet.Writer;

import DataSet.DataSet.*;
import DataSet.Instance.Instance;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by mertcan on 19.6.2016.
 */
public class WekaFormat {
    private DataSet dataSet;
    private int attributeCount;
    private final static int TRAINING = 0;
    private final static int TEST = 1;

    public WekaFormat(DataSet dataSet){
        if(dataSet.type == DataSet.TYPE.MULTI_LABEL_CLASSIFICATION)
            throw new IllegalArgumentException("Not appropriate for multi label classifications");
        this.dataSet = dataSet;
        attributeCount = dataSet.TRAINING_INSTANCES.get(0).x.length;
    }

    public void outputARFFFile(){
        System.out.println("Writing Training File");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("datasets" + File.separator + "arffs" + File.separator + dataSet.getName() + "_training.arff"))) {
            writeHeaders(writer, TRAINING);
            writeData(writer, TRAINING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Writing Test File");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("datasets" + File.separator + "arffs" + File.separator + dataSet.getName() + "_test.arff"))) {
            writeHeaders(writer, TEST);
            writeData(writer, TEST);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHeaders(BufferedWriter writer, int mode) throws IOException {
        String trainingOrTest = "training";
        if(mode == TEST)
            trainingOrTest = "test";
        writer.write("@relation " + dataSet.getName().replaceAll("\\s+", "_") + "_" + trainingOrTest + "\n");
        for(int i = 0; i < attributeCount; i++) {
            writer.write("@attribute attribute" + i + " numeric\n");
        }
        String s = "{0";
        for(int i = 1; i < dataSet.TRAINING_INSTANCES.get(0).r.length; i++)
            s += "," + i;
        s += "}";
        writer.write("@attribute class " + s + "\n");
        writer.write("@data\n");
    }

    private void writeData(BufferedWriter writer, int mode) throws IOException {
        ArrayList<Instance> instances;
        if(mode == TRAINING)
            instances = dataSet.TRAINING_INSTANCES;
        else
            instances = dataSet.VALIDATION_INSTANCES;
        for(int i = 0; i < instances.size(); i++){
            writer.write(toARFFData(instances.get(i)) + "\n");
            System.out.printf("\r%.2f", (i * 1.0) / instances.size());
        }
        System.out.println();
    }

    private String toARFFData(Instance instance) {
        String s = "";
        for(int i = 0; i < attributeCount; i++){
            s += instance.x[i] + ",";
        }
        if(instance.r.length == 1)
            s += instance.r[0];
        else{
            for(int i = 0; i < instance.r.length; i++)
                if(instance.r[i] == 1)
                    s += i;
        }
        return s;
    }


}
