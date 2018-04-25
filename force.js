/*queue()
    .defer(d3.json, "add2")
    .await(makeGraphs);

    function makeGraphs(error, projectsJson) {

    var data = projectsJson;  
  
    var ndx = crossfilter(projectsJson);

    var resourceTypeDim = ndx.dimension(function (d) {
        //console.log(d['word'])

        return d["word"];
    });


    var numProjectByResourceType = resourceTypeDim.group();

    var resourceTypeChart = dc.rowChart("#resource-type-row-chart");


    resourceTypeChart
        .width(300)
        .height(250)
        .dimension(resourceTypeDim)
        .group(numProjectByResourceType)
        .xAxis().ticks(4);

    dc.renderAll();

     
  }*/













queue()
    .defer(d3.json, "add2")
    .await(makeGraphs);

    function makeGraphs(error, projectsJson) {

    data = projectsJson;   
    ndx = crossfilter(data);

    var resourceTypeDim = ndx.dimension(function (d) {
        return d["word"];
    });

  var numProjectByResourceType = resourceTypeDim.group();


  //console.log(data)

      w = 1200,
      h =  800,
      circleWidth = 30; 

    var colors = d3.scale.category20();



    var myChart = d3.select('svg')
            .attr("preserveAspectRatio", "xMinYMin meet")
            .attr("width", w)
            .attr("height", h)
            .classed("svg-content-responsive", true)


    var force = d3.layout.force()
          .nodes(data)
          .gravity(0.10)
          .charge(-500)
          .size([w,h]); 


          var node =  myChart.selectAll('circle')  
                .data(data).enter() 
                .append('g') 
                .call(force.drag); 

         
         node.append('circle')
                .attr('cx', function(d){return d.x; })
                .attr('cy', function(d){return d.y; })
                .attr('r', function(d){
                      console.log(d.word + d.count)
                            return circleWidth + (d.count/3); 
                    
                })
                .attr('fill', function(d,i){
                      return '#C8A2C8'
                })
                //.attr('strokewidth', '1')
                .attr('stroke', 'black');


          force.on('tick', function(e){ 
                node.attr('transform', function(d, i){
                  return 'translate(' + d.x + ','+ d.y + ')'
                })

          });


          node.append('text')
                .text(function(d){ return d.word + '\n' + d.count; })
                .attr('font-family', 'Raleway', 'Helvetica Neue, Helvetica')
                .attr('fill', 'lilac')
                .attr('text-anchor', function(d, i) {
                      return 'middle';
                })
                .attr('font-size', '1.2em') 

    force.start();
}







































/*
queue()
    .defer(d3.json, "add2")
    .await(makeGraphs);
    function makeGraphs(error, projectsJson) {

    data = projectsJson;   
    ndx = crossfilter(data);



    const canvasWidth = 910,
      canvasHeight = 500,
      padding = {top: 20, right: 20, bottom: 20, left: 20 },
      maxRadius = 70;

    const width = canvasWidth - padding.left - padding.right;
    const height = canvasHeight - padding.top - padding.bottom;

    var rScale = d3.scaleSqrt().range([0, maxRadius])

    const count = (topic) => topic.count
    const wordId = (topic) => topic.word
    const textContent = (topic) => topic.word.charAt(0).toUpperCase() + topic.word.slice(1)

    //console.log(textContent)

    function draw() {

          rScale.domain([0, xMax])

          d3.select('svg')
                  .selectAll('g.container')
                  .data([chartData])
                  .enter().append('g')
                  .attr('class', 'container')
                  .attr('transform', `translate( ${padding.left} , ${padding.top} )`);

          container = d3.select('g.container');

          // label = container.selectAll('')
          redraw();

      }

      function redraw() {

          force.nodes(chartData)

          node = container.selectAll('.node')
              .data(chartData, wordId)

          node.exit().remove()

          var nodeEnter = node.enter()
              .append('g')
              .attr('class', 'node')
              // .attr('xlink:href', (d) => `${ encodeURIComponent(wordId(d)) }`)
              .on('click', click)
              .on('mouseover', mouseover)
              .on('mouseout', mouseout)
              .call(d3.drag()
                  .on('start', dragstarted)
                  .on('drag', dragged)
                  .on('end', dragended));

          node = container.selectAll('.node')

          nodeEnter
              .append('circle')
              .attr('r', (d) => rScale(count(d)))

          nodeEnter.append('text')
              .attr('class', 'label')
              .text(textContent)
              .style('font-size', (d) => Math.max(8, rScale(count(d) / 2)) + 'px')
              .attr('transform', function(d) {
                  var w = ( this.getBBox ? this.getBBox() : this.getBoundingClientRect() ).width
                  return `translate( ${ -w/2 } , ${ rScale(count(d)) - Math.max(8, rScale(count(d)))/1.25 } )`
              })
              .style('width', (d) => 2.5 * rScale(count(d)) + 'px')

          label = container.selectAll('text.label')

          // label
              // .style('font-size', (d) => {
              //     console.log('selected', this)
              //     return Math.max(8, rScale(count(d) / 2))
              // })
              // .attr('transform', function(d) {
              //     var w = ( this.getBBox ? this.getBBox() : this.getBoundingClientRect() ).width
              //     return `translate( ${ -w/2 } , ${ rScale(count(d)) - Math.max(8, rScale(count(d)))/1.25 } )`
              // })
              // .style('width', (d) => 2.5 * rScale(count(d)))

          nodeEnter.append('text', 'count')
              .attr('class', 'count')
              .text(count)

          countLabel = container.selectAll('text.count')

          countLabel
              .style('font-size', 10)
              .attr('transform', function(d) {
                  var w = ( this.getBBox ? this.getBBox() : this.getBoundingClientRect() ).width
                  return `translate( ${ -w/2 } , ${ Math.max(8, rScale(count(d)))/1.25 } )`
              })

          }

        function dragstarted(d) {
            if (!d3.event.active) force.alphaTarget(0.3).restart();
            d.fx = d.x;
            d.fy = d.y;
        }

        function dragged(d) {
            d.fx = d3.event.x;
            d.fy = d3.event.y;
        }

        function dragended(d) {
            if (!d3.event.active) force.alphaTarget(0);
            d.fx = null;
            d.fy = null;
        }

        function click(d) {
            node.classed('active', (n) => wordId(n) == wordId(d) )
            Controller.displayData(d, data)
        }

        function mouseover(d) {
            node.classed('hover', (n) => wordId(n) == wordId(d))
        }

        function mouseout(d) {
            node.classed('hover', false)
        }

        function tick(e) {
            container.selectAll('.node')
                .attr('transform', (d) => `translate( ${ d.x }, ${ d.y } )`)
        }

        function topic(topic) {
            topic.count = 0;
            topic.mentions = [];

            data.articles.forEach(function(article, idx) {
                var text = article.text,
                    match,
                    localCount = 0;

                topic.re.lastIndex = 0;

                while (match = topic.re.exec(text)) {
                    ++topic.count;
                    ++localCount;
                }

                if(localCount > 0) {
                    topic.mentions.push({
                        title: article.title,
                        index: idx,
                        count: localCount
                    });
                }

            });

            return topic;
        }


        function addTopic(word) {

            var t = topic({ word: word, re: new RegExp("\\b(" + word.trim() + ")\\b", "gi")});

            var check = chartData.find(function(w) {
                return w.word.toLowerCase() === word.toLowerCase()
            })

            if(check) return click(check)

            t.x = width
            t.y = 0

            chartData.push(t);

            // force.stop()
            redraw()
            force.alpha(0.1)
            force.restart()
            // force.alphaTarget(1)
            click(t)

          }
    }
*/