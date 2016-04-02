package example

import d3util.Margin
import org.scalajs.dom.EventTarget
import org.singlespaced.d3js.{Selection, d3}
import org.singlespaced.d3js.svg.Area

import scala.scalajs.js
import scala.scalajs.js.{Dictionary, _}
import org.singlespaced.d3js.Ops._
import org.singlespaced.d3js._


// adapted from http://bl.ocks.org/mbostock/3883195

object ScalaJSExample extends js.JSApp {

  def main(): Unit = {

    trait Data {
      var date: js.Date
      var close: Double
    }

    type DataArray = js.Array[Data]

    val margin = Margin(top = 20, right = 20, bottom = 30, left = 50)

    val width = 960 - margin.left - margin.right
    val height = 500 - margin.top - margin.bottom

    val parseDate = d3.time.format("%d-%b-%y").parse(_)

    val x = d3.time.scale().range(js.Array(0, width))
    val y = d3.scale.linear().range(js.Array(height, 0))

    val xAxis = d3.svg.axis().scale(x).orient("bottom")
    val yAxis = d3.svg.axis().scale(y).orient("left")

    val xf = (d: Data) => x(d.date)
    val yf = (d: Data) => x(d.close)

    val area: Area[Data] = d3.svg.area()
      .x(xf)
      .y0(height)
      .y1(yf)


    val svg: Selection[EventTarget] = d3.select("body")
      .append("svg")
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom)
      .append("g")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    d3.tsv("apple.tsv", (error: Any, rawdata: Array[Dictionary[String]]) => {

      val data: Array[Data] = rawdata.map { line =>
        object d extends Data {
          var date = parseDate(line("date"))
          var close = line("close").toDouble
        }
        d
      }

      val Tuple2(minX, maxX) = d3.extent(data.map(_.date))
      x.domain(Array(minX, maxX))

      val maxY = d3.max(data.map(_.close))
      y.domain(Array(0, maxY))

      svg.append("path")
        .datum(data)
        .attr("class", "area")
        .attr("d", area)

      svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis)

      svg.append("g")
        .attr("class", "y axis")
        .call(yAxis)
        .append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 6)
        .attr("dy", ".71em")
        .style("text-anchor", "end")
        .text("Price ($)")
      ()
    }

    )

  }

}