package tr.com.orioninc.laborant.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.com.orioninc.laborant.app.model.Team;


@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

    public Team findByName(String name);
}