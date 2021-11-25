/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {EditorView, Decoration} from '@codemirror/view';
import {RangeSet, RangeSetBuilder} from '@codemirror/rangeset';

import {FileDetail} from '../file-detail';

@customElement('duplication-file-view')
/**
 * duplication file view.
 */
export class DuplicationFileView extends FileDetail {
  /**
   * render data list.
   * @return {unknown}
   */
  renderDataList() : unknown {
    if (this.currentDataList.length === 0) {
      return html ``;
    }

    return html`
      <h3>Duplication List</h3>
      <div class="list-group metashift-code">
        ${this.currentDataList.map((data) => html`
        <div class="list-item">
          <div>
            <span>Range: </span>${data.start} ~ ${data.end}
          </div>
          <ul>DuplicateBlocks
          ${data.duplicateBlocks.map((dupData) => html`
            <li>
              <span>${dupData.file}
                (${dupData.start} ~ ${dupData.end})</span>
            </li>
          `)}
          <ul>
          </div>
        `)}
      </div>
      `;
  }

  /**
   * update data list.
   * @param {number} newLine
   */
  protected updateDataList(newLine: number) : void {
    if (newLine != undefined && newLine === this.currentLine) {
      return;
    }

    this.currentLine = newLine;
    const datas = [];

    if (this.currentLine !== undefined) {
      for (let i = 0; i < this.dataList.length; i++) {
        const data = this.dataList[i];
        if (data.start <= newLine && data.end >= newLine) {
          datas.push(data);
        }
      }
    }

    this.currentDataList = datas;
  }

  /**
   * return source decorations.
   * @param {unknown} view
   * @return {unknown}
   */
  getSourceDecorations(view: EditorView) : RangeSet<Decoration> {
    const lineList = {};
    for (const {start, end} of this.dataList) {
      for (let i = start; i <= end; i++) {
        if (!(i in lineList)) {
          lineList[i] = 0;
        } else {
          lineList[i]++;
        }
      }
    }

    const sourceDuplicated1Block = [
      Decoration.line({attributes: {class: 'sourceDuplicated1'}}),
      Decoration.line({attributes: {class: 'sourceDuplicated2'}}),
      Decoration.line({attributes: {class: 'sourceDuplicated3'}}),
      Decoration.line({attributes: {class: 'sourceDuplicated4'}}),
      Decoration.line({attributes: {class: 'sourceDuplicated5'}}),
    ];

    const builder = new RangeSetBuilder<Decoration>();
    for (const {from, to} of view.visibleRanges) {
      for (let pos = from; pos <= to;) {
        const line = view.state.doc.lineAt(pos);
        if (line.number in lineList) {
          const decoIndex = Math.min(lineList[line.number],
              sourceDuplicated1Block.length - 1);
          builder.add(line.from, line.from,
              sourceDuplicated1Block[decoIndex]);
        }
        pos = line.to + 1;
      }
    }

    return builder.finish();
  }
}
