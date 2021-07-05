import {html, LitElement} from 'lit';
import {customElement} from 'lit/decorators.js';

import * as echarts from 'echarts';

@customElement('recipe-treemap')
/**
 * Recipe Treemap class
 */
export class RecipeTreemap extends LitElement {
  private treemapChart;

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
    return html`<div id="treemap-chart" class="chart"></div>
    <div class="legend">
      <span class="legend-7">▉</span>
      <span class="legend-6">▉</span>
      <span class="legend-5">▉</span>
      <span class="legend-4">▉</span>
      <span class="legend-3">▉</span>
      <span class="legend-2">▉</span>
      <span class="legend-1">▉</span>
    </div>`;
  }

  /**
   * first update.
   */
  firstUpdated() : void {
    this.treemapChart = echarts.init(document.getElementById('treemap-chart'));
  }

  /**
   * set ajax func.
   * @param {unknown} requestTreemapChartModel
   */
  setAjaxFunc(requestTreemapChartModel : (callback) => void) : void {
    const that = this;

    requestTreemapChartModel(function(model) {
      const formatUtil = echarts.format;
      const option = {
        title: {
          show: false,
        },
        tooltip: {
          formatter: function(info) {
            const value = info.value;
            const treePathInfo = info.treePathInfo;
            const treePath = [];

            for (let i = 1; i < treePathInfo.length; i++) {
              treePath.push(treePathInfo[i].name);
            }

            return [
              `<div class="tooltip-title">
              ${formatUtil.encodeHTML(treePath.join('/'))}</div>`,
              `<div class="tooltip-body">${value[1]}<div>`,
            ].join('');
          },
        },
        series: [{
          roam: false,
          breadcrumb: {
            show: false,
          },
          visualDimension: 1,
          type: 'treemap',
          label: {
            show: true,
            formatter: '{b}',
            color: '#fff',
          },
          itemStyle: {
            borderColor: '#fff',
          },
          levels: [{
            color: [
              '#e83336',
              '#b83b40',
              '#6e3d44',
              '#575866',
              '#306946',
              '#33a653',
              '#30cf3a',
            ],
            colorMappingBy: 'value',
            itemStyle: {
              borderWidth: 0,
              gapWidth: 1,
            },
          }],
          data: model.responseJSON['series'],
          nodeClick: 'link',
        }],
      };

      that.treemapChart.setOption(option);
      that.treemapChart.resize();
      window.onresize = function() {
        that.treemapChart.resize();
      };
    });
  }

  /**
   * test api
   *
   * @return {unknown}
   */
  getChartOption(): unknown {
    return this.treemapChart.getOption();
  }
}
