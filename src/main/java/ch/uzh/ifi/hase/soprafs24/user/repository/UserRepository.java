package ch.uzh.ifi.hase.soprafs24.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.user.entity.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
  User findByName(String name);

  User findByUsername(String username);
}
