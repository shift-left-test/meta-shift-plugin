/*
 * Copyright (c) 2021 LG Electronics Inc.
 * SPDX-License-Identifier: MIT
 */

package com.lge.plugins.metashift.analysis;

import com.lge.plugins.metashift.models.CommentData;
import com.lge.plugins.metashift.models.Distribution;
import com.lge.plugins.metashift.models.Streamable;

/**
 * CommentCounter class.
 *
 * @author Sung Gon Kim
 */
public class CommentCounter implements Counter {

  @Override
  public Distribution parse(Streamable s) {
    long total = s.objects(CommentData.class).mapToLong(CommentData::getLines).sum();
    long comments = s.objects(CommentData.class).mapToLong(CommentData::getCommentLines).sum();
    long others = total - comments;
    return new Distribution(comments, others);
  }
}
