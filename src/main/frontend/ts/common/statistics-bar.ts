import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';

@customElement('statistics-bar')
/**
 * scale bar.
 */
export class StatisticsBar extends LitElement {
  @property() statistics;
  @property() evaluation;

  /**
   * constructor.
   */
  constructor() {
    super();

    this.statistics = '{}';
    this.evaluation = '{}';
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
    const stats = JSON.parse(this.statistics);
    const evaluator = JSON.parse(this.evaluation);

    const isPercent = this.classList.contains('percent');
    const showScale = this.classList.contains('show_scale');
    const isSimple = this.classList.contains('simple');

    const low = isPercent ? Math.floor(stats.min * 100) + '%' :
      Number(stats.min).toFixed(2);
    const avg = isPercent ? Math.floor(stats.average * 100) + '%' :
      Number(stats.average).toFixed(2);
    const high = isPercent ? Math.floor(stats.max * 100) + '%' :
      Number(stats.max).toFixed(2);

    const graphMax = Math.max(stats.max, evaluator.threshold);

    const rangeOffset = isPercent ? Math.floor(stats.min * 100) :
      stats.min * 100 / graphMax;
    const rangeWidth = isPercent ?
      Math.floor(stats.max * 100) - Math.floor(stats.min * 100) :
      (stats.max - stats.min) * 100 / graphMax;
    const avgPosition = isPercent ? Math.floor(stats.average * 100) :
      stats.average * 100 / graphMax;
    const scalePosition = isPercent ? Math.floor(evaluator.ratio * 100) :
      evaluator.ratio * 100 / graphMax;

    const thresholdPosition = isPercent ?
      Math.floor(evaluator.threshold * 100) :
      evaluator.threshold * 100 / graphMax;

    return html`<div class="metrics-stats">
      <div class="range" style="width:${rangeWidth}%; left: ${rangeOffset}%">
      </div>
      <div class="pointer threshold" style="left: ${thresholdPosition}%">
        <i class="pointer-text fas fa-caret-up"></i>
      </div>
      <div class="pointer avg" style="left: ${avgPosition}%">
        <i class="pointer-text fas fa-caret-up"></i>
      </div>
      ${showScale && evaluator.available ? html`
        <div class="pointer scale" style="left: ${scalePosition}%">
          <i class="pointer-text fas fa-caret-up"></i>
        </div>` : html``}
      </div>
      ${isSimple ? html`` :
      html`
      <div class="legend">
        <div class="label">
        Low: ${low}
        </div>
        <div class="spacer"></div>
        <div class="label">
        Average: ${avg}
        </div>
        <div class="spacer"></div>
        <div class="label">
        High: ${high}
        </div>
      </div>`}`;
  }
}
