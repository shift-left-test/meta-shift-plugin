import { html, css, LitElement } from 'lit';
import { customElement, property, query } from 'lit/decorators.js';

import * as echarts from 'echarts';

@customElement('recipe-treemap')
export class RecipeTreemap extends LitElement {
  private treemapChart;

  createRenderRoot() {
    return this;
  }

  render() {
    return html`<div id="treemap-chart" class="chart"></div>`;
  }

  firstUpdated(changedProperties) {
    this.treemapChart = echarts.init(document.getElementById("treemap-chart"));
  }

  setAjaxFunc(requestTreemapChartModel) {
    var self = this;

    requestTreemapChartModel(function(model) {
      var formatUtil = echarts.format;
      var option = {
        title: {
          show: false,
        },
        tooltip: {
          formatter: function (info) {
            var value = info.value;
            var treePathInfo = info.treePathInfo;
            var treePath = [];

            for (var i = 1; i < treePathInfo.length; i++) {
              treePath.push(treePathInfo[i].name);
            }

            return [
              '<div class="tooltip-title">' + formatUtil.encodeHTML(treePath.join('/')) + '</div>',
              '' + formatUtil.addCommas(value[1]) + '',
            ].join('');
          }
        },
        series: [{
          breadcrumb: {
            show: false
          },
          visualDimension: 1,
          type: 'treemap',
          label: {
            show: true,
            formatter: '{b}'
          },
          itemStyle: {
            borderColor: '#fff'
          },
          levels: [{
            color: [
              '#dc511d',
              '#e28565',
              '#eee1d6',
              '#c5daa7',
              '#6cb559'
              ],
            colorMappingBy: 'value',
            itemStyle: {
              borderWidth: 0,
              gapWidth: 1
            },
          }],
          data: model.responseJSON['series']
        }]    
      };

      self.treemapChart.setOption(option);
      self.treemapChart.resize();
      window.onresize = function () {
        self.treemapChart.resize();
      };
    })
  }
}