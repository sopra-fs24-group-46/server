package ch.uzh.ifi.hase.soprafs24.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.game.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

}
