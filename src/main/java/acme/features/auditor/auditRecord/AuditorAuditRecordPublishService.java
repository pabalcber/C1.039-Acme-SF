
package acme.features.auditor.auditRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.auditRecords.AuditRecord;
import acme.roles.Auditor;

@Service
public class AuditorAuditRecordPublishService extends AbstractService<Auditor, AuditRecord> {

	@Autowired
	protected AuditorAuditRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		AuditRecord ra;
		Auditor auditor;
		masterId = super.getRequest().getData("id", int.class);

		ra = this.repository.findAuditRecordById(masterId);
		auditor = ra == null ? null : ra.getAuditor();
		status = ra != null && ra.isDraftMode() && super.getRequest().getPrincipal().hasRole(auditor);

		super.getResponse().setAuthorised(status);
	}
	@Override
	public void load() {
		AuditRecord object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findAuditRecordById(id);

		super.getBuffer().addData(object);
	}
	@Override
	public void bind(final AuditRecord object) {
		assert object != null;

		super.bind(object, "code", "startPeriod", "endPeriod", "mark", "link", "draftMode", "codeAudit");
	}
	@Override
	public void validate(final AuditRecord object) {
		assert object != null;
	}
	@Override
	public void perform(final AuditRecord object) {
		assert object != null;
		object.setDraftMode(false);

		this.repository.save(object);
	}

	@Override
	public void unbind(final AuditRecord object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "startPeriod", "endPeriod", "mark", "link", "draftMode", "codeAudit", "auditor");

		super.getResponse().addData(dataset);
	}

}
