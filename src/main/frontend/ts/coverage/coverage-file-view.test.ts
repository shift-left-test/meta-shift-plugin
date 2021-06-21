import {fixture, html} from '@open-wc/testing';
import {CoverageFileView} from './coverage-file-view';

import {assert} from 'chai';

suite('coverage-file-view', () => {
  test('is defined', () => {
    const el = document.createElement('coverage-file-view');
    assert.instanceOf(el, CoverageFileView);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <coverage-file-view></coverage-file-view>
    `)) as CoverageFileView;

    assert.isNotNull(el.querySelector('div#editor-panel'),
        el.outerHTML);
  });
});
