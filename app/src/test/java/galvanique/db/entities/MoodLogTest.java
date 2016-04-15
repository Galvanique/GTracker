package galvanique.db.entities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * TODO(kgeffen) write words
 */
public class MoodLogTest {
    int id = 7;
    MoodLog basicLog;
    MoodLog.Mood happy = MoodLog.Mood.Happy;

    @Before
    public void setUp() throws Exception {
        basicLog = new MoodLog(id, 0, happy, 0, 0, 0, 0, "");
    }

    @Test
    public void logs_with_same_id_equal() throws Exception {
        assert(basicLog.equals(
                new MoodLog(id, 2, happy, 1, 2, 1, 1, "anything")
            ));
    }

    @Test
    public void logs_with_different_id_unequal() throws Exception {
        assertFalse(basicLog.equals(
                new MoodLog(id + 1, 2, happy, 1, 2, 1, 1, "truly")
        ));
    }

    @Test
    public void get_mood_string_happy() {
        assertEquals(basicLog.getMoodString(), "Happy");
    }
}
