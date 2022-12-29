package tr.com.orioninc.laborant.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.security.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByUsername(String username);

    List<String> findAllByLabsContains(Lab lab);
}