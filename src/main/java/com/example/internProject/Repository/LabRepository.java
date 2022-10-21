package com.example.internProject.Repository;

import com.example.internProject.Model.Lab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabRepository extends JpaRepository<Lab,Integer> {
 public Lab findByLabName(String labName);
 public Lab findByUserNameAndHost(String userName, String host);
}
