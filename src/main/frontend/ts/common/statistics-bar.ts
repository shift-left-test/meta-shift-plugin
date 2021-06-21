import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';

/**
 * statistics item.
 */
class StatisticsItem {
  label: string;
  width: number;
  count: number;
  clazz: string;
}

@customElement('statistics-bar')
/**
 * statistics bar.
 */
export class StatisticsBar extends LitElement {
  @property() items;

  /**
   * constructor.
   */
  constructor() {
    super();

    this.items = '[]';
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
    const itemList = JSON.parse(this.items);

    return html `<div class="test-stats">${
      itemList.map((item: StatisticsItem) =>
        html`<div class="rate ${item.clazz}"
          style="width:${item.width}%"></div>`)
    }</div>
    <div class="legend">${itemList.map(
      (item: StatisticsItem, i) =>
        html`<div class="label ${item.clazz}">
          ${item.label} ${item.width}% (${item.count})</div>
          ${i < itemList.length - 1 ?
            html`<div class="spacer"></div>` : html``}`)}
    </div>`;
  }
}
