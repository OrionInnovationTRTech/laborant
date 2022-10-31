package tr.com.orioninc.laborant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.com.orioninc.laborant.model.Lab;

@Repository
public interface LabRepository extends JpaRepository<Lab, Integer> {
    public Lab findByLabName(String labName);

    public Lab findByUserNameAndHost(String userName, String host);
}
