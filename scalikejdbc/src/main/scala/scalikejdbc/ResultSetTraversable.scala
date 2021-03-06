package scalikejdbc

/*
 * Copyright 2012 Kazuhiro Sera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

import java.sql.ResultSet

/**
 * [[scala.collection.Traversable]] object which wraps [[java.sql.ResultSet]]
 */
class ResultSetTraversable(rs: ResultSet) extends Traversable[WrappedResultSet] {

  private val cursor: ResultSetCursor = new ResultSetCursor(0)

  /**
   * Applies a function.
   * @param f function
   * @tparam U type
   */
  def foreach[U](f: (WrappedResultSet) => U): Unit = {
    while (rs.next()) {
      cursor.position += 1
      f.apply(new WrappedResultSet(rs, cursor, cursor.position))
    }
  }

}
