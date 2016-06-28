package DataSet.Reader;

import DataSet.DataSet.DataSet;
import DataSet.Instance.Instance;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by mertcan on 19.6.2016.
 */
public class PENDATAReader {



    public static DataSet getStatic() throws IOException {
        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();


        File file_test = new File("datasets" + File.separator +
                "PENDATA" + File.separator +
                "staind16.txt");

        File file_train = new File("datasets" + File.separator +
                "PENDATA" + File.separator +
                "stadep16.txt");

        readOriginalFileStatic(tra, file_train);
        readOriginalFileStatic(val, file_test);

        normalize(tra, val);

        return new DataSet("PENDATA Static", tra, val, DataSet.TYPE.MULTI_CLASS_CLASSIFICATION);
    }

    private static void readOriginalFileStatic(ArrayList<Instance> I, File file) throws IOException {
        String line;

        int ATTRIBUTE_COUNT = 16 * 16;
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            Scanner sc = new Scanner(line);
            double[] attributes = new double[ATTRIBUTE_COUNT];
            for (int i = 0; i < 16; i++) {
                line = scanner.nextLine();
                for (int j = 0; j < 16; j++) {
                    attributes[i * 16 + j] = Double.parseDouble(line.charAt(j) + "");
                }
            }
            int[] r = new int[10];
            Arrays.fill(r, 0);
            r[sc.nextInt()] = 1;
            Instance instance = new Instance();
            instance.r = r;
            instance.x = attributes;
            I.add(instance);
        }
    }

    public static DataSet getDynamic() throws IOException {
        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();


        File file_test = new File("datasets" + File.separator +
                "PENDATA" + File.separator +
                "dynind-08.txt");

        File file_train = new File("datasets" + File.separator +
                "PENDATA" + File.separator +
                "dyndep-08.txt");

        readOriginalFileDynamic(tra, file_train);
        readOriginalFileDynamic(val, file_test);

        normalize(tra, val);

        return new DataSet("PENDATA Dynamic", tra, val, DataSet.TYPE.MULTI_CLASS_CLASSIFICATION);
    }

    private static void readOriginalFileDynamic(ArrayList<Instance> I, File file) throws IOException {
        String line;

        int ATTRIBUTE_COUNT = 8 * 2;
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.contains(".SEGMENT DIGIT")) {
                double[] attributes = new double[ATTRIBUTE_COUNT];
                String classname = line.charAt(line.indexOf("\"") + 1) + "";
                line = scanner.nextLine();
                for (int i = 0; i < 8; i++) {
                    line = scanner.nextLine();
                    Scanner sc = new Scanner(line);
                    for (int j = 0; j < 2; j++) {
                        attributes[i * 2 + j] = sc.nextDouble();
                    }
                }
                int[] r = new int[10];
                Arrays.fill(r, 0);
                r[Integer.parseInt(classname)] = 1;
                Instance instance = new Instance();
                instance.r = r;
                instance.x = attributes;
                I.add(instance);
            } else {
                continue;
            }
        }
    }

    public static DataSet getBoth() throws IOException {
        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();


        File file_test = new File("datasets" + File.separator +
                "PENDATA" + File.separator +
                "dynind-08.txt");

        File file_train = new File("datasets" + File.separator +
                "PENDATA" + File.separator +
                "dyndep-08.txt");

        readOriginalFileDynamic(tra, file_train);
        readOriginalFileDynamic(val, file_test);


        ArrayList<Instance> tra2 = new ArrayList<>();
        ArrayList<Instance> val2 = new ArrayList<>();

        file_test = new File("datasets" + File.separator +
                "PENDATA" + File.separator +
                "staind16.txt");

        file_train = new File("datasets" + File.separator +
                "PENDATA" + File.separator +
                "stadep16.txt");

        readOriginalFileStatic(tra2, file_train);
        readOriginalFileStatic(val2, file_test);


        ArrayList<Instance> tra_merged = new ArrayList<>();
        ArrayList<Instance> val_merged = new ArrayList<>();

        if(tra.size() != tra2.size()) {
            System.out.println("trainings are not equal in size");
            return null;
        }else if(val.size() != val2.size()) {
            System.out.println("validations are not equal in size");
            return null;
        }else{
            for(int i = 0; i < tra.size(); i++){
                Instance ins = new Instance();
                if(checkREqual(tra.get(i).r, tra2.get(i).r)){
                    ins.r = tra.get(i).r;
                    ins.x = concat(tra.get(i).x, tra2.get(i).x);
                    tra_merged.add(ins);
                }else{
                    System.out.println("not ordered same");
                    return null;
                }
            }
            for(int i = 0; i < val.size(); i++){
                Instance ins = new Instance();
                if(checkREqual(val.get(i).r, val2.get(i).r)){
                    ins.r = val.get(i).r;
                    ins.x = concat(val.get(i).x, val2.get(i).x);
                    val_merged.add(ins);
                }else{
                    System.out.println("not ordered same");
                    return null;
                }
            }

            normalize(tra_merged, val_merged);

            return new DataSet("PENDATA Both", tra_merged, val_merged, DataSet.TYPE.MULTI_CLASS_CLASSIFICATION, tra.get(0).x.length);

        }
    }

    private static void normalize(ArrayList<Instance> x, ArrayList<Instance> v) {
        if(Constants.normalizeEnabled) {
            int ATTRIBUTE_COUNT = x.get(0).x.length;
            for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
                double mean = 0;
                for (Instance ins : x) {
                    mean += ins.x[i];
                }
                mean /= x.size();

                double stdev = 0;
                for (Instance ins : x) {
                    stdev += (ins.x[i] - mean) * (ins.x[i] - mean);
                }
                stdev /= (x.size() - 1);
                stdev = Math.sqrt(stdev);

                for (Instance ins : x) {
                    ins.x[i] -= mean;
                    if (stdev != 0)
                        ins.x[i] /= stdev;
                }
                for (Instance ins : v) {
                    ins.x[i] -= mean;
                    if (stdev != 0)
                        ins.x[i] /= stdev;
                }
            }
        }
    }

    private static boolean checkREqual(int[] r, int[] r1) {
        if(r1.length != r.length)
            return false;
        else{
            for(int i = 0; i < r.length; i++){
                if(r[i] != r1[i])
                    return false;
            }
            return true;
        }
    }

    private static double[] concat(double[] a, double[] b) {
        if (b.length == 0)
            return a;
        if (a.length == 0)
            return b;

        int aLen = a.length;
        int bLen = b.length;
        double[] c = new double[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

}
