// When editing this file, remember to refer to following files
// transcriptomicOverview.xhtm
// metabolomicOverview.xhtml
// cistromicOverview.xhtml
"use strict";
var scatterPlot;

$(document).ready(function(){
	displayExpInfo();
	if($('#datasets') && $('#datasets').length > 0){
		blockUI('#datasets');
		$.when( lookUpTissues(), lookUpPathways() ).done( function(){
			getDatasets();
		});
	}
});

var displayExpInfo = function(){
	if(  $('#datasetType').val() == 'cistromics' ){
		getCistromicsExpInfo();
	} else {
		var fcCall = getDataSetsByExperimentId(null, function(){
			sliderListner();
			// sliderTour('datasets');
		});
	}
}

// Create scatter plot, default y-axis is Genes
var createScatterPlot = function( name, yAxisName, yAxisData, series ) {
	$('#graph-more-info').hide();
	var xAxisText = 'Transcript Relative Abundance (Fold Change)';
	if(  $('#datasetType').val() == 'cistromics' ){
		xAxisText = 'ChiP-Atlas MACS2 binding score(±10 kb from TSS)';
	}

	yAxisName = $('#ovtype').val();

	var height = 500;
	if( (yAxisData.length * 23) > height ) { 
		height = yAxisData.length * 23;
	}
	
	scatterPlot = new Highcharts.Chart({
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
            		enableToolTip();
            		$('#graph-more-info').hide();	
            	},
            	redraw:function() {
            		enableToolTip();
            		$('#graph-more-info').hide();
            	}
            },
            renderTo: "data-by-graph",
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
        title: {
            text: ''
        },
        subtitle: {
            text: ''
        },
        scrollbar:{enabled:true},
        xAxis: [{
	            title: {
	                text: xAxisText,
	                margin: 20
	            },
	            labels :{
	            	style:{
	            		fontSize: '13px',
			            fontWeight:"bold"
	            	}
	            }
        	},{
	            title: {
	                text: xAxisText,
	                margin: 20
	            },
	            opposite:true,
	            linkedTo:0,
	            labels :{
	            	style:{
	            		fontSize: '13px',
			            fontWeight:"bold"
	            	}
	            }
	        }
        ],
        yAxis: {
            title: {
                text: yAxisName
            },
            categories:yAxisData,
            alternateGridColor:'#f7f9fa',
            labels: {
				formatter: function() {
					var label = this.value;
					var thisLabel;
					if( label.title ) { // Check for the custom fields to style parent labels
						thisLabel = label.title;
					} else {
						thisLabel = this.value+'';
					}
					var textArray = thisLabel.split(', ');
					
					if( thisLabel.indexOf('##') > -1 ) {
						thisLabel = '<span style="font-size:16px; color:#434343">'+thisLabel.substr(2)+'</span>';
					} else if( thisLabel.indexOf('#') > -1 ) {
						thisLabel = '<span style="font-size:20px; color:#323232">'+thisLabel.substr(1)+'</span>';
					} 

					if(textArray.length > 2) {
						thisLabel = textArray[textArray.length - 2]+', '+textArray[textArray.length - 1];
					}
					if( thisLabel.indexOf("==") ) {
						thisLabel = (thisLabel.split("==")).pop();
					}
					thisLabel = thisLabel.capitalize();
					if( label.regulatoryMoleculeName && label.regulatoryMoleculeName != thisLabel ){
						 thisLabel = '<span data-toggle="tooltip" data-placement="right" title="'+label.regulatoryMoleculeName.capitalize()+'">'+ thisLabel + '</span>';
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
                            customToolTip(this, pageOrigin);
	                    }
                    }
                },
                stickyTracking: false
            }
        },
        series: series,
        tooltip:{ 
        	enabled:false
        }
    });
};

var getCistromicsExpInfo = function(){
	if( !$('#repoId').val()  ) return ; // This function is called on load but we do not need to run it unless it is cistromics 
	var getUrl = '/rest/cistromics/datapoints/top';
	var query = {experimentid:$('#expId').val()}
	var axisName = 'gene';
	var html;
	$.ajax({
		type: "GET",
        headers: {'Content-Type':'application/json'},
		url: url+getUrl,
		data: query,
		success: function( getRes ){
			var axis = [];
			var upperCaseAxis = [];
			var points = [];
			if( getRes && getRes.length > 0 ){
				_.each(sortData( 'score', getRes), function(row) {
					if( upperCaseAxis.indexOf(row[axisName].toUpperCase()) === -1 ) {
						upperCaseAxis.push(row[axisName].toUpperCase());
						axis.push(row[axisName]);
					}
					row.marker = {
						fillColor:'#df5353',
						states: {
		                    hover: {
		                        fillColor: '#df5353'
		                    }
		                }
					}
					row.dataType = 'topCistromics';
					row.x = row.score;
					row.y = upperCaseAxis.indexOf(row[axisName].toUpperCase());
					points.push(row);
				});
				createScatterPlot( 'Gene vs Score', 'Genes', axis, [{name:'tm-scatter',data:points}]);
			} else if( res.count > 1000 && res.initialCall ){
				// Too much data first time
				var notAvailableText = _.template($("script.datasets-too-many-data-points").html());
				$("#results-table-tab").html('<tr><th>'+notAvailableText()+'</th></tr>');
				$("#graph-view-tab").html('<p><b>'+notAvailableText()+'</b></p>');
				$('#experiment-content').unblock();
			} else if( res.count > 1000 ){
				// Too much data, slider initiated
				$('.num-of-results').text(res.count);
				$('#experiment-content').unblock();
				handleErrors(3);
			} else {
				var notAvailableText = _.template($("script.datasets-no-data").html());
				$("#results-table-tab").html('<tr><th>'+notAvailableText()+'</th></tr>');
				$("#graph-view-tab").html('<p><b>'+notAvailableText()+'</b></p>');
				$('#experiment-content').unblock();
			}
		},
        error:function( request, status, error ) {
            $('#datasets-fc-table').DataTable().destroy();
            $("#datasets-fc-table-body").html(_.template($("script.402-error" ).html())());
            $('#datasets-fc-table-wrap').unblock();
        }
	});
}

var MAXFC = 1001;
var getDataSetsByExperimentId = function( opts, cb ){
	if(!$('#expId').val()) { // Not a TM dataset page.
		return;
	}
	var query = {experiment_id:$('#expId').val(), foldChange:10};
	var requestUri = '/rest/omics/transcriptomics/datapoints/findDataPointsByExpId';

	if( opts && opts.foldChangeMin && opts.foldChangeMax ) {
		query.foldChangeMin = opts.foldChangeMin;
		query.foldChangeMax = opts.foldChangeMax
	}

	$('#datasets-fc-table').hide();
	blockUI("#experiment-content", 'We are pulling information from multiple tables to display the requested data points…please stand by.');
	// $('#datasets-fc-table').DataTable().destroy();
	var template = _.template($("#datasets-fc-table-row").html());
	$.ajax({
		type: "GET",
        headers: {'Content-Type':'application/json'},
		url: url+requestUri,
		data: query,
		success: function( res ){
			var html = "";
			res.initialCall = true;
			if( opts && opts.foldChangeMin && opts.foldChangeMax ) {
				res.initialCall = false;
			}
			if(res.results && res.results.length > 0) {
				res.tm = false;
				res.results = filterBadData( 'symbolSynonym', res.results );
				enableSlider(res);
				$('#currently-displaying').text(res.count);
				$('#display-total').text(res.count);
				_.each(JSON.parse(JSON.stringify(filterBadData('symbolSynonym', res.results))), function(row){
					row.foldChange = row.foldChange.toFixed(3);
					if( row.pValue ) {
						row.pValue = parseFloat(row.pValue);
						row.pValue = (row.pValue !== 0 ? row.pValue.toExponential(2) : '<1xE-10').toUpperCase();
					} else if( row.pValue == 0 ) {
						row.pValue = "<1E-10";
					} else {
						row.pValue = 'N/A';
					}
					if( row.metabolites && row.metabolites.length > 0 ) {
						row.symbolSynonym = "";
						_.each(row.metabolites, function(thisMetabolite){
							if( row.symbolSynonym.length > 0 ){
								row.symbolSynonym += ', ';
							}
							if( thisMetabolite.name != 'null' ) {
								row.symbolSynonym += thisMetabolite.name;
							}
						});
					}
					if( row.symbolSynonym && row.symbolSynonym.length > 0 ){
						html += template( row );	
					}
				});
				$("#datasets-fc-table-body").html(html);
				$('#datasets-fc-table').DataTable({
					"order": [[ 1, "desc" ]],
					destroy: true
				});
				$('#datasets-fc-table-wrap').unblock();
				$('#datasets-fc-table').show();
				
				$('#experiment-content').unblock();
				upDownFilter();
			} else if( res.count > 1000 && res.initialCall ){
				// Too much data first time
				var notAvailableText = _.template($("script.datasets-too-many-data-points").html());
				$("#results-table-tab").html('<tr><th>'+notAvailableText()+'</th></tr>');
				$("#graph-view-tab").html('<p><b>'+notAvailableText()+'</b></p>');
				$('#experiment-content').unblock();
			} else if( res.count > 1000 ){
				// Too much data, slider initiated
				$('.num-of-results').text(res.count);
				$('#experiment-content').unblock();
				handleErrors(3);
			} else {
				var notAvailableText = _.template($("script.datasets-no-data").html());
				$("#results-table-tab").html('<tr><th>'+notAvailableText()+'</th></tr>');
				$("#graph-view-tab").html('<p><b>'+notAvailableText()+'</b></p>');
				$('#experiment-content').unblock();
			}
			if(cb)cb();
		},
        error:function( request, status, error ) {
            $('#datasets-fc-table').DataTable().destroy();
            $("#datasets-fc-table-body").html(_.template($("script.402-error" ).html())());
            $('#datasets-fc-table-wrap').unblock();
            if(cb)cb();
        }
	});
};

// This method receives row data, filters out bad data and return clean data
var filterBadData = function( axisName, dataIn ) {
	var tmpCleanData = [];
	_.each(dataIn, function(row) {
		row[axisName] = $.trim(row[axisName]); // There are some names with spaces at the end, we need to get rid of them.
		// FC == 0 should not exist but in case, we need to remove them
		if( (row.foldChange !== 0) && ((row.metabolites && row.metabolites.length > 0 ) || (row.symbolSynonym && row.symbolSynonym.length > 0)) ) {
			tmpCleanData.push(row);
		}
	});
	return tmpCleanData;
};

// Format the data for display purpose
var formatData = function( axisName, dataIn ) {
	var tmpFormattedData = [];
	_.each(dataIn, function(row) {
		row[axisName] = $.trim(row[axisName]); // There are some names with spaces at the end, we need to get rid of them.
		
		var point = {
		    symbolUrl: row.symbolUrl,
			symbol: row.symbol,
			tissueName: row.tissueName,
			foldChange: row.foldChange,
			fcid:row.id,
			speciesCommonName: row.speciesCommonName,
			pValue: (row.pValue ? (row.pValue != 0 ? row.pValue.toExponential(2) : '<1xE-10').toUpperCase() : 'N/A'),
			experimentName: (row.experimentName || 'Not Available')
		};

		// Set the point color 
		if(row.foldChange > 0) {
			point.marker = {
				fillColor:'#df5353',
				states: {
                    hover: {
                        fillColor: '#df5353'
                    }
                }
			};
		}
		// This is only for TM and Metobolite gene lists
		if( row.metabolites && row.metabolites.length > 0 ) {
			row.symbolSynonym = "";
			_.each(row.metabolites, function(thisMetabolite){
				if( row.symbolSynonym.length > 0 ){
					row.symbolSynonym += ', ';
				}
				if( thisMetabolite.name != 'null' ) {
					row.symbolSynonym += thisMetabolite.name;
				}
			});
			tmpFormattedData.push(row);
		} else if( row.symbolSynonym && row.symbolSynonym.length > 0 ) {
			tmpFormattedData.push(row);
		}
	});
	return tmpFormattedData;
};

// Sort data by axis name
var sortData = function( axisName, dataIn ) {
	dataIn.sort(function(x, y){
	   // return d3.descending(x[axisName], y[axisName]);
	   return d3.ascending(x[axisName], y[axisName]);
	});
	return dataIn;
};

// 
var createPoints = function ( axisName, dataIn ) {
	var axis = [];
	var upperCaseAxis = [];
	var points = [];

	// Overriding the method to sort data by FC 
	// _.each(sortData( axisName, dataIn), function(row) {
	_.each(sortData( 'foldChange', dataIn), function(row) {
		if( upperCaseAxis.indexOf(row[axisName].toUpperCase()) === -1 ) {
			upperCaseAxis.push(row[axisName].toUpperCase());
			axis.push(row[axisName]);
		}
		if(row.foldChange > 0) {
			row.marker = {
				fillColor:'#df5353',
				states: {
                    hover: {
                        fillColor: '#df5353'
                    }
                }
			}
		}
		row.x = row.foldChange;
		row.y = upperCaseAxis.indexOf(row[axisName].toUpperCase());
		points.push(row);
	});
	return {'axis':axis, 'series':[{name:'tm-scatter',data:points}]};
};

var getDatasets = function() {
	$.ajax({
		type: "GET",
        headers: {'Content-Type':'application/json'},
		url: url+'/rest/dataset/getAll',
		success: function( res ){
			if(res) {
				$('#num-data-sets').text(res.length);
				addFilters( res );
				createTable( res );
				var selectedType = getParameterByName('datasetType');
				if( selectedType ) {
					$('input[value="'+selectedType+'"').trigger('click');
				}
				$('#datasets').unblock();
			}
		},
		error:function( request, status, error ) {
            $('#datasetsTable').DataTable().destroy();
            $("#datasetsTableBody").html(_.template($("script.402-error" ).html())());
            $('#datasets').unblock();
        }
    });
};

var addFilters = function( data ){
	var monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
	_.each(data, function(item, idx){
		if(data[idx].pathways) {
			data[idx].pathways = data[idx].pathways.split(',');
		} else {
			data[idx].pathways = [];
		}
		if(data[idx].speciesIds) {
			data[idx].speciesIds = data[idx].speciesIds.split(',');
		} else {
			data[idx].speciesIds = [];
		}
		var myDate = new Date(Date.parse(data[idx].releaseDate));
	    var theMonth = myDate.getMonth();
	    var theDay = myDate.getDate();
	    var theYear = myDate.getFullYear();

	 	data[idx].sorbyDate = myDate.getTime();
	    data[idx].releaseDate = monthNames[theMonth] + ' '+theDay + ', ' + theYear;
	});

	var dataByType = _.groupBy(data, 'type');

	$('input[name="species-filter"], input[name="type-filter"], #pathway-group select, #tissue-cell-line, .tissue-cell-line-sub select').on('change', function(){
		
		var filteredType = [];
		var filteredPathway = []; 
		var filteredSpecies = [];
		var filteredBiosource = [];
		var filteredSubBiosource = [];
		var dataToDisplay = [];

		$('#datasetsTable').DataTable().destroy();
		blockUI("#datasets");
		
		// Type filter
		if( $('input[name="type-filter"]:checked').length > 0 ) {
			$('input[name="type-filter"]:checked').each(function(){
				if( dataByType[$(this).val()] ){
					filteredType = filteredType.concat(dataByType[$(this).val()]);
				}
			});
		} else {
			filteredType = data;
		}
		dataToDisplay = filteredType;
		
		// Filter the pathway
		var tmpData;
		if( $('#pathway-modules').val() && $('#pathway-modules').val() !== 'all') {
			var pathway = $('#pathway-target').val();
			tmpData = _.filter(filteredType, function(item) {
				var foundIt = false;
				if( _.indexOf(item.pathways, pathway) > -1 ){
					foundIt = true;
				}
				return foundIt;
			});
			filteredPathway = tmpData;
		} else {
			filteredPathway = filteredType;
		}
		dataToDisplay = filteredPathway;

		// Filter the biosource main cat
		var tmpBiosource;
		if( $('#tissue-cell-line').val() !== 'all' ) {
			var tissueId = $('#tissue-category-target').val();
			tmpBiosource = _.filter(filteredPathway, function(item) {
				var foundIt = false;
				if( item.bioSamples.indexOf(tissueId) > -1 ) {
					foundIt = true;
				} 
				return foundIt;
			});
			filteredBiosource = tmpBiosource;
		} else {
			filteredBiosource = filteredPathway;
		}
		dataToDisplay = filteredBiosource;

		// Filter by species
		var tmpFilteredSpecies = [];
		if( $('input[name="species-filter"]:checked').length > 0 ) {
			$('input[name="species-filter"]:checked').each(function(){
				var speciesId = $(this).val();
				tmpFilteredSpecies = tmpFilteredSpecies.concat(_.filter(filteredBiosource, function(item) {
					var foundIt = false;
					if( item.speciesIds.indexOf(speciesId) > -1 ) {
						foundIt = true;
					} 
					return foundIt;
				}));
			});
			if( tmpFilteredSpecies.length > 0 ){
				filteredSpecies = tmpFilteredSpecies;
				dataToDisplay = tmpFilteredSpecies;
			}
		}

		createTable(_.uniq(dataToDisplay));
		$('#datasets').unblock();
	});
};

var createTable = function( data ) {
	var template = _.template($("script.datasetRow" ).html());
	var html = "";

	_.each(data, function(row){
		html += template( row );
	});

	$("#datasetsTableBody").html(html);
	$('#datasetsTable').DataTable({
		"destroy": true,
		"order": [[ 2, "desc" ]],
		"dom": '<"top"lifp<"clear">>rt<"bottom"lifp<"clear">>',
		"aoColumns": [
			{"bSortable": true},
			{"bSortable": true},
			{"iDataSort": 3},
			{"bVisible": false, bSearchable:false}
		],
		"oLanguage":{sInfoFiltered:" "}
	});
};