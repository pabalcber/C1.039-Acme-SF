
package acme.features.any.contract;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.contracts.Contract;

@Repository
public interface AnyContractRepository extends AbstractRepository {

	@Query("select c from Contract c where c.draftMode = false")
	Collection<Contract> findAllPublishedContracts();

	@Query("select c from Contract c where c.id = :contractId")
	Contract findContractById(int contractId);

}
