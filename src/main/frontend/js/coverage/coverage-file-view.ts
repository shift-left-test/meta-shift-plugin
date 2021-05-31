import { html } from 'lit';
import { customElement} from 'lit/decorators.js';

import { FileDetail } from '../common/file-detail';

import * as monaco from 'monaco-editor';

@customElement('coverage-file-view')
export class CoverageFileView extends FileDetail {

  renderDataList() {
    return html`
      <h3>Branch Coverage${this.currentLine !== undefined ? html`- #${this.currentLine}` : html``}</h3>
      <div class="list-group">
        ${this.currentDataList.map(data => data.type === "Branch" ?
          html`
        <div class="list-item ${data.covered === true ? 'sourceCovered' : 'sourceUncovered'}">
        ${data.index}
        </div>
        ` : html ``)}
      </div>
      `;
  }

  getSourceDecorations() {
    // create source file decoration info
    var decorations = [];

    var coveredLines = new Set();
    var lines = new Set();

    for (var i = 0; i < this.dataList.length; i++) {
      var data = this.dataList[i];
      lines.add(data.line)
      if (data.covered) {
        coveredLines.add(data.line)
      }
    }

    lines.forEach((line: number) => {
      decorations.push({
        range: new monaco.Range(line, 1, line, 1),
        options: {
          isWholeLine: true,
          className: coveredLines.has(line) ? 'sourceCovered' : 'sourceUncovered'
        }
      })
    });

    return decorations;
  }
}