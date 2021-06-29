import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../common/files-table';

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

    this.fileView = 'branch-coverage-file-view';

    this.columns = [ // Define Table Columns
      {title: 'File', field: 'file', widthGrow: 1},
      {title: 'Coverage', field: 'coverage',
        formatter: 'progress',
        formatterParams: {min: 0, max: 1, legend: function(value) {
          return Math.floor(value * 100) + '%';
        }}, width: 200},
    ];
  }
}
