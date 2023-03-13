/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {EditorView, Decoration} from '@codemirror/view';
import {RangeSet, RangeSetBuilder} from '@codemirror/rangeset';

import {FileDetail} from '../file-detail';

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
      <div class="list-group metashift-code">
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

  /**
   * source decorations.
   * @param {unknown} view
   * @return {unknown}
   */
  getSourceDecorations(view: EditorView) : RangeSet<Decoration> {
    const mutationKilledBlock = Decoration.line({
      attributes: {class: 'mutationKilled'},
    });
    const mutationSurvivedBlock = Decoration.line({
      attributes: {class: 'mutationSurvived'},
    });
    const mutationSkippedBlock = Decoration.line({
      attributes: {class: 'mutationSkipped'},
    });
    const mutationMixedWithSurvivedBlock = Decoration.line({
      attributes: {class: 'mutationMixedWithSurvived'},
    });
    const mutationMixedWithoutSurvivedBlock = Decoration.line({
      attributes: {class: 'mutationMixedWithoutSurvived'},
    });
    const builder = new RangeSetBuilder<Decoration>();
    for (const {from, to} of view.visibleRanges) {
      for (let pos = from; pos <= to;) {
        const line = view.state.doc.lineAt(pos);
        pos = line.to + 1;
        const mutantsInLine = this.dataList.filter((data) =>
          data.line == line.number);
        if (!mutantsInLine.length) {
          continue;
        }
        const uniqueStatus = new Set(mutantsInLine.map((mutant) =>
          mutant.status));
        if (uniqueStatus.size > 1 && uniqueStatus.has('SURVIVED')) {
          builder.add(line.from, line.from, mutationMixedWithSurvivedBlock);
        } else if (uniqueStatus.size > 1) {
          builder.add(line.from, line.from, mutationMixedWithoutSurvivedBlock);
        } else if (uniqueStatus.has('KILLED')) {
          builder.add(line.from, line.from, mutationKilledBlock);
        } else if (uniqueStatus.has('SURVIVED')) {
          builder.add(line.from, line.from, mutationSurvivedBlock);
        } else {
          builder.add(line.from, line.from, mutationSkippedBlock);
        }
      }
    }

    return builder.finish();
  }
}
