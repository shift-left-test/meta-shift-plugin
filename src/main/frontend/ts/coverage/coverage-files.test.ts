import {elementUpdated, fixture, html} from '@open-wc/testing';
import {CoverageFiles} from './coverage-files';

import {assert} from 'chai';

suite('coverage-files', () => {
  test('is defined', () => {
    const el = document.createElement('coverage-files');
    assert.instanceOf(el, CoverageFiles);
  });

  test('create', async () => {
    const el = (await fixture(html`
      <coverage-files></coverage-files>
    `)) as CoverageFiles;

    assert.isNotNull(el.querySelector('#files-table'),
        el.outerHTML);
  });

  test('setAjaxFunc', async () => {
    const el = (await fixture(html`
      <coverage-files></coverage-files>
    `)) as CoverageFiles;

    const model = {
      responseJSON: {
        last_page: 1,
        data: [
          {
            file: 'test',
            lineCoverage: 0.5,
            branchCoverage: 0.4,
          },
        ],
      },
    };
    el.setAjaxFunc((page, size, sorters, callback) => {
      callback(model);
    });

    await elementUpdated(el);
    let elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="file"]');
    assert.equal(elChild.textContent, 'test',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="lineCoverage"]');
    assert.equal(elChild.textContent, '50%',
        elChild.outerHTML);

    elChild = el.querySelector(
        'div.tabulator-cell[tabulator-field="branchCoverage');
    assert.equal(elChild.textContent, '40%',
        elChild.outerHTML);
  });
});
