package Analysis;

import DataSet.Instance.Instance;

/**
 * Created by mertcan on 28.6.2016.
 */
public interface MultiEvaluable {

    /**
     * Returns predicted value of given Instance
     *
     * @param instance1 An instance
     * @param instance2 An instance
     * @return Return value withot any modification (no softmax, sigmoid, argmax)
     */
    double[] evaluate(Instance instance1, Instance instance2);
}
