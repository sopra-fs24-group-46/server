package ch.uzh.ifi.hase.soprafs24.game;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.uzh.ifi.hase.soprafs24.game.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {

}
