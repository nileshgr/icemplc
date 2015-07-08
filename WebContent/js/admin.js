// jQuery ajax default parameters

$.ajaxSetup({
    cache : false
});

// Variable used for checking only 1 ajax request is active at a time

window.loading = false;

$(window).resize(function() {
    window.main.css('margin-left', window.main.width() * .08 + 'px');
});

function onReady() {
    var tab_parent_jq_cache = {};
    var tabs = $('.tabs');
    var sidebar = $('#sidebar');
    var main = $('#main');
    window.main = main;
    $('.null-link').click(function(event) {
	event.preventDefault();
    });

    window.html_body = $('html, body');

    window.modal = new Modal;
    window.ajax = new Ajax;
    window.common = new Common;

    window.studsearch = new StudentSearch(window.studsearch_url);
    delete window.studsearch_url;

    window.pendingsearch = new PendingSearch(window.pendingsearch_url);
    delete window.pendingsearch_url;

    window.companysearch = new CompanySearch(window.companysearch_url);    

    $('#add-student').click(window.modal.loadAndShow);
    $('#add-company').click(window.modal.loadAndShow);

    window.importcsv = new ImportCSV();
    window.placement = new Placement();
    window.batchstat = new BatchStats(window.batchstat_url);
    delete window.batchstat_url;

    tabs.on('show.bs.tab', function(event) {
	location.hash = event.target.hash;

	if (event.relatedTarget
		&& event.relatedTarget.hash == "#student-importcsv") {
	    window.importcsv.div_error.html('');
	    window.importcsv.div_error.addClass('hidden');
	}

	switch (event.target.hash) {
	case "#stats":
	    window.batchstat.load();
	    break;
	case "#student-pending":
	    window.pendingsearch.search({
		dontscroll : true
	    });
	    break;
	case "#student-list":
	    window.studsearch.search({
		dontscroll : true
	    });
	    break;
	case "#company-list":
	    window.companysearch.search({
		dontscroll : true
	    });
	    break;
	}

	tabs.find('a').each(function(index, element) {
	    if (element.hash != event.target.hash) {
		if (!tab_parent_jq_cache[element])
		    tab_parent_jq_cache[element] = $(element).parent();
		tab_parent_jq_cache[element].removeClass('active');
	    }
	});
    });

    tabs.on('shown.bs.tab', function() {
	setTimeout(function() {
	    var sidebar_h = main.outerHeight(true);
	    sidebar_h = sidebar_h < 360 ? 360 : sidebar_h;
	    sidebar.css('height', sidebar_h + 'px');
	}, 75);
	main.css('margin-left', main.width() * .08 + 'px');
    });

    if (location.hash != "") {
	$('a[href=' + location.hash + ']').tab('show');
    } else {
	$('a[href=#stats]').tab('show');
    }
}

$(document).ready(onReady);

function BatchStats(url) {
    var batchdiv = $('#stats');
    var batchstat_url = url;

    function load() {
	if (window.loading)
	    return;

	function success(data) {
	    window.ajax.complete();
	    batchdiv.html(data);
	}

	$.get(batchstat_url, success).fail(window.ajax.fail);
	window.ajax.loading();
    }

    this.load = load;
}

function Modal() {
    var jq = {
	'modal' : $('.modal'),
	'content' : $('.modal-content')
    };

    function loadAndShow(event) {
	event.preventDefault();

	if (window.loading)
	    return;

	window.ajax.loading();

	function ajaxresponse(response, status, xhr) {
	    window.ajax.complete();
	    if (status == "error")
		window.ajax.fail();
	    else {
		jq.modal.modal('show');
		$('#edit').one('click', window.studsearch.edit);
		$('#activate').one('click', window.pendingsearch.activate);
		$('.company-student-place')
			.one('click', window.placement.place);
		$('.company-student-deplace').one('click',
			window.placement.deplace);
	    }
	}

	jq.content.load(this.href, ajaxresponse);
    }

    function resize() {
	var window_height = $(window).height();
	var max_modal_height = 0.85 * window_height + 'px';
	$('.modal .modal-body').css('max-height', max_modal_height);
    }

    function show() {
	jq.modal.modal('show');
    }

    function hide() {
	jq.modal.modal('hide');
    }

    function loadOnHidden(target, callback) {
	jq.modal.one('hidden.bs.modal', function() {
	    jq.content.load(target, callback);
	});
    }

    this.show = show;
    this.loadAndShow = loadAndShow;
    this.hide = hide;
    this.loadOnHidden = loadOnHidden;

    Object.defineProperty(this, "content", {
	set : function(value) {
	    jq.content.html(value);
	}
    });

    jq.modal.on('show.bs.modal', resize);
}

function CompanySearch(url) {
    this.searchUrl = url;

    var jq = {
	search : $('#companysearch'),
	result : $('#companysearch-result'),
	panel : $('#companysearch-panel'),
	batchselect : $('#companysearch-batch')
    };

    function search(event) {
	if (window.loading)
	    return;

	if (!event)
	    event = {};

	window.ajax.loading();

	function success(data) {
	    window.ajax.complete();

	    jq.result.remove();
	    jq.panel.append(data);
	    jq.result = $('#companysearch-result');

	    if (!event.dontscroll)
		window.html_body.scrollTop(jq.result.offset().top);

	    jq.result.find('a[data-target=#modal]').click(
		    window.modal.loadAndShow);
	}

	var search_key = {
	    name : jq.search.val(),
	    batch : jq.batchselect.val()
	};

	var ajax = $.get(window.companysearch.searchUrl, search_key, success);
	ajax.fail(window.ajax.fail);
    }

    this.search = search;

    function focusout() {
	if (jq.result.data('size') == 0) {
	    jq.result.remove();
	    jq.search.val('');
	    search();
	}
    }

    jq.search.on({
	keyup : search,
	focusout : focusout
    });

    jq.batchselect.change(search);
}

function Placement() {
    function _eplist_err() {
	window.modal.hide();
	window.ajax.fail();
    }

    function place() {
	var url = $(this).data('placeurl');

	function success() {
	    $.get(window.elist_url, function(data) {
		window.ajax.complete();
		window.modal.content = data;
	    }).fail(_eplist_err);
	}

	$.get(url, success).fail(_eplist_err);
	window.ajax.loading();
    }

    function deplace() {
	var url = $(this).data('deplaceurl');

	function success() {
	    $.get(window.plist_url, function(data) {
		window.ajax.complete();
		window.modal.content = data;
	    }).fail(_eplist_err);
	}

	$.get(url, success).fail(_eplist_err);
	window.ajax.loading();
    }

    this.place = place;
    this.deplace = deplace;
}

function StudentSearch(url) {
    this.currentPage = 0;
    this.csvurl = "";
    this.searchUrl = url;

    var jq = {
	search : $('#studsearch'),
	mincriteria : $('#studsearch-mincriteria'),
	maxcriteria : $('#studsearch-maxcriteria'),
	batch : $('#studsearch-batch'),
	branch : $('#studsearch-branch'),
	result : $('#studsearch-result'),
	pager : $('#studsearch-pager'),
	pager_prev : $('#studsearch-pager-prev'),
	pager_next : $('#studsearch-pager-next'),
	download : $('#studsearch-download')
    };

    function search(event) {
	if (window.loading)
	    return;

	if (!event)
	    event = {};

	window.ajax.loading();

	function success(data) {
	    window.ajax.complete();
	    jq.result.remove();
	    jq.pager.before(data);
	    jq.result = $('#studsearch-result');

	    if (!event.dontscroll)
		window.html_body.scrollTop(jq.result.offset().top);

	    jq.result.find('a[data-target=#modal]').click(
		    window.modal.loadAndShow);
	}

	var search_key = {
	    name_id : jq.search.val(),
	    branch : jq.branch.val(),
	    batch : jq.batch.val(),
	    minCriteria : jq.mincriteria.val(),
	    maxCriteria : jq.maxcriteria.val(),
	    page : event.page || 1
	};

	var ajax = $.get(window.studsearch.searchUrl, search_key, success);
	ajax.fail(window.ajax.fail);
    }

    function focusout() {
	if (jq.result.data('size') == 0) {
	    jq.result.remove();
	    jq.search.val('');
	    search();
	}
    }

    function edit() {
	var target = $(this).data('url');

	window.modal.hide();
	window.ajax.loading();

	function success(response, status, xhr) {
	    window.ajax.complete();

	    if (status == "error")
		window.ajax.fail();
	    else
		window.modal.show();
	}

	window.modal.loadOnHidden(target, success);
    }

    this.search = search;
    this.edit = edit;

    jq.batch.change(search);
    jq.branch.change(search);

    jq.search.on({
	keyup : search,
	focusout : focusout
    });

    jq.mincriteria.on({
	keyup : search,
	focusout : focusout
    });

    jq.maxcriteria.on({
	keyup : search,
	focusout : focusout
    });

    jq.download.click(function(event) {
	event.preventDefault();
	window.location = window.studsearch.csvurl;
    });

    jq.pager_prev.click(function(event) {
	event.preventDefault();
	window.studsearch.search({
	    page : --window.studsearch.currentPage
	});
    });

    jq.pager_next.click(function(event) {
	event.preventDefault();
	window.studsearch.search({
	    page : ++window.studsearch.currentPage
	});
    });
}

function PendingSearch(url) {
    this.searchUrl = url;

    var jq = {
	search : $('#pendingsearch'),
	result : $('#pendingsearch-result'),
	panel : $('#pendingsearch-panel')
    };

    function search(event) {
	if (window.loading)
	    return;

	if (!event)
	    event = {};

	window.ajax.loading();

	function success(data) {
	    window.ajax.complete();
	    jq.result.remove();
	    jq.panel.append(data);

	    jq.result = $('#pendingsearch-result');
	    jq.result.find('a[data-target=#modal]').click(
		    window.modal.loadAndShow);

	    if (!event.dontscroll)
		window.html_body.scrollTop(jq.result.offset().top);
	}

	var search_key = {
	    pattern : jq.search.val()
	};

	var ajax = $.get(window.pendingsearch.searchUrl, search_key, success);
	ajax.fail(window.ajax.fail);
    }

    function activate() {
	var target = $(this).data('url');

	window.modal.hide();
	window.ajax.loading();

	function ajaxresponse(response, status, xhr) {
	    window.ajax.complete();

	    if (status == "error")
		window.ajax.fail();
	    else {
		window.modal.content = '<div class="modal-body">'
			+ '<div class="row">'
			+ '<div class="alert alert-success">Student Activated</div>'
			+ '</div>' + '</div>';
		window.modal.show();
		setTimeout(function() {
		    window.modal.hide();
		}, 1450);
		search();
	    }
	}

	window.modal.loadOnHidden(target, ajaxresponse);
    }

    function focusout() {
	if (jq.result.data('size') == 0) {
	    jq.result.remove();
	    jq.search.val('');
	    search();
	}
    }

    this.search = search;
    this.activate = activate;

    jq.search.on({
	keyup : search,
	focusout : focusout
    });
}

function Common() {
    var jqerror = $('#server-error');

    function show_error() {
	jqerror.removeClass('hidden');
	setTimeout(function() {
	    jqerror.addClass('hidden');
	}, 2000);
    }

    this.show_error = show_error;
}

function Ajax() {
    Ajax.loader = Ajax.loader || $('#ajaxldr');

    this.fail = function() {
	this.complete();
	window.common.show_error();
    };

    this.loading = function() {
	Ajax.loader.removeClass('hidden');
	window.loading = true;
    };

    this.complete = function() {
	Ajax.loader.addClass('hidden');
	window.loading = false;
    };
}

function ImportCSV() {
    var div_error = $('#student-importcsv-error');
    var div_success = $('#student-importcsv-success');
    this.div_error = div_error;

    function err(xhr) {
	if (xhr.status == 400) {
	    var json = $.parseJSON(xhr.responseText);
	    div_error.html(json.error);
	    div_error.removeClass('hidden');
	    setTimeout(function() {
		div_error.addClass('hidden');
	    }, 2500);
	} else
	    window.common.show_error();
    }

    function success(response) {
	if (response.failedUpdates) {
	    var finalStr = 'Failed Updates: <ul>';
	    for (u in response.failedUpdates)
		finalStr = finalStr + "<li>" + u + "</li>";
	    finalStr = finalStr + "</ul>";
	    div_error.html(finalStr);
	    div_error.removeClass('hidden');
	    div_success.html('Updated some records');
	} else
	    div_success.html('Records updated');
	div_success.removeClass('hidden');
	setTimeout(function() {
	    div_success.addClass('hidden');
	}, 1500);
    }

    $('#student-importcsv-form').ajaxForm({
	success : success,
	error : err,
	resetForm : true
    });
}