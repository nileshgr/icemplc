<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal">&times;</button>
	<h4 class="modal-title">Details of student #${student.ID}</h4>
</div>

<div class="modal-body">
	<table class="table table-striped">
		<tr>
			<th class="text-center">First Name</th>
			<th class="text-center">Middle Name</th>
			<th class="text-center">Last Name</th>
		</tr>
		<tr>
			<td class="text-center">${student.firstName}</td>
			<td class="text-center">${student.middleName}</td>
			<td class="text-center">${student.lastName}</td>
		</tr>
		<tr>
			<th class="text-center">Email</th>
			<th class="text-center">Primary Mobile</th>
			<th class="text-center">Secondary Mobile</th>
		</tr>
		<tr>
			<td class="text-center">${student.email}</td>
			<td class="text-center">${student.pmobile}</td>
			<td class="text-center">${student.smobile}</td>
		</tr>
		<tr>
			<th class="text-center">Batch</th>
			<th></th>
			<th class="text-center">Branch</th>			
		</tr>
		<tr>
			<td class="text-center">${student.batch}</td>			
			<td></td>
			<td class="text-center">${student.branch.name}</td>
		</tr>
		<tr>
			<th></th>
			<th class="text-center">Semester-wise score</th>
			<th></th>
		</tr>
		<tr>
			<th class="text-center">Semester 1</th>
			<th class="text-center">Semester 2</th>
			<th class="text-center">Semester 3</th>
		</tr>
		<tr>
			<td class="text-center">${student.score[0]}%</td>
			<td class="text-center">${student.score[1]}%</td>
			<td class="text-center">${student.score[2]}%</td>
		</tr>
		<tr>
			<th class="text-center">Semester 4</th>
			<th class="text-center">Semester 5</th>
			<th class="text-center">Semester 6</th>
		</tr>
		<tr>
			<td class="text-center">${student.score[3]}%</td>
			<td class="text-center">${student.score[4]}%</td>
			<td class="text-center">${student.score[5]}%</td>
		</tr>
		<c:choose>
			<c:when test="${fn:length(student.score) == 7}">
				<tr>
					<th></th>
					<th class="text-center">Semester 7</th>
					<th></th>
				</tr>
				<tr>
					<td></td>
					<td class="text-center">${student.score[6]}%</td>
					<td></td>
				</tr>
			</c:when>
			<c:otherwise>
				<c:if test="${fn:length(student.score) == 8}">
					<tr>
						<th class="text-center">Semester 7</th>
						<th></th>
						<th class="text-center">Semester 8</th>
					</tr>
					<tr>
						<td class="text-center">${student.score[6]}%</td>
						<td></td>
						<td class="text-center">${student.score[7]}%</td>
					</tr>
				</c:if>
			</c:otherwise>
		</c:choose>
	</table>
</div>
<div class="row">
	<div class="col-md-2"></div>
	<div class="col-md-8">
		<h5 class="text-center">
			<strong>Additional Qualifications</strong>
		</h5>
		<textarea rows="5" style="width: 100%;" disabled>${student.additional_qualifications}</textarea>
	</div>
	<div class="col-md-2"></div>
</div>
<div class="modal-footer">
	<br>
	<div class="row">
		<div class="col-md-2"></div>
		<c:choose>
			<c:when test='${!empty param.for_activation && param.for_activation == "1"}'>
				<c:url var="url" value="/admin/student/activate">
					<c:param name="studentID" value="${student.ID}" />
				</c:url>
				<button id="activate" data-url="${url}" data-target='#modal' class="col-md-3 btn btn-success" type="button">Activate</button>
			</c:when>
			<c:otherwise>
				<c:url var="url" value="/admin/student/modify">
					<c:param name="studentID" value="${student.ID}" />
				</c:url>

				<button id="edit" data-url="${url}" data-target='#modal' class="col-md-3 btn btn-success" type="button">Edit</button>
			</c:otherwise>
		</c:choose>
		<div class="col-md-2"></div>
		<button data-dismiss="modal" class="col-md-3 btn btn-danger" type="button">Close</button>
		<div class="col-md-2"></div>
	</div>
</div>