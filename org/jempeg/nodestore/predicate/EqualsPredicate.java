package org.jempeg.nodestore.predicate;

import org.jempeg.nodestore.IFIDNode;

public class EqualsPredicate extends AbstractPredicate {
  public EqualsPredicate(IPredicate _leftValue, IPredicate _rightValue) {
    super(_leftValue, _rightValue);
  }
  
  public String getValue(IFIDNode _node) {
  	return String.valueOf(evaluate(_node));
  }
  
  public boolean evaluate(IFIDNode _node) {
    boolean equals = getLeftValue(_node).equals(getRightValue(_node));
    return equals;
  }

  protected String operandToString() {
    return "=";
  }
}
