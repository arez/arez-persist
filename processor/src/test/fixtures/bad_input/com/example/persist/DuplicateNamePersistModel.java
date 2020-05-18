package com.example.persist;

import arez.annotations.ArezComponent;
import arez.annotations.Observable;
import arez.persist.Persist;
import arez.persist.PersistType;

@PersistType
@ArezComponent
abstract class DuplicateNamePersistModel
{
  @Observable
  @Persist( name = "X" )
  public abstract int getValue();

  public abstract void setValue( int v );

  @Observable
  @Persist( name = "X" )
  public abstract int getValue2();

  public abstract void setValue2( int v );
}
