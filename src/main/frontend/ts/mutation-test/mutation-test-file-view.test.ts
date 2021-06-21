import {fixture, html} from '@open-wc/testing';
import {MutationTestFileView} from './mutation-test-file-view';

import {assert} from 'chai';

suite('mutation-test-file-view', () => {
  test('is defined', () => {
    const el = document.createElement('mutation-test-file-view');
    assert.instanceOf(el, MutationTestFileView);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <mutation-test-file-view></mutation-test-file-view>
    `)) as MutationTestFileView;

    assert.isNotNull(el.querySelector('div#editor-panel'),
        el.outerHTML);
  });
});
