import { customElement, property } from 'lit/decorators.js';

import { FilesTable } from '../common/files-table';

@customElement('duplication-files')
export class DuplicationFiles extends FilesTable {
  constructor() {
    super();

    this.columns = [ //Define Table Columns
      { title:"File", field:"file", widthGrow:1},
      { title:"Lines", field:"lines", width:100},
      { title:"DuplicatedLines", field:"duplicatedLines", width:200},
    ];
  }
}