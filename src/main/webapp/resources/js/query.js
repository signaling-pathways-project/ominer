var pathArray = window.location.href.split( '/' );
var url = pathArray[0] + '//' + pathArray[2];
var doiFConsensome;

_.templateSettings = {
  evaluate : /\{\[([\s\S]+?)\]\}/g,
  interpolate : /\{\{([\s\S]+?)\}\}/g,
  variable:"rc"
};

$(document).ready(function(){
	
	// Block the form until we get data from the backend to build the query
	blockUI("#tm-search-form");
	$('input', "#tm-search-form").on('click, keydown', function(){
		removeError($(this));
	});

	$('select', "#tm-search-form").on('change', function(){
		removeError($(this));
	});

	var searchBy = 'any';
	var control = $("#fileUpload");


	// Gene type
	$('#gene-type').on('change', function() {
		searchBy = $(this).val(); // Possible values : any|consensome|gene|geneList|goTerm
		if( searchBy == 'consensome'){
			changeGenesOfInterest( true );
			$('#species').val('Human');
//			$('#omics-category option[value="any"]').attr({'disabled':'disabled'});
			checkForpathwayRequest();
		} else if( searchBy == 'gene'){
			changeGenesOfInterest();
			$('#gene-autocomplete-holder').slideDown();
			$('#gene-autocomplete-holder').find('input').focus();
//			$('#omics-category').val('any');

			$('#omics-category').change();
		} else if(searchBy == 'geneList'){
			changeGenesOfInterest();
			$('#geneList-holder').slideDown();
//			$('#omics-category option[value="any"]').attr({'disabled':'disabled'});
			$("#submit-query").addClass('disabled');

			$('#omics-category').val('transcriptomics');
		} else if( searchBy == 'goTerm' ){
			changeGenesOfInterest();
			$('#goTerm-autocomplete-holder').slideDown();
			$('#goTerm-autocomplete-holder').find('input').focus();
//			$('#omics-category option[value="any"]').attr({'disabled':'disabled'});
			$("#submit-query").addClass('disabled');

			$('#omics-category').val('transcriptomics');
		} else {
			$('#regulation').slideDown();
			$(".autocomplete-field").hide();
			$('#tissue-cell-line, #pathway-modules, #species, #tissue-category-target, #pathway-target').val('all');
//			$('#omics-category').val('any');
			$('#pathway-modules, #significance, #tissue-cell-line, #species, #significance').attr({'disabled':'disabled'});
			$('#submit-query').addClass('disabled');

			$('#omics-category').val('transcriptomics');
		}

		// Reset the gene list file upload
		$('#geneList-holder').removeClass('has-success has-error');
		control.replaceWith( control = control.clone( true ) );

	});

	// omics category
	$('#omics-category').on('change', function(){
		if( $(this).val() == 'cistromics' ){
			$('#regulation').hide();
		} else {
			$('#regulation').slideDown();
		}
	});

	// pathway selected
	$('#pathway-group').on('change', 'div select',function(){
		if( $('#gene-type').val() == 'geneList' || $('#gene-type').val() == 'goTerm')
			$('#submit-query').removeClass('disabled');
	});
	
	$.when( lookUpTissues(), lookUpPathways() ).done( function(){
    	$('#tm-search-form').unblock();
    	 processDirectUrlRequest(); // We have to wain until the tissue and pathways categories are loaded
    });

    // Override the styling for the list to distinguish Exact and Other matches
	$.widget( "custom.catcomplete", $.ui.autocomplete, {
		_create: function() {
			this._super();
			this.widget().menu( "option", "items", "> :not(.ui-autocomplete-category)" );
		},
		_renderMenu: function( ul, items ) {
			var that = this;
			$.each( items, function( index, item ) {
				var li;
				if ( (item.label == '=== Exact Matches ===') || (item.label == '=== Other Matches ===') || (item.label == 'No matches found') ) {
					ul.append( "<li class='ui-autocomplete-category'>" + item.label + "</li>" );
				} else {
					li = that._renderItemData( ul, item );
				}
			});
		},
		_renderItem: function (ul, item) {
			var newText = String(item.value).replace(
			        new RegExp(this.term, "gi"),
			        "<strong>$&</strong>");

			return $("<li></li>")
			    .data("item.autocomplete", item)
			    .append(newText)
			    .appendTo(ul);
		}
	});

	createAutocomplete({id:"gene-autocomplete", multiple:true, url:"/rest/omics/auto/gene"}, function(){});
    createAutocomplete({id:"goTerm-autocomplete", multiple:true, singleVals:true, url:"/rest/omics/auto/goterm"}, function(){});
    createAutocomplete({id:"tissue-autocomplete", multiple:true, url:"/rest/omics/auto/tissues"}, function(){});

    $('#fileUpload').on('change', function () { 
		validateGeneList(function(geneList){
			$('#file-upload-target').val(geneList);
		}); 
	});

	$('#tm-search-form').submit(function( e ) {
		// $('#searchResults').hide();
		e.preventDefault(); // We want to get data only via ajax request
		var validatedForm = validateForm();
		if( validatedForm ){
			if( $('#omics-category').val() == 'cistromics' ){
				$('#tm-tab-header').hide();
				$('#cistromics-tab-header').show();
				$('#cistromics-tab-header a').trigger('click');
				validatedForm.query.omicsCategory = 'cistromics';
				getResults(validatedForm.queryParameters, 'cistromics');
			} else if( $('#omics-category').val() == 'transcriptomics' ) {
				$('#cistromics-tab-header').hide();
				$('#tm-tab-header').show();
				$('#tm-tab-header a').trigger('click');
				validatedForm.query.omicsCategory = 'transcriptomics';
				getResults(validatedForm.queryParameters, 'tm');
			} else { // Both
				$('#cistromics-tab-header').show();
				$('#tm-tab-header').show();
				$('#tm-tab-header a').trigger('click');
				getResults(validatedForm.queryParameters, 'both');
			}
		}
	});

	
	
	$('#reset-search').click(function(){
		queryParameters = {};
		$('#fold-change-min').val(2);
		$('#fold-change-max').val(30);
		$('#significance').val('0.05').change();
		$('#tm-search-form')[0].reset();
		$('div[id^="consensome-"]').hide();

		$('#gene-type').trigger('change');
		$('#tissue-cell-line-group .tissue-cell-line-sub').hide();
		$('#searchResults').hide();
		$('.pathway-sub').hide();
		$('.autocomplete-field').hide();
		$('#omics-category').attr({'disabled':'disabled'});
		$('#pathway-modules').attr({'disabled':'disabled'});
		
		$('#species').attr({'disabled':'disabled'});
        $('#species option[value="all"]').removeAttr('disabled');
        $('#species').change();
		
		$('#tissue-cell-line').attr({'disabled':'disabled'});
		
		$('#tissue-cell-line').val('');
		$('#physiological-systems-target').val('all');
		$('#physiological-systems-target').change();

		$('#pathway-target').attr('ptype','category');
		$('#regulation').hide();
		$("#submit-query").addClass('disabled');

		control.replaceWith( control = control.clone( true ) );
		History.pushState({state:'form'}, null, '/ominer/query.jsf');
	});

	$('#cistromics-plot-option').on('change', function() {
		updateCistromicsScatterplot($(this).val());
	});

	$('#plot-option').on('change', function() {
		$( "#up-down-checkboxes input" ).prop('checked', true);
		updateTMScatterplot($(this).val());
	});

	$( "#up-down-checkboxes input" ).prop('checked', true);
	
	let up = true;
	let down = true;
	
	$( "#up-down-checkboxes input" ).on( "change", function() {
		
		if( $(this).val() == 'up' ){ if( this.checked ){ up = true } else { up = false } } 

		if( $(this).val() == 'down' ){ if( this.checked ){ down = true } else { down = false } }

		if( down && !up ){
			updateTMScatterplot($('#plot-option').val(), 'down');
		} else if( up && !down ) {
			updateTMScatterplot($('#plot-option').val(), 'up');
		} else {
			updateTMScatterplot($('#plot-option').val());
		}
	});

	// Hide graph tooltip
	$(document).click(function() {
		$('#graph-more-info').fadeOut();    
	});

	var checkForpathwayRequest = function(){
		if( searchBy == 'consensome' ){
			$("#submit-query").addClass('disabled');
			$('#regulation').slideUp();
			if( $('#species').val() != 'all'){
				if( $('#tissue-cell-line').val() != 'all') {
					   $('#physiological-systems-target').val($('#tissue-cell-line').val());
					   $('#physiological-systems-target').attr('displaytext', $(this).find(':selected').text());
					   $('#physiological-systems-target').change();
			
					$("#submit-query").removeClass('disabled');
					consensomSummary(true);
				}
				else {
					$('#physiological-systems-target').val($('#tissue-cell-line').val());
					
					if ($('#pathway-target').val() && $('#pathway-target').attr('ptype') == 'family') {
						$("#submit-query").removeClass('disabled');
						consensomSummary(true);
					}
				}
			}
		} else {
//			$("#submit-query").removeClass('disabled');
		}
	};

	$('#tissue-cell-line, #species, #pathway-modules').on('change', function(e){
		if (e.originalEvent) { // Only execute this part if a user manually change the dropdowns.
			checkForpathwayRequest();
		}
	});
	
	$('#pathway-group').on('change', '.pathway-sub select', function(e) {
		var curPathway = $(this).val();

		$('#pathway-target').val(curPathway);
		$('#pathway-target').attr('ptype', $(this).find(':selected').attr('ptype'));

		if (e.originalEvent) { // Only execute this part if a user manually change the dropdowns.
			checkForpathwayRequest();
		}
	});
	

	$('#tissue-cell-line-group').on('change', '.tissue-cell-line-sub', function(e) {
		if (e.originalEvent) { // Only execute this part if a user manually change the dropdowns.
			checkForpathwayRequest();
		}
	});

});


var getGeneId = function(type){
	if (type == 'goTerm')
		return true;
	var geneLookup = {geneId:null};
	var geneSymbol = getParameterByName('gene');
	if(geneSymbol){
		return $.ajax(url+'/rest/omics/auto/gene?symbol='+geneSymbol).done(function(res){
			var foundGene = false;
			if(res && res.exactList) {
				_.each(res.exactList, function(item){
					if( item.officialSymbol === geneSymbol ){
						$('#gene-autocomplete').val(item.officialSymbol);
						$('#gene-autocomplete-target').val(item.officialSymbol);
						foundGene = true;
					}
				});
				if( !foundGene ) {
					_.each(res.exactList, function(item){
						if( item.synonymTerm === geneSymbol ){
							$('#gene-autocomplete').val(item.officialSymbol);
							$('#gene-autocomplete-target').val(item.officialSymbol);
							foundGene = true;
						}
					});
				}
			}
			if( !foundGene ) {
				$('#gene-autocomplete-holder').addClass('has-error');
			}
		});
	}
	else {
		return geneLookup;
	}
};

var processDirectUrlRequest = function() {
	var transferGene = '';
	if( getParameterByName('doi') ){
		window.location.href = url+'/doi/?doi='+getParameterByName('doi');
	}
	if(getParameterByName('geneSearchType')){
		if (getParameterByName('geneSearchType') == 'goTerm')
			transferGene = getParameterByName('geneSearchType');

		$('#gene-type').val(getParameterByName('geneSearchType'));

		if (getParameterByName('geneSearchType').toLowerCase() == 'consensome') {
			$('#gene-type').val('consensome');
		}
		$('#gene-type').trigger('change');

		if(getParameterByName('gene')){
			$('#goTerm-autocomplete-target').val(getParameterByName('gene'));
			$('#goTerm-autocomplete').val(getParameterByName('gene'));
		}
		if( getParameterByName('foldChangeMin') ){
			$('#fold-change-min').val(getParameterByName('foldChangeMin'));
		}
		if( getParameterByName('foldChangeMax') ){
			$('#fold-change-max').val(getParameterByName('foldChangeMax'));
		}
		if(getParameterByName('significance')){
			$('#significance').val(getParameterByName('significance'));
		}
		if(getParameterByName('species')){
			$('#species').val(getParameterByName('species'));
			$('#species').trigger('change');
		}
		
		if(getParameterByName('ps') && getParameterByName('ps') != 'null'){
			$('#tissue-cell-line').val(getParameterByName('ps'));
			$('#tissue-cell-line').trigger('change');
		}
		
		if(getParameterByName('organ') && getParameterByName('organ') != 'null'){
			$('#tissue-cell-line-'+getParameterByName('ps')).val(getParameterByName('organ'));
			$('#tissue-cell-line-'+getParameterByName('ps')).trigger('change');
			$('#organ-target').val(getParameterByName('organ'));
			$('#organ-target').trigger('change');
		}
		
		if( getParameterByName('omicsCategory') ){
			if (getParameterByName('omicsCategory').toLowerCase() === 'tm')
				$('#omics-category').val('transcriptomics');
			if (getParameterByName('omicsCategory').toLowerCase() === 'transcriptomics')
				$('#omics-category').val('transcriptomics');
			else if (getParameterByName('omicsCategory').toLowerCase() == 'cistromics')
				$('#omics-category').val('cistromics');
//			else
//				$('#omics-category').val('any');
			
			$('#omics-category').trigger('change');
		}
		
		if( getParameterByName('signalingPathway') ) {
			$('#pathway-target').val(getParameterByName('signalingPathway'));
			$('#pathway-target').trigger('change');
		}
		
		if( getParameterByName('pathwayType') ) {
			$('#pathway-target').attr(getParameterByName('pathwayType'));
		}
		if (getParameterByName('geneSearchType').toLowerCase() == 'consensome') {

			var dataParams = {};
			var query = {};
			if( $('#physiological-systems-target').val() !== 'all' ) {
				dataParams.ps = $('#physiological-systems-target').val();
				query.ps = dataParams.ps;
				if( $('#organ-target').val() !== 'all' ) {
					dataParams.organ = $('#organ-target').val();
					query.organ = $('#organ-target').val();
				}
			}

			if( $('#pathway-modules').val() !== 'all' ) {
				dataParams.signalingPathway = $('#pathway-target').val();
				dataParams.pathwayType = $('#pathway-target').attr('ptype');

				query.signalingPathway = dataParams.signalingPathway;
				query.pathwayType = dataParams.pathwayType;
			}

			dataParams.species = $('#species').val();
		//dataParams.species = 'test';
			query.species = $('#species').val();
			
			var detailUrl = '';
			var downloadUrl = '';
			if ($('#omics-category').val() == 'transcriptomics') {
				detailUrl = '/rest/omics/transcriptomics/v1/consensome/summary';
				dataParams.omicsCategory = 'transcriptomics'
			}
			else {
				detailUrl = '/rest/omics/cistromics/v1/consensome/summary';
				dataParams.omicsCategory = 'cistromics'
			}
	
			$.get(url + detailUrl, dataParams , function (data) {
				//apollo 10.30.2018 missing numberDatapoints
				if( data && data.numberOfDatasets > 0 ){
					doiFConsensome = data.key.doi;	
				} else {
					consensomReqirements(true);
				}
			}).fail(function () {
				consensomeError(true);
			});
		}
		// $.when(getGeneId(), getMolId(), getTissueId(), getTissueCategory(true), getPathway(true)).done(function(a1, a2, a3){
		$.when(getGeneId(transferGene), getTissueId(), getTissueCategory(true), getPathway(true)).done(function(a1, a2, a3){
//			validateForm(true);
			var validatedForm = validateForm(true);
			if( validatedForm ){
				if( $('#omics-category').val().toLowerCase() == 'cistromics'){
					$('#tm-tab-header').hide();
					$('#cistromics-tab-header').show();
					$('#cistromics-tab-header a').trigger('click');
					validatedForm.query.omicsCategory = 'cistromics';
					getResults(validatedForm.queryParameters, 'cistromics');
				} else if( $('#omics-category').val().toLowerCase()== 'transcriptomics' ) {
					$('#cistromics-tab-header').hide();
					$('#tm-tab-header').show();
					$('#tm-tab-header a').trigger('click');
					validatedForm.query.omicsCategory = 'transcriptomics';
					getResults(validatedForm.queryParameters, 'tm');
				} else { // Both
					$('#cistromics-tab-header').show();
					$('#tm-tab-header').show();
					$('#tm-tab-header a').trigger('click');
					getResults(validatedForm.queryParameters, 'both');
				}
			}
			
		});
	} else if( getParameterByName('doi') ){
		  window.location.href = url+'/doi/?doi='+getParameterByName('doi');
	}
};


var getPathway = function( urlRequest ){
	var signalingPathway = getParameterByName('signalingPathway');
	var pathwayType = getParameterByName('pathwayType');
	var findFlag = false;
	var vcategory;
	var vcclass;
	var vfamily;
	if( signalingPathway && signalingPathway != 'all' ){
		
		for ( var i = 0; i < global_pathway.length; i++ ){
			if( global_pathway[i].id == -1000 && global_pathway[i].pathways ){
				// type
				var cpathway = global_pathway[i].pathways;
				// category
				for ( var j = 0; j < cpathway.length; j++ ) {
					if (cpathway[j].type !== pathwayType) {
						var ccpathway = cpathway[j].pathways;
						// cclass
						for (var m = 0; m < ccpathway.length; m++) {
							if (ccpathway[m].type !== pathwayType) {
								var fpathway = ccpathway[m].pathways;
								// family
								for (var n=0; n<fpathway.length; n++) {
									if (fpathway[n].id == signalingPathway) {
										findFlag = true;
										vfamily = signalingPathway;
										break;
									}
								}
								if (findFlag) {
									vcclass = ccpathway[m].id;
									break;
								}
							}
							else {
								if (ccpathway[m].id == signalingPathway) {							
									findFlag = true;
									vcclass = ccpathway[m].id;
									break;
								}
							}
						}
						if (findFlag) {
							vcategory = cpathway[j].id;
							break;
						}
					}
					else {
						//search in cclass 
						if (cpathway[j].id == signalingPathway) {
							findFlag = true;
							vcategory = cpathway[j].id;
							break;
						}
					}
				}
			}
		}
		
		if (vcategory) {
			var selectedPathID = $('#pathway-modules option[tname="'+vcategory+'"]').val();
			if( selectedPathID !== 'all' ) {
				$('#pathway-modules').val(selectedPathID);	
			}
			if( urlRequest ) $('#pathway-modules').trigger('change');
		}
		if (vcclass) {
			var selectedPathID = $('#select-category'+vcategory+' option[tname="'+vcclass+'"]').val();
			if( selectedPathID !== 'all' ) {
				$('#select-category'+vcategory).val(selectedPathID);	
			}
			if( urlRequest ) $('#select-category'+vcategory).trigger('change');
		}
		
		if (vfamily) {
			var selectedPathID = $('#select-cclass'+vcclass+' option[tname="'+vfamily+'"]').val();
			if( selectedPathID !== 'all' ) {
				$('#select-cclass'+vcclass).val(selectedPathID);	
			}
			if( urlRequest ) $('#select-cclass'+vcclass).trigger('change');
		}

	}
};
var consensomSummary = function( display ){
	$('.consensome').not('#consensome-summary').hide();
	if( display === true ){
		blockUI("#tm-search-form", 'We are pulling information from multiple tables to display the requested data points…please stand by.');

		var dataParams = {};
		var query = {};
		if( $('#physiological-systems-target').val() !== 'all' ) {
			dataParams.ps = $('#physiological-systems-target').val();
			query.ps = dataParams.ps;
			if( $('#organ-target').val() !== 'all' ) {
				dataParams.organ = $('#organ-target').val();
				query.organ = $('#organ-target').val();
			}
		}

		if( $('#pathway-modules').val() !== 'all' ) {
			dataParams.signalingPathway = $('#pathway-target').val();
			dataParams.pathwayType = $('#pathway-target').attr('ptype');

			query.signalingPathway = dataParams.signalingPathway;
			query.pathwayType = dataParams.pathwayType;
		}

		dataParams.species = $('#species').val();
	//dataParams.species = 'test';
		query.species = $('#species').val();
		
		
		var detailUrl = '';
		var downloadUrl = '';
		if ($('#omics-category').val() == 'transcriptomics') {
			detailUrl = '/rest/omics/transcriptomics/v1/consensome/summary';
			dataParams.omicsCategory = 'transcriptomics'
		}
		else {
			detailUrl = '/rest/omics/cistromics/v1/consensome/summary';
			dataParams.omicsCategory = 'cistromics'
		}

		$.get(url + detailUrl, dataParams , function (data) {

		    //apollo 10.30.2018 missing numberDatapoints in data
			if( data && data.numberOfDatasets > 0 ){
				doiFConsensome = data.key.doi;
				var template = _.template($("script.consensome-summary-data" ).html());
				var html = '';
				html += template( data);

				$("#consensome-summary-data").html(html);
				handleConsensomDivs($('#consensome-summary-data'));
				$('.consensome').not('#consensome-summary').hide();
				handleConsensomDivs($('#consensome-summary'));
				
				$('#num-of-experiments').text(format_number(data.numberOfExperiments));
				//apollo 10.30.2018 $('#num-of-data-points').text(format_number(data.numberDatapoints));
				$('#num-of-datasets').text(format_number(data.numberOfDatasets));

				$("#submit-query").removeClass('disabled');
			} else {
				consensomReqirements(true);
			}
			$("#tm-search-form").unblock();
		}).fail(function () {
			console.log("@query.js@consensomSummary apo.test  consensome sumary 2 failed..");
			consensomeError(true);
			$("#tm-search-form").unblock();
		});
	} else{
		$('#consensome-summary').fadeOut();
	}
};

var consensomReqirements = function( display ){
	$('.consensome').not('#consensome-requirements').hide();
	if( display === true ){
		handleConsensomDivs($('#consensome-requirements'));
		$("#submit-query").addClass('disabled');
	} else{
		$('#consensome-requirements').fadeOut();
	}
};

var getConsensomeResults = function( doiRequest ){
	$('.consensome').not('#consensome-results').hide();
	blockUI("#tm-search-form", 'We are pulling information from multiple tables to display the requested data points…please stand by.');
	
	var dataParams = {};
	var query = {};
	if( $('#physiological-systems-target').val() !== 'all' ) {
		dataParams.ps = $('#physiological-systems-target').val();
		query.ps = dataParams.ps;
		if( $('#organ-target').val() !== 'all' ) {
			dataParams.organ = $('#organ-target').val();
			query.organ = $('#organ-target').val();
		}
	}

	if( $('#pathway-modules').val() !== 'all' ) {
		dataParams.signalingPathway = $('#pathway-target').val();
		dataParams.pathwayType = $('#pathway-target').attr('ptype');
	}

	dataParams.species = $('#species').val();
	
	if( doiRequest ){
		dataParams = {
			doi: getParameterByName('doi')
		}
	}
	var detailUrl = '';
	var downloadUrl = '';
	var localDownload='';
    var isLocalDownload=true;

    dataParams.omicsCategory = $('#omics-category').val();
	if (dataParams.omicsCategory == 'tm' || dataParams.omicsCategory == 'transcriptomics') {
		detailUrl = '/rest/omics/transcriptomics/v1/consensome/datalist';
		dataParams.omicsCategory == 'tm';
		localDownload='/rest/omics/transcriptomics/v1/consensome/download'
	}
	else {
		detailUrl = '/rest/omics/cistromics/v1/consensome/datalist';
        localDownload='/rest/omics/cistromics/v1/consensome/download'
	}

	downloadUrl = '/datasetfiles/consensome/download/';

	////Apollo 1.9.2019  There is a bug of file caching. If consensome files are cached in mongodb, remove ref to isLocalDownload and localDownload

    if(isLocalDownload)
		downloadUrl=localDownload;

	$.get(url + detailUrl, dataParams , function (res) {
		if( res && res.data && res.data.length > 0 ){
			
			var template;
			if (dataParams.omicsCategory == 'tm' || dataParams.omicsCategory == 'transcriptomics')
				template= _.template($("script.consensome-results-table-row-tm" ).html());
			else
				template= _.template($("script.consensome-results-table-row-cis" ).html());					
				
			var html = '';

			if( doiRequest ){
				dataParams = {
					signalingPathway:res.consensomeSummary.pathway,
					ps:res.consensomeSummary.physiologicalSystem,
					organ:res.consensomeSummary.organ,
					species:res.consensomeSummary.species
				};

//				populateTissueCategory(dataParams);

				$('#num-of-experiments').text(format_number(res.consensomeSummary.numberOfExperiments));
				$('#num-of-data-points').text(format_number(res.consensomeSummary.numberDatapoints));
				$('#num-of-datasets').text(format_number(res.consensomeSummary.numberOfDatasets));
			}
//
            if(!isLocalDownload)
			    $("#consensome-download").attr({"href": url+downloadUrl+res.consensomeSummary.key.doi});
            else
                $("#consensome-download").attr({"href": url+downloadUrl+"?doi="+res.consensomeSummary.key.doi});

			if (dataParams.omicsCategory == 'tm' || dataParams.omicsCategory == 'transcriptomics')
				$('#consensome-results-table-tm').dataTable().fnDestroy();
			else
				$('#consensome-results-table-cis').dataTable().fnDestroy();
			
			_.each(res.data, function(row){
				row.cNumber = d3.round(row.cNumber, 3);
				row.discoveryRate = d3.round(row.discoveryRate, 3);
				row.qValue = (d3.format(".3n")(row.cpvalue)).toUpperCase();
				row.gmFc = d3.round(row.gmFc, 3);
				row.macs2score = d3.round(row.averageScore,2);
				row.targetName = row.targetName;
				row.geneUrl = 'geneSearchType=gene&gene='+encodeURIComponent(row.gene);
				row.geneUrl += '&foldChangeMin=1&foldChangeMax=30';

				if( dataParams.signalingPathway && dataParams.signalingPathway.toLowerCase() != 'all' ){
					row.geneUrl += '&signalingPathway='+encodeURIComponent(dataParams.signalingPathway);	
				}
				
				if( dataParams.ps && dataParams.ps.toLowerCase() != 'all' ){
					row.geneUrl += '&ps='+encodeURIComponent(dataParams.ps);	
				}

				if( dataParams.organ && dataParams.organ.toLowerCase() != 'all' ){
					row.geneUrl += '&organ='+encodeURIComponent(dataParams.organ);	
				}
				
				row.geneUrl += '&species='+encodeURIComponent(dataParams.species);
				row.geneUrl += '&omicsCategory=' + dataParams.omicsCategory;
//				row.geneUrl += '&pathwayType=' + query.pathwayType;
				
				html += template( row );
			});

			if (dataParams.omicsCategory == 'tm' || dataParams.omicsCategory == 'transcriptomics') {
				$('#consensome-results-table-tm-wrap').css({'display':''});
				$('#consensome-results-table-cis-wrap').css('display','none');
			
				$("#consensome-results-table-body-tm").html(html);				
				$('#consensome-results-table-tm').DataTable(
						{"pageLength": 50,
						"order": [[ 4, "asc" ]],
						"columnDefs": [{
							"className": 'dt-center',
							"targets": '_all'
							}],
						}
						);
			}
			else {
				$('#consensome-results-table-cis-wrap').css({'display':''});
				$('#consensome-results-table-tm-wrap').css('display','none');
				$("#consensome-results-table-body-cis").html(html);				
				$('#consensome-results-table-cis').DataTable(
						{"pageLength": 50,
							"columnDefs": [{
								"className": 'dt-center',
								"targets": '_all'
								}],
						"order": [[ 2, "desc" ]],
						}
						);
			}
			
			if (dataParams.signalingPathway) {
				// for category
				if ($('#pathway-target').attr('ptype') == 'category') {
					$('#category-cp').text($('#pathway-modules option:selected').text());
					$('#class-cp').text('All');
					$('#family-cp').text('All');
				}
				else if ($('#pathway-target').attr('ptype') == 'cclass') {
					$('#category-cp').text($('#pathway-modules option:selected').text());
					$('#class-cp').text($('#select-category'+ $('#pathway-modules option:selected').val() + ' option:selected').text());
					$('#family-cp').text('All');
				}
				else {
					$('#category-cp').text($('#pathway-modules option:selected').text());
					$('#class-cp').text($('#select-category'+ $('#pathway-modules option:selected').val() + ' option:selected').text());
					var cclassid = $('#select-category'+ $('#pathway-modules option:selected').val() + ' option:selected').val();
					$('#family-cp').text($('#select-cclass'+ cclassid + ' option:selected').text());
				}
				
				$('#pathway-group option[value="'+$('#pathway').val()+'"]').attr('tname');
				$('#pathway-group option[value="'+$('#pathway').val()+'"]').attr('tname');
				$('#pathway-group option[value="'+$('#pathway').val()+'"]').attr('tname')
			}
			else {
				$('#category-cp').text('All');
				$('#class-cp').text('All');
				$('#family-cp').text('All');
			}
			if (dataParams.ps) {
				$('#physiological-system-cp').text($('#tissue-cell-line option:selected').text().capitalize());
			}
			else
				$('#physiological-system-cp').text("All");
			if (dataParams.organ)
				$('#organ-cp').text($('#tissue-cell-line-' + dataParams.ps + ' option:selected').text().capitalize());
			else
				$('#organ-cp').text("All");
			if (dataParams.species)
				$('#species-cp').text(dataParams.species.capitalize());
			else
				$('#species-cp').text("All");

			$("#tm-search-form").unblock();
			var doiParam = {}; 					
			doiParam.doi =doiFConsensome;
			doiParam.geneSearchType='consensome';
			History.pushState({state:'results'}, null, '/ominer/query.jsf?'+$.param(doiParam));

			consensomResults(true, dataParams.omicsCategory);
		} else {
			console.log('No results', res);
			consensomReqirements(true);
			$('#tm-search-form').unblock();
		}
	}).fail(function () { 
		consensomeError(true);
		$("#tm-search-form").unblock();
	});
};

var handleConsensomDivs = function( $dv ){
	$('#searchResults').removeClass('active');
	$('#searchResults').hide();
	$('#consensome').show();
	if( !$dv.is(":visible") ){
		$dv.slideDown(function(){
			$("html, body").animate({ scrollTop: $(document).height() - $dv.height() }, 500); 
		});
	}
};

var consensomResults = function( display,omicsCategory ){
	if( display === true ){
		handleConsensomDivs($('#consensome-results'));
		if (omicsCategory == 'tm' || omicsCategory == 'transcriptomics') {
			handleConsensomDivs($('#consensome-results-tm-table-wrap'));
		}
		else {
			handleConsensomDivs($('#consensome-results-cis-table-wrap'));
		}
		
	} else{
		$('#consensome-results').fadeOut();
	}
	
};

var consensomeError = function( display ){
	$('.consensome').not('#consensome-error').hide();
	if( display === true ){
		handleConsensomDivs($('#consensome-error'));
	} else if( display == 500 ){

	} else {
		$('#consensome-error').fadeOut();
	}	
};

var getTissueId = function(){
	var tissueLookup = {tissue:null};
	var tissueSymbol = getParameterByName('tissue');
	if(tissueSymbol){
		return $.ajax(url+'/rest/omics/auto/tissues?symbol='+tissueSymbol).done(function(res){
			var foundTissue = false;
			if(res && res.exactList) {
				_.each(res.exactList, function(item){
					if( item.officialSymbol === tissueSymbol ){
						$('#tissue-cell-line-old').val(item.identifier);
						$('#tissue-cell-line-old').attr('displayname', item.officialSymbol);
						foundTissue = true;
					}
				});
				if( !foundTissue ) {
					_.each(res.exactList, function(item){
						if( item.synonymTerm === geneSymbol ){
							$('#tissue-cell-line-old').val(item.identifier);
							$('#tissue-cell-line-old').attr('displayname', item.officialSymbol);
							foundTissue = true;
						}
					});
				}
			}
			if( !foundTissue ) {
				$('#tissue-autocomplete-holder').addClass('has-error');
			}
		});
	} else {
		return tissueLookup;
	}
};

var getTissueCategory = function( urlRequest ){
	var parentTissue = getParameterByName('parentTissue');
	var subTissue = getParameterByName('subTissue');

	if( parentTissue && parentTissue != 'all' ) {
		$('#tissue-cell-line').val($('#tissue-cell-line option[tname="'+parentTissue+'"]').val() || 'all');
		if( urlRequest ) $('#tissue-cell-line').trigger('change'); 
	}
	if( subTissue && subTissue != 'all' ) {
		var organID = $('#tissue-cell-line-'+$('#tissue-cell-line').val()+' option[tname="'+subTissue+'"]').val() || 'all';
		$('#tissue-cell-line-'+$('#tissue-cell-line').val()).val(organID);
		if( urlRequest ) $('#tissue-cell-line-'+$('#tissue-cell-line').val()).trigger('change'); 
	}
};


// Reset error field
var removeError = function($div){
	$div.parents('.form-group').removeClass('has-error');
	if( $("#tm-search-form").find('.has-error').length === 0 ) {
		if ($('#gene-type').val() != 'geneList' && $('#gene-type').val() != 'goTerm')
			$("#submit-query").removeClass('disabled');
	}
};

// Genes of interest change
var changeGenesOfInterest = function( consensome ){
	if( consensome ){
		$('#omics-category, #pathway-modules, #tissue-cell-line, #species').removeAttr('disabled');
//		$('#omics-category option[value="any"]').attr({'disabled':'disabled'});
		$('#regulation').hide();
	} else {
		$('#omics-category, #pathway-modules, #significance, #tissue-cell-line, #species').removeAttr('disabled');
		$('#omics-category option[value="any"]').removeAttr('disabled');
		$('#regulation').slideDown();
		// For consensome species defaul to Human and quering both is not an option
		if ($('#gene-type').val() == 'geneList' || $('#gene-type').val() == 'goTerm') {
			$('#species option[value="all"]').attr({'disabled':'disabled'});
			$('#species').val('Human');		
		}
		else {
			$('#species option[value="all"]').removeAttr('disabled');
			$('#species').val('all');		
		}
	}
	

	$('#species').change();
	$('.consensome').hide();
	$('#searchResults').hide();
	
	$(".autocomplete-field").hide();
};

// Cistromics
var getResults = function( dataParams, type ){
	blockUI("#tm-search-form", 'We are pulling information from multiple tables to display the requested data points…please stand by.');

	dataParams.reportsBy = 'pathways'; // Add this parameter to get points by pathways
	
	dataParams.omicsCategory = type;
	
	var maxResults = 3000;
	dataParams.countMax=maxResults;
	let resultsAreaOpen = false;

	if( type == 'both' || type == 'tm'){
		blockUI('#tm-results', 'We are pulling information from multiple tables to display the requested data points…please stand by.');
		var downUrlPath="/rest/omics/transcriptomics/download/excel/";
		$.ajax({
		    data: dataParams,
		    url: url+'/rest/omics/transcriptomics/datapoints'
		}).done(function( res ) {
		    // If successful
		   if( res && res.count && res.count > 0 ) {
				$('.num-of-results').text(format_number(res.count));
				if( res.count > 66000 || res.count > maxResults  ) {
					if( res.count > maxResults ){
						// $("#download-results-01").attr({"href": url+"/rest/transcriptomine/download/excel/"+res.queryForm.id});
					}
					///Apollo need to pass download url

					displayErrors(res, maxResults,downUrlPath, $('#tm-errors'));
					$('#tm-results').unblock();
					$('#tm-results').hide();
				}
				else {
					$('#tm-results').show();
					res.tm = true;
					prepairTMResults(res.resultsByPathways);
					$('#tm-errors').empty();
					$("#download-results").attr({"href": url+"/rest/omics/transcriptomics/download/excel/"+res.queryForm.id});
					 $('#display-total').text(res.count);
					 $('#currently-displaying').text(res.count);
					if ($('#gene-type').val() == 'geneList' || $('#gene-type').val() == 'goTerm')
						$('#plot-option').val('gene'); // Default is set to genes
					else
						$('#plot-option').val('pathway'); // Default is set to pathway
					$('#plot-option').trigger('change'); // Activate the graph manually
				}
				if ($('#gene-type').val() != 'geneList')
					History.pushState({state:'results'}, null, '/ominer/query.jsf?'+$.param(dataParams));
			} else {
				$('#tm-errors').html('<p>Your query returned zero results</p>');
				$('#tm-results').unblock();
				$('#tm-results').hide();
			}

			if( !resultsAreaOpen ){
				resultsAreaOpen = true;
				$("html, body").animate({ scrollTop: $(document).height() }, 500); // Scroll to the bottom of the page to see the results
			}
			$('#searchResults').show();
			$("#submit-query").removeClass('disabled');
			$('#tm-search-form').unblock();
		}).fail(function(jqXHR, textStatus, errorThrown) {
		    // If fail
		    if( !resultsAreaOpen ){
				resultsAreaOpen = true;
				$("html, body").animate({ scrollTop: $(document).height() }, 500); // Scroll to the bottom of the page to see the results

			}
		    $('#searchResults').show();
		    $('#tm-errors').html('<p>There was an error while processing your request. Please refresh the page and try again. If you continue to experience this please e-mail us at <a href="mailto:support@signalingpathways.org">support@signalingpathways.org</a></p>');
		    $("#submit-query").removeClass('disabled');
		    $('#tm-results').unblock();
		    $('#tm-results').hide();
		    $('#tm-search-form').unblock();
		});
	}
	if( type == 'both' || type == 'cistromics' ){
		blockUI('#cistromics-results', 'We are pulling information from multiple tables to display the requested data points…please stand by.');
		var downUrlPath="/rest/omics/cistromics/datapoints/download/excel/";
		$.ajax({
		    data: dataParams,
		    dataType: 'json',
		    url: url+'/rest/omics/cistromics/datapoints'
		}).done(function( res ) {
		    // If successful
		   if( res && res.count && res.count > 0 ) {
				if( res.count > 66000 || res.count > maxResults  ) {
					if( res.count > maxResults ){
						// $("#cistromics-download").attr({"href": url+"/rest/omics/cistromics/download/excel/"+res.queryForm.id});
					}
					$('#cistromics-results').unblock();
					$('#cistromics-results').hide();
                    ///Apollo need to pass download url
					displayErrors( res, maxResults,downUrlPath, $('#cistromics-errors') );
				}
				else {
					res.tm = false;
					$('#cistromics-results').show();
					prepareCistromicResults(res.resultsByPathways);
					copyParametersToresultsArea(dataParams);
					$('#cistromics-errors').empty();
					$("#cistromics-download").attr({"href": url+"/rest/omics/cistromics/datapoints/download/excel/"+res.queryForm.id});
					$('#display-total').text(res.count);
					$('#currently-displaying').text(res.count);
					
					if ($('#gene-type').val() == 'geneList' || $('#gene-type').val() == 'goTerm')
						$('#cistromics-plot-option').val('gene'); // Default is set to genes
					else
						$('#cistromics-plot-option').val('pathway'); // Default is set to pathway
					$('#cistromics-plot-option').trigger('change'); // Activate the graph manually
				}
				if ($('#gene-type').val() != 'geneList')
					History.pushState({state:'results'}, null, '/ominer/query.jsf?'+$.param(dataParams));
			} else {
				$('#cistromics-errors').html('<p>Your query returned zero results</a></p>');
				$('#cistromics-results').unblock();
				$('#cistromics-results').hide();
			}
			if( !resultsAreaOpen ){
				resultsAreaOpen = true;
				// $('#cistromics-tab-header a').trigger('click');
				if( type == 'cistromics' || type == 'both')
					$("html, body").animate({ scrollTop: $(document).height() }, 500); 
			}
			$('#searchResults').show();
//			$("#submit-query").removeClass('disabled');

			$('#tm-search-form').unblock();
		}).fail(function( jqXHR, textStatus, errorThrown ) {
		    // If fail
		    if( !resultsAreaOpen ){
				resultsAreaOpen = true;
				// $('#cistromics-tab-header a').trigger('click');
				if( type == 'cistromics' )
					$("html, body").animate({ scrollTop: $(document).height() }, 500); 
			}
		    $('#searchResults').show();
			$('#cistromics-errors').html('<p>There was an error while processing your request. Please refresh the page and try again. If you continue to experience this please e-mail us at <a href="mailto:support@signalingpathways.org">support@signalingpathways.org</a></p>');
			$('#cistromics-results').unblock();
			$('#cistromics-results').hide();
			$('#tm-search-form').unblock();
		});

	}
};

var copyParametersToresultsArea = function( searchTerm ){
	let searchTypeText = 'Any';
	if( $('#gene-type').val() == 'gene' ){
		searchTypeText = 'Single Gene';
	} else if( $('#gene-type').val() == 'geneList' ){
		searchTypeText = 'Gene List';
	} else if( $('#gene-type').val() == 'goTerm' ){
		searchTypeText = 'Gene Ontology Term';
	} else if( $('#gene-type').val() == 'consensome' ){
		searchTypeText = 'Consensome';
	} 
	$('#cistromics-genes-of-interest-cp span').text(searchTypeText+', '+searchTerm.gene);
	$('#cistromics-species-cp').text($('#species option:selected').text());
	
	$('#cistromics-pathway-cp').text($("#pathway-modules option:selected").text());

	
	if (searchTerm.signalingPathway) {
		// for category
		if ($('#pathway-target').attr('ptype') == 'category') {
			$('#cistromics-pathway-cp').text($('#pathway-modules option:selected').text());
			$('#cistromics-class-cp').text('All');
			$('#cistromics-family-cp').text('All');
		}
		else if ($('#pathway-target').attr('ptype') == 'cclass') {
			$('#cistromics-pathway-cp').text($('#pathway-modules option:selected').text());
			$('#cistromics-class-cp').text($('#select-category'+ $('#pathway-modules option:selected').val() + ' option:selected').text());
			$('#cistromics-family-cp').text('All');
		}
		else {
			$('#cistromics-pathway-cp').text($('#pathway-modules option:selected').text());
			$('#cistromics-class-cp').text($('#select-category'+ $('#pathway-modules option:selected').val() + ' option:selected').text());
			var cclassid = $('#select-category'+ $('#pathway-modules option:selected').val() + ' option:selected').val();
			$('#cistromics-family-cp').text($('#select-cclass'+ cclassid + ' option:selected').text());
		}
		
	}
	else {
		$('#cistromics-pathway-cp').text('All');
		$('#cistromics-class-cp').text('All');
		$('#cistromics-family-cp').text('All');
	}
	
};


var cistromicsPoints;
var prepareCistromicResults = function( resultsByPathways ){
	cistromicsPoints = createAllPoints(resultsByPathways, 'cistromics');

	if( cistromicsPoints.pointsByPsOrgan && cistromicsPoints.pointsByPsOrgan.axis.length < 2 ) {
		$( "#cistromics-plot-option option[value='rna']" ).attr({"disabled":"disabled"}); 
	}
	if( cistromicsPoints.pointsBySpecies && cistromicsPoints.pointsBySpecies.axis.length < 2 ) {
		$( "#cistromics-plot-option option[value='species']" ).attr({"disabled":"disabled"});
	}
	if( cistromicsPoints.pointsByGenes && cistromicsPoints.pointsByGenes.axis.length < 2 ) {
		$( "#cistromics-plot-option option[value='gene']" ).attr({"disabled":"disabled"});
	}
	if( cistromicsPoints.pointsByGenes && cistromicsPoints.pointsByGenes.axis.length == 2 
			&& (cistromicsPoints.pointsByGenes.axis[0].uniqueYaxisLabel.toUpperCase() == cistromicsPoints.pointsByGenes.axis[1].uniqueYaxisLabel.toUpperCase()) ) {
		$( "#cistromics-plot-option option[value='gene']" ).attr({"disabled":"disabled"});
	}
	if( cistromicsPoints.pointsByGenes && cistromicsPoints.pointsByGenes.axis.length > 2 ) {
		$( "#cistromics-plot-option option[value='gene']" ).removeAttr('disabled');
	}
	
	$('#cistromics-plot-option').trigger('change');
};

var updateCistromicsScatterplot = function( view ){
	$('#cistromics-scatter-plot').empty();
	if( view == 'rna' ) {
		if( cistromicsPoints.pointsByPsOrgan.axis && cistromicsPoints.pointsByPsOrgan.series ){
			drawScatterPlot( 
				'Biosample vs Binding Score', 
				'RNA Source', 
				cistromicsPoints.pointsByPsOrgan.axis, 
				[{"name":'cistromics-scatter-plot',"data":cistromicsPoints.pointsByPsOrgan.series,"turboThreshold":5000}], 
				'cistromics' 
			);
		} else {
			console.log('No data')
		}
	} else if( view == 'species' ) {
		if( cistromicsPoints.pointsBySpecies.axis && cistromicsPoints.pointsBySpecies.series ){
			drawScatterPlot( 
				'Species vs Binding score', 
				'Species', 
				cistromicsPoints.pointsBySpecies.axis, 
				[{"name":'cistromics-scatter-plot',"data":cistromicsPoints.pointsBySpecies.series,"turboThreshold":5000}],
				'cistromics' 
			);
		} else {
			console.log('No data')
		}
	} else if( view == 'gene') {
		if( cistromicsPoints.pointsByGenes.axis && cistromicsPoints.pointsByGenes.series ){
			drawScatterPlot( 
				'Gene vs Binding score', 
				'Gene', 
				cistromicsPoints.pointsByGenes.axis, 
				[{"name":'cistromics-scatter-plot',"data":cistromicsPoints.pointsByGenes.series,"turboThreshold":5000}], 
				'cistromics' 
			);
		} else {
			console.log('No data');
		}
	} else {
		if( cistromicsPoints.pointsByPathways.axis && cistromicsPoints.pointsByPathways.series ){
			drawScatterPlot( 
				'Pathway vs Binding score', 
				'Pathway', 
				cistromicsPoints.pointsByPathways.axis, 
				[{"name":'cistromics-scatter-plot',"data":cistromicsPoints.pointsByPathways.series,"turboThreshold":5000}], 
				'cistromics' 
			);
		} else {
			console.log('No data');
		}
	}
};
// End::Cistromics

// Transcriptomine
var transcriptominePoints;

var prepairTMResults = function( resultsByPathways ){

	transcriptominePoints = createAllPoints(resultsByPathways, 'tm');
	
	transcriptominePoints.pointsByPsOrgan.positive = [];
	transcriptominePoints.pointsByPsOrgan.negative = [];

	transcriptominePoints.pointsBySpecies.positive = [];
	transcriptominePoints.pointsBySpecies.negative = [];

	transcriptominePoints.pointsByGenes.positive = [];
	transcriptominePoints.pointsByGenes.negative = [];

	transcriptominePoints.pointsByPathways.positive = [];
	transcriptominePoints.pointsByPathways.negative = [];
	
	_.each(JSON.parse(JSON.stringify(transcriptominePoints.pointsByPsOrgan.series)), function( pt ){
		if( pt.foldChange > 0 ){
			transcriptominePoints.pointsByPsOrgan.positive.push(pt)
		} else {
			transcriptominePoints.pointsByPsOrgan.negative.push(pt)
		}
	});

	_.each(JSON.parse(JSON.stringify(transcriptominePoints.pointsBySpecies.series)), function( pt ){
		if( pt.foldChange > 0 ){
			transcriptominePoints.pointsBySpecies.positive.push(pt)
		} else {
			transcriptominePoints.pointsBySpecies.negative.push(pt)
		}
	});

	_.each(JSON.parse(JSON.stringify(transcriptominePoints.pointsByGenes.series)), function( pt ){
		if( pt.foldChange > 0 ){
			transcriptominePoints.pointsByGenes.positive.push(pt)
		} else {
			transcriptominePoints.pointsByGenes.negative.push(pt)
		}
	});

	_.each(JSON.parse(JSON.stringify(transcriptominePoints.pointsByPathways.series)), function( pt ){
		if( pt.foldChange > 0 ){
			transcriptominePoints.pointsByPathways.positive.push(pt)
		} else {
			transcriptominePoints.pointsByPathways.negative.push(pt)
		}
	});

	$( "#plot-option option" ).removeAttr('disabled');

	if( transcriptominePoints.pointsByPsOrgan && transcriptominePoints.pointsByPsOrgan.axis.length < 2 ) {
		$( "#plot-option option[value='rna']" ).attr({"disabled":"disabled"}); 
	} 
	if( transcriptominePoints.pointsBySpecies && transcriptominePoints.pointsBySpecies.axis.length < 2 ) {
		$( "#plot-option option[value='species']" ).attr({"disabled":"disabled"});
	}
	if( transcriptominePoints.pointsByGenes && transcriptominePoints.pointsByGenes.axis.length < 2 ) {
		$( "#plot-option option[value='gene']" ).attr({"disabled":"disabled"});
	}
	if( transcriptominePoints.pointsByGenes && transcriptominePoints.pointsByGenes.axis.length == 2 && (transcriptominePoints.pointsByGenes.axis[0].uniqueYaxisLabel.toUpperCase() == transcriptominePoints.pointsByGenes.axis[1].uniqueYaxisLabel.toUpperCase()) ) {
		$( "#plot-option option[value='gene']" ).attr({"disabled":"disabled"});
	}
	
	if( transcriptominePoints.pointsByGenes && transcriptominePoints.pointsByGenes.axis.length > 2) {
		$( "#plot-option option[value='gene']" ).removeAttr('disabled');
	}
	
	$( "#up-down-checkboxes input" ).prop('checked', true);
	$('#plot-option').val('pathway'); // Default is set to pathway
	$('#plot-option').trigger('change'); // Activate the graph manually
};

var updateTMScatterplot = function( view, upDown ){
	$('#scatter-plot').empty();

	if( view == 'rna' ) {
		if( upDown == 'up' ){
			drawScatterPlot( 
				'Biosample vs Fold Change', 
				'RNA Source', 
				transcriptominePoints.pointsByPsOrgan.axis, 
				[{"name":'tm-scatter',"data":transcriptominePoints.pointsByPsOrgan.positive,"turboThreshold":5000}], 
				'tm' );
		} else if( upDown == 'down' ){
			drawScatterPlot( 
				'Biosample vs Fold Change', 
				'RNA Source', 
				transcriptominePoints.pointsByPsOrgan.axis, 
				[{"name":'tm-scatter',"data":transcriptominePoints.pointsByPsOrgan.negative,"turboThreshold":5000}], 
				'tm' );
		} else {
			drawScatterPlot( 
				'Biosample vs Fold Change', 
				'RNA Source', 
				transcriptominePoints.pointsByPsOrgan.axis, 
				[{"name":'tm-scatter',"data":transcriptominePoints.pointsByPsOrgan.series,"turboThreshold":5000}], 
				'tm' );
		}
	} else if( view == 'species' ) {
		if( upDown == 'up' ){
			drawScatterPlot( 
					'Species vs Fold Change', 
					'Species', 
					transcriptominePoints.pointsBySpecies.axis, 
					[{"name":'tm-scatter',"data":transcriptominePoints.pointsBySpecies.positive,"turboThreshold":5000}],
					'tm' );
		} else if(  upDown == 'down' ){
			drawScatterPlot( 
					'Species vs Fold Change', 
					'Species', 
					transcriptominePoints.pointsBySpecies.axis, 
					[{"name":'tm-scatter',"data":transcriptominePoints.pointsBySpecies.negative,"turboThreshold":5000}],
					'tm' );
		} else {
			drawScatterPlot( 
				'Species vs Fold Change', 
				'Species', 
				transcriptominePoints.pointsBySpecies.axis, 
				[{"name":'tm-scatter',"data":transcriptominePoints.pointsBySpecies.series,"turboThreshold":5000}],
				'tm' );
		}
	} else if( view == 'gene' ) {
		if( upDown == 'up' ){
			drawScatterPlot( 
				'Gene vs Fold Change', 
				'Genes', 
				transcriptominePoints.pointsByGenes.axis, 
				[{"name":'tm-scatter',"data":transcriptominePoints.pointsByGenes.positive,"turboThreshold":5000}],
				'tm' );
		} else if( upDown == 'down'){
			drawScatterPlot( 
				'Gene vs Fold Change', 
				'Genes', 
				transcriptominePoints.pointsByGenes.axis, 
				[{"name":'tm-scatter',"data":transcriptominePoints.pointsByGenes.negative,"turboThreshold":5000}],
				'tm' );
		} else {
			drawScatterPlot( 
				'Gene vs Fold Change', 
				'Genes', 
				transcriptominePoints.pointsByGenes.axis, 
				[{"name":'tm-scatter',"data":transcriptominePoints.pointsByGenes.series,"turboThreshold":5000}],
				'tm' );
		}
	} else {
		if( upDown == 'up' ){
			drawScatterPlot( 
				'Pathway vs Fold Change', 
				'Pathway', 
				transcriptominePoints.pointsByPathways.axis, 
				[{"name":'tm-scatter',"data":transcriptominePoints.pointsByPathways.positive,"turboThreshold":5000}],
				'tm' );
		} else if( upDown == 'down' ){
			drawScatterPlot( 
				'Pathway vs Fold Change', 
				'Pathway', 
				transcriptominePoints.pointsByPathways.axis, 
				[{"name":'tm-scatter',"data":transcriptominePoints.pointsByPathways.negative,"turboThreshold":5000}],
				'tm' );
		} else {
			drawScatterPlot( 
				'Pathway vs Fold Change', 
				'Pathway', 
				transcriptominePoints.pointsByPathways.axis, 
				[{"name":'tm-scatter',"data":transcriptominePoints.pointsByPathways.series,"turboThreshold":5000}],
				'tm' );
		}
	}
};
// End::Transcriptomine

// Plot points
var plotGraphByRowName = function ( points, rowName, xAxis ) {
	let axis=[];
	let graphedPoints = [];
	points = _.sortBy(points, rowName);
	points.reverse();
	_.each(points, function( cPt ){
		let indexed = _.findIndex( axis, function( pt ){
			return pt.uniqueYaxisLabel === cPt[rowName]
		});
		cPt.fcid = cPt.id;
		if( indexed === -1 ){
			cPt.x = cPt[xAxis];
			cPt.y = axis.length;
			axis.push({
				uniqueYaxisLabel:cPt[rowName], 
				level:3,
				displayName:cPt[rowName]
			});
		} else {
			cPt.x = cPt[xAxis];
			cPt.y = indexed;
		}

		if( xAxis == 'foldChange' ){ // Alternative colors only when FC is available
			if(cPt.foldChange < 2 && cPt.foldChange > -2) {
				cPt.marker = {fillColor:'#b2b2b2',states: {hover: {fillColor: '#666666'}}}
			} else if( cPt.foldChange >= 2 ) {
				cPt.marker = { fillColor:'#df5353', states: {hover: {fillColor: '#df5353'}}};
			}
		} else {
			cPt.marker = { fillColor:'#df5353', states: {hover: {fillColor: '#df5353'}} };
		}

		graphedPoints.push(cPt);
	});
	// Returning the same object we received with modified data
	return {'axis':axis, 'series':graphedPoints};
};

var plotGraphByCategories = function( resultsByCategory, labelName, displayName, xAxis ){
	// plotGraphByCategories(resultsByPsOrgan, 'psOrganNode', 'tissue', 'foldChange');
	let axis = [];
	let points = [];
	let firstLevelCat;

	resultsByCategory = _.sortBy(resultsByCategory, labelName);
	resultsByCategory.reverse();
	_.each(resultsByCategory, function( node, idx ){
		let categoryArray = node[labelName].split('::');

		// Insert previous first level category, if we find a new first level category
		if( firstLevelCat && (firstLevelCat != categoryArray[0]) ){
			points.push({
				x: 0,
				y: axis.length,
				uniqueYaxisLabel:firstLevelCat,
				marker:{enabled: false, states:{hover:{enabled:false}}}	
			});
			axis.push({
				uniqueYaxisLabel:firstLevelCat, 
				level:1,
				displayName:firstLevelCat
			});
		}

		_.each(node.datapoints, function( cPt ){
			cPt.uniqueYaxisLabel = node[labelName]+'::'+cPt[displayName];
			cPt.fcid = cPt.id;
			if( xAxis == 'foldChange' ){ // Alternative colors only when FC is available
				if(cPt.foldChange < 2 && cPt.foldChange > -2) {
					cPt.marker = {fillColor:'#b2b2b2',states: {hover: {fillColor: '#666666'}}}
				} else if( cPt.foldChange >= 2 ) {
					cPt.marker = {fillColor:'#df5353',states: {hover: {fillColor: '#df5353'}}}
				}
			} else {
				cPt.marker = {fillColor:'#df5353',states: {hover: {fillColor: '#df5353'}}}
			}

			let indexed = _.findIndex( axis, function( pt ){
				return pt.uniqueYaxisLabel === cPt.uniqueYaxisLabel
			});

			if( indexed === -1 ){
				cPt.x = cPt[xAxis];
				cPt.y = axis.length;
				points.push(cPt);
				axis.push({
					uniqueYaxisLabel:cPt.uniqueYaxisLabel, 
					level:3,
					displayName:cPt[displayName]
				});
			} else {
				cPt.x = cPt[xAxis];
				cPt.y = indexed;
				points.push(cPt);
			}
		});

		// Second level
		points.push({
			x: 0,
			y: axis.length,
			uniqueYaxisLabel:node[labelName],
			marker:{enabled: false, states:{hover:{enabled:false}}}	
		});

		axis.push({
			uniqueYaxisLabel:node[labelName], 
			level:2,
			displayName:categoryArray[1]
		});

		firstLevelCat = categoryArray[0];
	});
	// We need to insert the last first level category
	points.push({
		x: 0,
		y: axis.length,
		uniqueYaxisLabel:firstLevelCat,
		marker:{enabled: false, states:{hover:{enabled:false}}}	
	});
	axis.push({
		uniqueYaxisLabel:firstLevelCat, 
		level:1,
		displayName:firstLevelCat
	});
	return {'axis':axis, 'series':points};
};
// End plot points

// Create points
var createAllPoints = function( resultsByPathways, type ){
	// Re-factor me: Just create one type of points at a time
	let pointsForGenes = [], pointsForSpecies = [], pointsForPsOrgan = [];
	_.each(resultsByPathways, function( thisPathway ){
		if( thisPathway.datapoints ){
			// Deep copy here, otherwise x and y axis will be overridden in point object
			pointsForGenes = pointsForGenes.concat(JSON.parse(JSON.stringify(thisPathway.datapoints)));
			pointsForSpecies = pointsForSpecies.concat(JSON.parse(JSON.stringify(thisPathway.datapoints)));
			pointsForPsOrgan = pointsForPsOrgan.concat(JSON.parse(JSON.stringify(thisPathway.datapoints)));

			if( type == 'tm' ){
				_.each(thisPathway.datapoints, function( cPt ){
					
					if (cPt.nodeSource == 'models') {
						cPt.displayName = cPt.models || cPt.molecules;
						cPt.displayName = '<b>'+cPt.displayName+'</b>';
						cPt.fcid = cPt.id;
					}
					else {
						cPt.displayName = cPt.bsms || cPt.molecules;
						// Not the ideal place to do this but faster way to get this thing bold
						if( cPt.nodeSource == 'bsm'){
							cPt.displayName = '<b>'+cPt.displayName+'</b>';
						} else if( cPt.nodeSource == 'ipags'){
							cPt.displayName = '<i>'+cPt.displayName+'</i>';
						}
						cPt.fcid = cPt.id;
					}
					
				});
			} else if( type == 'cistromics' ){
				_.each(thisPathway.datapoints, function( cPt ){
					
					cPt.displayName = cPt.molecules;
					// Not the ideal place to do this but faster way to get this thing bold and italicized
					if( cPt.bsms ){
						cPt.displayName += ' | <b>'+cPt.bsms+'</b>';
					}
					if( cPt.models ){
						cPt.displayName += ' | <b>'+cPt.models+'</b>';
					}
					if( cPt.omolecules ){
						cPt.displayName += ' | <i>'+ cPt.omolecules+'</i>';
					}
					cPt.fcid = cPt.id;
				});
			}
		}
	});

	let pointsByGenes = [];
	let pointsBySpecies = [];
	let pointsByPathways = [];
	let pointsByPsOrgan = [];

	// Format data to reuse the method to create points to match with pathways format
	_.each(pointsForPsOrgan, function( cPt ){
		if( cPt.psOrgan[0] == '/' ){ // Remove the leading /
			cPt.psOrgan = cPt.psOrgan.substring(1)
		}
		cPt.psOrgan = cPt.psOrgan.replace('/', '::');
	});

	let resultsByPsOrgan = [];

	_.each(_.groupBy(pointsForPsOrgan, 'psOrgan'), function( thispsOrgan, idx ){
		resultsByPsOrgan.push({
			psOrganNode:idx,
			datapoints:thispsOrgan
		});
	});
	// End::Format data to reuse the method to create points to match with pathways format

	if( type == 'tm' ){
		pointsByGenes = plotGraphByRowName(pointsForGenes, 'symbol', 'foldChange');
		pointsBySpecies = plotGraphByRowName(pointsForSpecies, 'speciesCommonName', 'foldChange');
		pointsByPathways = plotGraphByCategories(resultsByPathways, 'pathwayNode', 'displayName', 'foldChange');
		pointsByPsOrgan = plotGraphByCategories(resultsByPsOrgan, 'psOrganNode', 'tissue', 'foldChange');
	} else if( type == 'cistromics' ){
		pointsByGenes = plotGraphByRowName(pointsForGenes, 'symbol', 'score');
		pointsBySpecies = plotGraphByRowName(pointsForSpecies, 'speciesCommonName', 'score');
		pointsByPathways = plotGraphByCategories(resultsByPathways, 'pathwayNode', 'displayName', 'score');
		pointsByPsOrgan = plotGraphByCategories(resultsByPsOrgan, 'psOrganNode', 'tissue', 'score');
	}

	let allPoints = {
		"pointsBySpecies":pointsBySpecies, 
		"pointsByPathways":pointsByPathways, 
		"pointsByPsOrgan":pointsByPsOrgan,
		"pointsByGenes":pointsByGenes
	};

	// if( type == 'tm' ){
	// 	allPoints.pointsByGenes = pointsByGenes;
	// }
	return allPoints;
};
// End create points

var scatterPlots = {};

// Create scatter plot, default x-axis is Genes
var drawScatterPlot = function( name, yAxisName, yAxisData, series, div ) {
	// $('#searchResults').show();
	var height = 500;
	if( (yAxisData.length * 23) > height ) { 
		height = yAxisData.length * 23;
	}
	var targetDiv = 'scatter-plot';
	var yAxisTitle = 'Transcript Relative Abundance (Fold Change)';
	var blockDiv = '#tm-results';
	var typeGene = 'tm';
	
	if( div == 'cistromics' ){ // Cistromics Title
		targetDiv = 'cistromics-scatter-plot';
		yAxisTitle = 'ChiP-Atlas MACS2 binding score(±10 kb from TSS)';
		blockDiv = '#cistromics-results';
		typeGene = 'cistromics';
	}

	scatterPlots[div] = new Highcharts.Chart({
        chart: {
            type: 'scatter',
            zoomType: 'xy',
            resetZoomButton: {
                position: {
                    x: 0,
                    y: -30
                }
            },
            events:{
            	load:function() {
            		$(blockDiv).unblock();
            		enableToolTip();
            	},
            	redraw:function() {
            		enableToolTip();
            	}
            },
            renderTo: targetDiv,
            height:height,
            width:1102
        },
        exporting: {
            fallbackToExportServer: false,
            useHTML:true,
            buttons: {
                contextButton: {
                    text: 'Export Graph'
                }
            }
        },
        title: { text: '' },
        subtitle: { text: '' },
        xAxis: [{ title: {text: yAxisTitle,margin: 20}, labels :{ style:{ fontSize: '13px', fontWeight:"bold" } } }, { title: { text: yAxisTitle, margin: 20 }, opposite:true, linkedTo:0, labels :{ style:{ fontSize: '13px', fontWeight:"bold" }}}],
        yAxis: {
        	title:{text:null},
            categories:yAxisData,
            alternateGridColor:'#f7f9fa',
            labels: {
				formatter: function() {
					let label = this.value;
					let thisLabel;

					if( label.displayName ){
						thisLabel = label.displayName;
					} else {
						thisLabel = label
					}


					thisLabel = thisLabel.replace('##', " | ");
					var shortLabel = thisLabel.match(/\b\w+\b/g);
					
					
					if( label.level && label.level == 2 ) {
						if( shortLabel.length > 4){
							thisLabel = '<span style="color:#575754;font-size: 15px;" data-toggle="tooltip" data-placement="right" title="'+thisLabel+'">'+ shortLabel[0]+' '+shortLabel[1]+' ... '+shortLabel[shortLabel.length-2]+" "+shortLabel[shortLabel.length-1] + '</span>';
						} else {
							thisLabel = '<span style="color:#575754;font-size: 15px;">'+thisLabel+'</span>';
						}
					} else if( label.level && label.level == 1 ) {
						if( shortLabel.length > 4){
							thisLabel = '<span style="color:#000000; font-size:15px" data-toggle="tooltip" data-placement="right" title="'+thisLabel+'"><b>'+ shortLabel[0]+' '+shortLabel[1]+' ... '+shortLabel[shortLabel.length-2]+" "+shortLabel[shortLabel.length-1] + '<b></span>';
						} else {
							thisLabel = '<span style="color:#000000; font-size:15px"><b>'+thisLabel+'</b></span>';
						}
					} else {
						if (yAxisName == 'Genes' || yAxisName == 'Gene')
							thisLabel = '<span style="color:#aeaea8;font-style: italic;">'+ createGeneUrl(thisLabel, typeGene) +'</span>';
						else
							thisLabel = '<span style="color:#aeaea8;">'+ thisLabel +'</span>';
					}

					return thisLabel;
				},
				useHTML:true,
            	step:1,
            	style: {
		            fontSize: '12px'
		        }
			},
			showLastLabel: true
        },
        legend: {
            backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF',
            borderWidth: 1,
            enabled:false,
            verticalAlign:'top'
        },
        plotOptions: {
            scatter: {
                marker: {
                    radius: 5,
                    states: {
                        hover: {
                            enabled: true
                        }
                    }
                },
                states: {
                    hover: {
                        marker: {
                            enabled: false
                        }
                    }
                }
                ,
                point: {
                    events: {
                        click: function (e) {
                            var pageOrigin = {
                                x: e.pageX || e.clientX,
                                y: e.pageY || e.clientY
                            };
                            e.stopPropagation();
                            pointToolTip(this, pageOrigin);
                            if( this.bsms ){
                            	bsmInfo(this);
                            	$('.show-bsms').show();
                            } else {
                            	$('.show-bsms').hide();
                            }
                            if( this.fcid){
                            	foldChangeDetails(div);
                            }
	                    }
                    }
                },
                stickyTracking: false
            }
        },
        series: series,
        tooltip:{ 
        	enabled:false,
        	useHTML:true,
        	borderColor:'#336477',
        	borderRadius: 7,
        	borderWidth: 2,
        	delayForDisplay: 10000,
            hideDelay: true,
        	snap: false,
        	formatter: function() {
		    	var template = _.template($("script.graphFoldChangeInformation" ).html());
        		var tooltipHtml = template( this.point );
        		tooltipHtml = '<h3 class="popover-title">Fold Change Information</h3>'+
					'<div class="popover-content">'+
					tooltipHtml+
					'</div>';
        		return tooltipHtml;
		    }
        }
    });
};
// End Create scatter plot
var createGeneUrl = function (label, typeGene) {
	var genelist = label.split('|');
	var urlGenePre = url + "/ominer/query.jsf?geneSearchType=gene&findMax=y&gene=";
	var urlGenePost = "&foldChangeMin=2&foldChangeMax=30&significance=0.05&species=all&reportsBy=pathways&omicsCategory=" + typeGene;
	
	return genelist.map(x=>'<a target="_blank" href="'+ urlGenePre + modifyAndAttacheGene(x) + urlGenePost +'">'+ x +'</a>').join('|');
};

var modifyAndAttacheGene = function (gene) {
	return gene.trim().replace('<b>', '').replace('</b>', '').replace('<i>', '').replace('</i>', '');
};

var bsmInfo = function( point ){
	blockUI('#bsm-modal-body');
	$.get(url+'/rest/omics/bsm/'+point.bsms, function (data) {
		var $templateDiv = $("script.bsm-template" ).html();
		var template = _.template($templateDiv);
		$('#bsm-modal-body').empty();
		if( data.bsm ){
			_.each(data.bsm, function( thisBsm, idx ){
				if( !thisBsm.url ){
					thisBsm.url = '#';
				}
				$('#bsm-modal-body').append('<div class="bsm-wrap-'+(idx%2)+'">'+template( thisBsm )+'</div>');
			});
		}
		if( data.pathways ){
			$('#bsm-modal-body').append('<h4>Pharmacology</h4>');
			$templateDiv = $("script.bsm-pathway-template" ).html();
			template = _.template($templateDiv);
			_.each(data.pathways, function( pw, idx ){
				$('#bsm-modal-body').append('<div class="bsm-pathway-wrap-'+(idx%2)+'">'+template(pw)+'</div>')
			});
		}
		$('#bsm-modal-body').unblock();

	}).fail(function (e) {
		$('#bsm-modal-body').unblock();
		$('#bsm-modal-body').html('<p>There was an error while processing your request. If you continue to experience this please e-mail us at <a href="mailto:support@signalingpathways.org">support@signalingpathways.org</a></p>')
	});
};

var foldChangeDetails = function(omicsType){
	blockUI("#fc-details .modal-body");
		
	var id = $('.fc-details').attr('rel');
	var urltogo;
	if (omicsType == 'tm') {
		urltogo = '/rest/omics/transcriptomics/foldchange/';
		$('#changeTitleTm').css('display','block');
		$('#changeTitleCis').css('display','none');
	}
	else {
		urltogo	= '/rest/omics/cistromics/datapoints/macs2peak/';
		$('#changeTitleTm').css('display','none');
		$('#changeTitleCis').css('display','block');
	}
	jQuery.get(url+urltogo+id, function (fcData) {
		if( fcData ) {
			// window.location.hash = id;
			fcData.foldChange = precisionRound(fcData.foldChange, 3); 
			if( fcData.pValue ) {
				if (omicsType == 'tm') {
					fcData.pValue = parseFloat(fcData.pValue);
					fcData.pValue = (fcData.pValue != 0 ? fcData.pValue.toExponential(2) : '<1xE-10').toUpperCase();
				}
			} else {
				fcData.pValue = 'N/A';
			}
			
			if (omicsType == 'tm')
				fcData.title = 'Fold Change Information';
			else
				fcData.title = 'MACS2 Score Information';
			
			var template;
			
			if (omicsType == 'tm')
				template = _.template($("script.fc-change-template" ).html());
			else
				template = _.template($("script.fc-change-template-cistromics" ).html());

				
			if( fcData.orgType ) {
				if( fcData.orgType == 'GEO' ){
					fcData.repoUrl = 'http://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc='+fcData.repo;
				} else if( fcData.orgType == 'ARRAYEXPRESS' ){
					fcData.repoUrl = 'https://www.ebi.ac.uk/arrayexpress/experiments/'+fcData.repo;
				} else if( fcData.orgType == 'NURSA' ){
					// Data retrieved from NURSA
					fcData.repoText = 'Data retrieved from NURSA';
				} else {
					// Data were retrieved from the original publication
					fcData.repoText = 'Data were retrieved from the original publication';
				}
			} else {
				// Data were retrieved from the original publication
				fcData.repoText = 'Data were retrieved from the original publication';
			}

			$("#fc-details .modal-body").html(template(fcData));
			$('.citation').hide();
			$('.no-citation').show();
			if( fcData.pubmedId ){
				$.ajax( {url:url+"/rest/pubmed/abstract/"+fcData.pubmedId}).done(function( res ){
					if(res){
						var abstractTemplate = _.template($("script.abstract-information" ).html());
					    $("#citation").html(abstractTemplate(res));
					    $('.no-citation').hide();
					    $('.citation').show();
					}
				});
			} 
			$("#fc-details .modal-body").unblock();
		} else {
			$("#fc-details .modal-body").html('<p>No data was found.</p>');
			$("#fc-details .modal-body").unblock();
		}
	}).fail(function (e) {
		$("#fc-details .modal-body").html('<p>There was an error while processing your request. If you continue to experience this please e-mail us at <a href="mailto:support@signalingpathways.org">support@signalingpathways.org</a></p>');
		$("#fc-details .modal-body").unblock();
	});
};
// End more information


var scatterPlot;

var prepareScatterPlot = function( graphData ) {
	if( graphData.length === 0 ){ 
		$('#tm-view-change-form').hide();
		$('#scatter-plot').empty();
		return

} // We do not need to show an empty graph
    $('#tm-view-change-form').show();
	var geneyAxis = [], expyAxis = [], rnayAxis = [], speciesyAxis = [], pathwayyAxis = []; 
	var genePoints = [], expPoints = [], rnaPoints = [], speciesPoints = [], pathwayPoints = [];
	var series = [];

	$('#change-view').val('table');

	var color = d3.scale.category10();

	var createPoints = function ( axisName ) {
		var axis = [], upperCaseAxis = [];
		var points = [];

		graphData.sort(function(x, y){ // Use underscore sorting here
		   return d3.descending(x[axisName], y[axisName]);
		});

		_.each(graphData, function(row) {
			row[axisName] = $.trim(row[axisName]); // There are some names with spaces at the end, we need to get rid of them.
			if( upperCaseAxis.indexOf(row[axisName].toUpperCase()) === -1 ) {
				upperCaseAxis.push(row[axisName].toUpperCase());
				axis.push(row[axisName]);
			}
			if( row.foldChange !== 0 ) {
				var point = {
				    x: row.foldChange,
				    y: upperCaseAxis.indexOf(row[axisName].toUpperCase()),
				    symbolUrl: row.symbolUrl,
					symbol: row.symbol,
					tissueName: row.tissueName,
					foldChange: row.foldChange,
					fcid:row.id,
					speciesCommonName: row.speciesCommonName,
					pValue: (row.pValue ? (row.pValue != 0 ? row.pValue.toExponential(2) : '<1xE-10').toUpperCase() : 'N/A'),
					experimentName: (row.experimentName || 'Not Available')
				};
				if( row.foldChange < 2 && row.foldChange > -2 ){
					point.marker = {
						fillColor:'#b2b2b2',
						states: {
		                    hover: {
		                        fillColor: '#666666'
		                    }
		                }
					}
				} else if( row.foldChange >= 2 ) {
					point.marker = {
						fillColor:'#df5353',
						states: {
		                    hover: {
		                        fillColor: '#df5353'
		                    }
		                }
					}
				}
				points.push(point);
			}
		});
		return {'axis':axis, 'series':[{name:'tm-scatter',data:points}]};
	};

	var createPointsByCategories = function ( axisName, catName ) {
		var catIndex = 0, obj = {};
		var axis = [], points = [], pointsTree = [], thisCat = [], mainCatNames = [];
		var parentId = 1, curIndex = -1, subCatId = 1;

		var dataByCategories = _.groupBy(_.sortBy(graphData, axisName), catName);
		
		_.each( dataByCategories, function( cat, idx ){
			mainCatNames.push(idx);
		});
		mainCatNames.sort();
		
		if(dataByCategories['null']) {
			console.log('Deleting Null', dataByCategories['Others']);
			delete dataByCategories['null'];
		}

		if(dataByCategories['Others']) {
			console.log('Deleting Others', dataByCategories['Others']);
			delete dataByCategories['Others'];
		}

		//  Let's go through each category and create a tree
		//  This loop is designed onlo for 2 parent levels
		var idx, cat;
		for (var i = 0; i < mainCatNames.length; i++) {
			idx = mainCatNames[i];
			cat = dataByCategories[mainCatNames[i]];
			// Get all the 	category names
			if(idx[0] === '/') {
				thisCat = (idx.substr(1)).split('/');
			} else {
				thisCat = idx.split('/');
			}
			
			curIndex = _.findIndex( pointsTree, function( leaf ){
				return leaf.name == thisCat[0]
			});
			
			subCatId = 1; 

			if( curIndex > -1 ){
				parentId = pointsTree[curIndex].thisId;
				// Assuming we will always have children :::: What???
				subCatId = pointsTree[curIndex].children.length-1; // We need to do have the last index value of the array
			}

			_.each( cat, function( pt ){
				pt.title = thisCat[0]+'##'+thisCat[1]+'##'+pt[axisName]
			});

			if( curIndex === -1 ){
				pointsTree.push({
					name:thisCat[0],
					thisId:pointsTree.length+1,
					title:thisCat[0],
					children:[{
						name:thisCat[1],
						title:thisCat[0]+'##'+thisCat[1],
						parentId:pointsTree.length+1,
						thisId:1, // Second level's first element
						children:cat
					}]
				});

				// 1st level
				axis.push({
					title:thisCat[0], 
					displayName:thisCat[0],
					level:1
				});

				// 2nd level
				axis.push({
					title:thisCat[0]+'##'+thisCat[1], 
					displayName:thisCat[1],
					level:2
				});

				// 3rd level
				_.each( cat, function( pt ){
					if( getAxis(axis, thisCat[0]+'##'+thisCat[1]+'##'+pt[axisName]) === -1 ){
						axis.push({
							title:thisCat[0]+'##'+thisCat[1]+'##'+pt[axisName], 
							displayName:pt[axisName], // For pathways, we need to use regulatoryMoleculeSymbol
							level:3,
							parentName:thisCat[1],
							regulatoryMoleculeName:pt.regulatoryMoleculeName?pt.regulatoryMoleculeName:false,
							fake:(pt[axisName].indexOf('fake') > -1?true:false)
						});
					}
				});
				pointsTree[pointsTree.length-1][axisName] = thisCat[0];
				pointsTree[pointsTree.length-1].children[0][axisName] = thisCat[1];

			} else { // Already in the array, let's append to the existing
				pointsTree[curIndex].children.push({
					name:thisCat[1],
					title:thisCat[0]+'##'+thisCat[1], 
					parentId:parentId,
					thisId:subCatId+1,// Second level next element
					children:cat
				});

				// 2nd level
				axis.push({
					title:thisCat[0]+'##'+thisCat[1], 
					displayName:thisCat[1],
					level:2
				});

				// 3rd level
				_.each( cat, function( pt ){
					if( getAxis(axis, thisCat[0]+'##'+thisCat[1]+'##'+pt[axisName]) === -1 ){
						axis.push({
							title:thisCat[0]+'##'+thisCat[1]+'##'+pt[axisName], 
							displayName:pt[axisName], // For pathways, we need to use regulatoryMoleculeSymbol
							level:3,
							parentName:thisCat[1],
							regulatoryMoleculeName:pt.regulatoryMoleculeName?pt.regulatoryMoleculeName:false,
							fake:(pt[axisName].indexOf('fake') > -1?true:false)
						});
						pt.title = thisCat[0]+'##'+thisCat[1]+'##'+pt[axisName];
					} else {
						console.log('Already in the array', thisCat[0]+'##'+thisCat[1]+'##'+pt[axisName] );
					}
				});

				pointsTree[curIndex].children[subCatId][axisName] = thisCat[1];
			}
		}

		axis.reverse();

		// Let's go though the tree and create points for the scatter plot
		var totalP = 0;
		var traverseTree = function( tree ) {
			var yAxisIndex = 0;
			_.each(tree, function( sCat ) {
				if( sCat.children ){
					// Parent points
					yAxisIndex = getAxis(axis, sCat.title);
					if( yAxisIndex && axis.length === 0 ) yAxisIndex = 0;
					points.push({
					    x: 0,
					    y: (yAxisIndex === -1 ? 0 : yAxisIndex), // for the first time we need to set the index to 0;
						marker:{enabled: false, states:{hover:{enabled:false}}}		
					});
					traverseTree( sCat.children );
				} else {
					// Original Points
					totalP++;
					if( sCat.foldChange !== 0 ) {
						yAxisIndex = getAxis(axis, sCat.title);
						var point = {
						    x: sCat.foldChange,
						    y: yAxisIndex,
						    symbolUrl: sCat.symbolUrl,
							symbol: sCat.symbol,
							tissueName: sCat.tissueName,
							tissueCategory: sCat.tissueCategory,
							foldChange: sCat.foldChange,
							speciesCommonName: sCat.speciesCommonName,
							category:sCat[catName],
							fcid: sCat.id,
							pValue: (sCat.pValue ? (sCat.pValue != 0 ? sCat.pValue.toExponential(2) : '<1xE-10').toUpperCase() : 'N/A'),
							experimentName: (sCat.experimentName || 'Not Available')
						};
						if(point.foldChange < 2 && point.foldChange > -2) {
							point.marker = {
								fillColor:'#b2b2b2',
								states: {
				                    hover: {
				                        fillColor: '#666666'
				                    }
				                }
							}
						} else if( point.foldChange >= 2 ) {
							point.marker = {
								fillColor:'#df5353',
								states: {
				                    hover: {
				                        fillColor: '#df5353'
				                    }
				                }
							}
						}
						points.push(point);
					}
				}
			});
		};
		traverseTree(pointsTree);
		return {'axis':axis, 'series':[{name:'tm-scatter', data:points, turboThreshold:0}]};
	};

	var getAxis = function( axis, title ){
		var tmpIndex = _.findIndex(axis, function(i){
			return i.title === title
			// return ((i.title === title) && (i.level === level))
		});
		return tmpIndex;
	};

	var geneInfo = createPoints('symbolSynonym');
	geneyAxis = geneInfo.axis;
	genePoints = geneInfo.series;

	var pathwayInfo = createPointsByCategories('regulatoryMoleculeSymbol', 'moleculePathway');
	pathwayyAxis = pathwayInfo.axis;
	pathwayPoints = pathwayInfo.series;

	var rnaInfo = createPointsByCategories('tissueName', 'tissueCategory');
	rnayAxis = rnaInfo.axis;
	rnaPoints = rnaInfo.series;

	var speciesInfo = createPoints('speciesCommonName');
	speciesyAxis = speciesInfo.axis;
	speciesPoints = speciesInfo.series;

	$( "#plot-option option" ).removeAttr('disabled');

	if( rnayAxis.length < 2 ) {
		$( "#plot-option option[value='rna']" ).attr({"disabled":"disabled"}); 
	} 
	if( speciesyAxis.length < 2 ) {
		$( "#plot-option option[value='species']" ).attr({"disabled":"disabled"});
	} 
	if( geneyAxis.length < 2 ) {
		$( "#plot-option option[value='gene']" ).attr({"disabled":"disabled"});
	} 
	if( geneyAxis.length == 2 && (geneyAxis[0].toUpperCase() == geneyAxis[1].toUpperCase()) ) {
		$( "#plot-option option[value='gene']" ).attr({"disabled":"disabled"});
	}
	
	$('#plot-option').on('change', function() {
		var view = $(this).val();
		var name;
		var axisAndPoints;
		
		if( view == 'rna' ) {
			name = 'Biosample vs Fold Change';
			createScatterPlot( name, 'RNA Source', rnayAxis, rnaPoints );
		} else if( view == 'species' ) {
			name = 'Species vs Fold Change';
			createScatterPlot( name, 'Species', speciesyAxis, speciesPoints );
		} else if( view == 'pathway' ) {
			name = 'Pathway vs Fold Change';
			createScatterPlot( name, 'Pathway', pathwayyAxis, pathwayPoints );
		} else {
			name = 'Gene vs Fold Change';
			createScatterPlot( name, 'Genes', geneyAxis, genePoints );
		}
	});

	$('#result-tabs a[href="#graph-view-tab"]').trigger('click');
	$('#result-tabs a').on('click', function(){
		$('#graph-more-info').hide();
	});
	if( $("#plot-option option[value='pathway']").is(':enabled') ){
		$('#plot-option').val($("#plot-option option[value='pathway']").val());
	} else {
		$('#plot-option').val($( "#plot-option option[disabled!='disabled']" ).first().attr('value'));
	}
	$('#plot-option').trigger('change');
};

// Validation
var validateForm = function( constructedUrl ){
	var $btn = $("#submit-query");
	var submitForm = true;
	var searchBy = $('#gene-type').val();
	var queryParameters = {'geneSearchType':searchBy};
	var query = {'geneSearchType':searchBy};
	queryParameters.findMax = 'y';
	query.gene = 'y';
	
	$btn.addClass('disabled');
	if( searchBy ) {
		if( searchBy.toLowerCase() == 'consensome'){
			submitForm = false;
			getConsensomeResults();
		} else if( searchBy == 'gene' ){
			queryParameters.gene = $('#gene-autocomplete-target').val();
			query.gene = $('#gene-autocomplete-target').attr('symbol');

			if( !$('#gene-autocomplete-target').val() ) {
				$('#gene-autocomplete-holder').addClass('has-error');
				$('#gene-autocomplete').focus();
				$btn.removeClass('disabled'); // Why do I need to do this here?
				submitForm = false;
			}
		} else if( searchBy == 'geneList' ) {
			if( $('#file-upload-target').val() ) {
				queryParameters.gene = $('#file-upload-target').val();
				query.gene = $('#file-upload-target').val();
			} else {
				$('#geneList-holder').removeClass('has-success');
				$('#geneList-holder').addClass('has-error');
				$('#fileUpload').val('');
				$('#gene-list').focus();
				$btn.removeClass('disabled');
				submitForm = false;
			}
		} else if( searchBy == 'goTerm' ){
			queryParameters.gene = $('#goTerm-autocomplete-target').val();
			query.gene = $('#goTerm-autocomplete-target').val();
			if( !$('#goTerm-autocomplete-target').val() ) {
				$('#goTerm-autocomplete-holder').addClass('has-error');
				$('#goTerm-autocomplete').focus();
				$btn.removeClass('disabled');
				submitForm = false;
			}
		}
	}

	// There is no foldChange or significance value for cistromics
	if( $('#omics-category').val().toLowerCase() != 'cistromics'){
		var foldChangeMin = $('#fold-change-min').val();
		var foldChangeMax = $('#fold-change-max').val();
		
		queryParameters.foldChangeMin = foldChangeMin;
		queryParameters.foldChangeMax = foldChangeMax;

		query.foldChangeMin = foldChangeMin;
		query.foldChangeMax = foldChangeMax;

		if( $('#significance').val() ){
			queryParameters.significance = $('#significance').val();
			query.significance = $('#significance').val();
		}
	}

	if( $('#physiological-systems-target').val() !== 'all' ) {
		queryParameters.ps = $('#physiological-systems-target').val();
		query.ps = queryParameters.ps;
		if( $('#organ-target').val() !== 'all' ) {
			queryParameters.organ = $('#organ-target').val();
			query.organ = $('#organ-target').val();
		}
	}

	if( $('#pathway-modules').val() !== 'all' ) {
		queryParameters.signalingPathway = $('#pathway-target').val();
		queryParameters.pathwayType = $('#pathway-target').attr('ptype');

		query.signalingPathway = queryParameters.signalingPathway;
		query.pathwayType = queryParameters.pathwayType;
	}

	queryParameters.species = $('#species').val();
	query.species = $('#species').val();

	if(!submitForm || $('.form-group.has-error').length > 0) {
		return false;
	}
	return {queryParameters:queryParameters, query:query};
};

var validateGeneList = function( cb ){
	var regex = /^([a-zA-Z0-9\s_\\.\-:])+(.csv|.txt)$/;
    $('#geneList-holder').removeClass('has-success has-error');
    var geneList = '';
    if (typeof (FileReader) != "undefined") {
    	var fileToRead = $("#fileUpload")[0].files[0];
        var reader = new FileReader();
        reader.onload = function (e) {
        	var csv = $.trim(e.target.result).replace(/\n/g, ",");
        	csv = csv.replace(/^[,\s]+|[,\s]+$/g, '').replace(/,[,\s]*,/g, ',');
        	blockUI('#tm-search-form', 'Please wait while we validate the uploaded gene list.');
			$.ajax({
				type: "post",
		        headers: {'Content-Type':'application/json'},
				url: url+'/rest/transcriptomine/lookup/genelist',
				data: csv,
				success: function( res ){
					var total = (csv.split(',')).length;
					$('#invalid-genes').text(res);
					$('#total-genes').text(total);
					if( (total - res) >= 2  ) {
						geneList = csv;
						$('#geneList-holder').addClass('has-success');
						cb(geneList);
					} else {
						$('#geneList-holder').addClass('has-error');
						cb( false )
						// Invalid gene list
					}
					$('#tm-search-form').unblock();
				}
			});
        };
        reader.readAsText($("#fileUpload")[0].files[0]);
    } else {
        alert("This browser does not support HTML5.");
        cb(false);
    }
};
// End Validation

// auto completes
var createAutocomplete = function(opt, cb ){
	$('#'+opt.id).catcomplete({
	    source: function (request, response) {
			jQuery.get(url+opt.url, {
			    symbol: (request.term)
			}, function (data) {
				var dropdowList = [];
				if(data && (data.exactList.length || data.otherList.length)){
					if( opt.singleVals ){
						if(data.exactList && data.exactList.length > 0) {
							data.exactList.unshift('=== Exact Matches ===');
						}
						if(data.otherList && data.otherList.length > 0) {
							data.exactList.push("=== Other Matches ===");
						}
						response(data.exactList.concat(data.otherList));
					} else {
						if(data.exactList && data.exactList.length > 0) {
							dropdowList.push('=== Exact Matches ===');
							$.each(data.exactList, function(i, item) {
                                var realValue;
                                // if (typeof item.identifier === 'undefined') {
                                //     realValue = item.officialSymbol;
                                // } else {
                                //     realValue = item.identifier;
                                // }
								dropdowList.push({
									label:(item.synonymTerm?item.synonymTerm+' ('+item.officialSymbol+')':item.officialSymbol), 
									realValue:item.officialSymbol, 
									type:item.type,
									symbol:item.officialSymbol
								});
							});
						}
						if(data.otherList && data.otherList.length > 0) {
							dropdowList.push('=== Other Matches ===');
							$.each(data.otherList, function(i, item){
                                var realValue;
                                // if (typeof item.identifier === 'undefined') {
                                //     realValue = item.officialSymbol;
                                // } else {
                                //     realValue = item.identifier;
                                // }
								dropdowList.push({
									label:(item.synonymTerm?item.synonymTerm+' ('+item.officialSymbol+')':item.officialSymbol), 
									realValue:item.officialSymbol, 
									type:item.type,
									symbol:item.officialSymbol
								});
							})
						}
						response(dropdowList);
					}
				} else { response(dropdowList.push({
					label:'', 
					realValue:'', 
					type:'',
					symbol:''
				})) }
			});
		},
		select:function( event, ui ){
			$('#'+opt.id+'-target').val(ui.item.realValue || ui.item.value);
			$('#'+opt.id+'-target').attr('symbol', ui.item.symbol);
		},
		minLength: 2,
		delay: 300,
		appendTo:"#"+opt.id+"-holder",
		autoFocus: true
    });
};
// End auto completes
