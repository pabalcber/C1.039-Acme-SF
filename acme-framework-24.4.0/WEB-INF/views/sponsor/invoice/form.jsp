<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="sponsor.invoice.form.label.code"
		path="code" />
	<acme:input-moment code="sponsor.invoice.form.label.registrationTime"
		path="registrationTime" />
	<acme:input-moment code="sponsor.invoice.form.label.dueDate"
		path="dueDate" />
	<acme:input-money code="sponsor.invoice.form.label.quantity"
		path="quantity" />
	<acme:input-double code="sponsor.invoice.form.label.tax"
		path="tax" />
	<acme:input-url code="sponsor.sponsorship.form.label.link"
		path="link" />
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode==true}">
		<acme:input-money code="sponsor.inovice.list.label.totalAmount"
				path="totalAmount" readonly="true" />
			<acme:submit code="sponsor.invoice.form.button.update"
				action="/sponsor/invoice/update" />
			<acme:submit code="sponsor.invoice.form.button.delete"
				action="/sponsor/invoice/delete" />
				<acme:submit code="sponsor.invoice.form.button.publish"
				action="/sponsor/invoice/publish" />
		</jstl:when>
		
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode==false}">
		<acme:input-money code="sponsor.inovice.list.label.totalAmount"
				path="totalAmount" readonly="true" />
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="sponsor.invoice.form.button.create"
				action="/sponsor/invoice/create?sponsorshipId=${sponsorshipId}" />
		</jstl:when>
	</jstl:choose>
</acme:form>

