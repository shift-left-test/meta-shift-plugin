import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../files-table';

@customElement('comment-files')
/**
 * comment files.
 */
export class CommentFiles extends FilesTable {
  /**
   * constructor.
   */
  constructor() {
    super();

    this.columns = [ // Define Table Columns
      {title: 'File', field: 'name', widthGrow: 1},
      {title: 'Comment Rate', field: 'ratio',
        formatter: 'progress',
        formatterParams: {min: 0, max: 1, legend: function(value) {
          return Math.floor(value * 100) + '%';
        }},
        accessorDownload: this.progressCellAccessorDownload.bind(this),
        width: 200},
      {title: 'Lines', field: 'linesOfCode', width: 100,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Comment Lines', field: 'first', width: 200,
        formatter: this.localeNumberString.bind(this)},
    ];
    this.hasRowClick = false;
  }
}