
var width = 1000,
    height = 750;

var projection = d3.geo.mercator()
  .center([0, 5])
  .scale(175);

/*var zoom = d3.behavior.zoom()
  .scaleExtent([1, 20])
  .on("zoom", zoomed);

  
function zoomed(){
  g.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
}
*/
d3.select("body")
  .append("h1")
  .style("font", "32px sans-serif")
  .style("padding", "16px 0")
  .style("text-align", "center")

var svg = d3.select("body").append("svg")
  .attr("width", width)
  .attr("height", height)
  .style("display", "block")
  .style("margin", "0 auto");

  

// Tooltip
var div = d3.select('body').append("div")
  .style('position', 'absolute')
  .style('text-align', 'center')
  .style('padding', '2px')
  .style('font', '12px sans-serif')
  .style('background', 'lightgrey')
  .style('border', '0px')
  .style('border-radius', '8px')
  .style('pointer-events', 'none')
  .style("opacity", 1);

var path = d3.geo.path()
  .projection(projection);

var g = svg.append("g");

// load and display the World
d3.json("https://gist.githubusercontent.com/mbostock/4090846/raw/d534aba169207548a8a3d670c9c2cc719ff05c47/world-50m.json", function(error, data) {
  g.selectAll("path")
    .data(topojson.feature(data, data.objects.countries).features)
  .enter()
    .append("path")
    .attr("d", path)

    //console.log(path)
  
  d3.json("add1", function(data){
    
    //data.features = data.features.filter(d => d.geometry ? true : false);
    
    var scale = d3.scale.linear()
      .domain([d3.min(data, d => + 15),d3.max(data, d => + 15)])
      .range([2, 50])
    
    var circles = svg.append('g').selectAll(".pin")
      .data(data)
      .enter().append("circle", ".pin")
      .attr("r", 6)
      .attr("fill", "red")
      .attr("opacity", "0.5")
      .attr("transform", function(d) {
        return "translate(" + projection([
          d.lon,
          d.lat
        ]) + ")";
      })
      .on("mouseover", function(d) {    
        div.transition()    
          .duration(200)    
          .style("opacity", .9);    
        div.html(d.country)
          //.style("left", (d3.event.pageX) + "px")   
          //.style("top", (d3.event.pageY - 28) + "px");  
      })
      .on("mouseout", function(d) {   
        div.transition()    
          .duration(500)    
          .style("opacity", 0);
      });
   /* var zoom = d3.behavior.zoom()
        .on("zoom",function() {
            g.attr("transform","translate("+ 
                d3.event.translate.join(",")+")scale("+d3.event.scale+")");
            g.selectAll("path")
                .attr("d", path.projection(projection));
      });
    svg.call(zoom)*/
      
  })
});


