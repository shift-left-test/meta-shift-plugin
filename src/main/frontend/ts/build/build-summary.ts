import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';

@customElement('build-summary')
/**
 * build summary.
 */
export class BuildSummary extends LitElement {
  @property() title
  @property() metricsValue

  /**
   * constructor.
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
   * render.
   * @return {unknown}
   */
  render() : unknown {
    const evaluator = JSON.parse(this.metricsValue);
    const isPercent = this.classList.contains('percent');

    const qualified = evaluator.available ?
    (evaluator.qualified ? 'bg-pass':'bg-fail'):'bg-na';
    const ratioPercentage = Math.floor(evaluator.ratio * 100);
    return html`
      <div class="title"><b>${this.title}</b></div>
      <div class="ratio"><b>${evaluator.available ?
        (isPercent ? html`${ratioPercentage}%`:
          html`${Number(evaluator.ratio).toFixed(2)}`) :
        html`N/A`}</b></div>
      ${evaluator.available && isPercent ?
        html`<div class="progress">
          <div class="progress-bar ${qualified}"
            style="width: ${ratioPercentage}%">
          </div>
        </div>` :
        html ``}
      `;
  }
}
