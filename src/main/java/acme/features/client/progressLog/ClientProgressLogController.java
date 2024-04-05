
package acme.features.client.progressLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractController;
import acme.entities.contracts.ProgressLog;
import acme.roles.clients.Client;

@Controller
public class ClientProgressLogController extends AbstractController<Client, ProgressLog> {

	// Internal State ---------------------------------------------------------------------------------------------------------------------------------------

	@Autowired
	private ClientProgressLogListService listService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		//super.addBasicCommand("show", this.showService);
		//super.addBasicCommand("create", this.createService);
		//super.addBasicCommand("update", this.updateService);
		//super.addBasicCommand("delete", this.deleteService);

		//super.addCustomCommand("publish", "update", this.publishService);
		//super.addCustomCommand("list-all", "list", this.listAllService);
	}

}
