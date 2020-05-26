/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.streams;

import org.junit.Before;
import org.junit.Test;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;

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
        this.collection = Streams.toLowerCase(this.collection);
        for (String s : this.collection) {
            assertNotEquals(s.toUpperCase(), s);
        }
    }

    @Test
    public void testFilter() {
        String s = Streams.filter(this.collection, e -> e.equals("test"));
        assertNotNull(s);
    }

    @Test
    public void testFilterAndApply() {
        Optional<String> optional = Streams.filterAndApply(this.collection, e -> e.equals("test"), Optional::ofNullable);
        assertNotNull(optional);
        assertTrue(optional.isPresent());
    }

    @Test
    public void testOthers() {
        Collection<String> others = Streams.others(this.collection, e -> e.equals("test"));
        assertEquals(1, others.size());
    }

    @Test
    public void concatTest() {
        String[] first = new String[]{"test", "test1"};
        assertEquals(4, Streams.concat(first, first).length);
    }
}
