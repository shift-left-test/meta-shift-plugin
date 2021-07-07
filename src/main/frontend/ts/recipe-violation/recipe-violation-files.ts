import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../common/files-table';

@customElement('recipe-violation-files')
/**
 * recipe violation files.
 */
export class RecipeViolationFiles extends FilesTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [ // Define Table Columns
      {title: 'File', field: 'file', widthGrow: 1},
      {title: 'Major', field: 'major', width: 100},
      {title: 'Minor', field: 'minor', width: 100},
      {title: 'Info', field: 'info', width: 100},
    ];
  }
}
