package tr.com.orioninc.laborant.security.authenticate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.com.orioninc.laborant.security.authenticate.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByUsername(String username);

}