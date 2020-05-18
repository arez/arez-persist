package com.example.persist;

import arez.Arez;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("arez.persist.processor.ArezPersistProcessor")
abstract class CustomStorePersistModel_PersistSidecar {
  private static final class Keys {
    @Nonnull
    private static final String TYPE = Arez.areNamesEnabled() ? "CustomStorePersistModel" : CustomStorePersistModel.class.getName();

    @Nonnull
    private static final String PROPERTY_getValue = Arez.areNamesEnabled() ? "getValue" : "a";
  }
}
