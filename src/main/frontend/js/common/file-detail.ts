import { html, css, LitElement } from 'lit';
import { customElement, property, query } from 'lit/decorators.js';
import Split from 'split.js';

import * as monaco from 'monaco-editor';

export class FileDetail extends LitElement {

  @query('#source-code-editor') sourceCodeEditor
  @query('#editor-panel') editorPanel
  @query('#data-list-panel') dataListPanel

  private codeEditor;

  protected filePath;
  protected dataList;

  protected currentLine;
  @property({type:Array}) protected currentDataList = [];
  
  createRenderRoot() {
    return this;
  }

  setSourceFile(filePath, content, dataList) {
    this.filePath = filePath;
    this.dataList = dataList;


    this.codeEditor.setValue(content);
    this.codeEditor.deltaDecorations([], this.getSourceDecorations());
    this.codeEditor.layout();
    this.updateDataList(undefined);
  }

  render() {
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

  renderDataList() {
    return html``
  }

  getSourceDecorations() {
    // create source file decoration info
    var decorations = [];
    for (var i = 0; i < this.dataList.length; i++) {
      var data = this.dataList[i];
      decorations.push({
        range: new monaco.Range(data.line, 1, data.line, 1),
        options: {
          isWholeLine: true,
          className: 'sourceBlock',
          glyphMarginClassName: 'sourceMarginBlock'
        }
      })
    }
    
    return decorations;
  }

  firstUpdated(changedProperties) {
    Split([this.editorPanel, this.dataListPanel], {
      sizes: [72, 25],
      onDragEnd: this._handleResize.bind(this)
    });

    this.codeEditor = monaco.editor.create(
      this.sourceCodeEditor, {
      value: [].join('\n'),
      language: 'bash',
      readOnly: true
    });
  
    this.codeEditor.onMouseDown(function (e) {
      this.updateDataList(e.target.position.lineNumber);
    }.bind(this));

    window.addEventListener('resize', this._handleResize.bind(this));
  }

  updated() {
    this.codeEditor.layout();
  }

  _handleResize() {
    this.codeEditor.layout();
  }

  private updateDataList(newLine) {
    if (newLine != undefined && newLine === this.currentLine) {
      return;
    }

    this.currentLine = newLine;
    var datas = [];

    if (this.currentLine !== undefined) {
      for (var i = 0; i < this.dataList.length; i++) {
        var data = this.dataList[i];
        if (data.line == newLine) {
          datas.push(data);
        }
      }
    }
    this.currentDataList = datas;
  }
}