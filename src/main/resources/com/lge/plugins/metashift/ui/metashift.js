/*
 * Copyright (c) 2026 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

/*
 * Client-side behavior for meta-shift report views, loaded as a Stapler adjunct. Jenkins
 * enforces a Content-Security-Policy of "script-src 'self'" on plugin views, which blocks
 * inline <script> blocks and inline event handlers, so all JavaScript lives here. Chart data
 * is passed from Jelly via data-* attributes.
 */
(function () {
  'use strict';

  /* Bootstrap 5.3 status colors, matching the bg-success/bg-danger/bg-warning/bg-secondary
     badges rendered next to these donuts, so the chart and the badges agree. */
  var COLOR = {
    Passed: '#198754',
    Killed: '#198754',
    Failed: '#dc3545',
    Survived: '#dc3545',
    Error: '#ffc107',
    Skipped: '#6c757d'
  };

  function readJson(el, attr) {
    var raw = el.getAttribute(attr);
    if (!raw) {
      return null;
    }
    return JSON.parse(raw);
  }

  /* Runs onResize whenever the element's size changes, covering both window resizes and a
     container that changes size after tab switches (echarts renders 0x0 into a hidden tab
     pane, so the chart must be told to resize once its pane becomes visible). */
  function observeSize(el, onResize) {
    if (typeof ResizeObserver === 'undefined') {
      window.addEventListener('resize', onResize);
      return;
    }
    new ResizeObserver(onResize).observe(el);
  }

  function initChart(el, option) {
    var chart = echarts.init(el);
    chart.setOption(option);
    observeSize(el, function () {
      chart.resize();
    });
    return chart;
  }

  function withKnownColors(data) {
    return data.map(function (d) {
      var color = COLOR[d.name];
      return color ? {name: d.name, value: d.value, itemStyle: {color: color}} : d;
    });
  }

  function donutOption(data) {
    return {
      tooltip: {trigger: 'item', formatter: '{b}: {c} ({d}%)'},
      legend: {orient: 'vertical', right: 10, top: 'center'},
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {borderRadius: 6, borderColor: '#fff', borderWidth: 2},
        label: {show: false},
        emphasis: {label: {show: true, fontSize: 16, fontWeight: 'bold'}},
        data: withKnownColors(data)
      }]
    };
  }

  /* Donut charts on the build/recipe report pages (Overview tab): any element with
     data-ms-donut and a data-dist JSON array of {name, value} entries. */
  function initDonuts() {
    var elements = document.querySelectorAll('[data-ms-donut]');
    for (var i = 0; i < elements.length; i++) {
      var el = elements[i];
      var data = readJson(el, 'data-dist');
      if (data && data.length > 0) {
        initChart(el, donutOption(data));
      }
    }
  }

  function init() {
    if (typeof echarts !== 'undefined') {
      initDonuts();
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
