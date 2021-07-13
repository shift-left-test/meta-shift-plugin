import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../common/files-table';

@customElement('duplication-files')
/**
 * duplication files.
 */
export class DuplicationFiles extends FilesTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [ // Define Table Columns
      {title: 'File', field: 'file', widthGrow: 1},
      {title: 'Lines', field: 'lines', width: 100,
        formatter: this.localeNumberString.bind(this)},
      {title: 'DuplicatedLines', field: 'duplicatedLines', width: 200,
        formatter: this.localeNumberString.bind(this)},
    ];
  }
}
