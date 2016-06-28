package Analysis;

import DataSet.DataSet.DataSet;
import DataSet.Instance.Instance;

import java.util.ArrayList;
import java.util.Collections;

import static Misc.Util.argMax;
import static Misc.Util.sigmoid;
import static Misc.Util.softmax;


public class CalculateError {
    public static ClassificationError getClassificationError(Evaluable evaluable, ArrayList<Instance> instances, DataSet.TYPE type) {
        ClassificationError classificationError = new ClassificationError(instances.get(0).r.length);
        int _y;
        for (Instance instance : instances) {
            double[] y = evaluable.evaluate(instance);
            if (type == DataSet.TYPE.BINARY_CLASSIFICATION) {
                y[0] = sigmoid(y[0]);
                if (y[0] > 0.5)
                    _y = 1;
                else
                    _y = 0;
                classificationError.add(instance.r[0], _y);

            } else {
                y = softmax(y);
                _y = argMax(y);
            }

            classificationError.add(argMax(instance.r), _y);

        }
        return classificationError;
    }

    public static ClassificationError getClassificationError(MultiEvaluable evaluable, ArrayList<Instance> instances, ArrayList<Instance> instances2, DataSet.TYPE type) {
        ClassificationError classificationError = new ClassificationError(instances.get(0).r.length);
        int _y;
        for (int i = 0; i < instances.size(); i++) {
            Instance instance = instances.get(i);
            Instance instance2 = instances2.get(i);
            double[] y = evaluable.evaluate(instance, instance2);
            if (type == DataSet.TYPE.BINARY_CLASSIFICATION) {
                y[0] = sigmoid(y[0]);
                if (y[0] > 0.5)
                    _y = 1;
                else
                    _y = 0;
                classificationError.add(instance.r[0], _y);

            } else {
                y = softmax(y);
                _y = argMax(y);
            }

            classificationError.add(argMax(instance.r), _y);

        }
        return classificationError;
    }

    @SuppressWarnings("unused")
    public static double getAbsoluteDifference(Evaluable evaluable, ArrayList<Instance> instances) {
        double difference = 0;

        for (Instance instance : instances) {
            double y = evaluable.evaluate(instance)[0];
            difference += Math.abs(y - instance.r[0]);
        }

        return difference / instances.size();
    }

    public static double getMeanSquareError(Evaluable evaluable, ArrayList<Instance> instances) {
        double difference = 0;

        for (Instance instance : instances) {
            double y = evaluable.evaluate(instance)[0];
            difference += Math.pow(y - instance.r[0], 2);
        }

        return difference / instances.size();
    }

    public static MAPError getMAP_P50_error(Evaluable evaluable, ArrayList<Instance> instances) {
        int CLASS_COUNT = instances.get(0).r.length;
        MAPError MAPError = new MAPError(CLASS_COUNT);

        for (Instance instance : instances) {
            instance.y = evaluable.evaluate(instance).clone();
        }

        for (int i = 0; i < CLASS_COUNT; i++) {
            double error = 0;
            double positive_count = 0;
            double pre_count = 0;
            final int finalI = i;
            Collections.sort(instances, (o1, o2) -> Double.compare(o2.y[finalI], o1.y[finalI]));

            for (int j = 0; j < instances.size(); j++) {
                if (instances.get(j).r[i] == 1) {
                    if (j < 50)
                        pre_count++;
                    positive_count++;
                    error += (positive_count * 1.0) / (j + 1);
                }
            }

            error /= positive_count;

            MAPError.MAP[i] = error;
            MAPError.precision[i] = pre_count / 50.0f;
        }

        return MAPError;
    }

    public static MAPError getMAP_P50_error(MultiEvaluable evaluable, ArrayList<Instance> instances, ArrayList<Instance> instances2) {
        int CLASS_COUNT = instances.get(0).r.length;
        MAPError MAPError = new MAPError(CLASS_COUNT);


        for (int i = 0; i < instances.size(); i++) {
            instances.get(i).y = evaluable.evaluate(instances.get(i), instances2.get(i)).clone();
        }

        for (int i = 0; i < CLASS_COUNT; i++) {
            double error = 0;
            double positive_count = 0;
            double pre_count = 0;
            final int finalI = i;
            Collections.sort(instances, (o1, o2) -> Double.compare(o2.y[finalI], o1.y[finalI]));

            for (int j = 0; j < instances.size(); j++) {
                if (instances.get(j).r[i] == 1) {
                    if (j < 50)
                        pre_count++;
                    positive_count++;
                    error += (positive_count * 1.0) / (j + 1);
                }
            }

            error /= positive_count;

            MAPError.MAP[i] = error;
            MAPError.precision[i] = pre_count / 50.0f;
        }

        return MAPError;
    }
}
