package argh.ds3saveparser;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * SL2 file represents filesystem (or complex archive). Several USER DATA items could be extracted from it.
 *
 * @author arghtype@gmail.com
 * @since 31.05.2016
 */
public class UserDataExtractor {
    private static final String EXPECTED_HEADER = "BND4";
    BufferedInputStream sl2file;

    public UserDataExtractor(InputStream sl2file) {
        this.sl2file = new BufferedInputStream(sl2file);
    }

    public void extract() {
        try {
            //ReadableByteChannel channel = Channels.newChannel(sl2file);
            //Reader reader = Channels.newReader(channel, "UTF-8");
            sl2file.mark(Integer.MAX_VALUE);

            byte[] buffer = new byte[4];
            sl2file.read(buffer);
            String header = new String(buffer, StandardCharsets.UTF_8);
            //int userDataCount = ByteBuffer.wrap(buffer).getInt();
            if (!EXPECTED_HEADER.equals(header)){
                throw new IllegalArgumentException("Incorrect file - BND4 header is expected!");
            }

            System.out.println(header);
            // ByteBuffer
            sl2file.skip(8);
            int userDataCount = getInt32(sl2file);
            System.out.println(userDataCount);
            sl2file.skip(8);
            buffer = new byte[8];
            sl2file.read(buffer);
            String version = new String(buffer, StandardCharsets.UTF_8);
            System.out.println(version);

            int directoryEntrySize = getInt32(sl2file);
            System.out.println(directoryEntrySize);
            sl2file.skip(4);
            int dataOffset = getInt32(sl2file);
            System.out.println(dataOffset);
            sl2file.skip(20);

            //there should be loop, but let's start from 1 data file
            //TODO LOOP
            sl2file.skip(8);
            int userDataSize = getInt32(sl2file);
            System.out.println(userDataSize);
            sl2file.skip(4);

            int userDataOffset = getInt32(sl2file);
            System.out.println(userDataOffset);
            int userDataNameOffset = getInt32(sl2file);
            System.out.println(userDataNameOffset);
            //for looping - sl2file.skip(8);
            //we don't need name
            sl2file.reset();
            sl2file.skip(userDataNameOffset);
            //23
            buffer = new byte[23];
            sl2file.read(buffer);
            String name = new String(buffer, StandardCharsets.US_ASCII);
            System.out.println(name);
            sl2file.reset();
            sl2file.skip(userDataOffset);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getInt32(InputStream sl2file) throws IOException {
        byte[] buffer;
        buffer = new byte[4];
        sl2file.read(buffer);
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
}
