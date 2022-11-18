package tr.com.orioninc.laborant.app.repository;

import tr.com.orioninc.laborant.app.model.Lab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabRepository extends JpaRepository<Lab, Integer> {
    public Lab findByLabName(String labName);

    public Lab findByUserNameAndHost(String userName, String host);
}
