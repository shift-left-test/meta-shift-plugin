import { customElement } from 'lit/decorators.js';

import { FilesTable } from '../common/files-table';

@customElement('comment-files')
export class CommentFiles extends FilesTable {
  constructor() {
    super();

    this.columns = [ //Define Table Columns
      { title:"File", field:"file", widthGrow:1},
      { title:"Lines", field:"lines", width:100},
      { title:"CommentLines", field:"commentLines", width:200},
    ];
  }
}