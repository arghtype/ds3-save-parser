package argh.ds3saveparser;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * SL2 file represents filesystem (or complex archive). Several USER DATA items could be extracted from it.
 *
 * @author arghtype@gmail.com
 * @since 31.05.2016
 */
public class UserDataExtractor {
    private static final String EXPECTED_HEADER = "BND4";
    InputStream sl2file;

    public UserDataExtractor(InputStream sl2file) {
        this.sl2file = sl2file;
    }

    public void extract() {
        try {
            ReadableByteChannel channel = Channels.newChannel(sl2file);
            Reader reader = Channels.newReader(channel, "UTF-8");
            char[] cbuf = new char[4];
            reader.read(cbuf, 0, 4);
            if (!EXPECTED_HEADER.equals(String.valueOf(cbuf))){
                throw new IllegalArgumentException("Incorrect file - BND4 header is expected!");
            }
            System.out.print(cbuf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
