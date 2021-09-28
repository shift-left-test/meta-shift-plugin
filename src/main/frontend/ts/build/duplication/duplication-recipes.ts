import {customElement} from 'lit/decorators.js';
import {PagedTable} from '../../common/paged-table';
import {Constants} from '../../common/utils';
import variables from '../../../scss/vars.scss';

@customElement('duplication-recipes')
/**
 * branch coverage recipes.
 */
export class DuplicationRecipes extends PagedTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [
      {title: 'Recipe', field: 'name', widthGrow: 1},
      {title: 'Lines of Code', field: 'linesOfCode',
        width: Constants.LinesOfCodeWidth,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Duplicate', field: 'first',
        width: Constants.IssueCountWidth + 10,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Unique', field: 'second',
        width: Constants.IssueCountWidth + 10,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Ratio', field: 'ratio',
        width: Constants.RatioWidth,
        formatter: 'progress',
        formatterParams: {min: 0, max: 1, color: variables.qualifiedFailColor,
          legend: function(value) {
            return Math.floor(value * 100) + '%';
          },
        },
        accessorDownload: this.progressCellAccessorDownload.bind(this)},
      {title: 'Qualified', field: 'qualified',
        width: Constants.QualifiedWidth,
        formatter: this.qualifiedCellformatter.bind(this)},
    ];
  }

  /**
   * recipe click event handler.
   * @param {unknown} e
   * @param {unknown} row
   */
  _handleRowClicked(e, row) {
    window.location.href = `../${row.getData().name}/duplications`;
  }
}
