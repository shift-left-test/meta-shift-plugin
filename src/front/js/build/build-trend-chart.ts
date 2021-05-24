import { html, css, LitElement } from 'lit';
import { customElement, property } from 'lit/decorators.js';

import * as echarts from 'echarts';

@customElement('build-trend-chart')
export class BuildTrendChart extends LitElement {
  private trendChart;

  createRenderRoot() {
    return this;
  }

  render() {
    return html`<div id="trend-chart" class="chart"></div>`;
  }

  firstUpdated(changedProperties) {
    this.trendChart = echarts.init(document.getElementById("trend-chart"));
  }

  setAjaxFunc(requestTrendChartModel) {
    var self = this;

    requestTrendChartModel(function(model) {
      var option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: model.responseJSON['legend']
        },
        xAxis: {
          type: 'category',
          data: model.responseJSON['builds']
        },
        yAxis: {
          type: 'value'
        },
        series: model.responseJSON['series']
      };

      self.trendChart.setOption(option);
    });
  }
}