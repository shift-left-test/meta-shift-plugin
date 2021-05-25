import { html, css, LitElement } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('metrics-simple-view')
export class MetricsSimpleView extends LitElement {
  @property() title
  @property() available
  @property() qualified
  @property() ratio
  @property() numerator
  @property() denominator
  @property() url
  @property() delta
  @property() qualifiedRate

  createRenderRoot() {
    return this;
  }
  
  render() {
    // TODO: if delta is less then 1%? it looks like "(▲0)"
    var diffDirection = this.delta == 0 ? '' :
        (this.delta > 0 ? '▲' : '▼');
    var textClass = this.available === "true" ? '' : 'text-na';

    return html`<div class="board">
      <div class="title">
        <b class=${this.available === "true" ?
        (this.qualified === "true" ? 
          'ico-pass':
          'ico-fail'):'ico-na'}>${this.title}</b>
      </div>
      <div class="size-number ${textClass}">
        ${this.available === "true" ?
        html`${(this.ratio * 100).toFixed(0)}%` :
        html`0%`}
      </div>
      ${this.delta ? 
        html`
      <div class="size-diff ${textClass}">
      (${diffDirection}${Math.abs(this.delta * 100).toFixed(0)}%)
      </div>`
      : html``}
      <div class="description ${textClass}">
        <b>${this.numerator}</b> out of <b>${this.denominator}</b>
      </div>
    </div>
    ${this.qualifiedRate ?
      html`
    <div class="progress">
      <div class="progress-bar" style="width:${this.qualifiedRate * 100}%">
      </div>
    </div>`
    : html``}
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