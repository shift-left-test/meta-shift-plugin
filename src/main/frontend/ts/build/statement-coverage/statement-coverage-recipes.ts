import {customElement} from 'lit/decorators.js';
import {PagedTable} from '../../common/paged-table';
import variables from '../../../scss/vars.scss';

@customElement('statement-coverage-recipes')
/**
 * branch coverage recipes.
 */
export class StatementCoverageRecipes extends PagedTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [
      {title: 'Recipe', field: 'name', widthGrow: 1},
      {title: 'Lines of Code', field: 'linesOfCode', width: 200,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Statements', field: 'total', width: 120,
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

  /**
   * recipe click event handler.
   * @param {unknown} e
   * @param {unknown} row
   */
  _handleRowClicked(e, row) {
    window.location.href = `../${row.getData().name}/statement_coverage`;
  }
}
