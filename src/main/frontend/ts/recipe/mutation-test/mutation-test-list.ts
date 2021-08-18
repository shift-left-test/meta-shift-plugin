import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../files-table';

@customElement('mutation-test-list')
/**
 * mutation test list.
 */
export class MutationTestList extends FilesTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [ // Define Table Columns
      {title: 'File', field: 'name', widthGrow: 1},
      {title: 'Killed', field: 'first', width: 100,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Survived', field: 'second', width: 100,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Skipped', field: 'third', width: 100,
        formatter: this.localeNumberString.bind(this)},
    ];
  }
}
