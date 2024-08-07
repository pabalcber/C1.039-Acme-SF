
package acme.features.client.contract;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.contracts.Contract;
import acme.entities.contracts.ProgressLog;
import acme.entities.projects.Project;
import acme.roles.clients.Client;

@Repository
public interface ClientContractRepository extends AbstractRepository {

	@Query("select c from Contract c where c.client.id = :id")
	Collection<Contract> findManyContractsByClientId(int id);

	@Query("select c from Contract c where c.id = :contractId")
	Contract findContractById(int contractId);

	@Query("select cl from Client cl where cl.id = :clientId")
	Client findClientById(int clientId);

	@Query("select p from Project p where p.id = :projectId")
	Project findOneProjectById(int projectId);

	@Query("select c from Contract c where c.code = :code")
	Contract findOneContractByCode(String code);

	@Query("SELECT DISTINCT c.project FROM Contract c WHERE c.client.id = :clientId")
	Collection<Project> findManyProjectsByClientId(int clientId);

	@Query("select  p from Project p WHERE  p.draftMode = false")
	Collection<Project> findManyProjects();

	@Query("select pl from ProgressLog pl where pl.contract.id = :id")
	Collection<ProgressLog> findManyProgressLogsByJobId(int id);

	@Query("SELECT SUM(c.budget.amount) FROM Contract c WHERE c.project.id = :id AND c.draftMode = false")
	Double combinedBudgetByContract(int id);

	@Query("select c from Contract c where c.draftMode = false")
	Collection<Contract> findAllPublishedContracts();

	@Query("SELECT cl FROM Client cl WHERE cl.userAccount.id = :accountId")
	Client findClientByAccountId(int accountId);

	@Query("select c.project from Contract c where c.id = :id")
	Project findProjectByContractId(int id);

	@Query("select p from Project p where p.code = :projectCode")
	Project findOneProjectByCode(String projectCode);

}
