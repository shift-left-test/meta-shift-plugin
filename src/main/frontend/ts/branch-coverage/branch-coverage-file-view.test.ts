import {fixture, html} from '@open-wc/testing';
import {BranchCoverageFileView} from './branch-coverage-file-view';

import {assert} from 'chai';

suite('branch-coverage-file-view', () => {
  test('is defined', () => {
    const el = document.createElement('branch-coverage-file-view');
    assert.instanceOf(el, BranchCoverageFileView);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <branch-coverage-file-view></branch-coverage-file-view>
    `)) as BranchCoverageFileView;

    assert.isNotNull(el.querySelector('div#editor-panel'),
        el.outerHTML);
  });
});
