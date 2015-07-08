<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="studsearch-result" data-size="${!empty studentset ? studentset.size() : 0}">
	<c:choose>
		<c:when test="${studentset.size() > 0}">
			<table class="table table-striped">
				<thead>
					<tr>
						<th class="text-center">ID</th>
						<th class="text-center">Name</th>
						<th class="text-center">Branch</th>
						<th class="text-center">Avg</th>
						<th class="text-center">Status</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="student" items="${studentset}">
						<tr>
							<td class="text-center">${student.ID}</td>
							<td class="text-center">${student.firstName} ${student.middleName} ${student.lastName}</td>
							<td class="text-center">${student.branch.name}</td>
							<td class="text-center">${student.scoreavg}</td>
							<td class="text-center">${student.active ? "Active" : "Inactive"}</td>
							<c:url value="/admin/student/get" var="url">
								<c:param name="studentID" value="${student.ID}" />
							</c:url>
							<td class="text-center"><a href="${url}" data-target="#modal">Show Details</a>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:otherwise>
			<div class="panel-body text-warning">No students found with that pattern. Try changing your pattern.</div>
		</c:otherwise>
	</c:choose>
	
	<c:url value="/admin/student/get/data.csv" var="csvurl">
	<c:param name="name_id" value="${name_id}" />
	<c:param name="minCriteria" value="${minCriteria}" />
	<c:param name="maxCriteria" value="${maxCriteria}" />
	<c:param name="branch" value="${branch}" />
	<c:param name="batch" value="${batch}" />
</c:url>
	
	<script type="text/javascript">	
	window.studsearch.currentPage = ${currentPage};
	window.studsearch.csvurl = "${csvurl}";
    </script>
	<div class="pull-right">${15 * currentPage} to ${15 * currentPage + 15} of ${15 * totalPages} &nbsp;&nbsp;</div>
</div>

