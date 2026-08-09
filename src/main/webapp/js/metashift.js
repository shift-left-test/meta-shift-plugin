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

  /* The trend model colors series with Jenkins theme variable names ("--blue"), which echarts
     cannot resolve itself. Reading them from the document keeps the chart in step with the
     active theme instead of hardcoding hex values that only work in one of them. */
  function trendOption(model) {
    var themeStyle = getComputedStyle(document.documentElement);
    var series = (model.series || []).map(function (s) {
      var name = s.itemStyle && s.itemStyle.color;
      var color = typeof name === 'string' && name.indexOf('--') === 0
        ? themeStyle.getPropertyValue(name).trim() || undefined
        : name;
      return {
        name: s.name,
        type: 'line',
        symbolSize: 7,
        lineStyle: {color: color, width: 2},
        itemStyle: {color: color},
        data: s.data
      };
    });
    return {
      tooltip: {trigger: 'axis', valueFormatter: function (v) {
        return v === null || v === undefined ? 'N/A' : v + '%';
      }},
      legend: {top: 0, type: 'scroll'},
      grid: {left: 48, right: 24, top: 40, bottom: 32},
      xAxis: {type: 'category', boundaryGap: false, data: model.domainAxisLabels || []},
      yAxis: {type: 'value', min: 0, max: 100, axisLabel: {formatter: '{value}%'}},
      series: series
    };
  }

  /* Versioned: a stored size outranks the markup default forever, so changing that default has
     to invalidate sizes saved against the old one. */
  var TREND_SIZE_KEY = 'metashift-trend-size-v2:';

  /* Restores a size the reader previously dragged this chart to. Scoped by pathname so one job's
     preference does not leak into another's. */
  function rememberSize(el) {
    var storageKey = TREND_SIZE_KEY + window.location.pathname;
    var pending = null;
    /* ResizeObserver fires once for the initial observation. Treating that as a drag would save
       the markup default as if the reader had chosen it, which then outranks every later change
       to that default. Only sizes seen after the first callback are the reader's. */
    var dragged = false;
    try {
      var saved = JSON.parse(window.localStorage.getItem(storageKey));
      if (saved && saved.w && saved.h) {
        el.style.width = saved.w + 'px';
        el.style.height = saved.h + 'px';
      }
    } catch (e) {
      /* no stored preference, or storage unavailable: keep the markup default */
    }
    return function () {
      if (!dragged) {
        dragged = true;
        return;
      }
      /* A drag fires the observer once per frame and localStorage writes are synchronous, so
         collapse the burst into one write. */
      window.clearTimeout(pending);
      pending = window.setTimeout(function () {
        try {
          window.localStorage.setItem(storageKey,
            JSON.stringify({w: el.clientWidth, h: el.clientHeight}));
        } catch (e) {
          /* storage unavailable: the chart still resizes, the size is just not remembered */
        }
      }, 250);
    };
  }

  /* Trend line chart on the job page widget and the trend page. The model is fetched through the
     Stapler proxy the trend-chart taglib bound, so reading it never blocks the page render.
     data-ms-trend-links, when present, is the action url name used for click-through. */
  function initTrends() {
    var elements = document.querySelectorAll('[data-ms-trend]');
    for (var i = 0; i < elements.length; i++) {
      renderTrend(elements[i]);
    }
  }

  function renderTrend(el) {
    var proxy = window['msTrendProxy' + el.getAttribute('data-ms-trend')];
    if (!proxy) {
      return;
    }
    var onResize = el.getAttribute('data-ms-resizable') ? rememberSize(el) : null;
    proxy.getConfigurableBuildTrendModel('{}', function (response) {
      /* The bound method returns the model as a JSON string, so the response carries it in
         responseJSON and it still has to be parsed - the same handling echarts-api uses for
         this exact call. Reading it as an object yields a string whose .series is undefined,
         which silently renders nothing. */
      var model = JSON.parse(response.responseJSON);
      if (!model || !model.series || model.series.length === 0) {
        return;
      }
      var chart = echarts.init(el);
      chart.setOption(trendOption(model));
      observeSize(el, function () {
        chart.resize();
        if (onResize) {
          onResize();
        }
      });
      var urlName = el.getAttribute('data-ms-trend-links');
      if (urlName) {
        /* Builds live one level down from the job page, where this widget renders. */
        chart.on('click', function (params) {
          var build = (model.buildNumbers || [])[params.dataIndex];
          if (build) {
            window.location.assign(build + '/' + urlName);
          }
        });
      }
    });
  }

  function init() {
    if (typeof echarts !== 'undefined') {
      initDonuts();
      initTrends();
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
