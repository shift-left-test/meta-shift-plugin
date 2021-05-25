import { customElement } from 'lit/decorators.js';

import { FilesTable } from '../common/files-table';

@customElement('cache-availability-list')
export class CacheAvailabilityList extends FilesTable {
  constructor() {
    super();

    this.columns = [ //Define Table Columns
      { title:"Signature", field:"signature", widthGrow:1},
      { title:"Available", field:"available", width:100},
    ]
  }
}