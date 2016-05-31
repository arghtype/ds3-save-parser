package argh.ds3saveparser;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author arghtype@gmail.com
 * @since 31.05.2016
 */
public class UserDataParser {

    public static Map<String, String> parse(UserData userData) {
        HashMap<String, String> stringStringHashMap = new HashMap<String, String>();
        stringStringHashMap.put("FileName", userData.getFilename());
        String x = new String(userData.getData(), StandardCharsets.UTF_16LE);
        //System.out.println(x);
        StringTokenizer str = new StringTokenizer(x);
        int i = 0;
        while(str.hasMoreTokens()) {
            String s = str.nextToken();
            if (s.contains("ARGHA")) {
                System.out.println(i); //17 token
                System.out.println(s);
            }
            i++;
        }
        return stringStringHashMap;
    }
}
