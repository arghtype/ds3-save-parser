package argh.ds3saveparser;

import argh.ds3saveparser.extract.UserDataExtractor;
import argh.ds3saveparser.model.UserData;
import argh.ds3saveparser.process.UserDataParser;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author arghtype@gmail.com
 * @since 31.05.2016
 */
public class UserDataExtractorUnitTest {
    @Test
    public void tc1() throws IOException {
        FileInputStream fip = new FileInputStream("D:/DS3/ds3-save-parser/target/test-classes/DS30000.sl2");
        List<UserData> extract = new UserDataExtractor(fip).extract();

        UserDataParser.parse(extract.get(0));
    }
}
