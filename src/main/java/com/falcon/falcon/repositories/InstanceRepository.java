package com.falcon.falcon.repositories;

import com.falcon.falcon.entities.Instance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstanceRepository extends JpaRepository<Instance, Long> {
}
