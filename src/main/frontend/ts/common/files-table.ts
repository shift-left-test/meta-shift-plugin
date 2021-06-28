import {html, LitElement} from 'lit';
import {customElement, property, query} from 'lit/decorators.js';
import Tabulator from 'tabulator-tables';
import {FileDetail} from './file-detail';

@customElement('files-table')
/**
 * files table.
 */
export class FilesTable extends LitElement {
  @property() fileView

  @query('#files-table') filesTable

  private requestFileDetailFunc;
  private tabulatorTable;
  protected columns;

  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [];
  }

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
    return html`<div style="width:100%" id="files-table"></div>`;
  }

  /**
   * first updated
   */
  firstUpdated() : void {
    this.tabulatorTable = new Tabulator(this.filesTable, {
      rowClick: this._handleFileClicked.bind(this),
      pagination: 'local',
      paginationSize: 10,
      layout: 'fitColumns', // fit columns to width of table (optional)
      columns: this.columns,
    });
  }

  /**
   * handle file click event.
   * @param {unknown} e
   * @param {unknown} row
   */
  private _handleFileClicked(e, row) {
    if (this.requestFileDetailFunc !== undefined) {
      const filePath = row.getData().file;
      this.requestFileDetailFunc(filePath, function(model) {
        const fileView = <FileDetail>document.querySelector(this.fileView);
        fileView.setSourceFile(filePath, model.responseJSON);
      }.bind(this));
    }
  }

  /**
   * set ajax func.
   * @param {unknown} requestFilesFunc
   * @param {unknown} requestFileDetailFunc
   */
  setAjaxFunc(requestFilesFunc: (callback) => void,
      requestFileDetailFunc = undefined) : void {
    const that = this;

    requestFilesFunc(function(model) {
      that.tabulatorTable.setData(model.responseJSON);
    });
    this.requestFileDetailFunc = requestFileDetailFunc;
  }
}
