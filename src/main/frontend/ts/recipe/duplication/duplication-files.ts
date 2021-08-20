import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../files-table';

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
      {title: 'File', field: 'name', widthGrow: 1},
      {title: 'Lines', field: 'linesOfCode', width: 100,
        formatter: this.localeNumberString.bind(this)},
      {title: 'DuplicatedLines', field: 'first', width: 200,
        formatter: this.localeNumberString.bind(this)},
    ];
  }
}
