import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';

@customElement('code-size')
/**
 * code size.
 */
export class CodeSize extends LitElement {
  @property() codeSize
  @property() codeSizeDelta

  /**
   * constructor.
   */
  constructor() {
    super();

    this.codeSize = '{}';
    this.codeSizeDelta = '{}';
  }
  /**
   * create render root.
   * @return {unknown}
   */
  createRenderRoot() : ShadowRoot | LitElement {
    return this;
  }

  /**
   * render size.
   * @param {string} title
   * @param {string} size
   * @param {string} delta
   * @return {unknown}
   */
  private renderSize(title, size, delta) {
    if (size !== undefined && delta !== undefined) {
      const diffDirection = delta == 0 ? '' :
        (delta > 0 ? '▲' : '▼');

      return html`
      <div class="size-item">
        <div class="title">${title}<div>
        <div class="size-number">${Number(size).toLocaleString()}</div>
        <div><span class="size-diff">
            (${diffDirection}${(Math.abs(delta)).toLocaleString()})</span>
        </div>
      </div>`;
    } else {
      return html``;
    }
  }

  /**
   * render.
   * @return {unknown}
   */
  render() : unknown {
    const codeSizeJson = JSON.parse(this.codeSize);
    const codeSizeDeltaJson = JSON.parse(this.codeSizeDelta);

    return html `
      ${this.renderSize('Recipes',
      codeSizeJson.recipes,
      codeSizeDeltaJson.recipes)}
      ${this.renderSize('Lines',
      codeSizeJson.lines,
      codeSizeDeltaJson.lines)}
      ${this.renderSize('Functions',
      codeSizeJson.functions,
      codeSizeDeltaJson.functions)}
      ${this.renderSize('Classes',
      codeSizeJson.classes,
      codeSizeDeltaJson.classes)}
      ${this.renderSize('Files',
      codeSizeJson.files,
      codeSizeDeltaJson.files)}
    `;
  }
}
