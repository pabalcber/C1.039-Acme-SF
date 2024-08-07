
package acme.features.authenticated.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Authenticated;
import acme.client.data.accounts.Principal;
import acme.client.data.accounts.UserAccount;
import acme.client.data.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.roles.clients.Client;
import acme.roles.clients.ClientType;

@Service
public class AuthenticatedClientCreateService extends AbstractService<Authenticated, Client> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedClientRepository	repository;

	// AbstractService<Authenticated, Client> ---------------------------

	private static String					identification	= "identification";


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Client object;
		Principal principal;
		int userAccountId;
		UserAccount userAccount;

		principal = super.getRequest().getPrincipal();
		userAccountId = principal.getAccountId();
		userAccount = this.repository.findOneUserAccountById(userAccountId);

		object = new Client();
		object.setIdentification("");
		object.setCompanyName("");
		object.setEmail("");
		object.setType(ClientType.INDIVIDUAL);
		object.setUserAccount(userAccount);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Client object) {
		assert object != null;

		super.bind(object, AuthenticatedClientCreateService.identification, "companyName", "email", "furtherInformation", "type");
	}

	@Override
	public void validate(final Client object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors(AuthenticatedClientCreateService.identification)) {
			Client existing;

			existing = this.repository.findClientByIdentification(object.getIdentification());
			super.state(existing == null, AuthenticatedClientCreateService.identification, "authenticated.client.form.error.duplicated");
		}
	}

	@Override
	public void perform(final Client object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final Client object) {
		assert object != null;

		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(ClientType.class, object.getType());

		dataset = super.unbind(object, AuthenticatedClientCreateService.identification, "companyName", "email", "furtherInformation", "type");
		dataset.put("types", choices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
