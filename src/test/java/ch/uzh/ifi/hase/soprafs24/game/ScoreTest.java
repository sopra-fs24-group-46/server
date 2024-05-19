package ch.uzh.ifi.hase.soprafs24.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.game.entity.Score;

class ScoreTest {
    
    @Test
    void add_scores() {
        Score score1 = new Score(10, 10.0);
        Score score2 = new Score(20, 20.0);
        Score sum = score1.add(score2);
        assertEquals(30, sum.getScore().intValue());
        assertEquals(30.0, sum.getDistance(), 0.1);
    }

    @Test
    void add_score_to_null() {
        Score score = new Score(10, 10.0);
        Score sum = score.add(null);
        assertEquals(10, sum.getScore().intValue());
        assertEquals(10.0, sum.getDistance(), 0.1);
    }

    @Test
    void add_null_to_score() {
        Score score = new Score(10, 10.0);
        Score sum = score.add(new Score(null, null));
        assertEquals(10, sum.getScore().intValue());
        assertEquals(10.0, sum.getDistance(), 0.1);
    }

    @Test
    void add_null_to_null() {
        Score sum = new Score(null, null).add(null);
        assertNull(sum.getScore());
        assertNull(sum.getDistance());
    }
}

