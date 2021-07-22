import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';

@customElement('scale-bar')
/**
 * scale bar.
 */
export class ScaleBar extends LitElement {
  @property() statistics;

  /**
   * constructor.
   */
  constructor() {
    super();

    this.statistics = '{}';
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

    const low = stats.percent ? Math.floor(stats.min * 100) + '%' :
      Number(stats.min).toFixed(2);
    const avg = stats.percent ? Math.floor(stats.average * 100) + '%' :
      Number(stats.average).toFixed(2);
    const high = stats.percent ? Math.floor(stats.max * 100) + '%' :
      Number(stats.max).toFixed(2);

    const rangeOffset = stats.percent ? Math.floor(stats.min * 100) :
      stats.min * 100 / stats.max;
    const rangewidth = stats.percent ?
      Math.floor(stats.max * 100) - Math.floor(stats.min * 100) :
      (stats.max - stats.min) * 100 / stats.max;
    const avgPosition = stats.percent ? Math.floor(stats.average * 100) :
      stats.average * 100 / stats.max;
    const scalePosition = stats.percent ? Math.floor(stats.scale * 100) :
      stats.scale * 100 / stats.max;

    const isSimple = this.classList.contains('simple');

    return html`<div class="metrics-stats">
      <div class="range" style="width:${rangewidth}%; left: ${rangeOffset}%">
      </div>
      <div class="pointer avg" style="left: ${avgPosition}%">
        <div class="pointer-text">▲</div>
      </div>${stats.available ? html`
      <div class="pointer scale" style="left: ${scalePosition}%">
        <div class="pointer-text">▲</div>
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
