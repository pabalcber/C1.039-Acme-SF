
package acme.features.auditor.codeAudits;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Principal;
import acme.client.data.models.Dataset;
import acme.client.services.AbstractService;
import acme.entities.codeAudits.CodeAudit;
import acme.features.auditor.auditRecord.AuditorAuditRecordRepository;
import acme.roles.Auditor;

@Service
public class AuditorCodeAuditListMineService extends AbstractService<Auditor, CodeAudit> {

	@Autowired
	private AuditorCodeAuditRepository		rp;
	@Autowired
	protected AuditorAuditRecordRepository	repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<CodeAudit> objects;
		Principal principal;
		principal = super.getRequest().getPrincipal();

		objects = this.rp.findCodeAuditsByAuditorId(principal.getActiveRoleId());

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final CodeAudit object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "code", "execution", "type", "project", "auditor", "draftMode");
		String mark = object.Mark(this.repository.getMarkOfAsociatedAuditRecords(object));
		dataset.put("Mark", mark);

		super.getResponse().addData(dataset);
	}
}
