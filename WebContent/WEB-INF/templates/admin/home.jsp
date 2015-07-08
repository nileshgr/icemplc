<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="css" value="admin" />
</jsp:include>

<div class="container-fluid pg-body">
	<div class="row">
		<div class="col-md-2" id="sidebar">
			<ul class="nav nav-pills nav-stacked tabs" id="mainnav">
				<li><a class="menu-title-link" id="stats-tab-link" href="#stats" data-toggle="tab">Home (Statistics)</a></li>
				<li><a class="null-link menu-title-link">Students</a>
					<ul class="nav nav-pills nav-stacked submenu tabs">
						<li><a href="#student-pending" data-toggle="tab">Pending Activations</a></li>
						<li><a href="#student-list" data-toggle="tab">List</a></li>
						<li><a href="#student-importcsv" data-toggle="tab">Import Results CSV</a></li>
					</ul></li>
				<li><a class="null-link menu-title-link">Companies</a>
					<ul class="nav nav-pills nav-stacked submenu tabs">
						<li><a href="#company-list" data-toggle="tab">List</a></li>
					</ul></li>
			</ul>
		</div>


		<div class="col-md-10" id="main">
			<div class="row hidden" id="server-error">
				<div class="col-md-3"></div>
				<div class="col-md-6">
					<div class="alert alert-danger text-center">An error occurred. Please try again or contact administrator.</div>
				</div>
				<div class="col-md-3"></div>
			</div>
			<div class="row hidden" id="ajaxldr">
				<div class="col-md-3"></div>
				<div class="col-md-6">
					<div class="alert alert-info text-center">
						Loading ... <img src="${pageContext.request.contextPath}/images/ajax-loader.gif">
					</div>
					<div class="col-md-3"></div>
				</div>
			</div>

			<div class="tab-content">
				<div class="tab-pane fade in active" id="stats"></div>
				<div class="tab-pane fade" id="student-pending">
					<div class="row">
						<div class="col-md-1"></div>
						<div class="col-md-10">
							<div class="panel panel-default panel-primary" id="pendingsearch-panel">
								<div class="panel-heading">
									<h3 class="panel-title">Pending Activations</h3>
								</div>
								<div class="panel-body">
									<p>List of students whose activation is pending. Students newly registered or those who updated their data after first activation are shown here.</p>
									<form class="form-horizontal">
										<div class="form-group">
											<div class="col-md-7">
												<input id="pendingsearch" class="form-control" type="text" name="pendingsearch" placeholder="Search by Name or ID">
											</div>
										</div>
									</form>
									<p id="activation-alert-success" class="alert alert-success hidden">Activated</p>
								</div>
							</div>
						</div>
						<div class="col-md-1"></div>
					</div>
				</div>
				<div class="tab-pane fade" id="student-list">
					<div class="row">
						<div class="col-md-1"></div>
						<div class="col-md-10">
							<div class="panel panel-default panel-primary" id="studsearch-panel">
								<div class="panel-heading">
									<p class="panel-title">Student Search</p>
								</div>
								<div class="panel-body">
									<form class="form-horizontal">
										<div class="form-group">
											<div class="col-md-3"></div>
											<div class="col-md-2">
												<label for="studsearch" class="control-label">Student ID or Name</label>
											</div>
											<div class="col-md-3">
												<input id="studsearch" class="form-control" type="text" name="studsearch">
											</div>
											<div class="col-md-1">
												<a title="Add New Student" id='add-student' data-target='#modal' href='${pageContext.request.contextPath}/admin/student/add'><img src="${pageContext.request.contextPath}/images/new.png"></a>
											</div>
											<div class="col-md-3"></div>
										</div>
										<div class="form-group">
											<div class="col-md-3"></div>
											<div class="col-md-2">
												<label class="control-label">Min Criteria</label> <input id="studsearch-mincriteria" class="form-control" type="number" size=3 maxlength=3 name="mincriteria">
											</div>
											<div class="col-md-2">
												<label class="control-label">Max Criteria</label> <input id="studsearch-maxcriteria" class="form-control" type="number" size=3 maxlength=3 name="maxcriteria">
											</div>
											<div class="col-md-5"></div>
										</div>
										<div class="form-group">
											<div class="col-md-3"></div>
											<div class="col-md-2">
												<select class="form-control" id="studsearch-batch">
													<option value="0">Batch (All)</option>
													<c:forEach var="batch" items="${distinct_batch_set}">
														<option value="${batch}">${batch}</option>
													</c:forEach>
												</select>
											</div>
											<div class="col-md-2">
												<select class="form-control" id="studsearch-branch">
													<option value="0">Branch (All)</option>
													<c:forEach var="branch" items="${branchset}">
														<option value="${branch.ID}">${branch.name}</option>
													</c:forEach>
												</select>
											</div>

										</div>
										<div class="form-group">
											<div class="col-md-3"></div>
											<div class="col-md-2">
												<button class="btn btn-primary" type="button" id="studsearch-download">Download CSV</button>
											</div>
										</div>
									</form>
								</div>
								<ul id="studsearch-pager" class="pager">
									<li><a id='studsearch-pager-prev' href="#">Previous</a></li>
									<li><a id='studsearch-pager-next' href="#">Next</a></li>
								</ul>
							</div>
						</div>
						<div class="col-md-1"></div>
					</div>
				</div>
				<div class="tab-pane fade" id="company-list">
					<div class="row">
						<div class="col-md-1"></div>
						<div class="col-md-10">
							<div class="panel panel-default panel-primary" id="companysearch-panel">
								<div class="panel-heading">
									<h3 class="panel-title">Company Search</h3>
								</div>
								<div class="panel-body">
									<form class="form-horizontal">
										<div class="form-group">
											<label for="companysearch" class="col-md-4 control-label">Company Name</label>
											<div class="col-md-6">
												<input id="companysearch" class="form-control" type="text" name="companysearch">
											</div>
											<div class="col-md-2">
												<div class="pull-right">
													<a title="Add New Company" id='add-company' data-target='#modal' href='${pageContext.request.contextPath}/admin/company/add'><img src="${pageContext.request.contextPath}/images/new.png"></a>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-4 control-label">Batch filter (for student list)</label>
											<div class="col-md-2">
												<select id="companysearch-batch" class="form-control">
													<option value="0" selected>All</option>
													<c:forEach var="batch" items="${distinct_batch_set}">
														<option value="${batch}">${batch}</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</form>
								</div>
							</div>
						</div>
						<div class="col-md-1"></div>
					</div>
				</div>
				<div class="tab-pane fade" id="student-importcsv">
					<div class="row">
						<div class="col-md-1"></div>
						<div class="col-md-10">
							<div class="alert alert-success hidden" id="student-importcsv-success"></div>
							<div class="alert alert-danger hidden" id="student-importcsv-error"></div>
							<div class="panel panel-default panel-primary" id="importcsv-panel">
								<div class="panel-heading">
									<h3 class="panel-title">Update students' scores by uploading CSV</h3>
								</div>
								<div class="panel-body">
									<ul>
										<li>File must contain ID field</li>
										<li>To update 7th semester scores, there must be a column "Sem7" (without quotes)</li>
										<li>To update 8th semester scores, there must be a column "Sem8" (without quotes)</li>
										<li>8th semester scores won't be updated if 7th semester scores weren't updated (when uploading only Semester 8 scores)</li>
									</ul>
								</div>
								<form class="form-horizontal" id="student-importcsv-form" action="${pageContext.request.contextPath}/admin/student/importcsv" method="POST" enctype="multipart/form-data">
									<div class="form-group">
										<label class="control-label col-md-2">CSV File (max 10 MB)</label>
										<div class="col-md-3">
											<input type="file" name="file">
										</div>
										<div class="col-md-2">
											<button type="submit" class="btn btn-primary" style="position: relative; left: 4px;">Upload</button>
										</div>
									</div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="modal fade" id="modal" tabindex='-1'>
			<div class="modal-dialog">
				<div class="modal-content"></div>
			</div>
		</div>

	</div>
</div>

<c:url var="batchstat_url" value="/admin/batchstat">
	<c:param name="batchstat" value="1" />
</c:url>

<script type="text/javascript">
    window.studsearch_url = "${pageContext.request.contextPath}/admin/student/search";
    window.pendingsearch_url = "${pageContext.request.contextPath}/admin/student/pendingsearch";
    window.companysearch_url = "${pageContext.request.contextPath}/admin/company/search";
    window.batchstat_url = "${batchstat_url}";
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/admin.js"></script>
<%@ include file="/WEB-INF/templates/footer.jsp"%>