import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';

@customElement('metrics-simple-view')
/**
 * metrics simple view.
 */
export class MetricsSimpleView extends LitElement {
  @property() title
  @property() metricsValue
  @property() url
  @property() delta
  @property() qualifiedRate
  @property() threshold

  /**
   * constructor
   */
  constructor() {
    super();

    this.metricsValue = '{}';
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
    const evaluator = JSON.parse(this.metricsValue);
    const isPercent = this.classList.contains('percent');
    const isSummary = this.classList.contains('summary');

    const diffDirection = this.delta == 0 ? null :
        (this.delta > 0 ? '▲' : '▼');
    const textClass = evaluator.available ? '' : 'text-na';
    const iconClass = evaluator.available ?
        (evaluator.qualified ? 'ico-pass' : 'ico-fail'):'ico-na';

    const diffThreshold = isPercent ?
      Math.floor(evaluator.ratio * 100) - this.threshold :
      evaluator.ratio - this.threshold;
    const diffThresholdPrefix = diffThreshold > 0 ? '+' : null;

    return html`<div class="board">
      <div class="title">
        <b class="${iconClass}">${this.title}</b>
      </div>
      <div class="size-number ${textClass}">
        ${evaluator.available ?
        (isPercent ?
          html`${Math.floor(evaluator.ratio * 100)}%`:
          html`${Number(evaluator.ratio).toFixed(2)}`):
        html`N/A`}
      </div>
      ${this.delta ?
        html`
          <div class="size-diff ${textClass}">
            (${diffDirection}${isPercent ?
              html`${Math.floor(Math.abs(this.delta * 100))}%`:
              html`${Number(Math.abs(this.delta)).toFixed(2)}`})
          </div>` :
        html``}
      ${!isSummary ?
        html`
          <div class="description ${textClass}">
            <b>${Number(evaluator.numerator).toLocaleString()}</b>
            out of <b>${Number(evaluator.denominator).toLocaleString()}</b>
          </div>` :
        html``}
      ${this.threshold ?
        html`
          <div>Threshold: ${this.threshold}${isPercent ?
            html`%(${diffThresholdPrefix}${diffThreshold}%)`:
            html`(${diffThresholdPrefix}${Number(diffThreshold).toFixed(2)})`}
          </div>` :
        html``}
    </div>
    ${this.qualifiedRate ?
      html`
        <div class="progress">
          <div class="progress-bar" style="width:${this.qualifiedRate * 100}%">
          </div>
        </div>` :
      html``}
    `;
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
    window.location.href = this.url;
  }
}
