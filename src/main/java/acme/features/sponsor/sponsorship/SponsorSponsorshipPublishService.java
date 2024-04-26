
package acme.features.sponsor.sponsorship;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.datatypes.Money;
import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.projects.Project;
import acme.entities.sponsorships.Invoice;
import acme.entities.sponsorships.Sponsorship;
import acme.entities.sponsorships.SponsorshipType;
import acme.roles.Sponsor;

@Service
public class SponsorSponsorshipPublishService extends AbstractService<Sponsor, Sponsorship> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorSponsorshipRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		int sponsorId;
		Sponsorship sponsorship;

		id = super.getRequest().getData("id", int.class);
		sponsorship = this.repository.findOneSponsorshipById(id);

		sponsorId = super.getRequest().getPrincipal().getActiveRoleId();

		status = sponsorId == sponsorship.getSponsor().getId() && sponsorship.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Sponsorship object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneSponsorshipById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Sponsorship object) {
		assert object != null;

		super.bind(object, "code", "moment", "durationStartTime", "durationEndTime", "amount", "type", "email", "link", "project");
	}

	@Override
	public void validate(final Sponsorship object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("totalAmount")) {
			Collection<Invoice> invoices = this.repository.findInvoicesOfASponsorship(object.getId());

			double invoiceTotAmount = invoices.stream().mapToDouble(i -> this.currencyTransformerUsd(i.getQuantity(), i.totalAmount().getAmount()).getAmount()).sum();
			super.state(invoiceTotAmount == this.currencyTransformerUsd(object.getAmount(), object.getAmount().getAmount()).getAmount(), "*", "sponsor.sponsorship.form.error.invalidTotalAmount");
		}

		if (!super.getBuffer().getErrors().hasErrors("durationStartTime")) {
			Date durationStartTime;
			Date moment;
			durationStartTime = object.getDurationStartTime();
			moment = object.getMoment();

			super.state(durationStartTime.after(moment), "durationStartTime", "sponsor.sponsorship.form.error.durationStartTime");
		}

		if (!super.getBuffer().getErrors().hasErrors("durationEndTime")) {
			Date durationStartTime;
			Date durationEndTime;

			durationStartTime = object.getDurationStartTime();
			durationEndTime = object.getDurationEndTime();

			super.state(MomentHelper.isLongEnough(durationStartTime, durationEndTime, 1, ChronoUnit.MONTHS) && durationEndTime.after(durationStartTime), "durationEndTime", "sponsor.sponsorship.form.error.durationEndTime");
		}

		if (!super.getBuffer().getErrors().hasErrors("amount"))
			super.state(object.getAmount().getAmount() >= 0, "amount", "sponsor.sponsorship.form.error.amount");

		if (!super.getBuffer().getErrors().hasErrors("unpublishedInvoices")) {

			Collection<Invoice> unpublishedInvoices;

			unpublishedInvoices = this.repository.findUnpublishedInvoicesBySponsorshipId(object.getId());

			super.state(unpublishedInvoices.isEmpty(), "*", "sponsor.sponsorship.form.error.unpublished-invoices");
		}

		if (!super.getBuffer().getErrors().hasErrors("project"))
			super.state(!object.getProject().isDraftMode(), "project", "sponsor.sponsorship.form.error.project-not-published");

	}

	private Money currencyTransformerUsd(final Money currency, final Double amount) {
		Money res = new Money();
		res.setCurrency("USD");

		if (currency.getCurrency().equals("USD"))
			res.setAmount(amount);

		else if (currency.getCurrency().equals("EUR"))
			res.setAmount(amount * 1.07);

		else
			res.setAmount(amount * 1.25);

		return res;
	}

	@Override
	public void perform(final Sponsorship object) {
		assert object != null;

		object.setDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Sponsorship object) {
		assert object != null;

		SelectChoices choices;

		Collection<Project> projects = this.repository.findProjects();
		SelectChoices choices2;
		Dataset dataset;

		choices = SelectChoices.from(SponsorshipType.class, object.getType());
		choices2 = SelectChoices.from(projects, "code", (Project) projects.toArray()[0]);

		dataset = super.unbind(object, "code", "moment", "durationStartTime", "durationEndTime", "amount", "type", "email", "link", "project", "draftMode");
		dataset.put("types", choices);
		dataset.put("projects", choices2);

		super.getResponse().addData(dataset);
	}

}
