import { customElement } from 'lit/decorators.js';

import { FilesTable } from '../common/files-table';

@customElement('mutation-test-list')
export class MutationTestList extends FilesTable {
  constructor() {
    super();

    this.fileView = "mutation-test-file-view";

    this.columns = [ //Define Table Columns
      { title:"File", field:"file", widthGrow:2},
      // TODO: check mutated class is possible
      //{ title:"MutatedClass", field:"mutatedClass", widthGrow:1},
      { title:"MutatedMethod", field:"mutatedMethod", widthGrow:1},
      { title:"Line", field:"line", width:100},
      { title:"Mutator", field:"mutator", width:100},
      { title:"Status", field:"status", width:100, formatter: this._statusCellFormatter.bind(this)},
      { title:"KillingTest", field:"killingTest", widthGrow:2},
    ]
  }

  _statusCellFormatter(cell, formatterParams, onRendered) {
    var statusClass = 'badge bg-na'
    switch(cell.getValue()) {
      case 'KILLED':
        statusClass = 'badge bg-pass'
        break;
      case 'SURVIVED':
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