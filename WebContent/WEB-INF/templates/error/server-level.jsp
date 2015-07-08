<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="pageName" scope="request" value="${requestScope['javax.servlet.error.status_code']}" />
<%@ include file="/WEB-INF/templates/header.jsp"%>
<div class="container pg-body">
	<div class="row">
		<div class="col-md-12">
			<p>			
				<strong>${requestScope['javax.servlet.error.status_code']} ${requestScope['javax.servlet.error.message']}</strong>
			</p>
		</div>
	</div>
</div>
<%@ include file="/WEB-INF/templates/footer.jsp"%>