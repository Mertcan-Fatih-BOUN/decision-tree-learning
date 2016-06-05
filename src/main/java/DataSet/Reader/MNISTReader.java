package DataSet.Reader;


import DataSet.DataSet.DataSet;
import DataSet.Instance.Instance;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

@SuppressWarnings("unused")
public class MNISTReader {
    private static int ATTRIBUTE_COUNT;

    @SuppressWarnings("unused")
    public static DataSet getMNIST() throws URISyntaxException, IOException {
        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();


        File file_test = new File("datasets" + File.separator +
                "mnist" + File.separator +
                "test.txt");

        File file_train = new File("datasets" + File.separator +
                "mnist" + File.separator +
                "train.txt");

        readFile(tra, file_train);
        readFile(val, file_test);

        normalize(tra, val);

        return new DataSet("MNIST", tra, val, DataSet.TYPE.MULTI_CLASS_CLASSIFICATION);
    }

    private static void readFile(ArrayList<Instance> I, File file) throws IOException {
        String line;

        InputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);

        line = br.readLine();

        br.close();
        String[] s;
        String splitter;
        if (!line.contains(","))
            splitter = "\\s+";
        else
            splitter = ",";
        s = line.split(splitter);

        ATTRIBUTE_COUNT = s.length - 1;
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            s = line.split(splitter);

            double[] attributes = new double[ATTRIBUTE_COUNT];
            for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
                attributes[i] = Double.parseDouble(s[i]);
            }
            String className = s[ATTRIBUTE_COUNT];

            int[] r = new int[10];
            Arrays.fill(r, 0);
            r[Integer.parseInt(className)] = 1;
            Instance instance = new Instance();
            instance.r = r;
            instance.x = attributes;

            I.add(instance);
        }
    }

    private static void normalize(ArrayList<Instance> x, ArrayList<Instance> v) {
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
