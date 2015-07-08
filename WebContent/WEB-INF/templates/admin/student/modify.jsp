<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal">&times;</button>
	<h4 class="modal-title">Modifying Student ${student.ID}</h4>
</div>
<div class="modal-body">
	<div class="row hidden" id="form-success">
		<div class="alert alert-success">Student Modified</div>
	</div>

	<form class="form-student" action='${pageContext.request.contextPath}/admin/student/modify' method="POST">
		<div class="row hidden" id="form-error">
			<div class="alert alert-danger">There are errors in your submission. Please fix them.</div>
		</div>
		<div class="row">
			<div class="col-md-6">
				<div class="form-group has-feedback" id="ID">
					<label class="control-label">PRN</label> <input type="text" class="form-control" name="ID" maxlength="10" value="${student.ID}" tabindex=1 required autofocus> <span id="ID-ok"
						class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="ID-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6">
				<div class="form-group has-feedback" id="firstName">
					<label class="control-label">First Name</label><input type="text" class="form-control" name="firstName" maxlength="25" tabindex="2" value="${student.firstName}" required> <span id="firstName-ok"
						class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="firstName-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>
				</div>

				<div class="form-group has-feedback" id="lastName">
					<label class="control-label">Last Name</label><input type="text" class="form-control" name="lastName" maxlength="25" tabindex="4" value="${student.lastName}" required> <span id="lastName-ok"
						class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="lastName-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>

				</div>
			</div>
			<div class="col-md-6">
				<div class="form-group has-feedback" id="middleName">
					<label class="control-label">Middle Name</label><input type="text" class="form-control" name="middleName" maxlength="25" tabindex="3" value="${student.middleName}"> <span id="middleName-ok"
						class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="middleName-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>
				</div>
				<div class="form-group has-feedback" id="batch">
					<label class="control-label">Batch</label> <select tabindex=5 name="batch" class="form-control" required>
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
					</select> <span id="batch-ok" class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="batch-error"
						class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6">
				<div class="form-group has-feedback" id="branch">
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
					</select> <span id="branch-ok" class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="branch-error"
						class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 form-title">
				<h4 class="text-left">Contact Details</h4>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6">
				<div class="form-group has-feedback" id="email">
					<label class="control-label">Email ID</label> <input type="email" class="form-control" name="email" maxlength="50" tabindex="6" value="${student.email}" required> <span id="email-ok"
						class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="email-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>

				</div>
				<div class="form-group has-feedback" id="smobile">
					<label class="control-label">Secondary Mobile Number</label> <input type="tel" placeholder="Optional" class="form-control" name="smobile" maxlength="10" tabindex="8" value="${student.smobile}"> <span
						id="smobile-ok" class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="smobile-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>

				</div>
			</div>
			<div class="col-md-6">
				<div class="form-group has-feedback" id="pmobile">
					<label class="control-label">Primary Mobile Number</label> <input type="tel" class="form-control" name="pmobile" maxlength="10" tabindex="7" value="${student.pmobile}" required> <span id="pmobile-ok"
						class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="pmobile-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>

				</div>

			</div>
		</div>
		<div class="row" id="part1btn">
			<div class="col-md-3"></div>
			<div class="col-md-6">
				<button class="form-control btn btn-primary" type="button" tabindex=9>Next</button>
			</div>
			<div class="col-md-3"></div>
		</div>

		<c:if test="{empty showZeroScore}">
			<c:set var="showZeroScore" value="false" />
		</c:if>

		<div id="part2" class="hidden">
			<div class="row">
				<div class="col-md-6">
					<div class="form-group has-feedback" id="sem1">
						<label class="control-label">%age in 1st semester</label> <input class="form-control" type="number" name="score" maxlength="5"
							value="${(student.score[0] != 0 || showZeroScore) && student.score[0] != -1 ? student.score[0] : null}" tabindex=10 required> <span id="sem1-ok"
							class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="sem1-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>
					</div>
					<div class="form-group has-feedback" id="sem2">
						<label class="control-label">%age in 2nd semester</label> <input class="form-control" type="number" name="score" maxlength="5"
							value="${(student.score[1] != 0 || showZeroScore) && student.score[1] != -1 ? student.score[1] : null}" required tabindex=11> <span id="sem2-ok"
							class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="sem2-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>

					</div>
					<div class="form-group has-feedback" id="sem3">
						<label class="control-label">%age in 3rd semester</label> <input class="form-control" type="number" name="score" maxlength="5"
							value="${(student.score[2] != 0 || showZeroScore) && student.score[2] != -1 ? student.score[2] : null}" required tabindex=12> <span id="sem3-ok"
							class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="sem3-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>

					</div>
					<div class="form-group has-feedback" id="sem4">
						<label class="control-label">%age in 4th semester</label> <input class="form-control" type="number" name="score" maxlength="5"
							value="${(student.score[3] != 0 || showZeroScore) && student.score[3] != -1 ? student.score[3] : null}" required tabindex=13> <span id="sem4-ok"
							class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="sem4-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>
					</div>
				</div>
				<div class="col-md-6">
					<div class="form-group has-feedback" id="sem5">
						<label class="control-label">%age in 5th semester</label> <input class="form-control" type="number" name="score" maxlength="5"
							value="${(student.score[4] != 0 || showZeroScore) && student.score[4] != -1 ? student.score[4] : null}" required tabindex=14> <span id="sem5-ok"
							class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="sem5-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>
					</div>
					<div class="form-group has-feedback" id="sem6">
						<label class="control-label">%age in 6th semester</label> <input class="form-control" type="number" name="score" maxlength="5"
							value="${(student.score[5] != 0 || showZeroScore) && student.score[5] != -1 ? student.score[5] : null}" required tabindex=15> <span id="sem6-ok"
							class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="sem6-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>
					</div>
					<div class="form-group has-feedback" id="sem7">
						<label class="control-label">%age in 7th semester</label> <input class="form-control" placeholder="Optional" type="number" name="score" maxlength="5" id="inp-sem7"
							value="${fn:length(student.score) >= 7  && (student.score[6] != 0 || showZeroScore) && student.score[6] != -1 ? student.score[6] : null}" tabindex=16> <span id="sem7-ok"
							class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="sem7-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>
					</div>
					<div class="form-group has-feedback" id="sem8">
						<label class="control-label">%age in 8th semester</label> <input class="form-control" placeholder="Optional" type="number" name="score" maxlength="5" id="inp-sem8"
							value="${fn:length(student.score) == 8  && (student.score[7] != 0 || showZeroScore) && student.score[7] != -1 ? student.score[7] : null}" tabindex=17> <span id="sem8-ok"
							class='glyphicon glyphicon-ok form-control-feedback hidden form-feedback-icon'></span> <span id="sem8-error" class='glyphicon glyphicon-remove form-control-feedback hidden form-feedback-icon'></span>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group has-feedback" id="addtional_qualifications">
						<label class="control-label">Additional Qualifications (Certifications, etc.)</label>
						<textarea class="form-control" name="additional_qualifications" rows="5" cols="30" placeholder="Optional" tabindex=18>${student.additional_qualifications}</textarea>
					</div>
				</div>
				<div class="row">
					<div class="col-md-4"></div>
					<div class="col-md-4">
						<button class="form-control btn btn-primary submitbtn" type="submit" tabindex=19>Submit</button>
					</div>
					<div class="col-md-4"></div>
				</div>
			</div>
		</div>
	</form>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/admin-student.js"></script>