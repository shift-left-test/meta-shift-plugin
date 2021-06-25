/*
 * MIT License
 *
 * Copyright (c) 2021 LG Electronics, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.lge.plugins.metashift.ui.models;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.sf.json.JSONObject;
import org.junit.Test;

public class SortableItemListTest {

  static class FakeSortableItemList extends SortableItemList<Integer> {

    public FakeSortableItemList(List<Integer> items) {
      super(items);
    }

    protected Comparator<Integer> createComparator(SortableItemList.SortInfo sortInfo) {
      String field = sortInfo.getField();

      if (!field.equals("value")) {
        throw new IllegalArgumentException();
      }
      Comparator<Integer> comparator = Comparator.comparingInt(a -> a);

      return sortInfo.getDir().equals("desc") ? comparator.reversed() : comparator;
    }
  }

  @Test
  public void testInitData() {
    FakeSortableItemList itemList = new FakeSortableItemList(null);

    assertNull(itemList.getItems());
  }

  @Test
  public void testSort() {
    FakeSortableItemList itemList = new FakeSortableItemList(Arrays.asList(2, 3, 1));

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("asc", "value")});

    assertArrayEquals(new Integer[]{1, 2, 3}, itemList.getItems().toArray());

    itemList.sort(new SortableItemList.SortInfo[]{
        new SortableItemList.SortInfo("desc", "value")});

    assertArrayEquals(new Integer[]{3, 2, 1}, itemList.getItems().toArray());
  }

  @Test
  public void testGetPage() {
    FakeSortableItemList itemList = new FakeSortableItemList(Arrays.asList(2, 3, 1));

    assertEquals(JSONObject.fromObject("{\"data\":[2, 3],\"last_page\":2}"),
        itemList.getPage(1, 2));
    assertEquals(JSONObject.fromObject("{\"data\":[1],\"last_page\":2}"),
        itemList.getPage(2, 2));
  }

  @Test
  public void testGetPageOutOfIndex() {
    FakeSortableItemList itemList = new FakeSortableItemList(Arrays.asList(2, 3, 1));

    assertEquals(JSONObject.fromObject("{\"data\":[2,3],\"last_page\":2}"),
        itemList.getPage(0, 2));
    assertEquals(JSONObject.fromObject("{\"data\":[1],\"last_page\":2}"),
        itemList.getPage(3, 2));
  }
}
