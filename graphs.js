
queue()
    .defer(d3.json, "add1")
    .await(makeGraphs);

function makeGraphs(error, projectsJson) {

    var data = projectsJson;	
	
	var ndx = crossfilter(projectsJson);
    
	var resourceTypeDim = ndx.dimension(function (d) {
        return d["country"];
    });

	var numProjectByResourceType = resourceTypeDim.group();
	
    var resourceTypeChart = dc.rowChart("#resource-type-row-chart");
   
    resourceTypeChart
        .width(900)
        .height(900)
        .dimension(resourceTypeDim)
        .group(numProjectByResourceType)
        .xAxis().ticks(6);
        //.append('text')
        //.yAxis().ticks(20);


    var fundingStatus = ndx.dimension(function (d) {
        return d["score"];
    });

    var numProjectByFundingStatus = fundingStatus.group();

    var fundingStatusChart = dc.pieChart("#funding-chart");


	fundingStatusChart
        .height(250)
        .radius(100)
        .innerRadius(40)
        .transitionDuration(1500)
        .dimension(fundingStatus)
        .group(numProjectByFundingStatus);
        

    dc.renderAll();

}