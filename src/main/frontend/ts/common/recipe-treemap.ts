import {html, LitElement} from 'lit';
import {customElement, query} from 'lit/decorators.js';

import * as echarts from 'echarts';
import variables from '../../scss/vars.scss';

@customElement('recipe-treemap')
/**
 * Recipe Treemap class
 */
export class RecipeTreemap extends LitElement {
  @query('#treemap-chart') treemapSelector

  private treemapChart;
  protected recipeRoot;
  protected tooltipInfos;

  /**
   * constructor.
   */
  constructor() {
    super();
    this.recipeRoot = './';
  }

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
    this.classList.add('treemap-chart');

    return html`<div id="treemap-chart" class="chart"></div>
    <div class="legend">
      <span class="legend-1">▉</span>
      <span class="legend-2">▉</span>
      <span class="legend-3">▉</span>
      <span class="legend-4">▉</span>
      <span class="legend-5">▉</span>
      <span class="legend-6">▉</span>
      <span class="legend-7">▉</span>
    </div>`;
  }

  /**
   * first update.
   */
  firstUpdated() : void {
    this.treemapChart = echarts.init(this.treemapSelector);
  }

  /**
   * tooltip generator.
   * @param {unknown} info
   * @return {unknown}
   */
  generateTooltip(info) {
    return ``;
  }

  /**
   * treemap valie pair generator.
   * @param {unknown} o
   * @return {unknown}
   */
  generateValueList(o) {
    return [o.linesOfCode, o.grade];
  }

  /**
   * set ajax func.
   * @param {unknown} requestTreemapChartModel
   */
  setAjaxFunc(requestTreemapChartModel : (callback) => void) : void {
    const that = this;

    requestTreemapChartModel(function(model) {
      that.tooltipInfos = model.responseJSON.tooltipInfo;
      const series = model.responseJSON.data.map((o: any) => {
        return {
          name: o.name,
          link: that.recipeRoot + o.name,
          path: '',
          target: '_self',
          value: that.generateValueList(o),
        };
      });

      // add color boundary value
      series.push({
        value: [0, 0],
      });
      series.push({
        value: [0, 6],
      });

      const option = {
        title: {
          show: false,
        },
        tooltip: {
          formatter: that.generateTooltip.bind(that),
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
              variables.treemapColorsLegend1,
              variables.treemapColorsLegend2,
              variables.treemapColorsLegend3,
              variables.treemapColorsLegend4,
              variables.treemapColorsLegend5,
              variables.treemapColorsLegend6,
              variables.treemapColorsLegend7,
            ],
            colorMappingBy: 'value',
            itemStyle: {
              borderWidth: 0,
              gapWidth: 1,
            },
          }],
          data: series,
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
