package DataSet.DataSet;


import DataSet.Instance.Instance;

import java.util.ArrayList;

public class DataSet {
    public enum TYPE {REGRESSION, BINARY_CLASSIFICATION, MULTI_CLASS_CLASSIFICATION, MULTI_LABEL_CLASSIFICATION}

    public String name = "";
    public ArrayList<Instance> TRAINING_INSTANCES;
    public ArrayList<Instance> VALIDATION_INSTANCES;
    public TYPE type;

    /**
     * For two modal data sets, the size of first modal
     */
    public int first_modal_size;


    public DataSet(String name, ArrayList<Instance> X, ArrayList<Instance> V, TYPE type, int first_modal_size) {
        this.name = name;
        this.TRAINING_INSTANCES = X;
        this.VALIDATION_INSTANCES = V;
        this.type = type;
        this.first_modal_size = first_modal_size;
    }

    public DataSet(String name, ArrayList<Instance> X, ArrayList<Instance> V, TYPE type) {
        this(name, X, V, type, -1);
    }

    public String getName() {
        return name;
    }
}
