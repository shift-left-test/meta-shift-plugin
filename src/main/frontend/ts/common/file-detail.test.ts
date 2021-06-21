import {fixture, html} from '@open-wc/testing';
import {FileDetail} from './file-detail';

import {assert} from 'chai';

suite('file-detail', () => {
  test('is defined', () => {
    const el = document.createElement('file-detail');
    assert.instanceOf(el, FileDetail);
  });

  test('create', async () => {
    const el = (await fixture(html`
        <file-detail></file-detail>`
    )) as FileDetail;

    assert.isNotNull(el.querySelector('#editor-panel'),
        el.outerHTML);
  });
});
