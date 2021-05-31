import { html } from 'lit';
import { customElement} from 'lit/decorators.js';

import { FileDetail } from '../common/file-detail';

@customElement('recipe-violation-file-view')
export class RecipeViolationFileView extends FileDetail {

  renderDataList() {
    return html`
      <h3>Violation List${this.currentLine !== undefined ? html`- #${this.currentLine}` : html``}</h3>
      <div class="list-group">
        ${this.currentDataList.map(data => html`
        <div class="list-item">
          <div>
            <span class="badge ${this.getBadgeClass(data.level)}">${data.level}</span>
            <span>${data.rule}</span>
          </div>
          <div><b>${data.description}</b></div>
        </div>`)}
      </div>`;
  }

  private getBadgeClass(level) {
    switch(level) {
      case 'MAJOR':
        return 'bg-major';
      case 'MINOR':
        return 'bg-minor';
      default:
        return 'bg-info';
    }
  }
}