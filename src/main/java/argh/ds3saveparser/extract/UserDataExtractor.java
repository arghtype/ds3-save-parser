package argh.ds3saveparser.extract;

import argh.ds3saveparser.model.UserData;
import argh.ds3saveparser.model.UserDataMetadata;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extracts UserData from SL2 file.
 *
 * SL2 file represents filesystem (or complex archive).
 * Several USER DATA items could be extracted from it.
 *
 * @author arghtype@gmail.com
 * @since 31.05.2016
 */
public class UserDataExtractor {
    private static final String EXPECTED_HEADER = "BND4";

    private BufferedInputStream sl2file; //TODO Consider switching to Commons IO collections

    public UserDataExtractor(InputStream sl2file) {
        this.sl2file = new BufferedInputStream(sl2file);
    }

    public List<UserData> extract() {
        try {
            sl2file.mark(Integer.MAX_VALUE);

            byte[] buffer = new byte[4];
            sl2file.read(buffer);
            String header = new String(buffer, StandardCharsets.UTF_8);
            //int userDataCount = ByteBuffer.wrap(buffer).getInt();
            if (!EXPECTED_HEADER.equals(header)) {
                throw new IllegalArgumentException("Incorrect file - BND4 header is expected!");
            }

            System.out.println("File Header is " + header);
            sl2file.skip(8);
            int userDataCount = getInt32(sl2file);
            System.out.println("User Data Count is " + userDataCount);
            sl2file.skip(8);
            buffer = new byte[8];
            sl2file.read(buffer);
            String version = new String(buffer, StandardCharsets.UTF_8);
            System.out.println("Version is " + version);

            int directoryEntrySize = getInt32(sl2file);
            sl2file.skip(4);
            int dataOffset = getInt32(sl2file);
            sl2file.skip(20);

            System.out.println("Reading user data...");
            List<UserDataMetadata> metadatas = new ArrayList<>(userDataCount);

            for(int i = 0; i < userDataCount; i++) {
                sl2file.skip(8);

                metadatas.add(readMetadata());

                sl2file.skip(8);
            }

            List<UserData> userDatas = new ArrayList<>(userDataCount);
            for (UserDataMetadata metadata : metadatas) {
                UserData ud = readUserData(metadata);
                userDatas.add(ud);
            }
            return userDatas;
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null; //todo return empty list
    }

    private UserDataMetadata readMetadata() throws IOException {
        int userDataSize = getInt32(sl2file);
        System.out.println("User Data Size is " + userDataSize);
        sl2file.skip(4);
        int userDataOffset = getInt32(sl2file);
        System.out.println("User Data Offset is " + userDataOffset);
        int userDataNameOffset = getInt32(sl2file);
        System.out.println("User Data Name Offset is " + userDataNameOffset);

        return new UserDataMetadata(userDataSize, userDataOffset, userDataNameOffset);
    }

    private UserData readUserData(UserDataMetadata metadata) throws IOException,
            GeneralSecurityException {
        byte[] buffer;
        sl2file.reset();
        sl2file.skip(metadata.nameOffset);
        //23
        buffer = new byte[24];
        sl2file.read(buffer);
        String name = new String(buffer, StandardCharsets.UTF_16LE);
        System.out.println("User Data Name is " + name);
        sl2file.reset();
        sl2file.skip(metadata.offset);

        byte[] iv = new byte[16];
        sl2file.read(iv);
        byte[] encrypted = new byte[metadata.size - 16];
        sl2file.read(encrypted);
        System.out.println("Decrypting user data...");
        byte[] decrypted = DecryptUtil.decrypt(encrypted, iv);
        return new UserData(decrypted, name);
    }

    private static int getInt32(InputStream stream) throws IOException {
        byte[] buffer;
        buffer = new byte[4];
        stream.read(buffer);
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

}
