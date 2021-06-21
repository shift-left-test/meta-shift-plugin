import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../common/files-table';

@customElement('code-violation-files')
/**
 * code violation files.
 */
export class CodeViolationFiles extends FilesTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.fileView = 'code-violation-file-view';

    this.columns = [ // Define Table Columns
      {title: 'File', field: 'file', widthGrow: 1},
      {title: 'Major', field: 'major', width: 100},
      {title: 'Minor', field: 'minor', width: 100},
      {title: 'Info', field: 'info', width: 100},
    ];
  }
}
