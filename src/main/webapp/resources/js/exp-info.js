// When editing this file, remember to refer to following files
// transcriptomicOverview.xhtm
// cistromicOverview.xhtml
"use strict";
var pathArray = window.location.href.split( '/' );
var url = pathArray[0] + '//' + pathArray[2];
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
		getExpInfo('cistromics');
	} else {
		getExpInfo('transcriptomics');
	}
}

var getExpInfo = function( type ){
	// if( !$('#repoId').val()  ) return ;
	if( !$('#expId').val()  ) return ;
	blockUI('#graph-view-tab');
	var maxResults = 2000;
	var fullUrl = url;
	var dataParams = { experimentid : $('#expId').val()};
	var graphInfo = {};
	graphInfo.yaxisName = 'Gene';
	graphInfo.yAixs = 'gene';
	if( type == 'cistromics' ){
		fullUrl = fullUrl+'/rest/omics/cistromics/datapoints/top';
		graphInfo.xaxisName = 'Gene vs Binding score';
		graphInfo.xAixs = 'score';
		graphInfo.graphType = 'datasets-cistromics';
	} else if( type == 'transcriptomics'){
		graphInfo.graphType = 'datasets-tm'
		graphInfo.yAixs = 'symbol';
		graphInfo.xaxisName = 'Gene vs Fold Change';
		graphInfo.xAixs = 'foldChange';
		fullUrl = fullUrl+'/rest/omics/transcriptomics/datapoints/findDataPointsByExpId'
	}
	$.ajax({
		    data: {experimentid:$('#expId').val()},
		    url: fullUrl
		}).done(function( res ) {
			let results = [];
			
			if( res.results && res.results.length > 0 ){
				results = res.results;
			} else if( res.length > 0 ){
				results = res;
			}
		    // If there is data
		   if( results.length > 0 ) {
				if( results.length > 66000 || results.length > maxResults  ) {
					$('#datasets-scatter-plot').hide(); // Doube check here
					displayErrors({count:results.length}, maxResults,null, $('#datasets-scatter-plot-errors'));
				}
				else {
					var pointsByGenes = plotGraphByRowName(results, graphInfo.yAixs, graphInfo.xAixs);
					drawScatterPlot( graphInfo.xaxisName, 
										graphInfo.yaxisName, 
										pointsByGenes.axis, 
										[{"name":'datasets-scatter-plot',"data":pointsByGenes.series,"turboThreshold":5000}], 
										graphInfo.graphType
									);
					// Doube check here
					$('#datasets-scatter-plot').show();
					$('#datasets-scatter-plot-errors').empty();
					
				}
			} else {
				// No data points
				$('#datasets-scatter-plot').hide();
				$('#datasets-scatter-plot-errors').html('<p>Your query returned zero results</p>');
			}
			$('#graph-view-tab').unblock();
		}).fail(function(jqXHR, textStatus, errorThrown) {
		    // If fail
		    $('#graph-view-tab').unblock();
		    $('#datasets-scatter-plot-errors').html('<p>There was an error while processing your request. Please refresh the page and try again. If you continue to experience this please e-mail us at <a href="mailto:support@signalingpathways.org">support@signalingpathways.org</a></p>')
		    $('#datasets-scatter-plot').hide();
		});
}

var plotGraphByRowName = function ( points, rowName, xAxis ) {
	let axis=[];
	let graphedPoints = [];
	points = _.sortBy(points, xAxis);
	// points.reverse();
	_.each(points, function( cPt ){
		let indexed = _.findIndex( axis, function( pt ){
			return pt.uniqueYaxisLabel === cPt[rowName]
		});

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
			cPt.dataType = 'topTranscriptomic';
		} else {
			cPt.dataType = 'topCistromics';
			cPt.marker = { fillColor:'#df5353', states: {hover: {fillColor: '#df5353'}} };
		}

		graphedPoints.push(cPt);
	});
	// Returning the same object we received with modified data
	return {'axis':axis, 'series':graphedPoints};
}
var scatterPlots = {};
var drawScatterPlot = function( name, yAxisName, yAxisData, series, div ) {
	$('#searchResults').show();
	var height = 500;
	if( (yAxisData.length * 23) > height ) { 
		height = yAxisData.length * 23;
	}
	var typeGene;
	var targetDiv = 'scatter-plot';
	var yAxisTitle = 'Transcript Relative Abundance (Fold Change)';
	var blockDiv = '#tm-results';

	if( div == 'cistromics' ){ // Cistromics Title
		targetDiv = 'cistromics-scatter-plot';
		yAxisTitle = 'ChiP-Atlas MACS2 binding score(±10 kb from TSS)';
		blockDiv = '#cistromics-results';
		typeGene = 'cistromics';
	} else if( div == 'datasets-cistromics'){
		targetDiv = 'datasets-scatter-plot';
		yAxisTitle = 'ChiP-Atlas MACS2 binding score(±10 kb from TSS)';
		blockDiv = '#graph-view-tab';
		typeGene = 'cistromics';
	} else if( div == 'datasets-tm'){
		targetDiv = 'datasets-scatter-plot';
		blockDiv = '#graph-view-tab';
		typeGene = 'tm';
		
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
					// for test
//					thisLabel = thisLabel + "sdf   h98ew  4r340  394 34  njl";
					thisLabel = thisLabel.replace('##', " | ");
					var shortLabel = thisLabel.match(/\b\w+\b/g);
					var shortLabelD;
					var urlGene = url + "/ominer/query.jsf?geneSearchType=gene&findMax=y&gene="+thisLabel+
						"&foldChangeMin=2&foldChangeMax=30&significance=0.05&species=all&reportsBy=pathways&omicsCategory=" + typeGene;
					var urlLink;
					if ( shortLabel.length > 4) {
						shortLabelD = shortLabel[0]+' '+shortLabel[1]+' ... '+shortLabel[shortLabel.length-2]+" "+shortLabel[shortLabel.length-1];
						urlLink = '<a target="_blank" href="'+urlGene+'">'+shortLabelD+'</a>'; 
					}
					else {
						urlLink = '<a target="_blank" href="'+urlGene+'">'+thisLabel+'</a>';
					}
					
					
					if( label.level && label.level == 2 ) {
						if( shortLabel.length > 4){
							thisLabel = '<span style="color:#575754;" data-toggle="tooltip" data-placement="right" title="'+thisLabel+'">'+ urlLink + '</span>';
						} else {
							thisLabel = '<span style="color:#575754;">'+urlLink+'</span>';
						}
					} else if( label.level && label.level == 1 ) {
						if( shortLabel.length > 4){
							thisLabel = '<span style="color:#000000; font-size:15px" data-toggle="tooltip" data-placement="right" title="'+thisLabel+'">'+ urlLink + '</span>';
						} else {
							thisLabel = '<span style="color:#000000; font-size:15px">'+urlLink+'</span>';
						}
					} else {
						if( shortLabel.length > 4){
							thisLabel = '<span style="color:#aeaea8;" data-toggle="tooltip" data-placement="right" title="'+thisLabel+'">'+ urlLink + '</span>';
						} else {
							thisLabel = '<span style="color:#aeaea8;font-style: italic;">'+urlLink+'</span>';
						}
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
                            }
                            e.stopPropagation();
                            pointToolTip(this, pageOrigin);
                            if( this.bsms ){
                            	bsmInfo(this);
                            	$('.show-bsms').show();
                            } else {
                            	$('.show-bsms').hide();
                            }
                            if( this.fcid ){
                            	foldChangeDetails();
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
}
// End Create scatter plot

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
			var tissueId = $('#physiological-systems-target').val();
			tmpBiosource = _.filter(filteredPathway, function(item) {
				var foundIt = false;
				if( item.bioSamples.split(',').indexOf(tissueId) > -1 ) {
					foundIt = true;
				} 
				return foundIt;
			});
			filteredBiosource = tmpBiosource;
		} else {
			filteredBiosource = filteredPathway;
		}
		dataToDisplay = filteredBiosource;
		
		var tissueId = $('#physiological-systems-target').val();
		// Filter the organ main cat
		var orgBiosource;
		if (tissueId !== 'all') {
			if( $('#tissue-cell-line-'+ tissueId).val() !== 'all' && $('#tissue-cell-line-'+ tissueId).val() !== '') {
				var organId = $('#tissue-cell-line-' + tissueId).val();
				orgBiosource = _.filter(filteredBiosource, function(item) {
					var foundIt = false;
					if( item.bioSamples.split(',').indexOf(organId) > -1 ) {
						foundIt = true;
					} 
					return foundIt;
				});
				filteredSubBiosource = orgBiosource;
			} else {
				filteredSubBiosource = filteredBiosource;
			}
			dataToDisplay = filteredSubBiosource;
		}
		else
			filteredSubBiosource = filteredBiosource;
		
		// Filter by species
		var tmpFilteredSpecies = [];
		if( $('input[name="species-filter"]:checked').length > 0 ) {
			$('input[name="species-filter"]:checked').each(function(){
				var speciesId = $(this).val();
				tmpFilteredSpecies = tmpFilteredSpecies.concat(_.filter(filteredSubBiosource, function(item) {
					var foundIt = false;
					if( item.speciesIds.indexOf(speciesId) > -1 ) {
						foundIt = true;
					} 
					return foundIt;
				}));
			});
			//if( tmpFilteredSpecies.length > 0 ){
			filteredSpecies = tmpFilteredSpecies;
			dataToDisplay = tmpFilteredSpecies;
			//}
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
		"order": [[ 3, "desc" ]],
		"columnDefs": [{
			"className": 'dt-center',
			"targets": [2]
			}],
		"iDisplayLength": 50,
		"dom": '<"top"lifp<"clear">>rt<"bottom"lifp<"clear">>',
		"aoColumns": [
			{"bSortable": true, "sWidth": '10%' },
			{"bSortable": true, "sWidth": '60%'},
			{"bSortable": false, "sWidth": '15%'},
			{"iDataSort": 4, "sWidth": '15%'},
			{"bVisible": false, bSearchable:false}
		],
		"oLanguage":{sInfoFiltered:" ",
			"sInfo": "Showing _START_ to _END_ of _TOTAL_ datasets",}
	});
};