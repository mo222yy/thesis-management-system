package project.model.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import project.model.entities.Supervisor;

public interface SupervisorRepository extends MongoRepository<Supervisor, String>  {
	Supervisor findFirstByuserId(String userId);
	//@Query("{availableForSupervisor:'?0'}")
	List<Supervisor> findByAvailableForSupervisorTrue();
}
