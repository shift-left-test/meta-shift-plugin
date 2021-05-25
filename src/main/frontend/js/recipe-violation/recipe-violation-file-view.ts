import { html } from 'lit';
import { customElement} from 'lit/decorators.js';

import { FileDetail } from '../common/file-detail';

@customElement('recipe-violation-file-view')
export class RecipeViolationFileView extends FileDetail {

  renderDataList() {
    return html`
      <h3>Violation List${this.currentLine !== undefined ? html`- #${this.currentLine}` : html``}</h3>
      <ul class="overflow-auto list-group">
        ${this.currentDataList.map(data => html`<li class="border rounded bg-light list-group-item">
          <div>
            <span class="badge ${this.getBadgeClass(data.level)}">${data.level}</span>
            <span>${data.rule}</span>
          </div>
          <div>${data.description}</div>
        </li>`)}
      </ul>`;
  }

  private getBadgeClass(level) {
    switch(level) {
      case 'MAJOR':
        return 'badge-danger';
      case 'MINOR':
        return 'badge-warning';
      default:
        return 'badge-white';
    }
  }
}