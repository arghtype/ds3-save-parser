package argh.ds3saveparser;

import java.io.IOException;

/**
 * @author arghtype@gmail.com
 * @since 31.05.2016
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected 1 parameter - filename");
        }
        System.out.println("Reading " + args[0]);
        //TODO
    }
}
