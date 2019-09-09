var pathArray = window.location.href.split( '/' );
var protocol = pathArray[0];
var host = pathArray[2];
var url = protocol + '//' + host;

_.templateSettings = {
  evaluate : /\{\[([\s\S]+?)\]\}/g,
  interpolate : /\{\{([\s\S]+?)\}\}/g,
  variable:"rc"
};

var pharmgkbLoaded = false;
var getPathways = function() {

	var requestId;
	var requestParams;
	if( pharmgkbLoaded == false ) {
		blockUI('#pathways-container');
		var gSymbol = $('input[name="gene-symbol"]').val();
		var pcSymbol = $('input[name="PubChem"]').val();
		var cSymbol = $('input[name="Chebi"]').val();
		if( gSymbol && gSymbol != '[]' ) {
			requestId = gSymbol;
			requestParams = 'data=pathways&gene='+requestId;
		} else if( pcSymbol && pcSymbol != '[]' ){
			requestId = pcSymbol.replace(/[\[\]']+/g,'');
			requestParams = 'data=pathways&chemical='+requestId+'&chemicalIdScheme=pubchem';
		} else if( cSymbol && cSymbol != '[]' ) {
			requestId = cSymbol.replace(/[\[\]']+/g,'');
			requestParams = 'data=pathways&chemical='+requestId+'&chemicalIdScheme=chebi';
		}
		$.ajax({
			type: "GET",
	        headers: {'Content-Type':'application/json'},
			url: url+'/rest/pharmgkb/data?'+requestParams,
			success: function( res ){
				$('#pathways-container').unblock();
				if( res && res.pathways ){
					createTable('#pathways-table-row', '#pathways-body', '#pathways-table', res.pathways);
				} else {
					createTable('#pathways-table-row', '#pathways-body', '#pathways-table', []);
				}
				pharmgkbLoaded = true;
			},
			error:function( request, status, error ) {
				$('#pathways-container').unblock();
				pharmgkbLoaded = true;	
				createTable('#pathways-table-row', '#pathways-body', '#pathways-table', []);
			}
		})
	}
}

var getPharmGKBData = function() {
	getLabel(); 
	getDosingGuidelines();
	getClinicalAnnotations();
}

var pharmgkbLabelLoaded = false;
var getLabel = function() {
	var requestId;
	var requestParams;
	if( pharmgkbLabelLoaded == false ) {
		blockUI('#drug-labels-container');
		if( $('input[name="gene-symbol"]').val() ) {
			requestId = $('input[name="gene-symbol"]').val();
			requestParams = 'data=clinicalPgx&dtype=label&gene='+requestId;
		} else if( $('input[name="PubChem"]').val() ) {
			requestId = $('input[name="PubChem"]').val().replace(/[\[\]']+/g,'');
			requestParams = 'data=clinicalPgx&dtype=label&chemical='+requestId+'&chemicalIdScheme=pubchem';
		} else if( $('input[name="Chebi"]').val() ) {
			requestId = $('input[name="Chebi"]').val().replace(/[\[\]']+/g,'');
			requestParams = 'data=clinicalPgx&dtype=label&chemical='+requestId+'&chemicalIdScheme=pubchem';
		}
		if( requestId ) {
				$.ajax({
				type: "GET",
		        headers: {'Content-Type':'application/json'},
				url: url+'/rest/pharmgkb/data?'+requestParams,
				success: function( res ){
					$('#drug-labels-container').unblock();
					if( res.labels ){
						_.each(res.labels, function(row){
							row.summary = $(row.summary).text();
						})
						createTable('#drug-labels-table-row', '#drug-labels-body', '#drug-labels-table', res.labels);
					} else {
						createTable('#drug-labels-table-row', '#drug-labels-body', '#drug-labels-table', []);
					}
					pharmgkbLabelLoaded = true;
				},
				error:function( request, status, error ) {
					$('#drug-labels-container').unblock();
					pharmgkbLabelLoaded = true;	
					createTable('#drug-labels-table-row', '#drug-labels-body', '#drug-labels-table', []);
				}
			})
		} else {
			// No input is avalilable make the request
			$('#drug-labels-container').unblock();
			pharmgkbLabelLoaded = true;	
			createTable('#drug-labels-table-row', '#drug-labels-body', '#drug-labels-table', []);	
		}
	}
}
var dosingGuidelines = false;
var getDosingGuidelines = function(){
	// dosing-guidelines
	var requestId;
	var requestParams;
	if( dosingGuidelines == false ) {
		blockUI('#dosing-guidelines-container');
		if( $('input[name="gene-symbol"]').val() ) {
			requestId = $('input[name="gene-symbol"]').val();
			requestParams = 'data=clinicalPgx&dtype=dosage&gene='+requestId;
		} else if( $('input[name="PubChem"]').val() ) {
			requestId = $('input[name="PubChem"]').val().replace(/[\[\]']+/g,'');
			requestParams = 'data=clinicalPgx&dtype=dosage&chemical='+requestId+'&chemicalIdScheme=pubchem';
		} else if( $('input[name="Chebi"]').val() ) {
			requestId = $('input[name="Chebi"]').val().replace(/[\[\]']+/g,'');
			requestParams = 'data=clinicalPgx&dtype=dosage&chemical='+requestId+'&chemicalIdScheme=pubchem';
		}
		if( requestId ) {
				$.ajax({
				type: "GET",
		        headers: {'Content-Type':'application/json'},
				url: url+'/rest/pharmgkb/data?'+requestParams,
				success: function( res ){
					$('#dosing-guidelines-container').unblock();
					if( res.guidelines ){
						_.each(res.guidelines, function(row){
							row.summary = $(row.summary).text();
						})
						createTable('#dosing-guidelines-table-row', '#dosing-guidelines-body', '#dosing-guidelines-table', res.guidelines);
					} else {
						createTable('#dosing-guidelines-table-row', '#dosing-guidelines-body', '#dosing-guidelines-table', []);
					}
					dosingGuidelines = true;
				},
				error:function( request, status, error ) {
					$('#dosing-guidelines-container').unblock();
					dosingGuidelines = true;	
					createTable('#dosing-guidelines-table-row', '#dosing-guidelines-body', '#dosing-guidelines-table', []);
				}
			})
		} else {
			// No input is avalilable make the request
			$('#dosing-guidelines-container').unblock();
			dosingGuidelines = true;	
			createTable('#dosing-guidelines-table-row', '#dosing-guidelines-body', '#dosing-guidelines-table', []);	
		}
	}
}

var clinicalAnnotations = false;

var getClinicalAnnotations = function() {
	var requestId;
	var requestParams;
	if( clinicalAnnotations == false ) {
		blockUI('#clinical-annotations-container');
		if( $('input[name="gene-symbol"]').val() ) {
			requestId = $('input[name="gene-symbol"]').val();
			requestParams = 'data=clinicalPgx&dtype=clinicalAnnotation&gene='+requestId;
		} else if( $('input[name="PubChem"]').val() ) {
			requestId = $('input[name="PubChem"]').val().replace(/[\[\]']+/g,'');
			requestParams = 'data=clinicalPgx&dtype=clinicalAnnotation&chemical='+requestId+'&chemicalIdScheme=pubchem';
		} else if( $('input[name="Chebi"]').val() ) {
			requestId = $('input[name="Chebi"]').val().replace(/[\[\]']+/g,'');
			requestParams = 'data=clinicalPgx&dtype=clinicalAnnotation&chemical='+requestId+'&chemicalIdScheme=pubchem';
		}
		if( requestId ) {
				$.ajax({
				type: "GET",
		        headers: {'Content-Type':'application/json'},
				url: url+'/rest/pharmgkb/data?'+requestParams,
				success: function( res ){
					$('#clinical-annotations-container').unblock();
					_.each(res.annotations, function(row){
						if( row.Phenotypes && row.Phenotypes.length > 0 ){
							row.Phenotypes = createString(row.Phenotypes, 'name');
						} else {
							row.Phenotypes = 'N/A';
						}

						// No genes
						if( !row.genes || row.genes.length == 0) {
							row.genes = "N/A"
						} 

						if( row.types && row.types.length > 0 ){
							_.each(row.types, function(type, idx){
								if( type == 'pk' ){
									row.types[idx] = 'Metabolism/PK';
								} else if( type == 'toxicity' ) {
									row.types[idx] = 'Toxicity/ADR'
								}
							})
							row.types = row.types.join(', ');
						} else {
							row.types = 'N/A';
						}


						// If clinicalAnnotations.mixedPopulation=true, display mixed population; and the value for clinicalAnnotations.raceNotes.  Ex: mixed population; mostly white 
						if( row.mixedPopulation == 'true' ) {
							row.ombRaces = row.raceNotes?('Mixed Population; '+(row.raceNotes?(', '+row.raceNotes):'')):'Mixed Population';

						} else {
							// If clinicalAnnotations.mixedPopulation=false, display values for clinicalAnnotations.ombRaces. If clinicalAnnotations.ombRaces is null, then display unknown.
							if( row.ombRaces && row.ombRaces.length > 0 ){
								row.ombRaces = row.ombRaces.join(', ');
							} else {
								row.ombRaces = 'Unknown';
							}
						}
						if( row.relatedChemicals && row.relatedChemicals.length > 0 ){
							row.relatedChemicals = createString(row.relatedChemicals, 'name')
						} else {
							row.relatedChemicals = 'N/A';
						}

						if( !row.clinicalVariant ) {
							if( row.haplotypes && row.haplotypes.length > 0 ) {
								row.clinicalVariant = createString(row.haplotypes, 'symbol');
							} else {
								row.clinicalVariant = 'N/A';
							}
						}
					})
					if( res.annotations ){
						createTable('#clinical-annotations-table-row', '#clinical-annotations-body', '#clinical-annotations-table', res.annotations);
					} else {
						createTable('#clinical-annotations-table-row', '#clinical-annotations-body', '#clinical-annotations-table', []);
					}
					clinicalAnnotations = true;
				},
				error:function( request, status, error ) {
					$('#clinical-annotations-container').unblock();
					clinicalAnnotations = true;	
					createTable('#clinical-annotations-table-row', '#clinical-annotations-body', '#clinical-annotations-table', []);
				}
			})
		} else {
			// No input is avalilable make the request
			$('#clinical-annotations-container').unblock();
			clinicalAnnotations = true;	
			createTable('#clinical-annotations-table-row', '#clinical-annotations-body', '#clinical-annotations-table', []);	
		}
	}
}

var createTable = function( tmpl, tableBody, dt, data ) {
	var template = _.template($(tmpl).html());
	var html = "";

	_.each(data, function(row){
		html += template( row );
	});

	$(tableBody).html(html);
	$(dt).DataTable();
}

var blockUI = function( target ){
	$(target).block({
		message:'<div class="ui-blockui-content ui-widget ui-widget-content ui-corner-all ui-shadow">Loading content...<div class="ajaxSpinner"><div class="dot1"></div><div class="dot2"></div></div></div>',
		css:{width:"158px", height:"110px", "border-radius": "10px"}
	});
}

var createString = function(arr, key) {
  return arr.map(function (obj) {
    return obj[key];
  }).join(', ');
}