package systems.reformcloud.reformcloud2.executor.api.utility.list;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import systems.reformcloud.reformcloud2.executor.api.utility.optional.ReferencedOptional;

import java.util.*;

class StreamsTest {

    @Test
    void testToLowerCase() {
        List<String> strings = Streams.toLowerCase(Arrays.asList("TEsT1", "LooL2"));
        Assertions.assertEquals(2, strings.size());
        Assertions.assertEquals("test1", strings.get(0));
        Assertions.assertEquals("lool2", strings.get(1));
    }

    @Test
    void testNewList() {
        List<String> list = Streams.newList(Collections.singletonList("zzz"));
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("zzz", list.get(0));
    }

    @Test
    void testApply() {
        List<Integer> list = Streams.apply(Collections.singletonList("1"), Integer::parseInt);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(1, list.get(0));
    }

    @Test
    void testFilter() {
        String s = Streams.filter(Arrays.asList("test", "lol", "ok", "derklaro"), t -> t.equals("ok"));
        Assertions.assertNotNull(s);
        Assertions.assertEquals("ok", s);
    }

    @Test
    void testFilterToReferenceCollection() {
        ReferencedOptional<String> optional = Streams.filterToReference(Arrays.asList("test", "lol", "ok", "derklaro"), t -> t.equals("ok"));
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals("ok", optional.get());
    }

    @Test
    void testFilterToReferenceMap() {
        Map<String, String> map = new HashMap<>();
        map.put("ok", "cool");
        map.put("derklaro", "cool");
        map.put("reformcloud", "good");

        ReferencedOptional<String> optional = Streams.filterToReference(map, t -> t.equals("ok"));
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals("cool", optional.get());
    }

    @Test
    void testFilterAndApply() {
        Integer i = Streams.filterAndApply(Arrays.asList("1", "2", "3", "4"), t -> t.equals("4"), Integer::parseInt);
        Assertions.assertNotNull(i);
        Assertions.assertEquals(4, i);
    }

    @Test
    void testGetValues() {
        Map<String, String> map = new HashMap<>();
        map.put("ok", "cool");
        map.put("derklaro", "cool");
        map.put("reformcloud", "good");

        List<String> result = Streams.getValues(map, s -> s.equals("ok") || s.equals("derklaro"));
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("cool", result.get(0));
        Assertions.assertEquals("cool", result.get(1));
    }

    @Test
    void testKeyApply() {
        Map<String, String> map = new HashMap<>();
        map.put("1", "cool");
        map.put("2", "cool");
        map.put("3", "good");

        List<Integer> result = Streams.keyApply(map, Integer::parseInt);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(1, result.get(0));
        Assertions.assertEquals(2, result.get(1));
        Assertions.assertEquals(3, result.get(2));
    }

    @Test
    void testFromIterator() {
        Collection<String> result = Streams.fromIterator(Arrays.asList("test1", "test1").iterator());
        Assertions.assertEquals(2, result.size());
        for (String s : result) {
            Assertions.assertEquals("test1", s);
        }
    }

    @Test
    void testConcat() {
        String[] result = Streams.concat(new String[]{"test0", "test1"}, new String[]{"test2", "test3"});
        Assertions.assertEquals(4, result.length);
        for (int i = 0; i < result.length; i++) {
            Assertions.assertEquals("test" + i, result[i]);
        }
    }

    @Test
    void testCount() {
        int count = Streams.count(Arrays.asList("test1", "test2", "test3", "ok"), s -> s.startsWith("test"));
        Assertions.assertEquals(3, count);
    }

    @Test
    void testMapCollection() {
        Collection<Integer> result = Streams.map(Arrays.asList("1", "1", "1", "1"), Integer::parseInt);
        Assertions.assertEquals(4, result.size());
        for (Integer integer : result) {
            Assertions.assertEquals(1, integer);
        }
    }

    @Test
    void testMapArray() {
        Collection<Integer> result = Streams.map(new String[]{"1", "1", "1", "1"}, Integer::parseInt);
        Assertions.assertEquals(4, result.size());
        for (Integer integer : result) {
            Assertions.assertEquals(1, integer);
        }
    }

    @Test
    void testHasMatch() {
        Assertions.assertTrue(Streams.hasMatch(Arrays.asList("test1", "ok", "cool", "lol"), s -> s.startsWith("test")));
        Assertions.assertFalse(Streams.hasMatch(Arrays.asList("boomer", "ok", "cool", "lol"), s -> s.startsWith("test")));
    }
}