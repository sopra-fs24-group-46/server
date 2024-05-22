package ch.uzh.ifi.hase.soprafs24.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;

@Repository("settingsRepository")
public interface SettingsRepository extends JpaRepository<Settings, Long> {
  List<Settings> findAllByHostUserId(Long hostUserId);

  List<Settings> findAllByName(String name);
}
