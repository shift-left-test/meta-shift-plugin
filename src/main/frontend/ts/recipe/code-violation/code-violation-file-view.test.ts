/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {elementUpdated, fixture, html} from '@open-wc/testing';
import {CodeViolationFileView} from './code-violation-file-view';

import {assert} from 'chai';

suite('code-violation-file-view', () => {
  test('is defined', () => {
    const el = document.createElement('code-violation-file-view');
    assert.instanceOf(el, CodeViolationFileView);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <code-violation-file-view filePath='test'></code-violation-file-view>
    `)) as CodeViolationFileView;

    assert.isNotNull(el.querySelector('div.list-group'),
        el.outerHTML);
  });

  test('setSourceFile', async () => {
    const el = (await fixture(html`
      <code-violation-file-view filePath='test'></code-violation-file-view>
    `)) as CodeViolationFileView;

    el.setSourceFile({
      content: '//test',
      dataList: [{
        line: 1,
        level: 'MAJOR',
        rule: 'test',
        tool: 'test',
        severity: 'error',
        message: 'test message'}],
    });

    // try private member call
    el['updateDataList'](1);
    await elementUpdated(el);

    assert.equal(el.querySelector('div#data-list-panel h3').textContent,
        'Violation List- #1', JSON.stringify(el.outerHTML));
    assert.include(el.querySelector('div.list-item span.badge').textContent,
        'MAJOR');
    assert.include(el.querySelectorAll('div.list-item div')[1].textContent,
        'tool: test');
    assert.include(el.querySelectorAll('div.list-item div')[2].textContent,
        'test message');
  });
});
