/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.Streamable;

/**
 * Counter interface.
 *
 * @author Sung Gon Kim
 */
public interface Counter extends Collector<Streamable, Distribution> {

}
