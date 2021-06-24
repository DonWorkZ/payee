package com.fdmgroup.pilotbank2.repo;

import com.fdmgroup.pilotbank2.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepo extends JpaRepository <Address, Long> {

}
