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

  private requestFilesFunc;
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
      pagination: 'remote',
      paginationSize: 10,
      ajaxRequestFunc: this._handleAjaxRequest.bind(this),
      ajaxSorting: true,
      layout: 'fitColumns', // fit columns to width of table (optional)
      columns: this.columns,
    });
  }

  /**
   * handle ajax request.
   * @param {unknown} url
   * @param {unknown} config
   * @param {unknown} params
   * @return {Promise}
   */
  private _handleAjaxRequest(url, config, params) {
    const that = this;
    return new Promise(function(resolve, ) {
      that.requestFilesFunc(params.page, params.size, params.sorters,
          function(model) {
            console.log(model.responseJSON);
            resolve(model.responseJSON);
          }
      );
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
  setAjaxFunc(requestFilesFunc: (page, size, sorters, callback) => void,
      requestFileDetailFunc = undefined) : void {
    this.requestFilesFunc = requestFilesFunc;
    this.requestFileDetailFunc = requestFileDetailFunc;

    // just triggering ajaxRequestFunc.
    // url('meta-shift') has no meaning, because we replace ajaxRequestFunc.
    this.tabulatorTable.setData('meta-shift');
  }
}
