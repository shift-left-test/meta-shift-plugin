import { html, css, LitElement } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('metrics-simple-view')
export class MetricsSimpleView extends LitElement {
  @property() available
  @property() qualified
  @property() ratio
  @property() numerator
  @property() denominator
  @property() url
  @property() title
  // TODO: need to input delta with previous build
  @property() delta = 1

  createRenderRoot() {
    return this;
  }
  
  render() {
    var diffDirection = this.delta == 0 ? '' :
        (this.delta > 0 ? '▲' : '▼');

    return html`<div>
      <p>
        <b class="title">${this.title}</b>
        <span>
        ${this.available === "true" ?
          (this.qualified === "true" ? 
            html`PASS`:
            html`FAIL`):html``}
        </span>
      </p>
      <p class="size-number">
        <b>${this.available === "true" ?
        html`${(this.ratio * 100).toFixed(2)}%` :
        html`N/A`}</b>
      </p>
      <p class="size-diff">
      (${diffDirection}${this.delta})
      </p>
      <p class="text-white description">
        ${this.available === "true" ?
          html`<b>${this.numerator}</b> out of <b>${this.denominator}</b>` :
          html``}</p>
      <div class="progress">
        <div class="progress-bar" style="width:${
          this.numerator / this.denominator * 100
        }%">
        </div>
      </div>
    </div>
    `
  }

  firstUpdated() {
    if (this.url !== undefined) {
      this.classList.add("has-url");
      this.addEventListener('click', this._handleClick);
    }
  }

  _handleClick() {
    window.location.href = this.url;
  }
}