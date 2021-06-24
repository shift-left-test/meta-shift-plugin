import {html, LitElement} from 'lit';
import {customElement, property, query} from 'lit/decorators.js';
import Split from 'split.js';

import * as monaco from 'monaco-editor';

@customElement('file-detail')
/**
 * file detail.
 */
export class FileDetail extends LitElement {
  @query('#source-code-editor') sourceCodeEditor
  @query('#editor-panel') editorPanel
  @query('#data-list-panel') dataListPanel

  private codeEditor;

  protected filePath;
  protected dataList;

  protected currentLine;
  @property({type: Array}) protected currentDataList = [];

  /**
   * create render root.
   * @return {unknown}
   */
  createRenderRoot() : ShadowRoot | LitElement {
    return this;
  }

  /**
   * set source file
   * @param {string} filePath
   * @param {unknown} response
   */
  setSourceFile(filePath: string, response: unknown)
      : void {
    this.filePath = filePath;
    this.dataList = response['dataList'];


    this.codeEditor.setValue(response['content']);
    this.codeEditor.deltaDecorations([], this.getSourceDecorations());
    this.codeEditor.layout();
    this.updateDataList(undefined);
  }

  /**
   * render.
   * @return {unknown}
   */
  render() : unknown {
    return html`<div class="split-panel" style="height: 500px">
        <div id="editor-panel">
          <h3>Source - ${this.filePath}</h3>
          <div id="source-code-editor" class="monaco-editor"></div>
        </div>
        <div id="data-list-panel">
          ${this.renderDataList()}
        </div>
      </div>`;
  }

  /**
   * render data list.
   * @return {unknown}
   */
  renderDataList() : unknown {
    return html``;
  }

  /**
   * return source decorations.
   * @return {unknown}
   */
  getSourceDecorations() : {range: monaco.Range, options: unknown}[] {
    // create source file decoration info
    const decorations = [];
    for (let i = 0; i < this.dataList.length; i++) {
      const data = this.dataList[i];
      decorations.push({
        range: new monaco.Range(data.line, 1, data.line, 1),
        options: {
          isWholeLine: true,
          className: 'sourceBlock',
          glyphMarginClassName: 'sourceMarginBlock',
        },
      });
    }

    return decorations;
  }

  /**
   * first updated.
   */
  firstUpdated() : void {
    // eslint-disable-next-line new-cap
    Split([this.editorPanel, this.dataListPanel], {
      sizes: [72, 25],
      onDragEnd: this._handleResize.bind(this),
    });

    this.codeEditor = monaco.editor.create(
        this.sourceCodeEditor, {
          value: [].join('\n'),
          language: 'bash',
          readOnly: true,
        }
    );

    this.codeEditor.onMouseDown(function(e) {
      this.updateDataList(e.target.position.lineNumber);
    }.bind(this));

    window.addEventListener('resize', this._handleResize.bind(this));
  }

  /**
   * updated.
   */
  updated() : void {
    this.codeEditor.layout();
  }

  /**
   * handle resize event.
   */
  _handleResize() : void {
    this.codeEditor.layout();
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
        if (data.line == newLine) {
          datas.push(data);
        }
      }
    }
    this.currentDataList = datas;
  }
}
