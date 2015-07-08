<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="css" value="registration" />
</jsp:include>

<div class="container pg-body">
	<form class="form-student" action="${pageContext.request.contextPath}/student/register" method="POST">
		<div class="row hidden" id="reg_err">
			<div class="col-md-12 alert alert-danger">There are errors in your submission. Please fix them.</div>
		</div>

		<div class="row">
			<div class="col-md-12 form-title">
				<h4>Student Registration</h4>
				<img class="pull-right form-icon" src="${pageContext.request.contextPath}/images/student_icon.png" alt="">
			</div>
		</div>

		<div class="row">
			<div class="col-md-6">
				<div class="form-group" id="ID">
					<label class="control-label">PRN</label> <input type="text" class="form-control" name="ID" maxlength="10" value="${student.ID}" tabindex="1" required autofocus>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="col-md-6">
				<div class="form-group" id="firstName">
					<label class="control-label">First Name</label><input type="text" class="form-control" name="firstName" maxlength="25" tabindex="2" value="${student.firstName}" required>
				</div>

				<div class="form-group" id="lastName">
					<label class="control-label">Last Name</label><input type="text" class="form-control" name="lastName" maxlength="25" tabindex="4" value="${student.lastName}" required>
				</div>
			</div>
			<div class="col-md-6">
				<div class="form-group" id="middleName">
					<label class="control-label">Middle Name</label><input type="text" class="form-control" name="middleName" maxlength="25" tabindex="3" value="${student.middleName}">
				</div>
				<div class="form-group" id="batch">
					<label class="control-label">Batch</label> <select class="form-control" name="batch"  tabindex="5" required>
						<c:forEach var="batch" items="${batches}">
							<c:choose>
								<c:when test="${batch == student.batch}">
									<option value="${batch}" selected>${batch}</option>
								</c:when>
								<c:otherwise>
									<option value="${batch}">${batch}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6">
				<div class="form-group" id="branch">
					<label class="control-label">Branch</label><select class="form-control" name="branch" tabindex="6" required>	
					
					<c:forEach var="branch" items="${branches}">
						<c:choose>
							<c:when test="${branch.ID == student.branch.ID}">
								<option value="${branch.ID}" selected>${branch.name}</option>
							</c:when>
							<c:otherwise>
								<option value="${branch.ID}">${branch.name}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					</select>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 form-title">
				<h4 class="text-left">Contact Details</h4>
				<img class="pull-right form-icon" src="/images/contact_icon.png" alt="">
			</div>
		</div>
		<div class="row">
			<div class="col-md-6">
				<div class="form-group" id="email">
					<label class="control-label">Email ID</label> <input type="email" class="form-control" name="email" maxlength="50" tabindex="7" value="${student.email}" required>
				</div>
				<div class="form-group" id="smobile">
					<label class="control-label">Secondary Mobile Number</label> <input type="tel" placeholder="Optional" class="form-control" name="smobile" maxlength="10" tabindex="9" value="${student.smobile}">
				</div>
			</div>
			<div class="col-md-6">
				<div class="form-group" id="pmobile">
					<label class="control-label">Primary Mobile Number</label> <input type="tel" class="form-control" name="pmobile" maxlength="10" tabindex="8" value="${student.pmobile}" required>
				</div>

			</div>
		</div>
		<div class="row" id="part1btn">
			<div class="col-md-3"></div>
			<div class="col-md-6">
				<button class="form-control btn btn-primary" type="button" tabindex="10">Next</button>
			</div>
			<div class="col-md-3"></div>
		</div>

		<c:choose>
			<c:when test="{empty reg_error}">
				<c:set var="showZeroScore" value="false" />
			</c:when>
			<c:otherwise>
				<c:set var="showZeroScore" value="true" />
			</c:otherwise>
		</c:choose>

		<div id="part2" class="hidden">
			<div class="row">
				<div class="col-md-6">
					<div class="form-group" id="sem1">
						<label class="control-label">%age in 1st semester</label> <input class="form-control" type="number" name="score" maxlength="5"
							value='${(student.score[0] != 0 || showZeroScore) && student.score[0] != -1 ? student.score[0] : ""}' tabindex="11" required>
					</div>
					<div class="form-group" id="sem2">
						<label class="control-label">%age in 2nd semester</label> <input class="form-control" type="number" name="score" maxlength="5"
							value='${(student.score[1] != 0 || showZeroScore) && student.score[1] != -1 ? student.score[1] : ""}' required>
					</div>

					<div class="form-group" id="sem3">
						<label class="control-label">%age in 3rd semester</label> <input class="form-control" type="number" name="score" maxlength="5"
							value='${(student.score[2] != 0 || showZeroScore) && student.score[2] != -1 ? student.score[2] : ""}' required>
					</div>
					<div class="form-group" id="sem4">
						<label class="control-label">%age in 4th semester</label> <input class="form-control" type="number" name="score" maxlength="5"
							value='${(student.score[3] != 0 || showZeroScore) && student.score[3] != -1 ? student.score[3] : ""}' required>
					</div>


				</div>
				<div class="col-md-6">
					<div class="form-group" id="sem5">
						<label class="control-label">%age in 5th semester</label> <input class="form-control" type="number" name="score" maxlength="5"
							value='${(student.score[4] != 0 || showZeroScore) && student.score[4] != -1 ? student.score[4] : ""}' required>
					</div>
					<div class="form-group" id="sem6">
						<label class="control-label">%age in 6th semester</label> <input class="form-control" type="number" name="score" maxlength="5"
							value='${(student.score[5] != 0 || showZeroScore) && student.score[5] != -1 ? student.score[5] : ""}' required>
					</div>
					<div class="form-group" id="sem7">
						<label class="control-label">%age in 7th semester</label> <input class="form-control" placeholder="Optional" type="number" name="score" maxlength="5" id="inp-sem7"
							value='${fn:length(student.score) >= 7  && (student.score[6] != 0 || showZeroScore) && student.score[6] != -1 ? student.score[6] : ""}'>
					</div>
					<div class="form-group" id="sem8">
						<label class="control-label">%age in 8th semester</label> <input class="form-control" placeholder="Optional" type="number" name="score" maxlength="5" id="inp-sem8"
							value='${fn:length(student.score) == 8  && (student.score[7] != 0 || showZeroScore) && student.score[7] != -1 ? student.score[7] : ""}'>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<div class="form-group" id="addtional_qualifications">
						<label class="control-label">Additional Qualifications (Certifications, etc.)</label>
						<textarea class="form-control" name="additional_qualifications" rows="5" cols="30" placeholder="Optional">${student.additional_qualifications}</textarea>
					</div>
				</div>
				<div class="row">
					<div class="col-md-3"></div>
					<div class="col-md-6">
						<button class="form-control btn btn-primary" type="submit">Submit form for verification</button>
					</div>
					<div class="col-md-3"></div>
				</div>
			</div>
		</div>
	</form>
</div>

<script type="text/javascript">
$(document).ready(function() {
	$('#part1btn').click(function() {
		$('#part2').removeClass('hidden'); 
		$('#part1btn').addClass('hidden');
		$('html, body').scrollTop($('#part2').offset().top)
	});	
	<c:if test="${!empty reg_error}">	
		$('#reg_err').removeClass('hidden');
		$('#part2').removeClass('hidden');
		$('#part1btn').addClass('hidden');
		<c:forEach var="field" items="${validity.get('valid')}">
			$("#${field}").addClass("has-success has-feedback");		
			$("#${field}").append("<span class='glyphicon glyphicon-ok form-control-feedback' id='" + $(this).attr('id') + "-icon'></span>");		
		</c:forEach>
		<c:forEach var="field" items="${validity.get('invalid')}">
			$("#${field}").addClass("has-error has-feedback");			
			$("#${field}").append("<span class='glyphicon glyphicon-remove form-control-feedback' id='" + $(this).attr('id') + "-icon'></span>");			
		</c:forEach>	
	</c:if>
});
</script>

<%@ include file="/WEB-INF/templates/footer.jsp"%>