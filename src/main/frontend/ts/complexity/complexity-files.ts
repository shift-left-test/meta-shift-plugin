import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../common/files-table';

@customElement('complexity-files')
/**
 * complexity files.
 */
export class ComplexityFiles extends FilesTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.fileView = 'complexity-file-view';

    this.columns = [ // Define Table Columns
      {title: 'File', field: 'file', widthGrow: 1},
      {title: 'Functions', field: 'functions', width: 100},
      {title: 'Complex Functions', field: 'complexFunctions', width: 200},
    ];
  }
}
