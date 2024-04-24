
package acme.features.any.project;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.projects.Project;

@Repository
public interface AnyProjectRepository extends AbstractRepository {

	@Query("select p from Project p where p.draftMode=false")
	Collection<Project> findManyProjectsByAvailability();

	@Query("select p from Project p where p.id = :id and p.draftMode=false")
	Project findOneProjectById(int id);

}
