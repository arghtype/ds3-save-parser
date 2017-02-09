package argh.ds3saveparser.model;

/**
 * @author arghtype@gmail.com
 * @since 2/8/2017 18:17
 */
public class UserDataMetadata {
    public final int size;
    public final int offset;
    public final int nameOffset;

    public UserDataMetadata(int size, int offset, int nameOffset) {
        this.size = size;
        this.offset = offset;
        this.nameOffset = nameOffset;
    }
}
