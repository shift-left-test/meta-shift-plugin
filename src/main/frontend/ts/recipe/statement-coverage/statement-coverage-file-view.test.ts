import {fixture, html} from '@open-wc/testing';
import {StatementCoverageFileView} from './statement-coverage-file-view';

import {assert} from 'chai';

suite('statement-coverage-file-view', () => {
  test('is defined', () => {
    const el = document.createElement('statement-coverage-file-view');
    assert.instanceOf(el, StatementCoverageFileView);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <statement-coverage-file-view filePath='test'>
      </statement-coverage-file-view>
    `)) as StatementCoverageFileView;

    assert.isNotNull(el.querySelector('div#editor-panel'),
        el.outerHTML);
  });
});
