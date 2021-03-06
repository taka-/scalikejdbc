package com.example.placeholder

import scalikejdbc._
import org.joda.time.{ LocalDate, DateTime }

case class Member(
    id: Int,
    name: String,
    description: Option[String] = None,
    birthday: Option[LocalDate] = None,
    createdAt: DateTime) {

  def save()(implicit session: DBSession = AutoSession): Member = Member.save(this)(session)

  def destroy()(implicit session: DBSession = AutoSession): Unit = Member.delete(this)(session)

}

object Member {

  val tableName = "MEMBER"

  object columnNames {
    val id = "ID"
    val name = "NAME"
    val description = "DESCRIPTION"
    val birthday = "BIRTHDAY"
    val createdAt = "CREATED_AT"
    val all = Seq(id, name, description, birthday, createdAt)
  }

  val * = {
    import columnNames._
    (rs: WrappedResultSet) => Member(
      id = rs.int(id),
      name = rs.string(name),
      description = Option(rs.string(description)),
      birthday = Option(rs.date(birthday)).map(_.toLocalDate),
      createdAt = rs.timestamp(createdAt).toDateTime)
  }

  def find(id: Int)(implicit session: DBSession = AutoSession): Option[Member] = {
    SQL("""SELECT * FROM MEMBER WHERE ID = ?""")
      .bind(id).map(*).single.apply()
  }

  def findAll()(implicit session: DBSession = AutoSession): List[Member] = {
    SQL("""SELECT * FROM MEMBER""").map(*).list.apply()
  }

  def countAll()(implicit session: DBSession = AutoSession): Long = {
    SQL("""SELECT COUNT(1) FROM MEMBER""").map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: String, params: Any*)(implicit session: DBSession = AutoSession): List[Member] = {
    SQL("""SELECT * FROM MEMBER WHERE """ + where)
      .bind(params: _*).map(*).list.apply()
  }

  def countBy(where: String, params: Any*)(implicit session: DBSession = AutoSession): Long = {
    SQL("""SELECT count(1) FROM MEMBER WHERE """ + where)
      .bind(params: _*).map(rs => rs.long(1)).single.apply().get
  }

  def create(
    name: String,
    description: Option[String] = None,
    birthday: Option[LocalDate] = None,
    createdAt: DateTime)(implicit session: DBSession = AutoSession): Member = {
    val generatedKey = SQL("""
      INSERT INTO MEMBER (
        NAME,
        DESCRIPTION,
        BIRTHDAY,
        CREATED_AT
      ) VALUES (
        ?,
        ?,
        ?,
        ?
      )
      """)
      .bind(
        name,
        description,
        birthday,
        createdAt
      ).updateAndReturnGeneratedKey.apply()
    Member(
      id = generatedKey.toInt,
      name = name,
      description = description,
      birthday = birthday,
      createdAt = createdAt)
  }

  def save(m: Member)(implicit session: DBSession = AutoSession): Member = {
    SQL("""
      UPDATE 
        MEMBER
      SET 
        ID = ?,
        NAME = ?,
        DESCRIPTION = ?,
        BIRTHDAY = ?,
        CREATED_AT = ?
      WHERE 
        ID = ?
      """)
      .bind(
        m.id,
        m.name,
        m.description,
        m.birthday,
        m.createdAt,
        m.id
      ).update.apply()
    m
  }

  def delete(m: Member)(implicit session: DBSession = AutoSession): Unit = {
    SQL("""DELETE FROM MEMBER WHERE ID = ?""")
      .bind(m.id).update.apply()
  }

}
