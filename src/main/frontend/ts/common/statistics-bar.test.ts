import {fixture, html} from '@open-wc/testing';
import {StatisticsBar} from './statistics-bar';

import {assert} from 'chai';

suite('statistics-bar', () => {
  test('is defined', () => {
    const el = document.createElement('statistics-bar');
    assert.instanceOf(el, StatisticsBar);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <statistics-bar></statistics-bar>
    `)) as StatisticsBar;

    assert.isNotNull(el.querySelector('.test-stats'),
        el.outerHTML);
  });

  test('itemlist', async () => {
    const items = [
      {clazz: 'valid-good', label: 'test', width: 30, count: 100},
      {clazz: 'valid-bad', label: 'test2', width: 70, count: 600},
    ];

    const el = (await fixture(html`
     <statistics-bar items="${JSON.stringify(items)}"><statistics-bar>
    `)) as StatisticsBar;

    let elChild = el.querySelector('div.label.valid-good');
    assert.include(elChild.textContent, 'test 30% (100)',
        elChild.outerHTML);

    elChild = el.querySelector('div.label.valid-bad');
    assert.include(elChild.textContent, 'test2 70% (600)',
        elChild.outerHTML);
  });
});
