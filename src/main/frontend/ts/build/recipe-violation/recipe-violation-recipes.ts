import {customElement} from 'lit/decorators.js';
import {PagedTable} from '../../common/paged-table';

@customElement('recipe-violation-recipes')
/**
 * branch coverage recipes.
 */
export class RecipeViolationRecipes extends PagedTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [
      {title: 'Recipe', field: 'name', widthGrow: 1},
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

  /**
   * recipe click event handler.
   * @param {unknown} e
   * @param {unknown} row
   */
  _handleRowClicked(e, row) {
    window.location.href = `../${row.getData().name}/recipe_violations`;
  }
}
