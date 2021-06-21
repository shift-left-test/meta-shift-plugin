import {elementUpdated, fixture, html} from '@open-wc/testing';
import {TestList} from './test-list';

import {assert} from 'chai';

suite('test-list', () => {
  test('is defined', () => {
    const el = document.createElement('test-list');
    assert.instanceOf(el, TestList);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <test-list></test-list>
    `)) as TestList;

    assert.isNotNull(el.querySelector('#files-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <test-list></test-list>
    `)) as TestList;

    const model = {
      responseJSON: {
        last_page: 1,
        data: [
          {
            suite: 'test',
            name: 'test2',
            status: 'PASSED',
            message: 'test3',
          },
        ],
      },
    };

    el.setAjaxFunc((page, size, sorters, callback) => {
      callback(model);
    });

    await elementUpdated(el);
    let elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="suite"]');
    assert.equal(elChild.textContent, 'test',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="name"]');
    assert.equal(elChild.textContent, 'test2',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="status"]');
    assert.include(elChild.textContent, 'PASSED',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="message"]');
    assert.equal(elChild.textContent, 'test3',
        elChild.outerHTML);
  });
});
