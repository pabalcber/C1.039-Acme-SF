
package acme.forms;

import acme.client.data.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeveloperDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	int							totalTrainingModulesWithUpdateMoment;
	int							totalTrainingSessionsWithLink;
	Double						trainingModulesAverageTime;
	Double						trainingModulesDeviationTime;
	Integer						trainingModulesMinimumTime;
	Integer						trainingModulesMaximumTime;

}
