import {customElement} from 'lit/decorators.js';

import {FilesTable} from '../common/files-table';

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
      {title: 'File', field: 'file', widthGrow: 1},
      {title: 'Comment Rate', field: 'ratio',
        formatter: 'progress',
        formatterParams: {min: 0, max: 1, legend: function(value) {
          return Math.floor(value * 100) + '%';
        }}, width: 200},
      {title: 'Lines', field: 'lines', width: 100,
        formatter: this.localeNumberString.bind(this)},
      {title: 'Comment Lines', field: 'commentLines', width: 200,
        formatter: this.localeNumberString.bind(this)},
    ];
  }
}
