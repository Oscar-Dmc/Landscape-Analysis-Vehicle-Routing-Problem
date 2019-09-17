// set the dimensions and margins of the graph
var margin = {top: 40, right: 30, bottom: 120, left: 10},
width = screen.availWidth - margin.left - margin.right,
height = screen.availHeight - margin.top - margin.bottom;

// append the svg object to the body of the page
var svg = d3.select("body")
.append("svg")
  .attr("width", width)
  .attr("height", height)
.append("g")
  .attr("transform",
        "translate(" + margin.left + "," + margin.top + ")");

d3.json("https://raw.githubusercontent.com/Oscar-Dmc/datas-vrp-landscape/master/data-real.json").then(function(data){ 
    var values = [];

    for(let node of data.nodes){
    values.push(node.ObjFunction)
    }

    maxRange = Math.max(...values) + 20
    minRange = Math.min(...values) - 20

    var bestColor = '#7ac3ff';
    var worstColor = '#003561';

    var colorScale = d3.scaleLinear([maxRange, minRange], [worstColor, bestColor]);

    d3.select("svg")
    .call(d3.zoom()
        .scaleExtent([1/8, 8])
        .on("zoom", zoom));

    var g = svg.append("g")
        

    var link = g.selectAll("line")
    .data(data.links)
    .enter()
    .append("line")
    .style("stroke", "rgb(177, 177, 177)")

    var node = g.selectAll("circle")
    .data(data.nodes)
    .enter()
    .append("circle")
    .attr("r", 10)
    .style("fill", function(d){
        return colorScale(d.ObjFunction)
    })
    .on("mouseover", focus)
    .on("mouseout", unfocus);

    // Let's list the force we wanna apply on the network
    var simulation = d3.forceSimulation(data.nodes)                 // Force algorithm is applied to data.nodes
        .force("link", d3.forceLink()                               // This force provides links between nodes
            .id(function(d) { return d.nodeId; })                     // This provide  the id of a node
            .links(data.links)    // and this the list of links
            .distance(200)                           
        )
        .force("charge", d3.forceManyBody().strength(-100))         // This adds repulsion between nodes. Play with the -400 for the repulsion strength 
        .force("center", d3.forceCenter(width / 2, height / 2))    // This force attracts nodes to the center of the svg area
        .force('collision', d3.forceCollide().radius(15))
        .on("tick", ticked);

    function zoom() {
    g.attr("transform", d3.event.transform);
    }

    function focus(d) {
        d3.select(this)
        .attr("r", document.getElementById("node-radius").value * 1.5)
    
        g.append("rect")
        .attr("x", this.getAttribute('cx') - 100)
        .attr("y", this.getAttribute('cy') - 80)
        .attr("rx", 5)
        .attr("ry", 5)
        .attr("width", 200)
        .attr("height", 60)
        .attr("id", "r" + d.nodeId)
    
        g.append("text")
        .attr("x", this.getAttribute('cx') - 92)
        .attr("y", this.getAttribute('cy') - 35)
        .attr("id", "t1" + d.nodeId)
        .text(function() {
            return ["ObjV: " + d.ObjFunction];  
        });
    
        g.append("text")
        .attr("x", this.getAttribute('cx') - 92)
        .attr("y", this.getAttribute('cy') - 60)
        .attr("id", "t2" + d.nodeId)
        .text(function() {
            return ["id: " + d.nodeId];  
        });
    
        markConnections(d, "rgb(0, 98, 184)", 3)
    }
  
  function unfocus(d) {
  
    d3.select(this)
      .attr("r", document.getElementById("node-radius").value);
  
    d3.select("rect").attr("id", "r" + d.nodeId).remove();
    d3.select("text").attr("id", "t1" + d.nodeId).remove();
    d3.select("text").attr("id", "t2" + d.nodeId).remove();
  
    restore()
  }
  
  function ticked() {
    
    link
    .attr("x1", function(d) { return d.source.x; })
    .attr("y1", function(d) { return d.source.y; })
    .attr("x2", function(d) { return d.target.x; })
    .attr("y2", function(d) { return d.target.y; });
    
    node
          .attr("cx", function (d) { return d.x; })
          .attr("cy", function(d) { return d.y; });
  }
  
  function markConnections(node, rgb, width){
    let nodesNotChange = [];
    nodesNotChange.push(node.nodeId);
  
    d3.selectAll("line").each(function(d){
      if(node.nodeId == d.source.nodeId){ 
        d3.select(this).style("stroke", rgb).style("stroke-width", width);
        if(nodesNotChange.indexOf(d.target.nodeId) == -1){
          nodesNotChange.push(d.target.nodeId);
        }
      } else if (node.nodeId == d.target.nodeId){
        d3.select(this).style("stroke", rgb).style("stroke-width", width);
        if(nodesNotChange.indexOf(d.source.nodeId) == -1){
          nodesNotChange.push(d.source.nodeId);
        }
      } else {
        d3.select(this).style("stroke", "rgba(0,0,0,0)");
      }
    });
  
    d3.selectAll("circle").filter(function(d){
      return(nodesNotChange.indexOf(d.nodeId) == -1)
    }).style("opacity", 0.1)
  
  }
  
  function restore(){
    d3.selectAll("line")
    .style("stroke", "rgb(177, 177, 177)")
    .style("stroke-width", 1);
    
    d3.selectAll("circle").style("opacity", 1)
  }

  document.getElementById("redraw").addEventListener("click", redraw);

  function redraw(){
      bestColor = document.getElementById("best-solution-color").value + "";
      worstColor = document.getElementById("worst-solution-color").value + "";
      let newColor = d3.scaleLinear([maxRange, minRange], [worstColor, bestColor]);
    
      d3.selectAll("circle")
          .attr("r", document.getElementById("node-radius").value)
          .style("fill", function(d){
              return newColor(d.ObjFunction)
          })
    
      simulation.force("link", d3.forceLink().links(data.links)
          .distance(document.getElementById("link-distance").value)
          .strength(document.getElementById("link-strength").value)
          .iterations(document.getElementById("link-iteration").value))

      if(document.getElementById("cbox-mstrength").checked){
        simulation.force("charge", d3.forceManyBody()
          .strength(document.getElementById("manyBody-strength").value))
      } else {
        simulation.force("charge", null)
      }

      if(document.getElementById("cbox-rcollide").checked){
        simulation.force("collision", d3.forceCollide()
          .radius(document.getElementById("collide-radius").value)
          .iterations(document.getElementById("collide-iterations").value));
      } else {
        simulation.force("collision", null);
      } 
      simulation.alpha(0.5).restart()
  }

});