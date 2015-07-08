<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:choose>
	<c:when test="${companyset.size() > 0 }">
		<table class="table table-striped" id="companysearch-result" data-size="${companyset.size()}">
			<thead>
				<tr>
					<th class="text-center">Name</th>
					<th class="text-center">Branch</th>
					<th class="text-center">Criteria</th>
					<th class="text-center"></th>
					<th class="text-center">Eligible Students</th>
					<th class="text-center">Placed Students</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="company" items="${companyset}">
					<tr>
						<td class="text-center">${company.name}</td>
						<td class="text-center">${company.branch.name}</td>
						<td class="text-center">${company.criteria}%</td>

						<c:url var="modify_url" value="/admin/company/modify">
							<c:param name="companyID" value="${company.ID}" />
						</c:url>


						<c:url var="elist_url" value="/admin/company/elist">
							<c:param name="companyID" value="${company.ID}" />
							<c:param name="batch" value="${batch}" />
						</c:url>

						<c:url var="elist_dl_url" value="/admin/company/elist_dl">
							<c:param name="companyID" value="${company.ID}" />
							<c:param name="batch" value="${batch}" />
						</c:url>

						<c:url var="plist_url" value="/admin/company/plist">
							<c:param name="companyID" value="${company.ID}" />
							<c:param name="batch" value="${batch}" />
						</c:url>

						<c:url var="plist_dl_url" value="/admin/company/plist_dl">
							<c:param name="companyID" value="${company.ID}" />
							<c:param name="batch" value="${batch}" />
						</c:url>

						<td class="text-center"><a href="${modify_url}" data-target="#modal"><span class="glyphicon glyphicon-edit"></span></a></td>
						<td class="text-center"><a href="${elist_url}" data-target="#modal"> <span class="glyphicon glyphicon-eye-open"></span></a>&nbsp; &nbsp;<a href="${elist_dl_url}"><span
								class="glyphicon glyphicon-download-alt"></span></a></td>
						<td class="text-center"><a href="${plist_url}" data-target="#modal"><span class="glyphicon glyphicon-eye-open"></span></a>&nbsp; &nbsp;<a href="${plist_dl_url}"><span
								class="glyphicon glyphicon-download-alt"></span></a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="panel-body text-warning" id="companysearch-result" data-size="0">No students found with that pattern. Try changing your pattern.</div>
	</c:otherwise>
</c:choose>