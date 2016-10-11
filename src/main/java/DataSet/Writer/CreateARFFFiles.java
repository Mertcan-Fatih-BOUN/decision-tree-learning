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
        createARFFSforMSDReaderWithFrequencies();
//        WekaFormat wekaFormat = new WekaFormat(getMSDGenre(4));
//        wekaFormat.outputARFFFile();
    }

    public static void createARFFSforMSDReaderWithFrequencies() throws IOException, URISyntaxException {
        int hardcode[] = new int[]{250, 500, 1000, 2500};
        WekaFormat wekaFormat;
        for(int i = 2; i < 5; i++){
            for(int j = 0; j < 4; j++){
                wekaFormat = new WekaFormat(getMSDGenreWithFrequency(i, hardcode[j]));
                wekaFormat.outputARFFFile();
            }
        }
    }

    public static DataSet getMNIST(int mode) throws IOException, URISyntaxException {
        switch (mode){
            case 0:
                return MNISTReader.getMNIST();
            case 1:
                return MNISTReader.getMNIST_TOP();
            case 2:
                return MNISTReader.getMNIST_BOTTOM();
            default:
                return MNISTReader.getMNIST();
        }
    }

    public static DataSet getPENDATA(int mode) throws IOException, URISyntaxException {
        switch (mode){
            case 0:
                return PENDATAReader.getStatic();
            case 1:
                return PENDATAReader.getDynamic();
            case 2:
                return PENDATAReader.getBoth();
            case 3:
                return PENDATAReader.getStaticFilled0();
            case 4:
                return PENDATAReader.getDynamicFilled0();
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
            case 3:
                return MSDReader.getSoundOnlyFilled0();
            case 4:
                return MSDReader.getLyricsOnlyFilled0();
            default:
                return MSDReader.getSoundOnly();
        }
    }

    public static DataSet getMSDGenreWithFrequency(int mode, int frequency) throws IOException, URISyntaxException {
        switch (mode){
            case 1:
                return MSDReader.getLyricsOnly(frequency);
            case 2:
                return MSDReader.getBoth(frequency);
            case 3:
                return MSDReader.getSoundOnlyFilled0(frequency);
            case 4:
                return MSDReader.getLyricsOnlyFilled0(frequency);
            default:
                return null;
        }
    }

}
