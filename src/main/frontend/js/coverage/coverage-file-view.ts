import { html } from 'lit';
import { customElement} from 'lit/decorators.js';

import { FileDetail } from '../common/file-detail';

@customElement('coverage-file-view')
export class CoverageFileView extends FileDetail {

  renderDataList() {
    return html`
      `;
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