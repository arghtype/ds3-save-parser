package argh.ds3saveparser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Extracts and decrypts UserData from
 *
 * SL2 file represents filesystem (or complex archive).
 * Several USER DATA items could be extracted from it.
 *
 * @author arghtype@gmail.com
 * @since 31.05.2016
 */
public class UserDataExtractor {
    private static final String EXPECTED_HEADER = "BND4";
    private static final byte[] USER_DATA_SECRET_KEY =
            {
                    (byte) 0xFD, 0x46, 0x4D, 0x69,
                    0x5E, 0x69, (byte) 0xA3, (byte) 0x9A,
                    0x10, (byte) 0xE3, 0x19, (byte) 0xA7,
                    (byte) 0xAC, (byte) 0xE8, (byte) 0xB7, (byte) 0xFA
            };

    BufferedInputStream sl2file; //TODO Consider switching to Commons IO collections

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

            System.out.println("File Header is" + header);
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

            //there should be loop, but let's start from 1 data file
            //TODO LOOP
            System.out.println("Reading user data...");
            sl2file.skip(8);
            int userDataSize = getInt32(sl2file);
            System.out.println("User Data Size is " + userDataSize);
            sl2file.skip(4);

            int userDataOffset = getInt32(sl2file);
            System.out.println("User Data Offset is " +  userDataOffset);
            int userDataNameOffset = getInt32(sl2file);
            System.out.println("User Data Name Offset is " + userDataNameOffset);
            //for looping - sl2file.skip(8);

            //we don't need name
            sl2file.reset();
            sl2file.skip(userDataNameOffset);
            //23
            buffer = new byte[24];
            sl2file.read(buffer);
            String name = new String(buffer, StandardCharsets.UTF_16LE);
            System.out.println("User Data Name is " + name);
            sl2file.reset();
            sl2file.skip(userDataOffset);

            byte[] iv = new byte[16];
            sl2file.read(iv);
            byte[] encrypted = new byte[userDataSize - 16];
            sl2file.read(encrypted);
            System.out.println("Decrypting user data...");
            byte[] decrypted = decrypt(encrypted, iv);
            UserData ud = new UserData(decrypted, name);
            UserData[] userDatas = {ud};
            return Arrays.asList(userDatas);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int getInt32(InputStream stream) throws IOException {
        byte[] buffer;
        buffer = new byte[4];
        stream.read(buffer);
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    /**
     * AES CBC 128
     */
    private static byte[] decrypt(byte[] encrypted, byte[] ivbytes) throws GeneralSecurityException {
        IvParameterSpec ivSpec = new IvParameterSpec(ivbytes);
        SecretKeySpec secretKeySpec = new SecretKeySpec(USER_DATA_SECRET_KEY, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

        return cipher.doFinal(encrypted);
    }
}
