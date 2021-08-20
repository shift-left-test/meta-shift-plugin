import {customElement} from 'lit/decorators.js';
import {PagedTable} from '../../common/paged-table';

@customElement('code-violation-recipes')
/**
 * branch coverage recipes.
 */
export class CodeViolationRecipes extends PagedTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [
      {title: 'Recipe', field: 'name', widthGrow: 1},
      {title: 'Lines of Code', field: 'linesOfCode', widthGrow: 1,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Issues', field: 'total', widthGrow: 1,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Major', field: 'first', widthGrow: 1,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Minor', field: 'second', widthGrow: 1,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Info', field: 'third', widthGrow: 1,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Density', field: 'ratio', widthGrow: 1,
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
    window.location.href = `../${row.getData().name}/code_violations`;
  }
}
