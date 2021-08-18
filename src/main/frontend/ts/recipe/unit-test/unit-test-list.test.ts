import {elementUpdated, fixture, html} from '@open-wc/testing';
import {UnitTestList} from './unit-test-list';

import {assert} from 'chai';

suite('test-list', () => {
  test('is defined', () => {
    const el = document.createElement('unit-test-list');
    assert.instanceOf(el, UnitTestList);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <unit-test-list></unit-test-list>
    `)) as UnitTestList;

    assert.isNotNull(el.querySelector('.paged-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <unit-test-list></unit-test-list>
    `)) as UnitTestList;

    const model = {
      responseJSON: [
        {
          suite: 'test',
          test: 'test2',
          status: 'PASSED',
          message: 'test3',
        },
      ],
    };

    el.setAjaxFunc((callback) => {
      callback(model);
    });

    await elementUpdated(el);
    let elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="suite"]');
    assert.equal(elChild.textContent, 'test',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="test"]');
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
