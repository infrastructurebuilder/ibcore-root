/*
 * @formatter:off
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * @formatter:on
 */
package org.infrastructurebuilder.util.executor;


import org.infrastructurebuilder.util.executor.model.executor.model.v1_0.GeneratedProcessExecution;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.ToJson;

public class ModeledProcessAdapter {

  public ModeledProcessAdapter() {
    // TODO Auto-generated constructor stub
  }

  @ToJson
  String toJson(GeneratedProcessExecution card) {
    return "";
  }

  @FromJson
  GeneratedProcessExecution fromJson(String card) {
//    if (card.length() != 2)
//      throw new JsonDataException("Unknown : " + card);
//
//    char rank = card.charAt(0);
//    switch (card.charAt(1)) {
//    case 'C':
//      return new Card(rank, Suit.CLUBS);
//    case 'D':
//      return new Card(rank, Suit.DIAMONDS);
//    case 'H':
//      return new Card(rank, Suit.HEARTS);
//    case 'S':
//      return new Card(rank, Suit.SPADES);
//    default:
//      throw new JsonDataException("unknown suit: " + card);
//    }
    return new GeneratedProcessExecution();
  }
}
