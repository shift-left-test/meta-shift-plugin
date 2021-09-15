import {customElement} from 'lit/decorators.js';
import {FilesTable} from '../files-table';
import variables from '../../../scss/vars.scss';

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
      {title: 'Lines of Code', field: 'linesOfCode', width: 200,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Duplicate', field: 'first', width: 120,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Unique', field: 'second', width: 120,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Ratio', field: 'ratio',
        formatter: 'progress',
        formatterParams: {min: 0, max: 1, color: variables.qualifiedFailColor,
          legend: function(value) {
            return Math.floor(value * 100) + '%';
          },
        },
        accessorDownload: this.progressCellAccessorDownload.bind(this),
        width: 200},
      {title: 'Qualified', field: 'qualified', width: 120,
        formatter: this.qualifiedCellformatter.bind(this)},
    ];
  }
}
