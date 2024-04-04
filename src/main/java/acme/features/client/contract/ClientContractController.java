
package acme.features.client.contract;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractController;
import acme.entities.contracts.Contract;
import acme.roles.clients.Client;

@Controller
public class ClientContractController extends AbstractController<Client, Contract> {

	// Internal State ---------------------------------------------------------------------------------------------------------------------------------------

	@Autowired
	private ClientContractListService listService;

	// Constructors -----------------------------------------------------------------------------------------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
	}
}
