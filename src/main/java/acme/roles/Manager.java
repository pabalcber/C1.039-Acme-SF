
package acme.roles;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import acme.client.data.AbstractRole;
import acme.entities.projects.Project;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Manager extends AbstractRole {
	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------
	@NotBlank
	@Length(max = 75)
	private String				degree;
	@NotBlank
	@Length(max = 100)
	private String				overview;
	@NotBlank
	@Length(max = 100)
	private String				certificationsList;
	@URL
	private String				optionalLink;
	// Relationships ----------------------------------------------------------
	@Valid
	@OneToMany
	private List<Project>		projects;
}
