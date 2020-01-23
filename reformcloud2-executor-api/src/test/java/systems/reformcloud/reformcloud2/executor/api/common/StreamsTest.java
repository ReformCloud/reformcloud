package systems.reformcloud.reformcloud2.executor.api.common;

import org.junit.Before;
import org.junit.Test;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public final class StreamsTest {

    private List<String> collection;

    @Before
    public void init() {
        this.collection = Arrays.asList("test", "TeST1");
    }

    @Test
    public void testToLowerCase() {
        this.collection = Streams.toLowerCase(collection);
        for (String s : collection) {
            assertNotEquals(s.toUpperCase(), s);
        }
    }

    @Test
    public void testFilter() {
        String s = Streams.filter(collection, e -> e.equals("test"));
        assertNotNull(s);
    }

    @Test
    public void testFilterAndApply() {
        Optional<String> optional = Streams.filterAndApply(collection, e -> e.equals("test"), Optional::ofNullable);
        assertNotNull(optional);
        assertTrue(optional.isPresent());
    }

    @Test
    public void testOthers() {
        Collection<String> others = Streams.others(collection, e -> e.equals("test"));
        assertEquals(1, others.size());
    }
}
