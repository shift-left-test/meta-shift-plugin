import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../common/files-table';

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
      {title: 'File', field: 'file', widthGrow: 1},
      {title: 'Killed', field: 'killed', width: 100},
      {title: 'Survived', field: 'survived', width: 100},
      {title: 'Skipped', field: 'skipped', width: 100},
    ];
  }
}
