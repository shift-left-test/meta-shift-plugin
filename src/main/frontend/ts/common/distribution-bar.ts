import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';

/**
 * statistics item.
 */
class LabelInfo {
  name: string;
  clazz: string;
}

@customElement('distribution-bar')
/**
 * distribution bar.
 */
export class DistributionBar extends LitElement {
  @property() distribution;
  @property() labels;

  /**
   * constructor.
   */
  constructor() {
    super();

    this.distribution = '{}';
    this.labels = '[]';
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
    const distributionMap = JSON.parse(this.distribution);
    const labelList = JSON.parse(this.labels);

    return html `<div class="test-stats">${
      labelList.map((label: LabelInfo, i) =>
        html`<div class="rate ${label.clazz}"
          style="width:${this.getWidth(i, distributionMap)}%"></div>`)
    }</div>
    <div class="legend">${labelList.map(
      (label: LabelInfo, i) =>
        html`<div class="label ${label.clazz}">
          ${label.name} ${this.getWidth(i, distributionMap)}% \
(${this.getCount(i, distributionMap)})</div>
          ${i < labelList.length - 1 ?
            html`<div class="spacer"></div>` : html``}`)}
    </div>`;
  }

  /**
   * @param {number} i
   * @param {unknown} distributionMap
   * @return {unknown}
   */
  private getWidth(i: number, distributionMap: any) {
    const distributionNames = ['first', 'second', 'third', 'fourth'];
    const dist = distributionMap[distributionNames[i]];
    return Math.floor(dist.ratio * 100);
  }

  /**
   *  @param {number} i
   * @param {unknown} distributionMap
   * @return {unknown}
   */
  private getCount(i: number, distributionMap: any) {
    const distributionNames = ['first', 'second', 'third', 'fourth'];
    const dist = distributionMap[distributionNames[i]];
    return dist.count;
  }
}
