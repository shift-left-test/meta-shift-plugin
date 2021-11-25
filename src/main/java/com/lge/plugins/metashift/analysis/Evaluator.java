/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.Evaluation;
import com.lge.plugins.metashift.models.Streamable;

/**
 * Evaluator interface.
 *
 * @author Sung Gon Kim
 */
public interface Evaluator extends Collector<Streamable, Evaluation> {

}
