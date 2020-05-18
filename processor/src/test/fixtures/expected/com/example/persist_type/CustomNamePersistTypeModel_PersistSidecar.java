package com.example.persist_type;

import arez.Arez;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("arez.persist.processor.ArezPersistProcessor")
abstract class CustomNamePersistTypeModel_PersistSidecar {
  private static final class Keys {
    @Nonnull
    private static final String TYPE = Arez.areNamesEnabled() ? "cn" : CustomNamePersistTypeModel.class.getName();

    @Nonnull
    private static final String PROPERTY_getValue = Arez.areNamesEnabled() ? "getValue" : "a";
  }
}
