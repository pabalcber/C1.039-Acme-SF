
package acme.roles.clients;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import acme.client.data.AbstractRole;
import acme.entities.contracts.Contract;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Client extends AbstractRole {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------
	@Column(unique = true)
	@NotBlank
	@Pattern(regexp = "CLI-[0-9]{4}", message = "Identification debe seguir el patrón 'CLI-[0-9]{4}'")
	private String				identification;

	@NotBlank
	@Length(min = 1, max = 76)
	@Column(unique = true)
	private String				companyName;

	@NotNull
	private ClientType			type;

	@NotBlank
	@Column(unique = true)
	private String				email;

	@URL
	private String				furtherInformation;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@OneToMany()
	private List<Contract>		contracts;

}
