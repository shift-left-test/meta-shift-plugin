import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';

@customElement('metrics-simple-view')
/**
 * metrics simple view.
 */
export class MetricsSimpleView extends LitElement {
  @property() name
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

    const diffDirection = !evaluator.available || this.delta == 0 ? null :
        (this.delta > 0 ? '▲' : '▼');
    const textClass = evaluator.available ? '' : 'text-na';
    const iconClass = evaluator.available ?
        (evaluator.qualified ? 'ico-pass' : 'ico-fail'):'ico-na';

    const diffThreshold = isPercent ?
      Math.floor(evaluator.ratio * 100) - this.threshold :
      evaluator.ratio - this.threshold;
    const diffThresholdPrefix = diffThreshold > 0 ? '+' : null;

    const diffThresholdHtml = evaluator.available ?
      (isPercent ? html`(${diffThresholdPrefix}${diffThreshold}%)` :
      html`(${diffThresholdPrefix}${Number(diffThreshold).toFixed(2)})`) :
      html`(N/A)`;

    const qualifiedrecipes = this.qualifiedRate ?
     JSON.parse(this.qualifiedRate) : undefined;

    return html`<div class="board">
      <div class="metrics-name">
        <b>${this.name}</b>
        <div class="icon ${iconClass}"></div>
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
          ${evaluator.available ?
            html`(${diffDirection}${isPercent ?
              html`${Math.floor(Math.abs(this.delta * 100))}%`:
              html`${Number(Math.abs(this.delta)).toFixed(2)}`})` :
            html`(N/A)`
          }
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
          <div class="description ${textClass}">
            Threshold: ${this.threshold}${isPercent ? html`%` : html``}
            ${diffThresholdHtml}
          </div>` :
        html``}
    </div>
    ${qualifiedrecipes ?
      html`
        <div class="progress">
          <div class="progress-bar"
            style="width:${qualifiedrecipes.ratio * 100}%">
          </div>
          <span class="progress-tooltip">
          Qualified Recipes: ${Math.floor(qualifiedrecipes.ratio * 100)}%<br>
          (${qualifiedrecipes.numerator.toLocaleString()}/
          ${qualifiedrecipes.denominator.toLocaleString()})
          </span>
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
