import { customElement } from 'lit/decorators.js';

import { FilesTable } from '../common/files-table';

@customElement('coverage-files')
export class CoverageFiles extends FilesTable {
  constructor() {
    super();

    this.fileView = "coverage-file-view";

    this.columns = [ //Define Table Columns
      { title:"File", field:"file", widthGrow:1},
      { title:"Line Coverage", field:"lineCoverage", width:200},
      { title:"Branch Coverage", field:"branchCoverage", width:200},
    ]
  }
}