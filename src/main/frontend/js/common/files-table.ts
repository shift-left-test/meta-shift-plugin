import { html, css, LitElement } from 'lit';
import { customElement, property, query } from 'lit/decorators.js';
import Tabulator from 'tabulator-tables';
import { FileDetail } from './file-detail';

import 'tabulator-tables/dist/css/tabulator.min.css';

export class FilesTable extends LitElement {
  @property() fileView

  @query('#files-table') filesTable

  private requestFilesFunc;
  private requestFileDetailFunc;
  private tabulatorTable;
  protected columns;

  createRenderRoot() {
    return this;
  }

  render() {
    return html`<div style="width:100%" id="files-table"></div>`;
  }

  firstUpdated(changedProperties) {
    this.tabulatorTable = new Tabulator(this.filesTable, {
      rowClick: this._handleFileClicked.bind(this),
      pagination: "remote",
      paginationSize: 10,
      ajaxRequestFunc: this._handleAjaxRequest.bind(this),
      ajaxSorting: true,
      layout: "fitColumns", //fit columns to width of table (optional)
      columns: this.columns
    });
  }

  private _handleAjaxRequest(url, config, params) {
    var self = this;
    return new Promise(function (resolve, reject) {
      self.requestFilesFunc(params.page, params.size, function (model) {
        console.log(model.responseJSON);
        resolve(model.responseJSON);
      });
    });
  }

  private _handleFileClicked(e, row) {
    if (this.requestFileDetailFunc !== undefined) {
      var filePath = row.getData().file
      this.requestFileDetailFunc(filePath, function(model) {
        var fileView = <FileDetail>document.querySelector(this.fileView)
        fileView.setSourceFile(filePath, model.responseJSON.content, model.responseJSON.dataList);
      }.bind(this));
    }
  }

  setAjaxFunc(requestFilesFunc, requestFileDetailFunc = undefined) {
    this.requestFilesFunc = requestFilesFunc;
    this.requestFileDetailFunc = requestFileDetailFunc;

    // just trigging ajaxRequestFunc.  
    // url('meta-shift') has no meaning, because we replace ajaxRequestFunc.
    this.tabulatorTable.setData("meta-shift");
  }
}