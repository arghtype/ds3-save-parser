package argh.ds3saveparser.extract;

import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author arghtype@gmail.com
 * @since 2/9/2017 13:44
 */
class DecryptUtil {

    private static final byte[] USER_DATA_SECRET_KEY =
            {
                    (byte) 0xFD, 0x46, 0x4D, 0x69,
                    0x5E, 0x69, (byte) 0xA3, (byte) 0x9A,
                    0x10, (byte) 0xE3, 0x19, (byte) 0xA7,
                    (byte) 0xAC, (byte) 0xE8, (byte) 0xB7, (byte) 0xFA
            };

    /**
     * AES CBC 128
     */
    static byte[] decrypt(byte[] encrypted, byte[] ivbytes) throws GeneralSecurityException {
        IvParameterSpec ivSpec = new IvParameterSpec(ivbytes);
        SecretKeySpec secretKeySpec = new SecretKeySpec(USER_DATA_SECRET_KEY, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

        return cipher.doFinal(encrypted);
    }
}
