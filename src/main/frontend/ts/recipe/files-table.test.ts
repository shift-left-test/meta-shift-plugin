/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {fixture, html} from '@open-wc/testing';
import {FilesTable} from './files-table';

import {assert} from 'chai';

suite('files-table', () => {
  test('is defined', () => {
    const el = document.createElement('files-table');
    assert.instanceOf(el, FilesTable);
  });

  test('create', async () => {
    const el = (await fixture(html`
        <files-table></files-table>`
    )) as FilesTable;

    assert.isNotNull(el.querySelector('.paged-table'),
        el.outerHTML);
  });
});
