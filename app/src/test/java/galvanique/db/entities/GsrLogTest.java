package galvanique.db.entities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO(kgeffen) write words
 */
public class GsrLogTest {
    @Test
    public void sanity_check() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void constructor_initializes_attributes() {
        int id = 4;
        long timestamp = 300;
        long conductivity = 8;

        GsrLog log3 = new GsrLog(
                id,
                timestamp,
                conductivity);
        assertEquals(log3.getId(), id);
        assertEquals(log3.getTimestamp(), timestamp);
        assertEquals(log3.getConductivity(), conductivity);

        GsrLog log2 = new GsrLog(
                timestamp,
                conductivity);
        assertEquals(log3.getTimestamp(), timestamp);
        assertEquals(log3.getConductivity(), conductivity);
    }

    // NOTE(kgeffen) Currently we haven't implemented a comparator for this class,
    // we might never because that comparison might happen as a db query instead.
//    @Test
//    public void comparator_orders_by_timestamp() {
//        long ts1 = 100;
//        long ts2 = ts1 + 1;
//
//        GsrLog log1 = new GsrLog(ts1, 0);
//        GsrLog log2 = new GsrLog(ts2, 0);
//
//        assert(log2 > log1);
//    }
}
