
package acme.features.auditor.codeAudits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.codeAudits.CodeAudit;
import acme.features.auditor.auditRecord.AuditorAuditRecordRepository;
import acme.roles.Auditor;

@Service
public class AuditorCodeAuditPublishService extends AbstractService<Auditor, CodeAudit> {

	@Autowired
	protected AuditorCodeAuditRepository	repository;

	@Autowired
	protected AuditorAuditRecordRepository	rp;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		CodeAudit ca;
		Auditor auditor;
		masterId = super.getRequest().getData("id", int.class);

		ca = this.repository.findCodeAuditById(masterId);
		auditor = ca == null ? null : ca.getAuditor();
		status = ca != null && ca.isDraftMode() && super.getRequest().getPrincipal().hasRole(auditor);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		CodeAudit object;

		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findCodeAuditById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final CodeAudit object) {
		assert object != null;

		super.bind(object, "code", "execution", "type", "correctiveActions", "link", "project");

	}
	@Override
	public void validate(final CodeAudit object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("Mark"))

			super.state(!object.Mark(this.rp.getMarkOfAsociatedAuditRecords(object)).trim().equals("F") && !object.Mark(this.rp.getMarkOfAsociatedAuditRecords(object)).trim().equals("F-"), "Mark", "auditor.codeAudit.error.Mark");
	}
	@Override
	public void perform(final CodeAudit object) {
		assert object != null;
		object.setDraftMode(false);
		this.repository.save(object);
	}
	@Override
	public void unbind(final CodeAudit object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "execution", "type", "correctiveActions", "link", "project", "draftMode");
		super.getResponse().addData(dataset);
	}

}
