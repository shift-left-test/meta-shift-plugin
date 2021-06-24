import {html} from 'lit';
import {customElement} from 'lit/decorators.js';

import * as monaco from 'monaco-editor';

import {FileDetail} from '../common/file-detail';

@customElement('complexity-file-view')
/**
 * complexity file view.
 */
export class ComplexityFileView extends FileDetail {
  private complexityLevel;

  /**
   * set source file override
   * @param {string} filePath
   * @param {unknown} response
   */
  setSourceFile(filePath: string, response: unknown)
      :void {
    this.complexityLevel = response['complexityLevel'];
    super.setSourceFile(filePath, response);
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
  protected updateDataList(newLine) : void {
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
   * source decorations.
   * @return {unknown}
   */
  getSourceDecorations() : {range:monaco.Range, options: unknown}[] {
    // create source file decoration info
    const decorations = [];
    for (let i = 0; i < this.dataList.length; i++) {
      const data = this.dataList[i];
      decorations.push({
        range: new monaco.Range(data.start, 1, data.end, 1),
        options: {
          isWholeLine: true,
          className: data.value >= this.complexityLevel ?
            'sourceComplex' : 'sourceNotComplex',
          glyphMarginClassName: 'soureMarginBlock',
        },
      });
    }

    return decorations;
  }
}
