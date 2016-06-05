package Analysis;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;

public class ClassificationError {
    private int true_count;
    private int false_count;
    private int[][] confusion_matrix;

    private final int class_count;

    ClassificationError(int class_count) {
        if (class_count == 1)
            class_count++;
        this.class_count = class_count;
        confusion_matrix = new int[class_count][class_count];
        for (int i = 0; i < class_count; i++)
            Arrays.fill(confusion_matrix[i], 0);
    }

    void add(int real_class, int predicted_class) {
        confusion_matrix[real_class][predicted_class]++;
        if (real_class == predicted_class)
            true_count++;
        else
            false_count++;
    }

    public double getMissClassificationError() {
        return (false_count * 1.0) / (true_count + false_count);
    }

    public double getAccuracy() {
        return 1 - getMissClassificationError();
    }

    public String getStringConfusionMatrix() {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder, Locale.US);

        for (int i = 0; i < class_count; i++) {
            int t = 0;
            int f = 0;
            for (int j = 0; j < class_count; j++) {
                if (i == j)
                    t += confusion_matrix[i][j];
                else
                    f += confusion_matrix[i][j];
                formatter.format("%5d  ", confusion_matrix[i][j]);
            }
            formatter.format("%.2f\n", (t * 1.0) / (t + f));
        }
        return stringBuilder.toString();
    }
}