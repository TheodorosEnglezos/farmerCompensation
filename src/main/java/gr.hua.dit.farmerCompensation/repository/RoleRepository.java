package gr.hua.dit.farmerCompensation.repository;

import gr.hua.dit.farmerCompensation.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    //find role by name
    Optional<Role> findByName(String roleName);
}
