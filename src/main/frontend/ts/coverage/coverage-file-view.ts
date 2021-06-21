import {html} from 'lit';
import {customElement} from 'lit/decorators.js';

import {FileDetail} from '../common/file-detail';

import * as monaco from 'monaco-editor';

@customElement('coverage-file-view')
/**
 * coverage file view.
 */
export class CoverageFileView extends FileDetail {
  /**
   * render data list.
   * @return {unknown}
   */
  renderDataList() : unknown {
    return html`
      <h3>Branch Coverage${this.currentLine !== undefined ? html`
      - #${this.currentLine}` : html``}</h3>
      <div class="list-group">
        ${this.currentDataList.map((data) => data.type === 'Branch' ?
          html`
        <div class="list-item ${data.covered === true ?
          'sourceCovered' : 'sourceUncovered'}">
        ${data.index}
        </div>
        ` : html ``)}
      </div>
      `;
  }

  /**
   * soure decorations.
   * @return {unknown}
   */
  getSourceDecorations() : {range: monaco.Range, options: unknown}[] {
    // create source file decoration info
    const decorations = [];

    const coveredLines = new Set();
    const lines = new Set();

    for (let i = 0; i < this.dataList.length; i++) {
      const data = this.dataList[i];
      lines.add(data.line);
      if (data.covered) {
        coveredLines.add(data.line);
      }
    }

    lines.forEach((line: number) => {
      decorations.push({
        range: new monaco.Range(line, 1, line, 1),
        options: {
          isWholeLine: true,
          className: coveredLines.has(line) ?
            'sourceCovered' : 'sourceUncovered',
        },
      });
    });

    return decorations;
  }
}
