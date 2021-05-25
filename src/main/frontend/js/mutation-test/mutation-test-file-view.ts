import { html } from 'lit';
import { customElement} from 'lit/decorators.js';

import { FileDetail } from '../common/file-detail';

@customElement('mutation-test-file-view')
export class MutationTestFileView extends FileDetail {

  renderDataList() {
    return html`
      `;
  }
}