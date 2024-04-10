//this stores the games
package ch.uzh.ifi.hase.soprafs24.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByPublicId(String gameId);

    Optional<Game> findById(Long id);

    boolean existsById(Long id);

    void deleteByPublicId(String publicId);

    void deleteById(Long id);

    void delete(Game game);

    List<Game> findAll();

    long count();

    void deleteAll();

    void flush();

}
