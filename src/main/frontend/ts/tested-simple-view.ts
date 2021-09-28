import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';

@customElement('tested-simple-view')
/**
 * tested simple view.
 */
export class TestedSimpleView extends LitElement {
  @property() testedRecipes
  @property() name
  @property() delta

  /**
   * constructor.
   */
  constructor() {
    super();

    this.testedRecipes = '{}';
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
    const tested = JSON.parse(this.testedRecipes);

    const diffDirection = this.delta == 0 ? '' :
        (this.delta > 0 ? '▲' : '▼');

    const testRate = Math.floor(tested.ratio * 100);

    return html`<div class="board">
      <div class="metrics-name">
        <b>${this.name}</b>
      </div>
      <div class="size-number">
       ${testRate}%
      </div>
      <div class="size-diff">
      (${diffDirection}${Math.floor(Math.abs((this.delta * 100)))}%)
      </div>
      <div class="description">
        <b>${Number(tested.numerator).toLocaleString()}</b>
        out of <b>${Number(tested.denominator).toLocaleString()}</b>
      </div>
      <div><br></div>
    </div>
    `;
  }
}
