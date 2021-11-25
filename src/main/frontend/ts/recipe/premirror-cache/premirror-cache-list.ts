/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../files-table';

@customElement('premirror-cache-list')
/**
 * premirror cache list.
 */
export class PremirrorCacheList extends FilesTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [ // Define Table Columns
      {title: 'Signature', field: 'signature', widthGrow: 1},
      {title: 'Available', field: 'available', width: 120,
        formatter: this.qualifiedCellformatter.bind(this)},
    ];
    this.defalutSortColumn = 'signature';
    this.hasRowClick = false;
  }
}
