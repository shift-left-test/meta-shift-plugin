import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../common/files-table';

@customElement('shared-state-cache-list')
/**
 * shared state cache list.
 */
export class SharedStateCacheList extends FilesTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [ // Define Table Columns
      {title: 'Signature', field: 'signature', widthGrow: 1},
      {title: 'Available', field: 'available', width: 100},
    ];
  }
}
