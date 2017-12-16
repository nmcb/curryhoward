package io.chymyst.ch.unit

import io.chymyst.ch._
import org.scalatest.{FlatSpec, Matchers}

class TermExprSpec extends FlatSpec with Matchers {

  behavior of "TermExpr#renameVar"

  val termExpr1 = CurriedE(List(PropE("x2", TP(3)), PropE("x3", TP(2)), PropE("x4", TP(1))), PropE("x3", TP(2)))
  val termExpr2 = CurriedE(List(PropE("x2", TP(3)), PropE("x3", TP(2)), PropE("x4", TP(1))), PropE("x1", TP(2)))
  val termExpr3 = CurriedE(List(PropE("x1", TP(2))), termExpr2)

  it should "rename one variable" in {
    termExpr1.renameVar("x2", "y2") shouldEqual CurriedE(List(PropE("y2", TP(3)), PropE("x3", TP(2)), PropE("x4", TP(1))), PropE("x3", TP(2)))
    termExpr1.renameVar("x3", "y3") shouldEqual CurriedE(List(PropE("x2", TP(3)), PropE("y3", TP(2)), PropE("x4", TP(1))), PropE("y3", TP(2)))
    termExpr2.renameVar("x1", "y1") shouldEqual CurriedE(List(PropE("x2", TP(3)), PropE("x3", TP(2)), PropE("x4", TP(1))), PropE("y1", TP(2)))
    termExpr3.renameVar("x1", "y1") shouldEqual CurriedE(List(PropE("y1", TP(2))), CurriedE(List(PropE("x2", TP(3)), PropE("x3", TP(2)), PropE("x4", TP(1))), PropE("y1", TP(2))))
  }

  it should "rename multiple variables" in {
    termExpr1.renameAllVars(Seq("x2", "x3", "x4"), Seq("y2", "y3", "y4")) shouldEqual CurriedE(List(PropE("y2", TP(3)), PropE("y3", TP(2)), PropE("y4", TP(1))), PropE("y3", TP(2)))
  }

  behavior of "TermExpr#freeVars"

  it should "detect free variables" in {
    termExpr1.freeVars shouldEqual Set()
    termExpr2.freeVars shouldEqual Set("x1")
    termExpr3.freeVars shouldEqual Set()
  }

  behavior of "TermExpr#equiv"

  it should "detect equivalent terms" in {
    val termExpr1a = termExpr1.renameAllVars(Seq("x2", "x3", "x4"), Seq("y2", "y3", "y4"))
    TermExpr.equiv(termExpr1a, termExpr1) shouldEqual true
  }

  behavior of "Sequent#constructResultTerm"

  it should "produce result terms in correct order" in {
    val sequent = Sequent(List(TP(1), TP(2), TP(3)), TP(1), new FreshIdents("t"))
    val premiseVars = sequent.premiseVars
    premiseVars shouldEqual List(PropE("t1", TP(1)), PropE("t2", TP(2)), PropE("t3", TP(3)))
    val term = sequent.constructResultTerm(premiseVars.head)
    term shouldEqual CurriedE(List(PropE("t1", TP(1)), PropE("t2", TP(2)), PropE("t3", TP(3))), PropE("t1", TP(1)))
  }
}
