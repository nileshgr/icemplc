<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ include file="/WEB-INF/templates/header.jsp"%>

<div class="container pg-body">
	<form id="form-signin" action="${pageContext.request.contextPath}/student/login" method="POST">
		<div class="row">
			<div class="col-md-12">
				<img class="pull-right" style="position: relative; top: -20px;" src="${pageContext.request.contextPath}/images/person-lock.png">
				<h4 class="text-left">Student Login</h4>
			</div>
		</div>
		<c:if test="${!empty login_error}">
			<div class="alert alert-danger">${errorMsg}</div>
		</c:if>
		<div class="row">
			<div class="col-md-12">
				<div class="form-group">
					<label>Email Address</label> <input type="email" class="form-control" required autofocus name="email">
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="form-group">
					<label>Password</label> <input type="password" class="form-control" required name="password">
				</div>
			</div>
		</div>
		<button class="btn btn-primary btn-block" type="submit">Login</button>
	</form>
</div>

<%@ include file="/WEB-INF/templates/footer.jsp"%>