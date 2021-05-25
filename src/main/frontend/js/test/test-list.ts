import { customElement, property } from 'lit/decorators.js';

import { FilesTable } from '../common/files-table';

@customElement('test-list')
export class TestList extends FilesTable {
  constructor() {
    super();

    this.columns = [ //Define Table Columns
      { title:"suite", field:"suite", widthGrow:1},
      { title:"name", field:"name", widthGrow:1},
      { title:"status", field:"status", width:100, formatter: this._statusCellFormatter.bind(this)},
      { title:"message", field:"message", widthGrow:2},
    ]
  }

  _statusCellFormatter(cell, formatterParams, onRendered) {
    var statusClass = 'badge badge-secondary'
    switch(cell.getValue()) {
      case 'PASSED':
        statusClass = 'badge badge-success'
        break;
      case 'FAILED':
        statusClass = 'badge badge-danger'
        break;
      case 'ERROR':
        statusClass = 'badge badge-warning'
        break;
    }
    return `
      <span class="${statusClass}">${cell.getValue()}</span>
      `
  }
}