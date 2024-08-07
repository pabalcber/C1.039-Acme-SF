
package acme.entities.projects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import acme.client.data.AbstractEntity;
import acme.client.data.datatypes.Money;
import acme.roles.Developer;
import acme.roles.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Project extends AbstractEntity {
	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Column(unique = true)
	@NotBlank
	@Pattern(regexp = "^[A-Z]{3}-[0-9]{4}$", message = "")
	private String				code;
	@NotBlank
	@Length(max = 75)
	private String				title;
	@NotBlank
	@Length(max = 100)
	private String				abstractPj;

	private boolean				fatalErrors;
	@NotNull
	private Money				cost;
	@URL
	@Length(max = 255)
	private String				optionalLink;

	private boolean				draftMode;
	//system must reject every project with fatal errors

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------
	@Valid
	@ManyToOne
	@NotNull
	private Manager				manager;

	@Valid
	@ManyToOne
	private Developer			developer;
}
