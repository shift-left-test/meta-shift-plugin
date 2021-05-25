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

    var textClass = this.available === "true" ? '' : 'text-na';
    // TODO: progress should be racipe rate
    var progress = this.available === "true" ? this.numerator / this.denominator * 100 : 0;
    return html`<div class="board">
      <div class="title">
        <b class=${this.available === "true" ?
        (this.qualified === "true" ? 
          'ico-pass':
          'ico-fail'):'ico-na'}>${this.title}</b>
      </div>
      <div class="size-number ${textClass}">
        ${this.available === "true" ?
        html`${(this.ratio * 100).toFixed(2)}%` :
        html`N/A`}
      </div>
      <div class="size-diff ${textClass}">
      (${diffDirection}${this.delta})
      </div>
      <div class="description ${textClass}">
        <b>${this.numerator}</b> out of <b>${this.denominator}</b>
      </div>
    </div>
    <div class="progress">
      <div class="progress-bar" style="width:${progress}%">
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