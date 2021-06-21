import {html} from 'lit';
import {customElement} from 'lit/decorators.js';

import {FileDetail} from '../common/file-detail';

@customElement('mutation-test-file-view')
/**
 * mutation test file view.
 */
export class MutationTestFileView extends FileDetail {
  /**
   * render data list.
   * @return {unknown}
   */
  renderDataList() : unknown {
    return html`
      <h3>Violation List${this.currentLine !== undefined ? html`
        - #${this.currentLine}` : html``}</h3>
      <div class="list-group">
        ${this.currentDataList.map((data) => html`
        <div class="list-item">
          <div>
            <span class="badge ${this.getBadgeClass(data.status)}">
              ${data.status}</span>
            <span>${data.mutator}</span>
          </div>
          <div>mutated class: ${data.mutatedClass}
          </div>
          <div>mutated method: ${data.mutatedMethod}
          </div>
          <div>killing test: <b>${data.killingTest}</b></div>
        </div>`)}
      </div>`;
  }

  /**
   * badge color
   * @param {unknown} status
   * @return {unknown}
   */
  private getBadgeClass(status) {
    switch (status) {
      case 'KILLED':
        return 'bg-pass';
      case 'SURVIVED':
        return 'bg-fail';
      default:
        return 'bg-na';
    }
  }
}
