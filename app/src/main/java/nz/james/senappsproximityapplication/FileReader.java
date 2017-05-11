package nz.james.senappsproximityapplication;

/**
 * Created by james on 11/05/2017.
 */

public class FileReader {

    private String filepath;

    public FileReader(String filepath){
        this.setFilepath(filepath);
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
