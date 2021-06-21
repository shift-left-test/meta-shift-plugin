import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../common/files-table';

@customElement('coverage-files')
/**
 * Coverage Files.
 */
export class CoverageFiles extends FilesTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.fileView = 'coverage-file-view';

    this.columns = [ // Define Table Columns
      {title: 'File', field: 'file', widthGrow: 1},
      {title: 'Line Coverage', field: 'lineCoverage',
        formatter: 'progress',
        formatterParams: {min: 0, max: 1, legend: function(value) {
          return Math.floor(value * 100) + '%';
        }}, width: 200},
      {title: 'Branch Coverage', field: 'branchCoverage',
        formatter: 'progress',
        formatterParams: {min: 0, max: 1, legend: function(value) {
          return Math.floor(value * 100) + '%';
        }}, width: 200},
    ];
  }
}
