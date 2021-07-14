import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';

@customElement('tested-simple-view')
/**
 * tested simple view.
 */
export class TestedSimpleView extends LitElement {
  @property() tested
  @property() recipes
  @property() title
  // TODO: need to input delta with previous build
  @property() delta

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
    const isSummary = this.classList.contains('summary');

    const diffDirection = this.delta == 0 ? '' :
        (this.delta > 0 ? '▲' : '▼');

    const testRate = this.recipes > 0 ?
      Math.floor(this.tested / this.recipes * 100) : 0;

    return html`<div class="board">
      <div class="title">
        <b>${this.title}</b>
      </div>
      <div class="size-number">
       ${testRate}%
      </div>
      ${!isSummary ?
        html`
          <div class="size-diff">
          (${diffDirection}${Math.floor(Math.abs((this.delta * 100)))}%)
          </div>
          <div class="description">
            <b>${Number(this.tested).toLocaleString()}</b>
            out of <b>${Number(this.recipes).toLocaleString()}</b>
          </div>
          <div><br></div>
          ` :
        html``}
    </div>
    `;
  }
}
