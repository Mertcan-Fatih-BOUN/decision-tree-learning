package DataSet.Reader;


import DataSet.DataSet.DataSet;
import DataSet.Instance.Instance;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class MSDReader {
    static {
        Locale.setDefault(Locale.US);
    }

    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    private static final String[] CLASS_NAMES = new String[]{"classic pop and rock", "punk", "folk", "pop", "dance and electronica",
            "metal", "jazz and blues", "classical", "-hip-hop", "soul and reggae"};

    private static Random random = new Random(45645);
    private static double training_ratio = 0.6;
    private static HashMap<String, MSDInstance> msdInstances_genre = new HashMap<>();
    private static HashMap<String, MSDInstance> msdInstances_lyrics = new HashMap<>();
    private static ArrayList<MSDInstance> merged = new ArrayList<>();
    private static ArrayList<String> genres = new ArrayList<>();

    private static void readGenre() throws FileNotFoundException {
        File file = new File("datasets" + File.separator +
                "msd_genre" + File.separator +
                "msd_genre_dataset.txt");
        Scanner scanner = new Scanner(file);


        boolean isReached = false;
        while (!isReached) {
            isReached = scanner.nextLine().charAt(0) == '%';
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] splits = line.split(",");
            int i = genres.indexOf(splits[0]);
            if (i == -1) {
                genres.add(splits[0]);
                i = genres.size() - 1;
            }

            MSDInstance msdInstance = new MSDInstance();
            msdInstance.class_value = i;
            msdInstance.id = splits[1];
            msdInstance.x_sound = new double[splits.length - 4];

            for (int k = 0; k < msdInstance.x_sound.length; k++) {
                msdInstance.x_sound[k] = Double.valueOf(splits[k + 4]);
            }
            msdInstances_genre.put(msdInstance.id, msdInstance);
        }

    }

    private static void readLyrics(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);

        boolean isReached = false;
        while (!isReached) {
            isReached = scanner.nextLine().charAt(0) == '%';
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] splits = line.split(",");
            MSDInstance instance = new MSDInstance();
            instance.id = splits[0];
            instance.x_lyrics = new double[5000];
            Arrays.fill(instance.x_lyrics, 0);
            for (int i = 2; i < splits.length; i++) {
                int index = splits[i].indexOf(':');
                int a = Integer.valueOf(splits[i].substring(0, index));
                int b = Integer.valueOf(splits[i].substring(index + 1));
                instance.x_lyrics[a - 1] = b;
            }
            msdInstances_lyrics.put(instance.id, instance);
        }

    }


    private static void merge() {
        for (MSDInstance msdInstance : msdInstances_genre.values()) {
            MSDInstance lyric = msdInstances_lyrics.get(msdInstance.id);
            if (lyric != null) {
                msdInstance.x_lyrics = lyric.x_lyrics;
                merged.add(msdInstance);
            }
        }
    }


    public static void main(String[] args) throws IOException {
        readGenre();

        File file_train = new File("datasets" + File.separator +
                "msd_genre" + File.separator +
                "mxm_dataset_train.txt");
        File file_test = new File("datasets" + File.separator +
                "msd_genre" + File.separator +
                "mxm_dataset_test.txt");


        readLyrics(file_train);
        readLyrics(file_test);
        merge();

        BufferedWriter writer = new BufferedWriter(new FileWriter("datasets" + File.separator +
                "msd_genre" + File.separator + "msd.txt"));
        writer.write(genres.size() + " " + merged.get(0).x_sound.length + " " + merged.get(0).x_lyrics.length + "\n");

        for (MSDInstance msdInstance : merged) {
            writer.write(msdInstance.class_value + " ");
            for (int i = 0; i < msdInstance.x_sound.length; i++) {
                writer.write(msdInstance.x_sound[i] + " ");
            }
            for (int i = 0; i < msdInstance.x_lyrics.length; i++) {
                writer.write(msdInstance.x_lyrics[i] + " ");
            }
            writer.write("\n");
        }

        writer.close();

    }


    @SuppressWarnings("unused")
    public static DataSet getSoundOnly() throws FileNotFoundException, URISyntaxException {
        ArrayList<Instance> instances = new ArrayList<>();

        File file = new File("datasets" + File.separator +
                "msd_genre" + File.separator +
                "msd.txt");
        Scanner scanner = new Scanner(file);


        int class_count = scanner.nextInt();
        int sound_count = scanner.nextInt();
        int lyric_count = scanner.nextInt();

        while (scanner.hasNext()) {
            Instance instance = new Instance();
            instance.r = new int[class_count];
            Arrays.fill(instance.r, 0);
            instance.r[scanner.nextInt()] = 1;
            instance.x = new double[sound_count];
            for (int i = 0; i < sound_count; i++) {
                instance.x[i] = scanner.nextDouble();
            }
            for (int i = 0; i < lyric_count; i++) {
                scanner.nextDouble();
            }
            instances.add(instance);
            System.out.printf("\r%.2f", (instances.size() * 1.0) / 17495);

        }

        normalize(instances);

        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();

        for (Instance instance : instances) {
            if (random.nextDouble() < training_ratio)
                tra.add(instance);
            else
                val.add(instance);
        }


        return new DataSet("MSD Sound only", tra, val, DataSet.TYPE.MULTI_CLASS_CLASSIFICATION);
    }


    @SuppressWarnings("unused")
    public static DataSet getLyricsOnly() throws FileNotFoundException, URISyntaxException {
        ArrayList<Instance> instances = new ArrayList<>();
        File file = new File("datasets" + File.separator +
                "msd_genre" + File.separator +
                "msd.txt");
        Scanner scanner = new Scanner(file);

        int class_count = scanner.nextInt();
        int sound_count = scanner.nextInt();
        int lyric_count = scanner.nextInt();

        while (scanner.hasNext()) {
            Instance instance = new Instance();
            instance.r = new int[class_count];
            Arrays.fill(instance.r, 0);
            instance.r[scanner.nextInt()] = 1;
            instance.x = new double[lyric_count];
            for (int i = 0; i < sound_count; i++) {
                scanner.nextDouble();
            }
            for (int i = 0; i < lyric_count; i++) {
                instance.x[i] = scanner.nextDouble();
            }
            instances.add(instance);
            System.out.printf("\r%.2f", (instances.size() * 1.0) / 17495);
        }

        normalize(instances);

        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();

        for (Instance instance : instances) {
            if (random.nextDouble() < training_ratio)
                tra.add(instance);
            else
                val.add(instance);
        }

        return new DataSet("MSD Lyrics only", tra, val, DataSet.TYPE.MULTI_CLASS_CLASSIFICATION);
    }

    @SuppressWarnings("unused")
    public static DataSet getBoth() throws FileNotFoundException, URISyntaxException {
        ArrayList<Instance> instances = new ArrayList<>();

        File file = new File("datasets" + File.separator +
                "msd_genre" + File.separator +
                "msd.txt");
        Scanner scanner = new Scanner(file);

        int class_count = scanner.nextInt();
        int sound_count = scanner.nextInt();
        int lyric_count = scanner.nextInt();

        while (scanner.hasNext()) {
            Instance instance = new Instance();
            instance.r = new int[class_count];
            Arrays.fill(instance.r, 0);
            instance.r[scanner.nextInt()] = 1;
            instance.x = new double[sound_count + lyric_count];
            for (int i = 0; i < sound_count + lyric_count; i++) {
                instance.x[i] = scanner.nextDouble();
            }

            instances.add(instance);
            System.out.printf("\r%.2f", (instances.size() * 1.0) / 17495);
        }

        normalize(instances);

        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();

        for (Instance instance : instances) {
            if (random.nextDouble() < training_ratio)
                tra.add(instance);
            else
                val.add(instance);
        }

        return new DataSet("MSD Both", tra, val, DataSet.TYPE.MULTI_CLASS_CLASSIFICATION, 30);
    }

    private static void normalize(ArrayList<Instance> instances) {
        if(Constants.normalizeEnabled) {
            for (int i = 0; i < instances.get(0).x.length; i++) {
                double mean = 0;

                for (Instance instance : instances)
                    mean += instance.x[i];

                mean /= instances.size();

                double stdev = 0;
                for (Instance instance : instances) {
                    stdev += (instance.x[i] - mean) * (instance.x[i] - mean);
                }

                stdev = stdev / (instances.size() - 1);
                stdev = Math.sqrt(stdev);

                for (Instance instance : instances) {
                    instance.x[i] -= mean;
                    if (stdev != 0)
                        instance.x[i] /= stdev;
                }
            }
        }
    }

    private static class MSDInstance {
        int class_value;
        double[] x_sound;
        double[] x_lyrics;
        String id;
    }
}
