<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="modal-header">
	<button class="close" data-dismiss="modal">&times;</button>
	<h4 class="modal-title">Add New Company</h4>
</div>

<div class="modal-body">
	<div class="row hidden" id="form-success">
		<div class="alert alert-success">Company created successfully</div>
	</div>

	<form class="form-company" action="${pageContext.request.contextPath}/admin/company/add" method="POST">
		<div class="row hidden" id="form-error">
			<div class="alert alert-danger">There are errors in your submission. Please fix them.</div>
		</div>

		<div class="row">
			<div class="col-md-12">
				<div class="form-group has-feedback form-feedback" id="name">
					<label class="control-label">Company Name</label><input tabindex="1" required type="text" class="form-control" name="name" maxlength="50"> <span id="name-ok"
						class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="name-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="form-group has-feedback" id="branch">
					<label class="control-label">Branch</label><select class="form-control" name="branch" tabindex="2" required>
						<c:forEach var="branch" items="${branches}">
							<option value="${branch.ID}">${branch.name}</option>
						</c:forEach>
					</select> <span id="branch-ok" class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="branch-error"
						class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="form-group has-feedback form-feedback" id="criteria">
					<label class="control-label">Criteria</label><input tabindex="3" required class="form-control" type="text" name="criteria"> <span id="criteria-ok" class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span>
					<span class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon' id="criteria-error"></span>
				</div>
				<span class="help-block">Enter 0 for no criteria, or any number between 40 and 100</span>
			</div>
		</div>
		<div class="row">
			<div class="col-md-4"></div>
			<div class="col-md-4">
				<button tabindex="4" id='company-add-submit' class="form-control btn btn-primary submitbtn" type="submit">Submit</button>
			</div>
			<div class="col-md-4"></div>
		</div>
		<input type="reset" class="hidden" id="form-company-add-reset">
	</form>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/admin-company.js"></script>