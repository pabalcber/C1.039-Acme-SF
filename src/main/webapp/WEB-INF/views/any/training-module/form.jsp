<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="developer.training-module.form.label.code" path="code" />
	<acme:input-textbox code="developer.training-module.form.label.difficulty" path="difficulty"/>
	<acme:input-textbox code="developer.training-module.form.label.details" path="details" />
	<acme:input-integer code="developer.training-module.form.label.total-time" path="totalTime" />
	<acme:input-url code="developer.training-module.form.label.link" path="link" />
	<acme:input-textbox code="developer.training-module.form.label.project" path="project"/>	
	<acme:input-moment code="developer.training-module.form.label.creation-moment" path="creationMoment" readonly="true" />
	<acme:input-moment code="developer.training-module.form.label.update-moment" path="updateMoment" readonly="true" />
</acme:form>