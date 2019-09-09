var pathArray = window.location.href.split( '/' );
var protocol = pathArray[0];
var host = pathArray[2];
var url = protocol + '//' + host;

var tooltip;
var getData = function( type, cb ){
	$.ajax( {url:url+"/rest/transcriptomine/graph/sunburst/"+type}).done(function( res ){
		cb( res.data );
	});
}

var getSummary = function(){
	$.ajax({"url":url+'/rest/transcriptomine/graph/summary'}).done(function(data){
		if( data && data.summary ) {
			data.summary.numberOfCoregulators = format_number(data.summary.numberOfCoregulators);
			data.summary.numberOfExperiments = format_number(data.summary.numberOfExperiments);
			data.summary.numberOfSpecies = format_number(data.summary.numberOfSpecies);
			data.summary.numberOfDataPoints = format_number(data.summary.numberOfDataPoints);
			data.summary.numberOfLigands = format_number(data.summary.numberOfLigands);
			data.summary.numberOfTissueAndCells = format_number(data.summary.numberOfTissueAndCells);
			data.summary.numberOfNuclearReceptors = format_number(data.summary.numberOfNuclearReceptors);
			var template = _.template($("script.dataSummary" ).html());
		    $(".summary").html(template( data.summary ));
		} 
	});
}

var blockUI = function( target ){
	$(target).block({
		message:'<div class="ui-blockui-content ui-widget ui-widget-content ui-corner-all ui-shadow">Loading content...<div class="ajaxSpinner"><div class="dot1"></div><div class="dot2"></div></div></div>',
		css:{width:"158px", height:"110px", "border-radius": "10px"}
	});
}

$(document).ready(function(){
	getSummary();
	setUpGraphs();
});

function setUpGraphs() {
	var $speciesGraph = d3.select('#sepeciesChart');
	var $molGraph = d3.select('#molChart');
	var $rnaGraph = d3.select('#rnaChart');

	var removeBlockUI = function(){
		if( gStatus.s && gStatus.m && gStatus.r ){
			$('#charts-carousel').unblock();
		}
	}; 

	tooltip = d3.select("body")
	    .append("div")
	    .attr("id", "tooltip")
	    .style("position", "absolute")
	    .style("z-index", "10")
	    .style("opacity", 0);

	blockUI("#charts-carousel")	;

	var gStatus = {s:false, m:false, r:false };
	getData('species', function(res){
		var species = {name:'species', children:res};
		$('#sepeciesChart').empty();
		createGraph($speciesGraph, species, function(){
			gStatus.s = true;
			removeBlockUI();
		});
	});
	getData('molecules', function(res){
		var molecules = {name:'molecules', children:res};
		$('#molChart').empty();
		createGraph($molGraph, molecules, function(){
			gStatus.m = true;
			removeBlockUI();
		});
	});
	getData('rna', function(res){
		var rna = {name:'RNA', children:res};
		$('#rnaChart').empty();
		createGraph($rnaGraph, rna, function(){
			gStatus.r = true;
			removeBlockUI();
		});
	});
}

var color = d3.scale.category20().range(["#b15928","#cab2d6","#ff7f00","#fdbf6f","#e31a1c","#fb9a99","#33a02c","#b2df8a",
											"#1f78b4","#a6cee3","#8dd3c7","#feb24c","#bebada","#fb8072","#80b1d3","#fdb462",
											"#b3de69","#fccde5","#d9d9d9","#bc80bd","#8c6bb1","#41ab5d","#addd8e","#78c679",
											"#41ab5d","#4292c6","#deebf7"]);

function createGraph( $div, root, gcb ) {

	root.size=0;
	for(var i=0;i<root.children.length;i++) {
		if(!root.children[i]){
			root.children[i] = [];
		}
		root.size+=root.children[i].length;
	}

	var margin = { top: 350, right: 550, bottom: 350, left: 550 };
	var radius = Math.min(margin.top, margin.right, margin.bottom, margin.left) - 10;

	var svg = $div.append("svg")
	    .attr("width", margin.left + margin.right)
	    .attr("height", margin.top + margin.bottom)
	  	.append("g")
	    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

	var partition = d3.layout.partition()
	    .sort(function(a, b) { return d3.descending(a.numberOfExperiments, b.numberOfExperiments); })
	    .size([2 * Math.PI, radius]);

	var arc = d3.svg.arc()
	    .startAngle(function(d) { return d.x; })
	    .endAngle(function(d) { return d.x + d.dx - .01 / (d.depth + .5); })
	    .innerRadius(function(d) { return radius / 3 * d.depth; })
	    .outerRadius(function(d) { return radius / 3 * (d.depth + 1.5) - 1; });

	f();

	var level=0;
	var synonyms;
	function f() { 
		// Compute the initial layout on the entire tree to sum sizes.
		// Also compute the full name and fill color for each node,
		// and stash the children so they can be restored as we descend.
		partition
			.value(function(d) { return d.numberOfExperiments; })
			.nodes(root)
			.forEach(function(d) {
				d._children = d.children;
				d.sum = d.value;
        		synonyms = d.name.split(',');
        		d.shortName = synonyms.pop();
				d.key = key(d);
			});

		// Now redefine the value function to use the previously-computed sum.
		partition
			.children(function(d, depth) { return depth < 1 ? d._children : null; })
			.value(function(d) { return d.numberOfExperiments; });

		var center = svg.append("circle")
			.attr("r", radius / 3)
			.style('fill', 'white')
			.on("click", zoomOut);
	 
	    /* Create the text for each block */
	    svg.append("text")
		    .attr("dx", -80)
		    .attr("y", -10)
		    .attr('class','center-text')
		    .text("Click on a category to")
		    .on("click", zoomOut);

		svg.append("text")
		    .attr("dx", -100)
		    .attr("y", 10)
		    .attr('class','center-text')
		    .text("drill down. Click in the center")
		    .on("click", zoomOut);

		svg.append("text")
		    .attr("dx", -85)
		    .attr("y", 30)
		    .attr('class','center-text')
		    .text("to go back up one level")
		    .on("click", zoomOut);

		// End:Add the text to the center of the graph

		center.append("title")
			.text("Zoom Out");

		var path = svg.selectAll("g")
			.data(partition.nodes(root).slice(1))
			.enter().append("path")
			.attr("d", arc)
			.style("fill", function(d) { d.color = color(d.name); return d.color; })
			.each(function(d) { this._current = updateArc(d); })
			.on("click", zoomIn)
			.on("mouseover", mouseOverArc)
			.on("mousemove", mouseMoveArc)
			.on("mouseout", mouseOutArc);

		var g = svg.selectAll("g")
			.data(partition.nodes(root).slice(1))
			.enter().append("g");

		var text = g.append("text")
			.attr("transform", function(d) { return "rotate(" + computeTextRotation(d) + ")"; })
			.attr("x", function(d) { return (d.y); })
		    .attr("dx", "-50") // margin
		    .attr("dy", ".35em") // vertical-align
			.text(function(d) {return d.shortName; })
			.on("click", zoomIn);

		svg.selectAll("text")
			.style("display", 'none')
			.filter(filter_min_arc_size_text)
			.transition().delay(750)
			.style("display", "inline-block");

		function computeTextRotation(d) {
			return ((d.x + d.dx / 2) - Math.PI / 2) / Math.PI * 180;
		}
		  
		function zoomIn(p) {
			if(!p._children) return; // We do not need to display the end lief.
			g.attr("opacity", 0);
			if (p.depth > 1) p = p.parent;
			zoom(p, p);
		}

		function zoomOut(p) {
			if(!p || !p.parent) return; 
			g.attr("opacity", 0); 
			zoom(p.parent, p);
		}

		function filter_min_arc_size_text(d, i) {
			if(d && d.dx && d.depth){
				return (d.dx*d.depth*radius/3)>14
			} else {
				return true;
			}
		};

		// Zoom to the specified new root.
		function zoom(root, p) {
		    if (document.documentElement.__transition__) return;
		    // Rescale outside angles to match the new layout.
		    var enterArc,
		        exitArc,
		        outsideAngle = d3.scale.linear().domain([0, 2 * Math.PI]);

		    function insideArc(d) {
		      return p.key > d.key
		          ? {depth: d.depth - 1, x: 0, dx: 0} : p.key < d.key
		          ? {depth: d.depth - 1, x: 2 * Math.PI, dx: 0}
		          : {depth: 0, x: 0, dx: 2 * Math.PI};
		    }

		    function outsideArc(d) {
		      return {depth: d.depth + 1, x: outsideAngle(d.x), dx: outsideAngle(d.x + d.dx) - outsideAngle(d.x)};
		    }

		    center.datum(root);
		    // When zooming in, arcs enter from the outside and exit to the inside.
		    // Entering outside arcs start from the old layout.
		    if (root === p) { enterArc = outsideArc, exitArc = insideArc, outsideAngle.range([p.x, p.x + p.dx]); }
		    path = path.data(partition.nodes(root).slice(1), function(d) { return d.key; });
			g = g.data(partition.nodes(root).slice(1), function(d) { return d.key; });
		    // When zooming out, arcs enter from the inside and exit to the outside.
		    // Exiting outside arcs transition to the new layout.
		    if (root !== p) enterArc = insideArc, exitArc = outsideArc, outsideAngle.range([p.x, p.x + p.dx]);
		    d3.transition().duration(d3.event.altKey ? 7500 : 750).each(function() {
		    	
		    	path.exit().transition()
					.style("fill-opacity", function(d) { return d.depth === 1 + (root === p) ? 1 : 0; })
					.attrTween("d", function(d) { return arcTween.call(this, exitArc(d)); })
					.remove();

		      	path.enter().append("path")
					.style("fill-opacity", function(d) { return d.depth === 2 - (root === p) ? 1 : 0; })
					.style("fill", function(d) { d.color = color(d.name); return d.color; })
					.on("click", zoomIn)
					.on("mouseover", mouseOverArc)
					.on("mousemove", mouseMoveArc)
					.on("mouseout", mouseOutArc)
					.each(function(d) { this._current = enterArc(d); });
			
				g.enter().append("text")
					.attr("transform", function(d) { return "rotate(" + computeTextRotation(d) + ")"; })
					.attr("x", function(d) { return (d.y); })
					.attr("dx", "-50") // margin
					.attr("dy", ".35em") // vertical-align
			      	.attr("font-family", "sans-serif")
					.text(function(d) { return d.shortName; });

				svg.selectAll("text")
					.style("display", 'none')
					.filter(filter_min_arc_size_text)
					.transition().delay(750)
					.style("display", "inline-block");
			
		      	path.transition()
					.style("fill-opacity", 1)
					.attrTween("d", function(d) { return arcTween.call(this, updateArc(d)); });
		    });
		}
	}

	function key(d) {
		var k = [], p = d;
		while (p.depth) k.push(p.name), p = p.parent;
		return k.reverse().join(".");
	}

	function arcTween(b) {
		var i = d3.interpolate(this._current, b);
		this._current = i(0);
		return function(t) {
			return arc(i(t));
		};
	}

	function updateArc(d) {
		return {depth: d.depth, x: d.x, dx: d.dx};
	}

	d3.select(self.frameElement).style("height", margin.top + margin.bottom + "px");
	gcb();
};

function mouseOverArc(d) {
	d3.select(this).style({"stroke":d3.rgb(d.color).darker(), 'stroke-width': '3px'});
	tooltip.html(format_description(d));
	return tooltip.transition().duration(50).style("opacity", 0.9);
}

function mouseOutArc(d){
	d3.select(this).style({"stroke":'', 'stroke-width': '0', fill:d.color})
	return tooltip.transition().duration(50).style("opacity", 0);
}

function mouseMoveArc (d) {
	return tooltip
		.style("top", (d3.event.pageY-10)+"px")
		.style("left", (d3.event.pageX+10)+"px");
}

function format_description(d) {
	return  '<b>' + 
			(d.name?d.name:"No Name") + '</b>'+ 
			' <p>Number of Experiments: ' + format_number(d.numberOfExperiments) +
			' <br> Number of Data Points: ' + format_number(d.numberOfDataPoints)+"</p>";
}

function format_number(x) {
	return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

var tooltip = d3.select("body")
	    .append("div")
	    .attr("id", "tooltip")
	    .style("position", "absolute")
	    .style("z-index", "10")
	    .style("opacity", 0);