import {html, LitElement} from 'lit';
import {customElement} from 'lit/decorators.js';

import * as echarts from 'echarts';
import {ECharts} from 'echarts';

@customElement('build-trend-chart')
/**
 * build trend chart.
 */
export class BuildTrendChart extends LitElement {
  private trendChart: ECharts;

  /**
   * create render root.
   * @return {unknown}
   */
  createRenderRoot() : ShadowRoot | LitElement {
    return this;
  }

  /**
   * render.
   * @return {unknown}
   */
  render() : unknown {
    return html`<div id="trend-chart" class="chart"></div>`;
  }

  /**
   * first updated.
   */
  firstUpdated() : void {
    this.trendChart = echarts.init(document.getElementById('trend-chart'));
  }

  /**
   * set ajax func.
   * @param {unknown} requestTrendChartModel
   */
  setAjaxFunc(requestTrendChartModel: (callback) => void) : void {
    const that = this;

    requestTrendChartModel(function(model) {
      const legend = model.responseJSON['legend'];
      const xaxis = model.responseJSON['builds'];
      const series = model.responseJSON['series'];

      for (const item of series) {
        const data = item.data;
        const yaxisindex = item.yAxisIndex;
        if (yaxisindex == 0) {
          item.data = data.map((x) => x == null ? x : Math.floor(x));
        } else {
          item.data = data.map((x) => x == null ? x : x.toFixed(2));
        }
      }
      const option = {
        tooltip: {
          trigger: 'axis',
        },
        legend: {
          type: 'scroll',
          pageButtonPosition: 'start',
          data: legend,
        },
        xAxis: {
          type: 'category',
          data: xaxis,
        },
        yAxis: [
          {
            type: 'value',
            name: 'percent',
            axisLabel: {
              formatter: '{value} %',
            },
          },
          {
            type: 'value',
            name: 'density',
          }],
        series: series,
      };

      that.trendChart.setOption(option);
    });
  }

  /**
   * test api
   *
   * @return {unknown}
   */
  getChartOption(): unknown {
    return this.trendChart.getOption();
  }
}
