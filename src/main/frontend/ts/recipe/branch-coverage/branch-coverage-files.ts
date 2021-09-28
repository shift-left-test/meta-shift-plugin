import {customElement} from 'lit/decorators.js';
import {FilesTable} from '../files-table';
import {Constants} from '../../common/utils';
import variables from '../../../scss/vars.scss';

@customElement('branch-coverage-files')
/**
 * Coverage Files.
 */
export class BranchCoverageFiles extends FilesTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [ // Define Table Columns
      {title: 'File', field: 'name', widthGrow: 1},
      {title: 'Lines of Code', field: 'linesOfCode',
        width: Constants.LinesOfCodeWidth,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Branches', field: 'total', width: 120,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Covered', field: 'first', width: 120,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Uncovered', field: 'second', width: 120,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Ratio', field: 'ratio',
        formatter: 'progress',
        formatterParams: {min: 0, max: 1, color: variables.qualifiedPassColor,
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
