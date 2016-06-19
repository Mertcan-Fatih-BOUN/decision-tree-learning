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


    public static DataSet readStatic() throws IOException {
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

    public static DataSet readDynamic() throws IOException {
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

}
