/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {elementUpdated, fixture, html} from '@open-wc/testing';
import {MutationTestList} from './mutation-test-list';

import {assert} from 'chai';

suite('mutation-test-list', () => {
  test('is defined', () => {
    const el = document.createElement('mutation-test-list');
    assert.instanceOf(el, MutationTestList);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <mutation-test-list></mutation-test-list>
    `)) as MutationTestList;

    assert.isNotNull(el.querySelector('.paged-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <mutation-test-list></mutation-test-list>
    `)) as MutationTestList;

    const model = {
      responseJSON: [
        {
          name: 'test',
          linesOfCode: 100,
          total: 10,
          first: 3,
          second: 2,
          third: 1,
          ratio: 0.5,
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
        'div.tabulator-cell[tabulator-field="first"]');
    assert.include(elChild.textContent, '3',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="second"]');
    assert.include(elChild.textContent, '2',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="third"]');
    assert.include(elChild.textContent, '1',
        elChild.outerHTML);
  });
});
