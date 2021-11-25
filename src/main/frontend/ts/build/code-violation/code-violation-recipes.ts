/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {customElement} from 'lit/decorators.js';
import {PagedTable} from '../../common/paged-table';
import {Constants} from '../../common/utils';

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
      {title: 'Lines of Code', field: 'linesOfCode',
        width: Constants.LinesOfCodeWidth,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Issues', field: 'total',
        width: Constants.IssueCountWidth,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Major', field: 'first',
        width: Constants.IssueCountWidth,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Minor', field: 'second',
        width: Constants.IssueCountWidth,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Info', field: 'third',
        width: Constants.IssueCountWidth,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Density', field: 'ratio',
        width: Constants.RatioWidth,
        formatter: this.floatNumberString.bind(this),
        accessorDownload: this.floatNumberCellAccessorDownload.bind(this)},
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
    window.location.href = `../${row.getData().name}/code_violations`;
  }
}
