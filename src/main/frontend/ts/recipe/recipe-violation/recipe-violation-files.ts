import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../files-table';

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
      {title: 'File', field: 'name', widthGrow: 1},
      {title: 'Major', field: 'first', width: 100,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Minor', field: 'second', width: 100,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Info', field: 'third', width: 100,
        formatter: this.localeNumberString.bind(this)},
    ];
  }
}