/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {fixture, html, assert} from '@open-wc/testing';
import {BuildTrendChart} from './build-trend-chart';

suite('build-trend-chart', () => {
  test('is defined', () => {
    const el = document.createElement('build-trend-chart');
    assert.instanceOf(el, BuildTrendChart);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <build-trend-chart></build-trend-chart>
    `)) as BuildTrendChart;

    assert.isNotNull(el.querySelector('#trend-chart'),
        el.outerHTML);
  });

  test('setajaxfunc', async () => {
    const el = (await fixture(html`
      <build-trend-chart></build-trend-chart>
    `)) as BuildTrendChart;

    const legendData = ['test'];
    const xAxisData = ['#1'];
    const seriesData = [
      {data: ['30'], name: 'test', type: 'line', yAxisIndex: 0},
    ];

    el.setAjaxFunc((callback) => {
      callback({
        responseJSON: {
          legend: legendData,
          builds: xAxisData,
          series: seriesData,
        },
      });
    });

    assert.deepInclude(el.getChartOption()['legend'][0], {data: legendData});
    assert.deepInclude(el.getChartOption()['xAxis'][0], {data: xAxisData});
    assert.deepInclude(el.getChartOption()['series'][0], seriesData[0],
        JSON.stringify(el.getChartOption()));
  });
});
