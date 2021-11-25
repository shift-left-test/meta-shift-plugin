/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {customElement, property} from 'lit/decorators.js';
import {PagedTable} from '../common/paged-table';

@customElement('files-table')
/**
 * files table.
 */
export class FilesTable extends PagedTable {
  @property() page
  @property({type: String}) sort
  @property({type: String}) select

  protected defalutSortColumn;

  /**
   * constructor.
   */
  constructor() {
    super();

    this.index = 'name';
    this.page = 1;
    this.sort = '"[]"';
    this.defalutSortColumn = 'name';
  }

  /**
   * handle file click event.
   * @param {unknown} e
   * @param {unknown} row
   */
  _handleRowClicked(e, row) {
    const sorters: [] = this.tabulatorTable.getSorters();
    const sortinfo = sorters.map((o) => {
      return {dir: o['dir'], column: o['field']};
    });

    window.location.href = `.?file=${row.getData().name}` +
        `&scrollX=${window.scrollX}&scrollY=${window.scrollY}` +
        `&page=${this.tabulatorTable.getPage()}` +
        `&sort=${JSON.stringify(sortinfo)}`;
  }

  /**
   * callback table data updated
   */
  onTableUpdated() {
    // to remove quotes parameter string, parse twice.
    let sortinfo = JSON.parse(JSON.parse(this.sort));
    if (sortinfo.length == 0) {
      sortinfo = [
        {column: this.defalutSortColumn, dir: 'asc'},
      ];
    }
    this.tabulatorTable.setSort(sortinfo);
    this.tabulatorTable.setPage(this.page);
    if (this.select) {
      this.tabulatorTable.selectRow(this.select);
    }
  }
}
