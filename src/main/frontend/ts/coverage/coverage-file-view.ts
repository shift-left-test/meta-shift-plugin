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
    const branchCoverages = this.currentDataList.filter(
        (o) => o.type === 'Branch').length;
    const coveredBranchCoverages = this.currentDataList.filter(
        (o) => o.covered === true && o.type === 'Branch').length;

    if (branchCoverages === 0) {
      return html ``;
    }

    return html`
      <h3>Branch Coverage${this.currentLine !== undefined ? html`
      - #${this.currentLine}` : html``}</h3>
      <div class="list-group metashift-code">
        <table class="branch-coverage">
          <tr>
            <th>Branches</th>
            <th>Covered</th>
            <th>%</th>
          </tr>
          <tr>
            <td>${branchCoverages}</td>
            <td>${coveredBranchCoverages}</td>
            <td>${Math.floor(coveredBranchCoverages /
              branchCoverages * 100)}</td>
          </tr>
        </table>
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
