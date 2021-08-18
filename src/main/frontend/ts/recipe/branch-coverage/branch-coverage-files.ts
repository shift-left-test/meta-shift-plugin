import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../files-table';

@customElement('branch-coverage-files')
/**
 * Coverage Files.
 */
export class BranchCoverageFiles extends FilesTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [ // Define Table Columns
      {title: 'File', field: 'name', widthGrow: 1},
      {title: 'Coverage', field: 'ratio',
        formatter: 'progress',
        formatterParams: {min: 0, max: 1, legend: function(value) {
          return Math.floor(value * 100) + '%';
        }}, width: 200},
    ];
  }
}
