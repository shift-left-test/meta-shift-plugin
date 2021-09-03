import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';
import {Utils} from './common/utils';

@customElement('metrics-simple-view')
/**
 * metrics simple view.
 */
export class MetricsSimpleView extends LitElement {
  @property() name
  @property() url
  @property() delta
  @property() evaluation
  @property() statistics
  /**
   * constructor
   */
  constructor() {
    super();

    this.evaluation = '{"ratio": 0, "threshold": 0}';
  }

  /**
   * create render root.
   * @return {unknown}
   */
  createRenderRoot() : ShadowRoot | LitElement {
    return this;
  }

  /**
   * render
   * @return {unknown}
   */
  render() : unknown {
    const evaluator = JSON.parse(this.evaluation);
    const isPercent = this.classList.contains('percent');
    const isSummary = this.classList.contains('summary');
    const showScale = this.classList.contains('show_scale');

    const diffDirection = !evaluator.available || this.delta == 0 ? null :
        (this.delta > 0 ? '▲' : '▼');
    const textClass = evaluator.available ? '' : 'text-na';
    const iconClass = evaluator.available ?
          (evaluator.qualified ? 'fa-check-circle' :
            'fa-times-circle') :
            'fa-minus-circle';
    const diffThreshold = isPercent ?
      Math.floor((evaluator.ratio - evaluator.threshold) * 100) :
      evaluator.ratio - evaluator.threshold;
    const diffThresholdPrefix = diffThreshold > 0 ? '+' : null;

    const diffThresholdHtml = evaluator.available ?
      (isPercent ? html`(${diffThresholdPrefix}${diffThreshold}%)` :
      html`(${diffThresholdPrefix}${Utils.toFixedFloor(diffThreshold)})`) :
      html`(N/A)`;

    return html`<div class="board">
      <div class="metrics-name">
        <b>${this.name}</b>
        <span class="icon"><i class="fas ${iconClass}"></i></span>
      </div>
      <div class="size-number ${textClass}">
        ${evaluator.available ?
        (isPercent ?
          html`${Math.floor(evaluator.ratio * 100)}%`:
          html`${Utils.toFixedFloor(evaluator.ratio)}`):
        html`N/A`}
      </div>
      ${this.delta ?
        html`
          <div class="size-diff ${textClass}">
          ${evaluator.available ?
            html`(${diffDirection}${isPercent ?
              html`${Math.floor(Math.abs(this.delta * 100))}%`:
              html`${Utils.toFixedFloor(Math.abs(this.delta))}`})` :
            html`(N/A)`
          }
          </div>` :
        html``}
      ${!isSummary ?
        html`
          <div class="description ${textClass}">
            <b>${Number(evaluator.numerator).toLocaleString()}</b>
            out of <b>${Number(evaluator.denominator).toLocaleString()}</b>
          </div>
          <div class="description ${textClass}">
            Threshold: ${isPercent ?
              html`${Math.floor(evaluator.threshold * 100)}%` :
              html`${Utils.toFixedFloor(evaluator.threshold)}`}
            ${diffThresholdHtml}${evaluator.tolerance ?
              html`, ${evaluator.tolerance}` :
              html``}
          </div>
          ` :
        html``}
    </div>
    ${this.renderStatistics(isPercent, showScale)}`;
  }

  /**
   * render statistics.
   * @param {boolean} isPercent
   * @param {boolean} showScale
   * @return {unknown}
   */
  renderStatistics(isPercent: boolean, showScale: boolean) {
    if (this.statistics) {
      const stats = JSON.parse(this.statistics);
      const low = stats.percent ? Math.floor(stats.min * 100) + '%' :
        Utils.toFixedFloor(stats.min);
      const avg = stats.percent ? Math.floor(stats.average * 100) + '%' :
        Utils.toFixedFloor(stats.average);
      const high = stats.percent ? Math.floor(stats.max * 100) + '%' :
        Utils.toFixedFloor(stats.max);

      return html`
        <div class="progress">
          <statistics-bar class='simple ${isPercent?'percent': ''}
              ${showScale?'show_scale': ''}'
            evaluation='${this.evaluation}'
            statistics='${this.statistics}'>
          </statistics-bar>
          <span class="progress-tooltip">
            ${low} / ${avg} / ${high}<br>
            ( min / avg / max )
          </span>
        </div>`;
    } else {
      return html``;
    }
  }

  /**
   * first updated.
   */
  firstUpdated() : void {
    if (this.url !== undefined) {
      this.classList.add('has-url');
      this.addEventListener('click', this._handleClick);
    }
  }

  /**
   * handle click event.
   */
  _handleClick() : void {
    const evaluator = JSON.parse(this.evaluation);
    if (evaluator.available) {
      window.location.href = this.url;
    }
  }
}
