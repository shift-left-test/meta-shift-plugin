import {html} from 'lit';
import {customElement} from 'lit/decorators.js';
import {EditorView, Decoration} from '@codemirror/view';
import {RangeSet, RangeSetBuilder} from '@codemirror/rangeset';

import {FileDetail} from '../common/file-detail';

@customElement('complexity-file-view')
/**
 * complexity file view.
 */
export class ComplexityFileView extends FileDetail {
  private complexityTolerance;

  /**
   * set source file override
   * @param {unknown} response
   */
  setSourceFile(response: unknown)
      :void {
    this.complexityTolerance = response['complexityTolerance'];
    super.setSourceFile(response);
  }
  /**
   * render data list.
   * @return {unknown}
   */
  renderDataList() : unknown {
    if (this.currentDataList.length === 0) {
      return html ``;
    }

    return html`
      <h3>Complexity</h3>
      <div class="list-group metashift-code">
        <table class="complexity">
          <tbody>
          <tr>
            <th>Complexity</th>
            <th>Function</th>
          </tr>
          ${this.currentDataList.map((data) => html`
            <tr>
              <td>${data.value}</td>
              <td>${data.function}</td>
            </tr>
          `)}
          </tbody>
        <table>
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
    const sourceComplexBlock = Decoration.line({
      attributes: {class: 'sourceComplex'},
    });
    const builder = new RangeSetBuilder<Decoration>();
    for (const {from, to} of view.visibleRanges) {
      for (let pos = from; pos <= to;) {
        const line = view.state.doc.lineAt(pos);
        if (this.dataList.some((data) =>
          data.start <= line.number && data.end >= line.number &&
          data.value >= this.complexityTolerance)) {
          builder.add(line.from, line.from, sourceComplexBlock);
        }
        pos = line.to + 1;
      }
    }

    return builder.finish();
  }
}
