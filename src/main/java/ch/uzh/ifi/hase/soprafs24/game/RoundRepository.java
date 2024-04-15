package ch.uzh.ifi.hase.soprafs24.game;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.uzh.ifi.hase.soprafs24.game.entity.Round;

import java.util.List;
import java.util.Optional;

public interface RoundRepository extends JpaRepository<Round, Long> {
    // Add custom find methods here, for example:
    // List<Round> findByName(String name);

    // Optional<Round> findByName(String name);

    // Round save(Round round);

    // void deleteByName(String name);
}
