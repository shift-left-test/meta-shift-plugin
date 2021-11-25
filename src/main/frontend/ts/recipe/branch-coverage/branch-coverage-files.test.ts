/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {elementUpdated, fixture, html} from '@open-wc/testing';
import {BranchCoverageFiles} from './branch-coverage-files';

import {assert} from 'chai';

suite('branch-coverage-files', () => {
  test('is defined', () => {
    const el = document.createElement('branch-coverage-files');
    assert.instanceOf(el, BranchCoverageFiles);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <branch-coverage-files></branch-coverage-files>
    `)) as BranchCoverageFiles;

    assert.isNotNull(el.querySelector('.paged-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <branch-coverage-files></branch-coverage-files>
    `)) as BranchCoverageFiles;

    const model = {
      responseJSON: [
        {
          name: 'test',
          linesOfCode: 100,
          total: 10,
          first: 1,
          second: 2,
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
        'div.tabulator-cell[tabulator-field="ratio"]');
    assert.equal(elChild.textContent, '50%',
        elChild.outerHTML);
  });
});
