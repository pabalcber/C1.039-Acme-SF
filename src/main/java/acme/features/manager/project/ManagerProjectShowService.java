
package acme.features.manager.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.projects.Project;
import acme.roles.Manager;

@Service
public class ManagerProjectShowService extends AbstractService<Manager, Project> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerProjectRepository repository;


	// AbstractService interface ----------------------------------------------
	@Override
	public void authorise() {
		boolean status;
		int projectId;
		Project project;
		projectId = super.getRequest().getData("id", int.class);
		project = this.repository.findOneProjectById(projectId);
		status = super.getRequest().getPrincipal().hasRole(project.getManager()) || project != null && !project.isDraftMode();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Project object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneProjectById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final Project object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "title", "abstractPj", "fatalErrors", "cost", "optionalLink", "draftMode");
		dataset.put("masterId", object.getManager().getId());
		super.getResponse().addData(dataset);
	}
}
