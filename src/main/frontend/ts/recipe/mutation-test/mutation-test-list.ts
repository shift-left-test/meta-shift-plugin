/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {customElement} from 'lit/decorators.js';
import {FilesTable} from '../files-table';
import {Constants} from '../../common/utils';
import variables from '../../../scss/vars.scss';

@customElement('mutation-test-list')
/**
 * mutation test list.
 */
export class MutationTestList extends FilesTable {
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
      {title: 'Tests', field: 'total', width: 120,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Killed', field: 'first', width: 120,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Survived', field: 'second', width: 120,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Skipped', field: 'third', width: 120,
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
