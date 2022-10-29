package tr.com.orioninc.laborant.repository;

import tr.com.orioninc.laborant.model.Lab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabRepository extends JpaRepository<Lab,Integer> {
 Lab findByLabName(String labName);
 Lab findByUserNameAndHost(String userName, String host);
}
