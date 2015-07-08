<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test="${studentset.size() > 0}">
		<table class="table table-striped" id="pendingsearch-result" data-size="${studentset.size()}">
			<thead>
				<tr>
					<th class="text-center">ID</th>
					<th class="text-center">Name</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="student" items="${studentset}">
					<tr>
						<td class="text-center">${student.ID}</td>
						<td class="text-center">${student.firstName} ${student.middleName} ${student.lastName}</td>										
						<c:url value="/admin/student/get" var="url">
							<c:param name="studentID" value="${student.ID}" />
							<c:param name="for_activation" value="1" />
						</c:url>
						<td class="text-center"><a href="${url}" data-target="#modal">Activate</a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="panel-body text-warning" id="pendingsearch-result" data-size="0">No students pending activation found with that pattern. Try changing your pattern or try normal search instead.</div>
	</c:otherwise>
</c:choose>