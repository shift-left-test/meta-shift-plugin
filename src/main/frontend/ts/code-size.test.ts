import {fixture, html} from '@open-wc/testing';
import {CodeSize} from './code-size';

import {assert} from 'chai';

suite('code-size', () => {
  test('is defined', () => {
    const el = document.createElement('code-size');
    assert.instanceOf(el, CodeSize);
  });

  test('create', async () => {
    const el = (await fixture(html`
        <code-size codeSize='{"recipes":1}' codeSizeDelta='{"recipes":1}'>
        </code-size>`
    )) as CodeSize;

    assert.isNotNull(el.querySelector('div.size-item'),
        el.outerHTML);
  });

  test('property', async () => {
    const codeSize = {
      recipes: 10,
      lines: 20,
      functions: 30,
      classes: 40,
      files: 50,
    };

    const codeSizeDelta = {
      recipes: 100,
      lines: 200,
      functions: 3000,
      classes: 4000,
      files: -5000,
    };

    const el = (await fixture(html`
        <code-size codeSize='${JSON.stringify(codeSize)}'
          codeSizeDelta='${JSON.stringify(codeSizeDelta)}'>
        </code-size>`
    )) as CodeSize;

    let elChild = el.querySelectorAll('div.size-number')[0];
    assert.include(elChild.textContent, '10',
        elChild.outerHTML);
    elChild = el.querySelectorAll('span.size-diff')[0];
    assert.include(elChild.textContent, '100',
        elChild.outerHTML);

    elChild = el.querySelectorAll('div.size-number')[1];
    assert.include(elChild.textContent, '20',
        elChild.outerHTML);
    elChild = el.querySelectorAll('span.size-diff')[1];
    assert.include(elChild.textContent, '200',
        elChild.outerHTML);

    elChild = el.querySelectorAll('div.size-number')[2];
    assert.include(elChild.textContent, '30',
        elChild.outerHTML);
    elChild = el.querySelectorAll('span.size-diff')[2];
    assert.include(elChild.textContent, '3,000',
        elChild.outerHTML);

    elChild = el.querySelectorAll('div.size-number')[3];
    assert.include(elChild.textContent, '40',
        elChild.outerHTML);
    elChild = el.querySelectorAll('span.size-diff')[3];
    assert.include(elChild.textContent, '4,000',
        elChild.outerHTML);

    elChild = el.querySelectorAll('div.size-number')[4];
    assert.include(elChild.textContent, '50',
        elChild.outerHTML);
    elChild = el.querySelectorAll('span.size-diff')[4];
    assert.include(elChild.textContent, '5,000',
        elChild.outerHTML);
  });
});
