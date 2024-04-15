package ch.uzh.ifi.hase.soprafs24.game;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.uzh.ifi.hase.soprafs24.game.entity.History;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

}
