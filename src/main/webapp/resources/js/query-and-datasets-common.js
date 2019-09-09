"use strict";
/**
 *
 * SPP JavaScript Common functions
 * 
 * Wasula Kankanamge
 * 
 */

var options;
var rowData;
var up = true;
var down = true;

var pathArray = window.location.href.split( '/' );
var url = pathArray[0] + '//' + pathArray[2];
var global_pathway = {};
_.templateSettings = {
  evaluate : /\{\[([\s\S]+?)\]\}/g,
  interpolate : /\{\{([\s\S]+?)\}\}/g,
  variable:"rc"
};

String.prototype.capitalize = function() {
    return this.replace(/(?:^|\s)\S/g, function(a) { return a.toUpperCase(); });
};

var displayErrors = function( res, maxResults,downUrlPath, $div ){

	if( res.count > 66000 ){
		$div.html('<p>Your query will return <b>'+res.count+'</b> data points, which exceeds the cap on the number that can be currently browsed in Cistromics ('+maxResults+') or which can be downloaded in Excel (approx. 66,000).</p>');
		$div.append('<p>To reduce the number of data points, please increase the stringency of the significance cut-off from its default setting (5E-02), or narrow your search to a specific biosample category (e.g. Metabolic…Liver).</p>');
		$div.append('<p>If you still require assistance with obtaining a large set of results, e-mail us at <a href="mailto:support@signalingpathways.org">support@signalingpathways.org</a></p>')
	} else if( res.count > maxResults ){
		$div.html('<p>There are <b>'+res.count+'</span></b> data points for the selected values. Currently there is a '+maxResults+' point limit to the graph.</p>');
		$div.append('<p>To reduce the number of data points, please increase the stringency of the significance cut-off from its default setting (5E-02), or narrow your search to a specific biosample category (e.g. Metabolic…Liver).</p>');
		$div.append('<p>Alternatively the data points can be downloaded in Excel. <a href="'+url+downUrlPath+res.queryForm.id+'">Download Results</a></p>')
		$div.append('<p>If you still require assistance with obtaining a large set of results, e-mail us at <a href="mailto:support@signalingpathways.org">support@signalingpathways.org</a></p>')	
	}
}

// Tooltip listner
var enableToolTip = function() {
	$('[data-toggle="tooltip"]').tooltip({container:'body'});
	$('#close-graph-more-info').on('click', function(){
		$('#graph-more-info').on('click', function(){
			return false;
		})
		$('#graph-more-info').fadeOut();
	});
}

// Formatting
var format_number = function (x) {
	return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ", ");
}

// Credit: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/round
var precisionRound =  function(number, precision) {
	var factor = Math.pow(10, precision);
	return Math.round(number * factor) / factor;
}

var getParameterByName = function( name ) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

var pointToolTip = function( point, pageOrigin ) {
	var $templateDiv = $("script.graphFoldChangeInformation" ).html(); 
	if( point.score ){ // Cistromics Query Results
		$templateDiv = $("script.cistromicsMoreInfo" ).html();
		$('#graph-more-info').addClass('cistromics-tooltip');
	} else if( point.foldChange ) {
		point.foldChange = precisionRound(point.foldChange, 3); 
		$('#graph-more-info').removeClass('cistromics-tooltip');
	} else {
		console.log('No FC or Score available');
	}

	if( point.dataType == 'topCistromics' ){ // Dataset detail page
		$templateDiv = $("script.cistromicsTopMoreInfo" ).html()
	} 

	if( point.pvalue ) {
		point.pvalue = parseFloat(point.pvalue);
		point.pvalue = (point.pvalue != 0 ? point.pvalue.toExponential(2) : '<1xE-10').toUpperCase();
	} else {
		point.pvalue = 'N/A';
	}

	var template = _.template($templateDiv);
	$('#graph-more-info-content').html(template( point ));
	$('#graph-more-info').css({'top':pageOrigin.y-($('#graph-more-info').height()), 'left':pageOrigin.x-($('#graph-more-info').width()/2)-2});
	$('#graph-more-info').fadeIn();
	// BSMS Modal listener
	$('.show-bsms').on('click', function(){
		$('#bsm-details').modal('show');
	});
	$('#graph-more-info').on('click', '.fc-details', function(){
		$('#fc-details').modal('show');
	})
};

var createPathwaysDropDown = function(node, $dropdown, $div, level) {
		
    if($.isArray(node)){
    	node = _.chain(node).sortBy('name').sortBy('displayOrder').value();
        $.each(node, function (key, value) {
        	//Apollo 11.20.2018 add Models
        	if ((value.id ==100 || value.id ==300 || value.id ==400 || value.id ==700 || value.id== 1101) || level!=1)
        		createPathwaysDropDown(value, $dropdown, $div, 2);
        });
        return;
    }
    
    if (node) {
        if (node.name) {
            $dropdown.append($('<option ptype="'+node.type+'" value="'+node.id+'" tname="'+node.id+'">'+node.name+'</option>'));
        }
        if (node.pathways && node.pathways.length) {
        	var allNameSetup = '';
        	if (node.type == 'category')
        		allNameSetup = 'classes';
        	else if  (node.type != 'category' && node.type != 'type')
        		allNameSetup = 'families';
			var $subcat = 
				$('<div pid="'+node.id+'" ptype="'+node.type+'" id="pathway-sub-'+node.id+
						'" class="pathway-sub pathway-sub-'+node.id+'"><select class="form-control" id="'+'select-' 
						+node.type+node.id+'" ><option tname="all" ptype="'+node.type+'" value="'+node.id+'">All '+
						allNameSetup+'</option></select></div>');
		
            createPathwaysDropDown(node.pathways, $subcat.find('select'), $subcat, 2);
            $div.append($subcat);
        }
    }
}

var filterData = function() {
	options.truncatedData = [];
	if( up && !down ) {
		_.each(options.results, function(row){
			if( row.foldChange >= options.selectedMin && row.foldChange <= options.selectedMax ) {
				options.truncatedData.push(row);
			}
		});
	} else if( down && !up ) {
		_.each(options.results, function(row){
			if( row.foldChange < 0 && Math.abs(row.foldChange) >= options.selectedMin && Math.abs(row.foldChange) <= options.selectedMax ) {
				options.truncatedData.push(row);
			}
		});
	} else if( up && down ) {
		_.each(options.results, function(row){
			if( Math.abs(row.foldChange) >= options.selectedMin && Math.abs(row.foldChange) <= options.selectedMax ) {
				options.truncatedData.push(row);
			}
		});
	}
}

var updateScatterPlot = function() {
	
	filterData();

	if( options.tm === false ) {
		var graphOptions = createPoints( 'symbolSynonym', formatData( 'symbolSynonym', JSON.parse( JSON.stringify(options.truncatedData) ) ));
		createScatterPlot( 'Gene vs Fold Change', 'Genes', graphOptions.axis, graphOptions.series );

	} else {
		prepareScatterPlot(JSON.parse( JSON.stringify(options.truncatedData)));
	}
	$('#currently-displaying').text(options.truncatedData.length);
}
var sliderTour = function( tourLabel ) {
	// Define the tour!
	if(hopscotch.getCurrTour())hopscotch.endTour();
    var tour = {
		id: "slider-tour",
		bubbleWidth: 600,
		steps: [{
			title:"Tip",
			content: "To prevent very large fold changes from distorting the Regulation Report, the default upper fold change cut-off is ≤30. Slide this to a higher cut-off to see other data points that might be of interest. Slide the lower fold change cutoff to 1 to see genes that don’t quite make the default cut-off of ≥2.",
			target: "ui-slider-wrap",
			placement: "top",
			xOffset:'center',
			arrowOffset: 'center'
		}],
		onEnd:function(){
			setCookie(tourLabel, tourLabel);
		},
		onClose:function(){
			setCookie(tourLabel, tourLabel);
		}
    };

    // Initialize tour if it's the user's first time
	if (!getCookie(tourLabel)) {
		hopscotch.startTour(tour);
	}
}
var enableSlider = function( opts, cb ) {
	options = opts;
	options.truncatedData = [];
	var range_all_sliders = {};
	var maxCutOff = 30;
options.tm= true;
	if( options.tm === true ) {
		options.max = Math.ceil(options.queryForm.largestFoldChange); 
		options.selectedMinOriginal = parseInt(options.queryForm.queryParameter.foldChangeMin);
		options.selectedMaxOriginal = parseInt(options.queryForm.queryParameter.foldChangeMax);

		if( options.max < maxCutOff ){
			options.max = 31;
			options.selectedMaxOriginal = 31;
		}

		range_all_sliders = {
			'min': [ 1, .5 ],
			'20%': [ 2,  28 ],
			'50%': [ maxCutOff, options.max-maxCutOff],
			'max': [ options.max ]
		};

		options.sliderSelectOptions = [1, 2, maxCutOff];
		options.sliderSelectOptions.push(options.max);

		options.selectedMin = options.selectedMinOriginal;
		options.selectedMax = options.selectedMaxOriginal;

//		prepareScatterPlot(JSON.parse(JSON.stringify(options.results)));

	} else if( options.tm === false ){
		maxCutOff = 10;
		options.max = Math.ceil(options.queryForm.largestFoldChange);

		options.selectedMinOriginal = Math.abs(options.queryForm.minFoldChange);
		options.selectedMaxOriginal = Math.abs(options.queryForm.maxFoldChange);

		if( options.max < maxCutOff ){
			options.max = 11;
		}

		if( options.selectedMinOriginal < 2 ){
			options.selectedMinOriginal = 1;
		} else if( options.selectedMinOriginal < 10 ){
			options.selectedMinOriginal = 2;
		} else if( options.selectedMinOriginal >= 10  ){
			options.selectedMinOriginal = 10;
		} else { // Defaults to 1
			options.selectedMinOriginal = 1;
		}

		if( options.selectedMaxOriginal < 2 ){
			options.selectedMaxOriginal = 2;
		} else if( options.selectedMaxOriginal < maxCutOff ){
			options.selectedMaxOriginal = maxCutOff;
		} else {
			options.selectedMaxOriginal = options.max;
		}

		range_all_sliders = {
			'min': [ 1, .5 ],
			'20%': [ 2,  8 ],
			'50%': [ maxCutOff,  options.max-maxCutOff ],
			'max': [ options.max ]
		};
		
		options.sliderSelectOptions = [1, 2, maxCutOff];
		options.sliderSelectOptions.push(options.max);

		options.selectedMin = options.selectedMinOriginal;
		options.selectedMax = options.selectedMaxOriginal;

		var graphOptions = createPoints( 'symbolSynonym', formatData( 'symbolSynonym', JSON.parse(JSON.stringify(options.results) )) );
		createScatterPlot( 'Gene vs Fold Change', 'Genes', graphOptions.axis, graphOptions.series );
		upDownFilter();
	}
	

	$('#min-fold-change-text').text(options.selectedMinOriginal);

	var slider = document.getElementById('ui-slider');
	
	if( slider.noUiSlider ) {
		slider.noUiSlider.destroy();
	}

	noUiSlider.create(slider, {
		range: range_all_sliders,
		start: [options.selectedMinOriginal, options.selectedMaxOriginal],
		connect:true,
		pips: {
			mode: 'values',
			values: options.sliderSelectOptions,
			density: 100,
			stepped: true,
			format: {
				to: function ( value ) { 
					if( range_all_sliders.max[0] === value ){ 
						return '>'+maxCutOff
					}
					return value;
				}
			}
		}
	}, true);
	if( options.initialCall === false ){
		sliderListner();
	}
};

var sliderListner = function(slider) {
	var slider = document.getElementById('ui-slider');
	slider.noUiSlider.on('set', function(values, handle){
		var selValues = this.get();

		selValues[0] = parseFloat(selValues[0]); 
		selValues[1] = parseFloat(selValues[1]); 
		
		if( (options.selectedMin == selValues[ 0 ] && options.selectedMax == selValues[ 1 ]) || selValues[ 0 ] == selValues[ 1 ] || selValues[ 0 ] > selValues[ 1 ] ) {
			return;
		}

		if( options.tm === true ) {
			
			if( options.requestNewData || (selValues[ 0 ] < options.selectedMinOriginal) || (selValues[ 1 ] > options.selectedMaxOriginal) ) {
				$('#fold-change-min').val(selValues[ 0 ]);
				$('#fold-change-max').val(selValues[ 1 ]);
				$('#submit-query').trigger('click');
			} else {
				options.selectedMin = selValues[ 0 ];	
				options.selectedMax = selValues[ 1 ];
				blockUI('#graph-view');
  				updateScatterPlot();
				$('#graph-view').unblock();
			}

		} else {
			if( options.requestNewData || (selValues[ 0 ] < options.selectedMinOriginal) || (selValues[ 1 ] > options.selectedMaxOriginal) ) {
				var dcParamas = {foldChangeMin:selValues[ 0 ], foldChangeMax: selValues[ 1 ]}
				getDataSetsByExperimentId(dcParamas);
			} else {
				options.selectedMin = selValues[ 0 ];	
				options.selectedMax = selValues[ 1 ];
				blockUI('#graph-view');
  				updateScatterPlot();
  				$('#graph-view').unblock();	
			}
		}

	});
}

var lookUpPathways = function(){
	return $.ajax( url+'/rest/transcriptomine/lookup/allSignalingPathways').done(function( res ){
		global_pathway = res;
		for ( var i = 0; i < res.length; i++ ){
			if( res[i].id == -1000 && res[i].pathways ){
				createPathwaysDropDown(res[i].pathways, $('#pathway-modules'), $('#pathway-group'), 1);
			}
		}

		$('#pathway-group .pathway-sub').hide();
		$('#pathway-modules').on('change', function(){
			var module = $(this).val();
			$('#pathway-target').val(module);
			$('#pathway-target').attr('ptype',$(this).find(':selected').attr('ptype'));
			$('.pathway-sub').hide();
			$('#pathway-sub-'+module).fadeIn();
			$('#pathway-group .pathway-sub select').each(function(){
				$(this).val($(this).find('option:first').attr('value'));
			});
			// Update value print out
			$('#cistromics-pathway-cp').text($("#pathway-modules option:selected").text());
		});

		$('#pathway-group .pathway-sub select').on('change', function(){
			if( $(this).parent().find('.pathway-sub') ){
				$(this).parent().find('.pathway-sub').hide();
			}

			var curPathway = $(this).val();

			$('#pathway-target').val(curPathway);
			$('#pathway-target').attr('ptype', $(this).find(':selected').attr('ptype'));

			$('#pathway-sub-'+curPathway+' .pathway-sub').hide();
			$('#pathway-sub-'+curPathway).fadeIn();
			// Update value print out
			$('#cistromics-pathway-cp').text($(this).find(':selected').text());
		});
    });
}

var lookUpTissues = function(){
    return $.ajax( url+'/rest/transcriptomine/lookup/allTissuesCategories').done(function( res ){
    	var tissueCats = _.sortBy(res, 'name');
	    _.each( tissueCats, function( child ){ 
	    	createDropDown( child, 'tissue-cell-line' );
	    });
	    
	    $('.tissue-cell-line-sub').hide();
		
		$('#tissue-cell-line').on('change', function(){
		   var selectedCat = $(this).val();
		   $('.tissue-cell-line-sub').hide();
		   $('#tissue-cell-line-'+ selectedCat).parent().fadeIn();
		   $('#physiological-systems-target').val(selectedCat);
		   $('#physiological-systems-target').attr('displaytext', $(this).find(':selected').text());
		   $('#physiological-systems-target').change();
		});

		$('#tissue-cell-line-group .tissue-cell-line-sub select').on('change', function(){
			$('#organ-target').val($(this).val());
			$('#organ-target').attr('displaytext', $(this).find(':selected').text());
			$('#cistromics-biosample-cp').text(', '+$(this).find(':selected').text());
		});
    });
}

var createDropDown = function( cat, ele, childrenName ) {
	childrenName = (typeof childrenName !== 'undefined') ?  childrenName : 'children';
	createDropDownElement( cat, ele );
	if( cat[childrenName] && cat[childrenName].length > 0 ) {
		var template = _.template($("script."+ele+"-sub-template" ).html());
    	$('#'+ele+'-group').append(template({parent:cat.id}));
    	_.each(_.sortBy(cat[childrenName], 'name'), function( child ){
    		createDropDown( child, ele+'-'+cat.id, childrenName );
    	});
    }
}

var createDropDownElement = function( row, ele ) {
	var template = _.template($("script.tissues-and-cell-lines-option" ).html());
	if( $('#'+ele.length ) ){
		$('#'+ele).append(template( row ));	
	} else {
		console.log('#'+ele.length)
	}
}


// Block UI
var blockUI = function( target, message ){
	if( !message ){
		message = 'Please be patient while we gather some data from our database.';
	}
	$(target).block({
		message:'<div class="ui-blockui-content ui-widget ui-widget-content ui-corner-all ui-shadow">'+message+'<div class="ajaxSpinner"><div class="dot1"></div><div class="dot2"></div></div></div>',
		css:{"border-radius": "10px"}
	});
}
// End block UI
var scrollTo = function( hash ) {
	var target = $(hash);
    if (target.length) {
        var top = target.offset().top;
        $('html,body').animate({scrollTop: top}, 1000);
        return false;
    }
}
