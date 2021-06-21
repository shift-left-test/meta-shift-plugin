import {html} from 'lit';
import {customElement} from 'lit/decorators.js';

import {FileDetail} from '../common/file-detail';

@customElement('recipe-violation-file-view')
/**
 * recipe violation file view.
 */
export class RecipeViolationFileView extends FileDetail {
  /**
   * render data list.
   * @return {unknown}
   */
  renderDataList() : unknown {
    return html`
      <h3>Violation List${this.currentLine !== undefined ? html`
        - #${this.currentLine}` : html``}</h3>
      <div class="list-group metashift-code">
        ${this.currentDataList.map((data) => html`
        <div class="list-item">
          <div>
            <span class="badge ${this.getBadgeClass(data.level)}">
              ${data.level}</span>
            <span>${data.rule}</span>
          </div>
          <div><b>${data.description}</b></div>
        </div>`)}
      </div>`;
  }

  /**
   * badge class.
   * @param {unknown} level
   * @return {unknown}
   */
  private getBadgeClass(level) {
    switch (level) {
      case 'MAJOR':
        return 'bg-major';
      case 'MINOR':
        return 'bg-minor';
      default:
        return 'bg-info';
    }
  }
}
