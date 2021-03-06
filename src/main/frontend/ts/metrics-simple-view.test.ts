/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {fixture, html} from '@open-wc/testing';
import {MetricsSimpleView} from './metrics-simple-view';

import {assert} from 'chai';

suite('metrics-simple-view', () => {
  test('is defined', () => {
    const el = document.createElement('metrics-simple-view');
    assert.instanceOf(el, MetricsSimpleView);
  });

  test('create', async () => {
    const el = (await fixture(html`
        <metrics-simple-view></metrics-simple-view>`
    )) as MetricsSimpleView;

    assert.isNotNull(el.querySelector('div.board'),
        el.outerHTML);
  });

  test('property', async () => {
    const metricsValue = {
      available: true,
      qualified: true,
      ratio: 0.3333333,
      threshold: 0.4,
    };

    const el = (await fixture(html`
        <metrics-simple-view name="test"
          evaluation='${JSON.stringify(metricsValue)}'
          delta='0.22222'
        ></metrics-simple-view>`
    )) as MetricsSimpleView;

    let elChild = el.querySelector('div.metrics-name');
    assert.include(elChild.textContent, 'test',
        elChild.outerHTML);

    elChild = el.querySelector('div.size-number');
    assert.include(elChild.textContent, '0.33',
        elChild.outerHTML);

    elChild = el.querySelector('div.size-diff');
    assert.include(elChild.textContent, '0.22',
        elChild.outerHTML);
  });

  test('class-percent', async () => {
    const metricsValue = {
      available: true,
      qualified: true,
      ratio: 0.3333333,
      threshold: 0.4,
    };

    const el = (await fixture(html`
        <metrics-simple-view name="test"
          class="percent"
          evaluation='${JSON.stringify(metricsValue)}'
          delta='0.22222'
        ></metrics-simple-view>`
    )) as MetricsSimpleView;

    let elChild = el.querySelector('div.metrics-name');
    assert.include(elChild.textContent, 'test',
        elChild.outerHTML);

    elChild = el.querySelector('div.size-number');
    assert.include(elChild.textContent, '33%',
        elChild.outerHTML);

    elChild = el.querySelector('div.size-diff');
    assert.include(elChild.textContent, '22%',
        elChild.outerHTML);
  });
});
