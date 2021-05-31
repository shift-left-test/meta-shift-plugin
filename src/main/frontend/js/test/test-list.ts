import { html } from 'lit';
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
    var statusClass = 'badge bg-na'
    switch(cell.getValue()) {
      case 'PASSED':
        statusClass = 'badge bg-pass'
        break;
      case 'FAILED':
        statusClass = 'badge bg-fail'
        break;
      case 'ERROR':
        statusClass = 'badge bg-error'
        break;
    }
    return `
      <span class="${statusClass}">${cell.getValue()}</span>
      `
  }
}