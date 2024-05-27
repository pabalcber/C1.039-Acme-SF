
package acme.features.sponsor.invoice;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.entities.sponsorships.Invoice;
import acme.roles.Sponsor;

@Service
public class SponsorInvoiceUpdateService extends AbstractService<Sponsor, Invoice> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorInvoiceRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id;
		int sponsorId;
		Invoice invoice;

		id = super.getRequest().getData("id", int.class);
		invoice = this.repository.findOneInvoiceById(id);

		sponsorId = super.getRequest().getPrincipal().getActiveRoleId();

		status = sponsorId == invoice.getSponsor().getId() && invoice.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Invoice object;
		int id;

		id = super.getRequest().getData("id", int.class);

		object = this.repository.findOneInvoiceById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Invoice object) {
		assert object != null;

		Integer sponsorId = super.getRequest().getPrincipal().getActiveRoleId();
		Sponsor sponsor = this.repository.findOneSponsorById(sponsorId);
		object.setSponsor(sponsor);
		super.bind(object, "code", "registrationTime", "dueDate", "quantity", "tax", "link");
	}

	@Override
	public void validate(final Invoice object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("code")) {

			Invoice projectSameCode = this.repository.findOneInvoiceByCode(object.getCode());

			if (projectSameCode != null)
				super.state(projectSameCode.getId() == object.getId(), "code", "sponsor.invoice.form.error.code");
		}

		if (!super.getBuffer().getErrors().hasErrors("registrationTime")) {

			Date registrationTime = object.getRegistrationTime();
			Date minimumDate = MomentHelper.parse("1969-12-31 23:59", "yyyy-MM-dd HH:mm");
			Date maximumDate = MomentHelper.parse("2200-12-31 23:59", "yyyy-MM-dd HH:mm");

			if (registrationTime != null) {
				Boolean isAfter = registrationTime.after(minimumDate) && registrationTime.before(maximumDate);
				super.state(isAfter, "registrationTime", "sponsor.invoice.form.error.registration-time");
			}
		}

		if (!super.getBuffer().getErrors().hasErrors("registrationTime")) {
			Date registrationTime;
			Date moment;

			registrationTime = object.getRegistrationTime();
			moment = object.getSponsorship().getMoment();

			if (registrationTime != null)
				super.state(registrationTime.after(moment), "registrationTime", "sponsor.invoice.form.error.registration-time-bis");
		}

		if (!super.getBuffer().getErrors().hasErrors("dueDate")) {
			Date registrationTime;
			Date dueDate;

			registrationTime = object.getRegistrationTime();
			dueDate = object.getDueDate();
			Date minimumDate = MomentHelper.parse("1969-12-31 23:59", "yyyy-MM-dd HH:mm");
			Date maximumDate = MomentHelper.parse("2200-12-31 23:59", "yyyy-MM-dd HH:mm");

			if (registrationTime != null && dueDate != null)
				super.state(MomentHelper.isLongEnough(registrationTime, dueDate, 1, ChronoUnit.MONTHS) && dueDate.after(registrationTime) && dueDate.after(minimumDate) && dueDate.before(maximumDate), "dueDate", "sponsor.invoice.form.error.dueDate");
		}

		if (!super.getBuffer().getErrors().hasErrors("publishedSponsorship"))
			super.state(object.getSponsorship().isDraftMode(), "*", "sponsor.invoice.form.error.published-sponsorship");

	}

	@Override
	public void perform(final Invoice object) {
		assert object != null;

		if (object.getTax() == null)
			object.setTax(0.0);

		this.repository.save(object);
	}

	@Override
	public void unbind(final Invoice object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "registrationTime", "dueDate", "quantity", "tax", "link", "draftMode");
		dataset.put("totalAmount", object.totalAmount());

		super.getResponse().addData(dataset);
	}

}
