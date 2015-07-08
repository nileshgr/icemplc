<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/templates/header.jsp"%>

<div class="container pg-body">
	<div class="row">
		<div class="col-md-12">
			<div class="text-left">
				<p>
					Dear <em>${student.firstName} ${student.lastName}</em>,
				</p>
				<p>
					Thank you for registration. You need to submit documents at Placement Cell for verification. You will get a notification on <em>${student.email}</em> upon successful verification and account activation.
				</p>
				<p>
					If you need to make changes to the submitted data before your account is activated, please contact the Placement Cell, do <strong>not</strong> resubmit the form!
				</p>				
			</div>
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/templates/footer.jsp"%>