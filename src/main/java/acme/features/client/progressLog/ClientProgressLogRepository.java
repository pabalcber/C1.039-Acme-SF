
package acme.features.client.progressLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.contracts.Contract;
import acme.entities.contracts.ProgressLog;

@Repository
public interface ClientProgressLogRepository extends AbstractRepository {

	@Query("select c from Contract c where c.id = :masterId")
	Contract findOneContractById(int masterId);

	@Query("select pl from ProgressLog pl where pl.contract.id = :masterId")
	Collection<ProgressLog> findManyProgressLogsByMasterId(int masterId);

	@Query("select pl.contract from ProgressLog pl where pl.id = :id")
	Contract findOneContractByProgressLogId(int id);

	@Query("select pl from ProgressLog pl where pl.id = :id")
	ProgressLog findOneProgressLogById(int id);

	@Query("select pl from ProgressLog pl where pl.recordId = :recordId")
	ProgressLog findOneProgressLogByRecordId(String recordId);

}
