import { html, css, LitElement } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('tested-simple-view')
export class TestedSimpleView extends LitElement {
  @property() tested
  @property() recipes
  @property() title
  // TODO: need to input delta with previous build
  @property() delta

  createRenderRoot() {
    return this;
  }
  
  render() {
    var diffDirection = this.delta == 0 ? '' :
        (this.delta > 0 ? '▲' : '▼');

    return html`<div class="board">
      <div class="title">
        <b>${this.title}</b>
      </div>
      <div class="size-number">
       ${Math.floor(this.tested / this.recipes * 100)}%
      </div>
      <div class="size-diff">
      (${diffDirection}${Math.floor(Math.abs((this.delta * 100)))}%)
      </div>
      <div class="description">
        <b>${Number(this.tested).toLocaleString()}</b> out of <b>${Number(this.recipes).toLocaleString()}</b>
      </div>
    </div>
    `
  }
}