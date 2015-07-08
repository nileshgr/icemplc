<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Indira College Of Engineering and Management :: Placement Cell :: ${empty pageName ? "Home" : pageName}</title>
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet" type="text/css">
<link href="${pageContext.request.contextPath}/css/bootstrap-select.css" rel="stylesheet" type="text/css">
<link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet" type="text/css">
<c:if test="${!empty paramValues.css}">
	<c:forEach var="c" items="${paramValues.css}">
		<link href="${pageContext.request.contextPath}/css/${c}.css" rel="stylesheet" type="text/css">
	</c:forEach>
</c:if>
<script src="${pageContext.request.contextPath}/js/jquery.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/jquery.form.js" type="text/javascript"></script>
</head>
<body>
	<div class="container">
		<img src="${pageContext.request.contextPath}/images/logo.png">
	</div>