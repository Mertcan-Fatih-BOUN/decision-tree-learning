package DataSet.Reader;

import DataSet.DataSet.DataSet;
import DataSet.Instance.Instance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.*;

public class FlickerReader {
    static {
        Locale.setDefault(Locale.US);
    }

    private static ArrayList<double[]> edgehistogram;
    private static ArrayList<double[]> homogeneoustexture;
    private static ArrayList<double[]> tags;
    private static ArrayList<int[]> annotations;
    private static int tag_size = 0;

    private static final String[] POTENTIAL_LABELS = new String[]{"bird", "baby", "animals", "car", "clouds", "dog", "female", "flower",
            "food", "indoor", "lake", "male", "night", "people", "plant_life", "portrait", "river", "sea",
            "sky", "structures", "sunset", "transport", "tree", "water", "bird_r1", "baby_r1", "car_r1", "clouds_r1", "dog_r1", "female_r1",
            "flower_r1", "male_r1", "night_r1", "people_r1", "portrait_r1", "river_r1", "sea_r1",
            "tree_r1"};

    private static ArrayList<double[]> readEdgehistogram() throws FileNotFoundException {
        if (edgehistogram != null)
            return edgehistogram;

        edgehistogram = new ArrayList<>();
        for (int i = 0; i < 25000; i++) {
            double[] values = new double[150];
            Scanner scanner = new Scanner(new BufferedReader(new FileReader("datasets" + File.separator +
                    "mirflickr" + File.separator +
                    "features edgehistogram30k" + File.separator +
                    i / 10000 + File.separator +
                    i + ".txt")));

            for (int j = 0; j < values.length; j++) {
                values[j] = scanner.nextDouble();
            }
            edgehistogram.add(values);
        }
        normalize(edgehistogram);
        return edgehistogram;
    }

    private static ArrayList<double[]> readhomogeneoustexture() throws FileNotFoundException {
        if (homogeneoustexture != null)
            return homogeneoustexture;

        homogeneoustexture = new ArrayList<>();
        for (int i = 0; i < 25000; i++) {
            double[] values = new double[43];
            Scanner scanner = new Scanner(new BufferedReader(new FileReader("datasets" + File.separator +
                    "mirflickr" + File.separator +
                    "features homogeneoustexture30k" + File.separator +
                    i / 10000 + File.separator +
                    i + ".txt")));

            for (int j = 0; j < values.length; j++) {
                values[j] = scanner.nextDouble();
            }
            homogeneoustexture.add(values);
        }
        normalize(homogeneoustexture);
        return homogeneoustexture;
    }

    private static ArrayList<double[]> readTags() throws FileNotFoundException, URISyntaxException {
        if (tags != null)
            return tags;
        tags = new ArrayList<>();
        ArrayList<ArrayList<String>> tag_strings = new ArrayList<>();
        HashMap<String, Integer> tag_count = new HashMap<>();

        for (int i = 1; i <= 25000; i++) {
            File file = new File("datasets" + File.separator +
                    "mirflickr" + File.separator +
                    "tags" + File.separator +
                    "tags" +
                    i + ".txt");

            Scanner scanner = new Scanner(file);
            ArrayList<String> t = new ArrayList<>();
            while (scanner.hasNext()) {
                String s = scanner.next();
                t.add(s);
                if (tag_count.get(s) == null)
                    tag_count.put(s, 1);
                else
                    tag_count.put(s, tag_count.get(s) + 1);
            }
            tag_strings.add(t);
        }
        ArrayList<String> valid_tags = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : tag_count.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (value >= 50) {
                valid_tags.add(key);
            }
        }

        for (ArrayList<String> tag_string : tag_strings) {
            double[] values = new double[valid_tags.size()];
            Arrays.fill(values, 0);
            for (String s : tag_string) {
                int index = valid_tags.indexOf(s);
                if (index != -1)
                    values[index] = 1;
            }
            tags.add(values);
        }

        tag_size = valid_tags.size();

        normalize(tags);
        return tags;
    }

    private static ArrayList<int[]> readAnnotations() throws FileNotFoundException, URISyntaxException {
        if (annotations != null)
            return annotations;

        annotations = new ArrayList<>();
        for (int i = 0; i < 25000; i++) {
            int[] values = new int[POTENTIAL_LABELS.length];
            Arrays.fill(values, 0);
            annotations.add(values);
        }

        for (int i = 0; i < POTENTIAL_LABELS.length; i++) {
            File file = new File("datasets" + File.separator +
                    "mirflickr" + File.separator + "mirflickr25k_annotations_v080" + File.separator +
                    POTENTIAL_LABELS[i] + ".txt");

            Scanner scanner = new Scanner(file);

            while (scanner.hasNextInt()) {
                annotations.get(scanner.nextInt() - 1)[i] = 1;
            }
        }
        return annotations;
    }


    private static ArrayList<double[]> readGithub(String filename) throws FileNotFoundException, URISyntaxException {


        ArrayList<double[]> list = new ArrayList<>();


        File file = new File("datasets" + File.separator +
                "mirflicker_github" + File.separator +
                filename);

        Scanner scanner = new Scanner(file);

        int attribute_count = 0;
        String line = scanner.nextLine();
        while (!line.equals("@data")) {
            if (line.contains("@attribute"))
                attribute_count++;
            line = scanner.nextLine();
        }

        for (int i = 0; i < 25000; i++) {
            double[] x = new double[attribute_count];
            line = scanner.nextLine();
            String[] ln = line.split(", ");
            for (int j = 0; j < x.length; j++) {
                x[j] = Double.parseDouble(ln[j]);
            }
            list.add(x);
        }
        normalize(list);
        return list;
    }

    @SuppressWarnings("unused")
    public static DataSet getGithubDatasetNoTag() throws FileNotFoundException, URISyntaxException {
        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();


        ArrayList<double[]> gist = readGithub("complete_mirflickr.txt");

        readAnnotations();

        for (int i = 0; i < annotations.size(); i++) {
            double[] x = gist.get(i);


            Instance instance = new Instance();
            instance.x = x;
            instance.r = annotations.get(i);


            if (i % 5 < 3) {
                tra.add(instance);
            } else
                val.add(instance);
        }
        return new DataSet("FLICKER NOTAG", tra, val, DataSet.TYPE.MULTI_LABEL_CLASSIFICATION);

    }

    @SuppressWarnings("unused")
    public static DataSet getGithubDatasetNoTag_v2() throws FileNotFoundException, URISyntaxException {
        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();

        ArrayList<double[]> gist = readGithub("complete_mirflickr.txt");

        readAnnotations();

        for (int i = 0; i < annotations.size(); i++) {
            double[] x = gist.get(i);
            Instance instance = new Instance();
            instance.x = x;
            instance.r = annotations.get(i);


            int add = 0;
            for (int t = 0; t < instance.r.length; t++)
                add += instance.r[t];

            if (i % 5 < 3 && add > 0) {
                tra.add(instance);
            } else if (add > 0)
                val.add(instance);
        }


        return new DataSet("FLICKER _v2", tra, val, DataSet.TYPE.MULTI_LABEL_CLASSIFICATION);
    }


    @SuppressWarnings("unused")
    public static DataSet getGithubDataset() throws FileNotFoundException, URISyntaxException {
        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();

        ArrayList<double[]> gist = readGithub("complete_mirflickr.txt");

        readAnnotations();
        readTags();

        for (int i = 0; i < annotations.size(); i++) {
            double[] x = concat(tags.get(i), gist.get(i));

            Instance instance = new Instance();
            instance.x = x;
            instance.r = annotations.get(i);


            if (i % 5 < 3) {
                tra.add(instance);
            } else
                val.add(instance);
        }


        return new DataSet("FLICKER tag", tra, val, DataSet.TYPE.MULTI_LABEL_CLASSIFICATION, tag_size);
    }

    @SuppressWarnings("unused")
    public static DataSet getDataset(boolean includeEdgeHistogram, boolean includeHomogeneousTexture, boolean includeTags) throws FileNotFoundException, URISyntaxException {
        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();


        if (!includeEdgeHistogram && !includeHomogeneousTexture && !includeTags)
            return null;

        if (includeEdgeHistogram)
            readEdgehistogram();
        if (includeHomogeneousTexture)
            readhomogeneoustexture();
        if (includeTags)
            readTags();

        readAnnotations();

        for (int i = 0; i < annotations.size(); i++) {
            double[] x = new double[0];
            if (includeEdgeHistogram)
                x = concat(x, edgehistogram.get(i));
            if (includeHomogeneousTexture)
                x = concat(x, homogeneoustexture.get(i));
            if (includeTags)
                x = concat(x, tags.get(i));

            Instance instance = new Instance();
            instance.x = x;
            instance.r = annotations.get(i);

            if (i % 5 < 3) {
                tra.add(instance);
            } else
                val.add(instance);
        }

        String name = "FLICKER ";
        if (includeEdgeHistogram)
            name += "EdgeHistogram ";
        if (includeHomogeneousTexture)
            name += "HomogeneousTexture ";
        if (includeTags)
            name += "Tags";

        return new DataSet(name, tra, val, DataSet.TYPE.MULTI_LABEL_CLASSIFICATION);
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


    private static void normalize(ArrayList<double[]> values) {
        for (int i = 0; i < values.get(0).length; i++) {
            double mean = 0;

            for (double[] value : values) {
                mean += value[i];
            }
            mean /= values.size();

            double stdev = 0;
            for (double[] value : values) {
                stdev += (value[i] - mean) * (value[i] - mean);
            }


            stdev = stdev / (values.size() - 1);
            stdev = Math.sqrt(stdev);


            for (double[] value : values) {
                value[i] -= mean;
                if (stdev != 0)
                    value[i] /= stdev;

            }

        }
    }


}
