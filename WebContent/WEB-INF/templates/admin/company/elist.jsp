<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal">&times;</button>
	<h4 class="modal-title">Students eligible for ${company.name}</h4>
</div>

<div class="modal-body">
	<c:choose>
		<c:when test="${studentset.size() > 0}">
			<table class="table table-striped" id="company-elist" data-size="${studentset.size()}">
				<tr>
					<th class="text-center">Name</th>
					<th></th>
				</tr>
				<c:forEach var="student" items="${studentset}">
					<c:url var="place_url" value="/admin/student/place">
						<c:param name="studentID" value="${student.ID}" />
						<c:param name="companyID" value="${company.ID}" />
					</c:url>
					<tr>
						<td class="text-center">${student.firstName} ${student.middleName} ${student.lastName}</td>
						<td class="text-center"><button class="company-student-place btn btn-success" data-placeurl="${place_url}">Mark placed</button></td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
	No students eligible
	</c:otherwise>
	</c:choose>
</div>

<c:url var="elist_url" value="/admin/company/elist">
	<c:param name="companyID" value="${company.ID}" />
	<c:param name="batch" value="${batch}" />
</c:url>

<script type="text/javascript">
    window.elist_url = "${elist_url}";
</script>