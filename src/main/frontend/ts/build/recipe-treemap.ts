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
    return html`<div id="treemap-chart" class="chart"></div>`;
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
              `<div style="text-align: center; width: 100%">${value[1]}<div>`,
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
            color: '#000',
          },
          itemStyle: {
            borderColor: '#fff',
          },
          levels: [{
            color: [
              '#dc511d',
              '#e28565',
              '#eee1d6',
              '#c5daa7',
              '#6cb559',
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
