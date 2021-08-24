import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../files-table';

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

    this.columns = [ // Define Table Columns
      {title: 'File', field: 'name', widthGrow: 1},
      {title: 'Abnormal', field: 'first', width: 100,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Normal', field: 'second', width: 100,
        formatter: this.localeNumberString.bind(this)},
    ];
  }
}
