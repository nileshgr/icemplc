<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="${!empty failedUpdates && failedUpdates.size() > 0}">
	<div class="alert alert-danger text-center">
		<h2>Failed updates</h2>
		<c:forEach var="sID" items="${failedUpdates}">
${sID}<br />
		</c:forEach>
	</div>
</c:if>

<c:if test="${!empty error}">
	<div class="alert alert-danger text-center">${errorMsg}</div>
</c:if>

<form id="student-importcsv-form" action="${pageContext.request.contextPath}/admin/student/importcsv" method="POST" enctype="multipart/form-data">
	<div class="form-group">
		<label class="control-label col-md-1" style="position: relative; top: 6px;">CSV File</label>
		<div class="col-md-2">
			<input type="file" name="file">
		</div>
		<button type="submit" class="btn btn-primary">Upload</button>
	</div>
</form>