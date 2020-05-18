package com.example.persist;

import arez.annotations.ArezComponent;
import arez.persist.Persist;
import arez.persist.PersistType;

@PersistType
@ArezComponent( allowEmpty = true )
public abstract class MissingObservablePersistModel
{
  @Persist
  public abstract int getValue();

  public abstract void setValue( int v );
}
