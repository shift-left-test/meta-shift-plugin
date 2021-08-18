import {customElement} from 'lit/decorators.js';
import {PagedTable} from '../../common/paged-table';
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
      {title: 'Lines of Code', field: 'linesOfCode', widthGrow: 1,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Duplicate', field: 'first', widthGrow: 1,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Unique', field: 'second', widthGrow: 1,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Ratio', field: 'ratio',
        formatter: 'progress',
        formatterParams: {min: 0, max: 1, color: [variables.qualifiedFailColor],
          legend: function(value) {
            return Math.floor(value * 100) + '%';
          },
        }, widthGrow: 1},
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
    window.location.href = `../${row.getData().name}/duplications`;
  }
}
