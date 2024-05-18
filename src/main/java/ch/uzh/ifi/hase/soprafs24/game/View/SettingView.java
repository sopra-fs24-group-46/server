//This is an immutable wrapper for the Settings Class
package ch.uzh.ifi.hase.soprafs24.game.View;

import java.util.List;

import ch.uzh.ifi.hase.soprafs24.game.entity.LocationTypes;
import ch.uzh.ifi.hase.soprafs24.geo_admin_api.RegionType;

public interface SettingView {
    public Long getHostUserId();

    public Integer getMaxPlayers();

    public Integer getRounds();

    public Integer getGuessingTime();

    public Integer getQuestionTime();

    public Integer getMapRevealTime();

    public Integer getLeaderBoardTime();
    
    public String getRegion();

    public RegionType getRegionType();

    public List<LocationTypes> getLocationTypes();

    public List<String> getLocationNames();
}

