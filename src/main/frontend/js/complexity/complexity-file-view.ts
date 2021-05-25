import { html } from 'lit';
import { customElement} from 'lit/decorators.js';

import * as monaco from 'monaco-editor';

import { FileDetail } from '../common/file-detail';

@customElement('complexity-file-view')
export class ComplexityFileView extends FileDetail {

  renderDataList() {
    return html`
      `;
  }

  getSourceDecorations() {
    // create source file decoration info
    var decorations = [];
    for (var i = 0; i < this.dataList.length; i++) {
      var data = this.dataList[i];
      decorations.push({
        range: new monaco.Range(data.start, 1, data.end, 1),
        options: {
          isWholeLine: true,
          className: 'sourceBlock',
          glyphMarginClassName: 'soureMarginBlock'
        }
      })
    }
    
    return decorations;
  }
}