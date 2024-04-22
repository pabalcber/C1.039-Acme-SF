
package acme.features.developer.TrainingModule;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.projects.Project;
import acme.entities.training.TrainingModule;
import acme.entities.training.difficultyLevelType;
import acme.roles.Developer;

@Service
public class DeveloperTrainingModuleCreateService extends AbstractService<Developer, TrainingModule> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private DeveloperTrainingModuleRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRole(Developer.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrainingModule object;
		Developer developer;

		developer = this.repository.findDeveloperById(super.getRequest().getPrincipal().getActiveRoleId());
		object = new TrainingModule();
		object.setDeveloper(developer);
		object.setDraftMode(true);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final TrainingModule object) {
		assert object != null;
		super.bind(object, "code", "creationMoment", "details", "difficultyLevel", "updatedMoment", "optionalLink", "totalTime", "draftMode", "project");

	}

	@Override
	public void validate(final TrainingModule object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("code")) {
			TrainingModule existing;

			existing = this.repository.findTrainingModuleByCode(object.getCode());
			super.state(existing == null, "code", "developer.trainingModule.error.code.duplicated");
		}
	}

	@Override
	public void perform(final TrainingModule object) {
		assert object != null;

		this.repository.save(object);
	}

	@Override
	public void unbind(final TrainingModule object) {
		assert object != null;

		Dataset dataset;
		SelectChoices choices;
		SelectChoices choices2;
		final Collection<Project> projects = this.repository.findAllProject();

		choices = SelectChoices.from(projects, "code", object.getProject());
		choices2 = SelectChoices.from(difficultyLevelType.class, object.getDifficultyLevel());
		dataset = super.unbind(object, "code", "creationMoment", "details", "updatedMoment", "optionalLink", "totalTime", "draftMode");
		dataset.put("difficultyLevels", choices2);
		dataset.put("project", choices.getSelected().getKey());
		dataset.put("projects", choices);
		super.getResponse().addData(dataset);

	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}