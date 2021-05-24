import { html, css, LitElement } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('code-size')
export class CodeSize extends LitElement {
  @property() recipeSize
  @property() recipeSizeDelta
  @property() lineSize
  @property() lineSizeDelta
  @property() functionSize
  @property() functionSizeDelta
  @property() classSize
  @property() classSizeDelta
  @property() fileSize
  @property() fileSizeDelta

  createRenderRoot() {
    return this;
  }
  
  private renderSize(title, size, delta) {
    if (size !== undefined && delta !== undefined) {
      var diffDirection = delta == 0 ? '' :
        (delta > 0 ? '▲' : '▼');

      return html`
      <div class="size-item">
        <p>${title}<p>
        <p class="size-number">${size}
          <span class="size-diff">(${diffDirection}${delta})</span>
        </p>
      </div>`
    } else{
      return html``
    }
  }

  render() {
    return html `
      ${this.renderSize("Recipes", this.recipeSize, this.recipeSizeDelta)}
      ${this.renderSize("Lines", this.lineSize, this.lineSizeDelta)}
      ${this.renderSize("Functions", this.functionSize, this.functionSizeDelta)}
      ${this.renderSize("Classes", this.classSize, this.classSizeDelta)}
      ${this.renderSize("Files", this.fileSize, this.fileSizeDelta)}
    `;
  }
}