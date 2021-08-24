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
      {title: 'Lines of Code', field: 'linesOfCode', width: 200,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Issues', field: 'total', width: 120,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Major', field: 'first', width: 120,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Minor', field: 'second', width: 120,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Info', field: 'third', width: 120,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Density', field: 'ratio', width: 100,
        formatter: this.floatNumberString.bind(this),
        accessorDownload: this.floatNumberCellAccessorDownload.bind(this)},
      {title: 'Qualified', field: 'qualified', width: 120,
        formatter: this.qualifiedCellformatter.bind(this)},
    ];
  }
}
