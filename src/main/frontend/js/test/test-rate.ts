import { html, LitElement } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('test-rate')
export class TestRate extends LitElement {
  @property() passRate;
  @property() failRate;
  @property() errorRate;

  createRenderRoot() {
    return this;
  }

  render() {
    return html`
    <div class="test-stats">
      <div class="pass-rate" style="width:${this.passRate * 100}%"></div>
      <div class="fail-rate" style="width:${this.failRate * 100}%"></div>
      <div class="error-rate" style="width:${this.errorRate * 100}%"></div>
    </div>
    `;
  }
}