/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {elementUpdated, fixture, html} from '@open-wc/testing';
import {CommentFiles} from './comment-files';

import {assert} from 'chai';

suite('comment-files', () => {
  test('is defined', () => {
    const el = document.createElement('comment-files');
    assert.instanceOf(el, CommentFiles);
  });

  test('create', async () => {
    const el = (await fixture(html`
        <comment-files></comment-files>`
    )) as CommentFiles;

    assert.isNotNull(el.querySelector('.paged-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
     <comment-files></comment-files>
    `)) as CommentFiles;

    const model = {
      responseJSON: [
        {
          name: 'test',
          linesOfCode: 20,
          first: 2,
          second: 3,
          ratio: 0.1,
          qualified: true,
        },
      ],
    };

    el.setAjaxFunc((callback) => {
      callback(model);
    });

    await elementUpdated(el);
    let elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="name"]');
    assert.equal(elChild.textContent, 'test',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="ratio"]');
    assert.equal(elChild.textContent, '10%',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="linesOfCode"]');
    assert.include(elChild.textContent, '20',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="first"]');
    assert.include(elChild.textContent, '2',
        elChild.outerHTML);
  });
});
