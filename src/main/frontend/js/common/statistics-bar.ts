import { html, css, LitElement } from 'lit';
import { customElement, property } from 'lit/decorators.js';

class StatisticsItem {
  label: string;
  width: number;
  count: number;
  clazz: string;
}

@customElement('statistics-bar')
export class StatisticsBar extends LitElement {
  @property() items;

  createRenderRoot() {
    return this;
  }

  render() {
    var itemList = JSON.parse(this.items);

    return html `<div class="test-stats">${
      itemList.map((item: StatisticsItem) =>
        html`<div class="rate ${item.clazz}" style="width:${item.width}%"></div>`)
    }
    </div>
    <div class="legend">
    ${
      itemList.map((item: StatisticsItem, i) =>
        html`<div class="label ${item.clazz}">${item.label} ${item.width}% (${item.count})</div>
        ${i < itemList.length - 1 ? html`<div class="spacer"></div>` : html``}`)
    }
    </div>
    `;
  }
}