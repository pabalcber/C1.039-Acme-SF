
package acme.entities.codeAudits;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import acme.client.data.AbstractEntity;
import acme.entities.projects.Project;
import acme.roles.Auditor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CodeAudit extends AbstractEntity {
	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@NotBlank
	@Unique
	@Pattern(regexp = "^[A-Z]{1,3}-[0-9]{3}$")
	@NotNull
	private String				code;

	@NotNull
	@Past
	@Temporal(TemporalType.TIMESTAMP)
	private Date				execution;

	@NotNull
	private Type				type;

	@NotNull
	@NotBlank
	@Length(max = 101)
	private String				correctiveActions;

	@URL
	@Length(max = 255)
	private String				link;

	private boolean				draftMode;

	// Derived attributes -----------------------------------------------------


	public String Mark(final List<String> lista) {
		Map<String, Integer> frecuencia = new HashMap<>();

		for (String str : lista)
			frecuencia.put(str, frecuencia.getOrDefault(str, 0) + 1);

		String moda = null;
		int maxFrecuencia = 0;
		for (Map.Entry<String, Integer> entry : frecuencia.entrySet())
			if (entry.getValue() > maxFrecuencia) {
				moda = entry.getKey();
				maxFrecuencia = entry.getValue();
			}

		return moda;
	}

	// Relationships ----------------------------------------------------------


	@NotNull
	@Valid
	@ManyToOne(optional = false)
	private Project		project;

	@NotNull
	@ManyToOne(optional = false)
	protected Auditor	auditor;

}
