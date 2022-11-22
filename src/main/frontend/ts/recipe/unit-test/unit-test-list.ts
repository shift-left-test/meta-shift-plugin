/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../files-table';
import {Cell} from 'tabulator-tables';

@customElement('unit-test-list')
/**
 * test list.
 */
export class UnitTestList extends FilesTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [ // Define Table Columns
      {title: 'Suite', field: 'suite', widthGrow: 1},
      {title: 'Name', field: 'test', widthGrow: 1},
      {title: 'Status', field: 'status', width: 100,
        formatter: this._statusCellFormatter.bind(this)},
      {title: 'Message', field: 'message', widthGrow: 2,
        formatter: function(cell) {
          cell.getElement().style.whiteSpace = 'pre-wrap';
          let value = cell.getValue();
          if (value) {
            const entityMap = {
              '&': '&amp;',
              '<': '&lt;',
              '>': '&gt;',
              '"': '&quot;',
              '\'': '&#39;',
              '/': '&#x2F;',
              '`': '&#x60;',
              '=': '&#x3D;',
            };
            value = String(value).replace(/[&<>"'`=/]/g, function(s) {
              return entityMap[s];
            });
            if (value.length > 300) {
              value = value.substring(0, 300) + '...';
            }
          }
          return (value) ? value : '$nbsp;';
        },
      },
    ];
    this.defalutSortColumn = 'status';
    this.hasRowClick = false;
  }

  /**
   * status formatter.
   * @param {unknown} cell
   * @return {unknown}
   */
  _statusCellFormatter(cell: Cell) : unknown {
    let statusClass = 'badge bg-na';
    switch (cell.getValue()) {
      case 'PASSED':
        statusClass = 'badge bg-pass';
        break;
      case 'FAILED':
        statusClass = 'badge bg-fail';
        break;
      case 'ERROR':
        statusClass = 'badge bg-error';
        break;
    }
    return `
      <span class="${statusClass}">${cell.getValue()}</span>
      `;
  }
}
