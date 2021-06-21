import {html} from 'lit';
import {customElement} from 'lit/decorators.js';

import * as monaco from 'monaco-editor';

import {FileDetail} from '../common/file-detail';

@customElement('complexity-file-view')
/**
 * complexity file view.
 */
export class ComplexityFileView extends FileDetail {
  /**
   * render data list.
   * @return {unknown}
   */
  renderDataList() : unknown {
    return html`
      `;
  }

  /**
   * source decorations.
   * @return {unknown}
   */
  getSourceDecorations() : {range:monaco.Range, options: unknown}[] {
    // create source file decoration info
    const decorations = [];
    for (let i = 0; i < this.dataList.length; i++) {
      const data = this.dataList[i];
      decorations.push({
        range: new monaco.Range(data.start, 1, data.end, 1),
        options: {
          isWholeLine: true,
          className: 'sourceBlock',
          glyphMarginClassName: 'soureMarginBlock',
        },
      });
    }

    return decorations;
  }
}
