
package acme.features.developer.trainingModule;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.trainingModule.DifficultyLevel;
import acme.entities.trainingModule.TrainingModule;
import acme.roles.Developer;

@Service
public class DeveloperTrainingModuleCreateService extends AbstractService<Developer, TrainingModule> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private DeveloperTrainingModuleRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		TrainingModule object;

		object = new TrainingModule();
		Integer developerId = super.getRequest().getPrincipal().getActiveRoleId();
		Developer developer = this.repository.findOneDeveloperById(developerId);
		object.setDeveloper(developer);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final TrainingModule object) {
		assert object != null;

		super.bind(object, "code", "creationMoment", "updateMoment", "difficulty", "details", "totalTime", "link", "project");

		Date currentMoment;
		Date creationMoment;

		currentMoment = MomentHelper.getCurrentMoment();
		creationMoment = new Date(currentMoment.getTime() - 2000); //Substracts two seconds to ensure the moment is in the past and before the update moment
		object.setCreationMoment(creationMoment);

		object.setPublished(false);
	}

	@Override
	public void validate(final TrainingModule object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			TrainingModule trainingModuleSameCode = this.repository.findOneTrainingModuleByCode(object.getCode());

			super.state(trainingModuleSameCode == null, "code", "developer.training-module.form.error.code");
		}

		if (!super.getBuffer().getErrors().hasErrors("updateMoment")) {
			Date creationMoment;
			Date updateMoment;

			creationMoment = object.getCreationMoment();
			updateMoment = object.getUpdateMoment();

			if (updateMoment != null)
				super.state(updateMoment.after(creationMoment), "updateMoment", "developer.training-module.form.error.update-moment");
		}

		if (!super.getBuffer().getErrors().hasErrors("project"))
			super.state(!object.getProject().isDraftMode(), "project", "developer.training-module.form.error.project");

		Date minimumDate;
		Date maximumDate;

		minimumDate = MomentHelper.parse("2000-01-01 00:00", "yyyy-MM-dd HH:mm");
		maximumDate = MomentHelper.parse("2200-12-31 23:59", "yyyy-MM-dd HH:mm");

		if (!super.getBuffer().getErrors().hasErrors("creationMoment"))
			super.state(MomentHelper.isAfterOrEqual(object.getCreationMoment(), minimumDate), "creationMoment", "developer.training-module.form.error.before-min-date");

		if (!super.getBuffer().getErrors().hasErrors("creationMoment"))
			super.state(MomentHelper.isBeforeOrEqual(object.getCreationMoment(), maximumDate), "creationMoment", "developer.training-module.form.error.after-max-date");

		if (!super.getBuffer().getErrors().hasErrors("creationMoment"))
			super.state(MomentHelper.isBeforeOrEqual(object.getCreationMoment(), MomentHelper.deltaFromMoment(maximumDate, -14, ChronoUnit.DAYS)), "creationMoment", "developer.training-module.form.error.no-room-for-period");

		Date updateMoment = object.getUpdateMoment();
		if (updateMoment != null) {
			if (!super.getBuffer().getErrors().hasErrors("updateMoment"))
				super.state(MomentHelper.isAfterOrEqual(object.getUpdateMoment(), minimumDate), "updateMoment", "developer.training-module.form.error.before-min-date");

			if (!super.getBuffer().getErrors().hasErrors("updateMoment"))
				super.state(MomentHelper.isBeforeOrEqual(object.getUpdateMoment(), maximumDate), "updateMoment", "developer.training-module.form.error.after-max-date");
		}
	}

	@Override
	public void perform(final TrainingModule object) {
		assert object != null;

		object.setId(0);
		this.repository.save(object);
	}

	@Override
	public void unbind(final TrainingModule object) {
		assert object != null;

		SelectChoices difficultyChoices;
		SelectChoices projectChoices;

		difficultyChoices = SelectChoices.from(DifficultyLevel.class, object.getDifficulty());
		projectChoices = SelectChoices.from(this.repository.findPublishedProjects(), "title", null);

		Dataset dataset;

		dataset = super.unbind(object, "code", "creationMoment", "updateMoment", "difficulty", "details", "totalTime", "link", "published", "project");
		dataset.put("difficulties", difficultyChoices);
		dataset.put("projects", projectChoices);

		super.getResponse().addData(dataset);
	}
}
