package DataSet.Writer;

import DataSet.DataSet.DataSet;
import DataSet.Reader.MNISTReader;
import DataSet.Reader.MSDReader;
import DataSet.Reader.PENDATAReader;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by mertcan on 19.6.2016.
 */
public class CreateARFFFiles {

    public static void main(String[] args) throws IOException, URISyntaxException  {
        WekaFormat wekaFormat = new WekaFormat(getPENDATA(2));
        wekaFormat.outputARFFFile();
    }

    public static DataSet getMNIST() throws IOException, URISyntaxException {
        return MNISTReader.getMNIST();
    }

    public static DataSet getPENDATA(int mode) throws IOException, URISyntaxException {
        switch (mode){
            case 0:
                return PENDATAReader.getStatic();
            case 1:
                return PENDATAReader.getDynamic();
            case 2:
                return PENDATAReader.getBoth();
            default:
                return PENDATAReader.getStatic();
        }
    }

    public static DataSet getMSDGenre(int mode) throws IOException, URISyntaxException {
        switch (mode){
            case 0:
                return MSDReader.getSoundOnly();
            case 1:
                return MSDReader.getLyricsOnly();
            case 2:
                return MSDReader.getBoth();
            default:
                return MSDReader.getSoundOnly();
        }
    }

}
