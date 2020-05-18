package com.example.persist;

import arez.Arez;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("arez.persist.processor.ArezPersistProcessor")
abstract class CustomNamePersistModel_PersistSidecar {
  private static final class Keys {
    @Nonnull
    private static final String TYPE = Arez.areNamesEnabled() ? "CustomNamePersistModel" : CustomNamePersistModel.class.getName();

    @Nonnull
    private static final String PROPERTY_v = Arez.areNamesEnabled() ? "v" : "a";
  }
}
