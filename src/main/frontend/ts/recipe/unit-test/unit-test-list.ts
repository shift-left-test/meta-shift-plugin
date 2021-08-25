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
        formatter: 'textarea'},
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
