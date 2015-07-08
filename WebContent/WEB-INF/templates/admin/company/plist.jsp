<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal">&times;</button>
	<h4 class="modal-title">Students placed under ${company.name}</h4>
</div>

<div class="modal-body">
	<c:choose>
		<c:when test="${studentset.size() > 0}">
			<table class="table table-striped" id="company-plist" data-size="${studentset.size()}">
				<tr>
					<th class="text-center">Name</th>
					<th></th>
				</tr>
				<c:forEach var="student" items="${studentset}">
					<c:url var="deplace_url" value="/admin/student/deplace">
						<c:param name="studentID" value="${student.ID}" />
						<c:param name="companyID" value="${company.ID}" />
					</c:url>
					<tr>
						<td class="text-center">${student.firstName} ${student.middleName} ${student.lastName}</td>
						<td class="text-center"><button class="company-student-deplace btn btn-success" data-deplaceurl="${deplace_url}">Deplace</button></td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
	No students placed
	</c:otherwise>
	</c:choose>
</div>

<c:url var="plist_url" value="/admin/company/plist">
	<c:param name="companyID" value="${company.ID}" />
	<c:param name="batch" value="${batch}" />
</c:url>

<script type="text/javascript">
    window.plist_url = "${plist_url}";
</script>