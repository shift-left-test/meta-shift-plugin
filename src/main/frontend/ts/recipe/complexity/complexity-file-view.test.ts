/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {fixture, html} from '@open-wc/testing';
import {ComplexityFileView} from './complexity-file-view';

import {assert} from 'chai';

suite('complexity-file-view', () => {
  test('is defined', () => {
    const el = document.createElement('complexity-file-view');
    assert.instanceOf(el, ComplexityFileView);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <complexity-file-view filePath='test'></complexity-file-view>
    `)) as ComplexityFileView;

    assert.isNotNull(el.querySelector('div#editor-panel'),
        el.outerHTML);
  });
});
