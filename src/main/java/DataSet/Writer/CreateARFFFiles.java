package DataSet.Writer;

import DataSet.DataSet.DataSet;
import DataSet.Reader.FlickerReader;
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
        WekaFormat wekaFormat = new WekaFormat(getPENDATA(1));
        wekaFormat.outputARFFFile();
    }

    public static DataSet getMNIST() throws IOException, URISyntaxException {
        return MNISTReader.getMNIST();
    }

    public static DataSet getPENDATA(int mode) throws IOException, URISyntaxException {
        switch (mode){
            case 0:
                return PENDATAReader.readStatic();
            case 1:
                return PENDATAReader.readDynamic();
            default:
                return PENDATAReader.readStatic();
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
