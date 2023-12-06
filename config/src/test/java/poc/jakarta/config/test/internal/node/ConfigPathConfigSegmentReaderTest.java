package poc.jakarta.config.test.internal.node;

import poc.jakarta.config.internal.node.ConfigPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

public class ConfigPathConfigSegmentReaderTest {

    @Test
    public void testTwoSegmentPaths() {
        String segment = "first.second";
        List<ConfigPath.ConfigPathSegment> segments = ConfigSegmentParserparse(segment);
        Assertions.assertEquals(2, segments.size());
        Assertions.assertEquals("first", segments.get(0).getName());
        Assertions.assertEquals("second", segments.get(1).getName());
    }

    @Test
    public void testTwoSegmentPathsWithComments() {
        String segment = "first/*comment1*/.second/* .comment.2 */";
        List<ConfigPath.ConfigPathSegment> segments = ConfigSegmentParserparse(segment);
        Assertions.assertEquals(2, segments.size());
        Assertions.assertEquals("first", segments.get(0).getName());
        Assertions.assertEquals("second", segments.get(1).getName());
    }

    @Test
    public void testTwoSegmentWithExtraSymbols() {
        String segment = "first//.*/ se/ *cond";
        List<ConfigPath.ConfigPathSegment> segments = ConfigSegmentParserparse(segment);
        Assertions.assertEquals(2, segments.size());
        Assertions.assertEquals("first//", segments.get(0).getName());
        Assertions.assertEquals("*/ se/ *cond", segments.get(1).getName());
    }

    @Test
    public void testComment() {
        String segment = "property2.sub2 /* sub property2 */ ";

        List<ConfigPath.ConfigPathSegment> segments = ConfigSegmentParserparse(segment);
        Assertions.assertEquals(2, segments.size());
        Assertions.assertEquals("property2", segments.get(0).getName());
        Assertions.assertEquals("sub2", segments.get(1).getName());
    }

    public List<ConfigPath.ConfigPathSegment> ConfigSegmentParserparse(String segment) {
        try {
            Class clazz = Class.forName("poc.jakarta.config.internal.node.ConfigSegmentParser");
            Method method = clazz.getDeclaredMethod("parse", String.class);
            method.setAccessible(true);
            return (List<ConfigPath.ConfigPathSegment>) method.invoke(null, segment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}