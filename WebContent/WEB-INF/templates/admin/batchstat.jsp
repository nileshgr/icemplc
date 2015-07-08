<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="row">
	<div class="col-md-1"></div>
	<div class="col-md-10">
		<div class="panel panel-default panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title">Batches</h3>
			</div>
			<div class="panel-body">
				<p>Batch-wise statistics about students and placements.</p>
			</div>
			<table class="table table-striped">
				<thead>
					<tr>
						<th class="text-center">Batch</th>
						<th class="text-center">Students</th>
						<th class="text-center">Placed</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="batch" items="${batchstat}">
						<tr>
							<td class="text-center">${batch.batch}</td>
							<td class="text-center">${batch.numOfStudents}</td>
							<td class="text-center">${batch.numOfPlacedStudents}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<div class="col-md-1"></div>
</div>