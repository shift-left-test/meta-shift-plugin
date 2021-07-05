import {elementUpdated, fixture, html} from '@open-wc/testing';
import {PremirrorCacheList} from './premirror-cache-list';

import {assert} from 'chai';

suite('premirror-cache-list', () => {
  test('is defined', () => {
    const el = document.createElement('premirror-cache-list');
    assert.instanceOf(el, PremirrorCacheList);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <premirror-cache-list></premirror-cache-list>
    `)) as PremirrorCacheList;

    assert.isNotNull(el.querySelector('.files-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <premirror-cache-list></premirror-cache-list>
    `)) as PremirrorCacheList;

    const model = {
      responseJSON: [
        {
          signature: 'test',
          available: true,
        },
      ],
    };

    el.setAjaxFunc(( callback) => {
      callback(model);
    });

    await elementUpdated(el);
    let elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="signature"]');
    assert.equal(elChild.textContent, 'test',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="available"]');
    assert.equal(elChild.textContent, 'true',
        elChild.outerHTML);
  });
});
