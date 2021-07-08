import {html, LitElement} from 'lit';
import {customElement, property, query} from 'lit/decorators.js';
import Split from 'split.js';

import {Extension} from '@codemirror/state';
import {EditorView, Decoration, DecorationSet, ViewPlugin, ViewUpdate,
  drawSelection} from '@codemirror/view';
import {EditorState} from '@codemirror/basic-setup';
import {RangeSet, RangeSetBuilder} from '@codemirror/rangeset';
import {cpp} from '@codemirror/lang-cpp';
import {StreamLanguage} from '@codemirror/stream-parser';
import {shell} from '@codemirror/legacy-modes/mode/shell';
import {defaultHighlightStyle} from '@codemirror/highlight';
import {lineNumbers} from '@codemirror/gutter';

@customElement('file-detail')
/**
 * file detail.
 */
export class FileDetail extends LitElement {
  @query('#source-code-editor') sourceCodeEditor
  @query('#editor-panel') editorPanel
  @query('#data-list-panel') dataListPanel

  private codeEditor: EditorView;
  protected dataList;

  protected currentLine;

  @property() filePath;
  @property({type: Number}) scrollX;
  @property({type: Number}) scrollY;
  @property({type: Array}) protected currentDataList = [];

  /**
   * create render root.
   * @return {unknown}
   */
  createRenderRoot() : ShadowRoot | LitElement {
    return this;
  }

  /**
   * render.
   * @return {unknown}
   */
  render() : unknown {
    if (this.filePath) {
      return html`<div class="split-panel">
          <div id="editor-panel">
            <h3>Source - ${this.filePath}</h3>
            <div id="source-code-editor" class="source-code-editor"
              style="display: inline-block">
            </div>
          </div>
          <div id="data-list-panel">
            ${this.renderDataList()}
          </div>
        </div>`;
    } else {
      return html`Click on a file to view its details.`;
    }
  }

  /**
   * render data list.
   * @return {unknown}
   */
  renderDataList() : unknown {
    return html``;
  }

  /**
   * first updated.
   */
  firstUpdated() : void {
    if (this.filePath) {
      // eslint-disable-next-line new-cap
      Split([this.editorPanel, this.dataListPanel], {
        sizes: [75, 25],
      });

      this.codeEditor = new EditorView({
        parent: this.sourceCodeEditor,
      });
    }
  }

  /**
   * set ajax func.
   * @param {unknown} requestFileDetailFunc
   * @param {unknown} filePath
   */
  setAjaxFunc(requestFileDetailFunc = undefined) : void {
    if (this.filePath) {
      requestFileDetailFunc(this.filePath, function(model) {
        this.setSourceFile(model.responseJSON);
        window.scroll(this.scrollX, this.scrollY);
      }.bind(this));
    }
  }

  /**
   * set source file
   * @param {unknown} response
   */
  setSourceFile(response: unknown)
    : void {
    this.dataList = response['dataList'];

    const fileExtension = this.filePath.split('.').pop().toLowerCase();

    let syntaxHighlighter;
    if (['cpp', 'c', 'cxx', 'cc', 'h', 'hpp', 'hxx']
        .some((x) => x === fileExtension)) {
      syntaxHighlighter = cpp();
      console.log('cpp');
    } else {
      syntaxHighlighter = StreamLanguage.define(shell);
      console.log('shell');
    }

    // TODO: detect language from file extension.
    this.codeEditor.setState(EditorState.create({
      doc: response['content'],
      extensions: [
        lineNumbers(),
        drawSelection(),
        defaultHighlightStyle,
        EditorView.editable.of(false),
        syntaxHighlighter,
        this.addDecoration(),
      ],
    }));

    this.updateDataList(undefined);
  }

  /**
   * show decoration
   * @return {unknown}
   */
  addDecoration() : Extension {
    const that = this;
    const showBlocks = ViewPlugin.fromClass(class {
      decorations: DecorationSet

      /**
       * constructor
       * @param {unknown} view
       */
      constructor(view: EditorView) {
        this.decorations = that.getSourceDecorations(view);
      }

      /**
       * update
       * @param {unknown} update
       */
      update(update: ViewUpdate) {
        if (update.docChanged || update.viewportChanged) {
          this.decorations = that.getSourceDecorations(update.view);
        } else if (update.selectionSet) {
          const pos = update.view.state.selection.ranges[0].from;
          const line = update.view.state.doc.lineAt(pos);
          that.updateDataList(line.number);
        }
      }
    }, {
      decorations: (v) => v.decorations,
    });

    return showBlocks;
  }

  /**
   * return source decorations.
   * @param {unknown} view
   * @return {unknown}
   */
  getSourceDecorations(view: EditorView) : RangeSet<Decoration> {
    const sourceBlock = Decoration.line({
      attributes: {class: 'sourceBlock'},
    });
    const builder = new RangeSetBuilder<Decoration>();
    for (const {from, to} of view.visibleRanges) {
      for (let pos = from; pos <= to;) {
        const line = view.state.doc.lineAt(pos);
        if (this.dataList.some((data) => data.line == line.number)) {
          builder.add(line.from, line.from, sourceBlock);
        }
        pos = line.to + 1;
      }
    }

    return builder.finish();
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
        if (data.line == newLine) {
          datas.push(data);
        }
      }
    }
    this.currentDataList = datas;
  }
}
