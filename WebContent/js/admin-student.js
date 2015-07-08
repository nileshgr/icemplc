function StudentForm() {
    function success() {
	var form_success = $('#form-success');
	form_success.removeClass('hidden');
	$('.form-student').addClass('hidden');
	setTimeout(function() {
	    $('#modal').modal('hide');
	    window.studsearch.search();
	}, 1200);
    }

    function err(xhr) {
	if (xhr.status == 400) {
	    var form_error = $('#form-error');
	    form_error.removeClass('hidden');

	    $('.modal-body').scrollTop($('.modal-header').offset().top);

	    var validity = $.parseJSON(xhr.responseText);

	    for (var i = 0; i < validity.valid.length; i++) {
		var field_id = '#' + validity.valid[i];
		var field = $(field_id);

		field.removeClass('has-error');
		$(field_id + '-error').addClass('hidden');

		field.addClass('has-success');
		$(field_id + '-ok').removeClass('hidden');
	    }

	    for (var i = 0; i < validity.invalid.length; i++) {
		var field_id = '#' + validity.invalid[i];
		var field = $(field_id);
		field.removeClass('has-success');
		$(field_id + '-ok').addClass('hidden');

		field.addClass('has-error');
		$(field_id + '-error').removeClass('hidden');
	    }
	} else {
	    window.modal.hide();
	    window.common.show_error();
	}
    }

    $('.form-student').ajaxForm({
	error : err,
	success : success
    });

    var part1btn = $('#part1btn');
    var part2 = $('#part2');
    part1btn.click(function() {
	part2.removeClass('hidden');
	part1btn.addClass('hidden');
	$('.modal-body').scrollTop(part2.offset().top);
    });
}

$(document).ready(StudentForm);