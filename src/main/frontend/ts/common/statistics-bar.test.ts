/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

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

    assert.isNotNull(el.querySelector('.metrics-stats'),
        el.outerHTML);
  });

  test('statistics', async () => {
    const statistics = {
      min: 0.1,
      max: 0.9,
      average: 0.5,
    };

    const evaluation = {
      available: true,
      ratio: 0.4,
    };

    const el = (await fixture(html`
      <statistics-bar class="percent show_scale"
        statistics='${JSON.stringify(statistics)}'
        evaluation='${JSON.stringify(evaluation)}' >
      </statistics-bar>
    `)) as StatisticsBar;

    let elChild = el.querySelectorAll('div.label')[0];
    assert.include(elChild.textContent, 'Low:');
    assert.include(elChild.textContent, '10%');

    elChild = el.querySelectorAll('div.label')[1];
    assert.include(elChild.textContent, 'Average:');
    assert.include(elChild.textContent, '50%');

    elChild = el.querySelectorAll('div.label')[2];
    assert.include(elChild.textContent, 'High:');
    assert.include(elChild.textContent, '90%');

    assert.isNotNull(el.querySelector('div.pointer.scale'));
  });

  test('statistics-not available', async () => {
    const statistics = {
      min: 0.1,
      max: 0.9,
      average: 0.5,
      percent: true,
      available: false,
      scale: 0.4,
    };

    const el = (await fixture(html`
      <statistics-bar statistics='${JSON.stringify(statistics)}'>
      </statistics-bar>
    `)) as StatisticsBar;

    assert.isNull(el.querySelector('div.pointer.scale'));
  });
});
