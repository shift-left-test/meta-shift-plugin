/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {html, LitElement} from 'lit';
import {customElement, property, query} from 'lit/decorators.js';
import Tabulator from 'tabulator-tables';
import {Utils} from '../common/utils';
import * as XLSX from 'xlsx';

@customElement('paged-table')
/**
 * recipes table.
 */
export class PagedTable extends LitElement {
  @query('.paged-table') recipesTable
  @query('#filter-value') filterValue

  @property() downloadFileName;

  protected tabulatorTable: Tabulator;
  protected columns;
  protected index; // 'id' field for the data.
  protected hasRowClick;
  protected filterColumnIndex;

  /**
   * constructor.
   */
  constructor() {
    super();

    this.downloadFileName = 'data';
    this.hasRowClick = true;
    this.columns = [{title: 'test', field: 'test'}];
    this.filterColumnIndex = 0;
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
    return html`
    <div class="table-toolbox">
      <span>
        <input id="filter-value" type="text"
          placeholder="Filter table by '${this.columns[
      this.filterColumnIndex].title}'"
        @keyup="${this._updateFilter}" />
      </span>
      <button class="export-button" @click="${this._handleDownloadClicked}">
        Export to Excel</button>
    </div>
    <div class="paged-table"></div>`;
  }

  /**
   * first updated
   */
  firstUpdated() : void {
    this.tabulatorTable = new Tabulator(this.recipesTable, {
      persistenceMode: 'local',
      persistenceID: 'table',
      persistence: {
        page: true,
      },
      selectable: this.hasRowClick,
      rowClick: this.hasRowClick ? this._handleRowClicked.bind(this) : null,
      pagination: 'local',
      paginationSize: 10,
      paginationSizeSelector: [10, 25, 50, 100],
      layout: 'fitColumns',
      columns: this.columns,
      tooltipsHeader: true,
      index: this.index,
    });
  }

  /**
   * set ajax func.
   * @param {unknown} requestDataFunc
   */
  setAjaxFunc(requestDataFunc: (callback) => void) : void {
    const that = this;

    requestDataFunc(function(model) {
      that.tabulatorTable.setData(model.responseJSON);
      that.onTableUpdated();
    });
  }

  /**
   * handle row click event.
   * @param {unknown} e
   * @param {unknown} row
   */
  _handleRowClicked(e, row) {
    // TODO
  }

  /**
   * update filter.
   */
  _updateFilter() {
    this.tabulatorTable.setFilter(this.columns[this.filterColumnIndex].field,
        'like',
        this.filterValue.value);
  }

  /**
   * handle download button click event.
   */
  _handleDownloadClicked() {
    (window as any).XLSX = XLSX;
    this.tabulatorTable.download('xlsx', `${this.downloadFileName}.xlsx`,
        {sheetName: 'Report'});
  }

  /**
   * callback table data updated
   */
  onTableUpdated() {
    this.tabulatorTable.setSort([{column: 'name', dir: 'asc'}]);
  }


  /**
   * line of code formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected localeNumberString(cell: any) : unknown {
    return `<div class="locale-number">
      ${cell.getValue().toLocaleString()}</div>`;
  }

  /**
   * density formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected floatNumberString(cell: any) : unknown {
    return `<div class="locale-number">
      ${Utils.toFixedFloor(cell.getValue())}</div>`;
  }

  /**
   * qualifier downlaodformatter
   * @param {unknown} value
   * @return {unknown}
   */
  protected floatNumberCellAccessorDownload(value) {
    return Utils.toFixedFloor(value);
  }

  /**
   * qualifier formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected qualifierCellformatter(cell) {
    let available = false;
    let ratio = 0;

    if (cell.getValue()) {
      available = cell.getValue().available;
      ratio = cell.getValue().ratio;
    }

    if (available) {
      return `
      <div class="progress-bar-legend">
          ${Utils.toFixedFloor(ratio)}
      </div>
      `;
    } else {
      return `<div class="progress-bar-legend">N/A</div>`;
    }
  }

  /**
   * qualifier downlaodformatter
   * @param {unknown} value
   * @return {unknown}
   */
  protected qualifierCellAccessorDownload(value) {
    if (value.available) {
      return `${Utils.toFixedFloor(value.ratio)}`;
    } else {
      return `N/A`;
    }
  }

  /**
   * percent type qualifier formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected qualifierPercentCellformatterPositive(cell) {
    let available = false;
    let ratio = 0;

    if (cell.getValue()) {
      available = cell.getValue().available;
      ratio = cell.getValue().ratio * 100;
    }

    if (available) {
      return `
      <div class="progress">
        <div class="progress-bar positive"
          role="progressbar" style="width: ${ratio}%">
        </div>
        <div class="progress-bar-legend">${Math.floor(ratio)}% </div>
      </div>
      `;
    } else {
      return `<div class="progress-bar-legend">N/A</div>`;
    }
  }

  /**
   * percent type qualifier formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected qualifierPercentCellformatterNegative(cell) {
    let available = false;
    let ratio = 0;

    if (cell.getValue()) {
      available = cell.getValue().available;
      ratio = cell.getValue().ratio * 100;
    }

    if (available) {
      return `
      <div class="progress">
        <div class="progress-bar negative"
          role="progressbar" style="width: ${ratio}%">
        </div>
        <div class="progress-bar-legend">${Math.floor(ratio)}% </div>
      </div>
      `;
    } else {
      return `<div class="progress-bar-legend">N/A</div>`;
    }
  }

  /**
   * percent type qualified formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected qualifiedCellformatter(cell) {
    return `<div class="icon">
      <i class="fas ${cell.getValue() ? 'fa-check-circle' : 'fa-times-circle'}">
      </i>
      </div>`;
  }

  /**
   * percent type qualifier formatter.
   * @param {unknown} value
   * @return {unknown}
   */
  protected progressCellAccessorDownload(value) {
    return Utils.toFixedFloor(value);
  }

  /**
   * sort function for qualifier.
   * @param {unknown} a
   * @param {unknown} b
   * @return {unknown}
   */
  protected qualifierSorter(a, b) {
    return a.ratio - b.ratio;
  }

  /**
   * tootip for qualifier cell.
   * @param {unknown} cell
   * @return {unknown}
   */
  protected qualifierTooltip(cell: any) {
    if (cell.getValue().available) {
      return cell.getValue().numerator.toLocaleString() +
        ' / ' +
        cell.getValue().denominator.toLocaleString();
    }

    return 'N/A';
  }
}
