package Analysis;

import DataSet.Instance.Instance;


public interface Evaluable {
    /**
     * Returns predicted value of given Instance
     *
     * @param instance An instance
     * @return Return value withot any modification (no softmax, sigmoid, argmax)
     */
    double[] evaluate(Instance instance);
}
