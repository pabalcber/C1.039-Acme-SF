
package acme.features.client.progresslog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.contracts.Contract;
import acme.entities.contracts.ProgressLog;
import acme.roles.clients.Client;

@Service
public class ClientProgressLogUpdateService extends AbstractService<Client, ProgressLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClientProgressLogRepository	repository;

	// AbstractService interface ----------------------------------------------

	private static String				responsiblePerson	= "responsiblePerson";


	@Override
	public void authorise() {
		boolean status;
		int progressLogId;
		Contract contract;
		int clientContractId;
		int authClientId;
		Boolean isMyContract;
		int accountId;

		progressLogId = super.getRequest().getData("id", int.class);
		contract = this.repository.findOneContractByProgressLogId(progressLogId);

		clientContractId = contract.getClient().getId();
		accountId = super.getRequest().getPrincipal().getAccountId();
		authClientId = this.repository.findClientByAccountId(accountId).getId();
		isMyContract = authClientId == clientContractId;

		status =  contract.isDraftMode() ;

		super.getResponse().setAuthorised(status && isMyContract);
	}

	@Override
	public void load() {
		ProgressLog object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneProgressLogById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final ProgressLog object) {
		assert object != null;

		super.bind(object, "recordId", "completeness", "comment", ClientProgressLogUpdateService.responsiblePerson);
	}

	@Override
	public void validate(final ProgressLog object) {
		assert object != null;
	}

	@Override
	public void perform(final ProgressLog object) {
		assert object != null;

		int id = super.getRequest().getData("id", int.class);
		ProgressLog pl = this.repository.findOneProgressLogById(id);

		object.setRecordId(pl.getRecordId());
		this.repository.save(object);
	}

	@Override
	public void unbind(final ProgressLog object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "recordId", "completeness", "comment", "responsiblePerson");
		dataset.put("id", super.getRequest().getData("id", int.class));
		dataset.put("draftMode", object.getContract().isDraftMode());
		dataset.put("contract", object.getContract().getCode());

		super.getResponse().addData(dataset);
	}

}
