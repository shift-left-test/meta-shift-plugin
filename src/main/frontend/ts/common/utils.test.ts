/**
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

import {assert} from 'chai';
import {Utils} from './utils';

suite('utils', () => {
  test('fraction 2', () => {
    assert.equal(Utils.toFixedFloor(0.123), '0.12');
    assert.equal(Utils.toFixedFloor(0.125), '0.12');
    assert.equal(Utils.toFixedFloor(0.129), '0.12');
  });
  test('fraction 3', () => {
    assert.equal(Utils.toFixedFloor(0.1234, 3), '0.123');
    assert.equal(Utils.toFixedFloor(0.1235, 3), '0.123');
    assert.equal(Utils.toFixedFloor(0.1239, 3), '0.123');

    assert.equal(Utils.toFixedFloor(0.12, 3), '0.120');
  });
});
