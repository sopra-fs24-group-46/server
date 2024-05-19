package ch.uzh.ifi.hase.soprafs24.game;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.game.entity.GeoLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoLocationTest {
    
    @Test
    public void testGetDistanceTo() {
        GeoLocation location1 = new GeoLocation(46.0, 8.0);
        GeoLocation location2 = new GeoLocation(47.0, 8.0);
        assertEquals(110112.0, location1.getDistanceTo(location2), 1);
    }
    
    @Test
    public void testGetDistanceToNull() {
        GeoLocation location1 = new GeoLocation(46.0, 8.0);
        GeoLocation location2 = new GeoLocation(null, 8.0);
        assertThrows(IllegalArgumentException.class, () -> location1.getDistanceTo(location2));
    }
    
    @Test
    public void testIsNull() {
        GeoLocation location1 = new GeoLocation(46.0, 8.0);
        GeoLocation location2 = new GeoLocation(null, 8.0);
        assertFalse(location1.isNull());
        assertTrue(location2.isNull());
    }
}

