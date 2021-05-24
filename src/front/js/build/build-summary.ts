import { html, css, LitElement } from 'lit';
import { customElement, property } from 'lit/decorators.js';

import * as echarts from 'echarts';

@customElement('build-summary')
export class BuildSummary extends LitElement {
  @property() available
  @property() qualified
  @property() ratio
  @property() numerator
  @property() denominator
  @property() title

  createRenderRoot() {
    return this;
  }

  render() {
    var qualified = this.available === "true" ?
    (this.qualified === "true" ? 'bg-pass':'bg-fail'):'bg-na';
    var ratio_percentage = (this.ratio * 100)
    return html`
      <div class="title"><b>${this.title}</b></div>
      <div class="ratio"><b>${this.available === "true" ?
        html`${ratio_percentage.toFixed(2)}%` :
        html`N/A`}</b></div>
      <div class="description">
      <p class="text-white description">${this.available === "true" ?
        html`${this.numerator} / ${this.denominator}` :
        html` -- / -- `}</p></div>
      <div class="progress">
        <div class="progress-bar ${qualified}" style="width: ${ratio_percentage}%">
        </div>
      </div>
    `
  }
}