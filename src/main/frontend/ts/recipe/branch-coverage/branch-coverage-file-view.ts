/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {EditorView, Decoration} from '@codemirror/view';
import {RangeSet, RangeSetBuilder} from '@codemirror/rangeset';

import {FileDetail} from '../file-detail';

@customElement('branch-coverage-file-view')
/**
 * coverage file view.
 */
export class BranchCoverageFileView extends FileDetail {
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
   * source decorations.
   * @param {unknown} view
   * @return {unknown}
   */
  getSourceDecorations(view: EditorView) : RangeSet<Decoration> {
    const sourceCoveredBlock = Decoration.line({
      attributes: {class: 'sourceCovered'},
    });
    const sourceUncoveredBlock = Decoration.line({
      attributes: {class: 'sourceUncovered'},
    });
    const builder = new RangeSetBuilder<Decoration>();
    for (const {from, to} of view.visibleRanges) {
      for (let pos = from; pos <= to;) {
        const line = view.state.doc.lineAt(pos);
        if (this.dataList.some((data) =>
          data.line == line.number && data.covered)) {
          builder.add(line.from, line.from, sourceCoveredBlock);
        } else if (this.dataList.some((data) =>
          data.line == line.number && !data.covered)) {
          builder.add(line.from, line.from, sourceUncoveredBlock);
        }
        pos = line.to + 1;
      }
    }

    return builder.finish();
  }
}
