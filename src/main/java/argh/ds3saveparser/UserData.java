package argh.ds3saveparser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author arghtype@gmail.com
 * @since 31.05.2016
 */
public class UserData {
    private byte[] data;

    public String getFilename() {
        return filename;
    }

    private String filename;

    public UserData(byte[] data, String name) {
        this.data = data;
        this.filename = name;
    }

    public byte[] getData() {
        return data;
    }

    public void saveToFile() throws IOException {
        File file = new File(filename + "_" + System.currentTimeMillis());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
    }

}
