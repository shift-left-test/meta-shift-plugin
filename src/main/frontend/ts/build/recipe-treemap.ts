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
   * return qualified string.
   * @param {unknown}qualifiedMap
   * @param {string} key
   * @return {string}
   */
  private generateQualifiedTooltipString(qualifiedMap, key) {
    const qualified = qualifiedMap[key];
    if (qualified != undefined) {
      return `<div><div class="tooltip-qualified-key">${key}:</div>
      <div class="tooltip-qualified-value ${qualified ? 'good' : 'bad'}">
        ${qualified ? 'PASS' : 'FAIL'}</div>
      </div>`;
    } else {
      return `<div><div class="tooltip-qualified-key">${key}:</div>
      <div class="tooltip-qualified-value">N/A</div>
      </div>`;
    }
  }
  /**
   * return tooltip string with metrics.
   * @param {unknown} qualifiedMap
   * @return {string}
   */
  private qualifiedTooltip(qualifiedMap) {
    let tooltip = '';
    // build performance
    tooltip += '<div class="tooltip-column">';
    tooltip += '<div class="tooltip-section">Build Performance</div>';
    tooltip += this.generateQualifiedTooltipString(
        qualifiedMap, 'Premirror Cache');
    tooltip += this.generateQualifiedTooltipString(
        qualifiedMap, 'Shared State Cache');
    tooltip += this.generateQualifiedTooltipString(
        qualifiedMap, 'Recipe Violations');
    tooltip += '</div>';

    tooltip += '<div class="tooltip-column-gap"></div>';

    // code quality
    tooltip += '<div class="tooltip-column">';
    tooltip += '<div class="tooltip-section">Code Quality</div>';
    tooltip += this.generateQualifiedTooltipString(
        qualifiedMap, 'Comments');
    tooltip += this.generateQualifiedTooltipString(
        qualifiedMap, 'Code Violations');
    tooltip += this.generateQualifiedTooltipString(
        qualifiedMap, 'Complexity');
    tooltip += this.generateQualifiedTooltipString(
        qualifiedMap, 'Duplications');
    tooltip += this.generateQualifiedTooltipString(
        qualifiedMap, 'Unit Tests');
    tooltip += this.generateQualifiedTooltipString(
        qualifiedMap, 'Statement Coverage');
    tooltip += this.generateQualifiedTooltipString(
        qualifiedMap, 'Branch Coverage');
    tooltip += this.generateQualifiedTooltipString(
        qualifiedMap, 'Mutation Tests');
    tooltip += '</div>';

    return tooltip;
  }

  /**
   * set ajax func.
   * @param {unknown} requestTreemapChartModel
   */
  setAjaxFunc(requestTreemapChartModel : (callback) => void) : void {
    const that = this;

    requestTreemapChartModel(function(model) {
      // const formatUtil = echarts.format;
      const option = {
        title: {
          show: false,
        },
        tooltip: {
          formatter: function(info) {
            if (info.data.qualifiedMap !== undefined) {
              return `<div class="tooltip-title">
                ${info.name}</div>
                <div class="tooltip-body">
                ${that.qualifiedTooltip(info.data.qualifiedMap)}<div>`;
            }
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
