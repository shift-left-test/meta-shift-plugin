/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {html} from 'lit';
import {customElement} from 'lit/decorators.js';

import {FileDetail} from '../file-detail';

@customElement('code-violation-file-view')
/**
 * code violation file view.
 */
export class CodeViolationFileView extends FileDetail {
  /**
   * render data list.
   * @return {unknown}
   */
  renderDataList() : unknown {
    return html`
      <h3>Violation List${this.currentLine !== undefined ?
        html`- #${this.currentLine}` : html``}</h3>
      <div class="list-group metashift-code">
        ${this.currentDataList.map((data) => html`
        <div class="list-item">
          <div>
            <span class="badge ${this.getBadgeClass(data.level)}">
            ${data.level}</span>
            <span>${data.rule}</span>
          </div>
          <div>tool: ${data.tool} -
            <span class="severity">severity: ${data.severity}</span>
          </div>
          <div><b>${data.message}</b></div>
        </div>`)}
      </div>`;
  }

  /**
   * get badge class.
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
