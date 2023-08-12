/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.repository.composer.internal;

import java.util.Objects;

import org.sonatype.nexus.repository.config.WritePolicy;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.repository.storage.WritePolicySelector;

import static org.sonatype.nexus.repository.config.WritePolicy.ALLOW;
import static org.sonatype.nexus.repository.config.WritePolicy.ALLOW_ONCE;
import static org.sonatype.nexus.repository.storage.AssetEntityAdapter.P_ASSET_KIND;

public class ComposerWritePolicySelector implements WritePolicySelector {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ComposerWritePolicySelector.class);

  @Override
  public WritePolicy select(Asset asset, WritePolicy writePolicy) {
    log.info("XXX STEF XXX ComposerWritePolicySelector.select: asset={}, writePolicy={}", asset, writePolicy);
    if (ALLOW_ONCE == writePolicy) {
      final String assetKind = asset.formatAttributes().get(P_ASSET_KIND, String.class);
      log.info("XXX STEF XXX ComposerWritePolicySelector.select: assetKind={}", assetKind);
      if (!Objects.equals(AssetKind.ZIPBALL.name(), assetKind)) {
        return ALLOW;
      }
    }
    return writePolicy;
  }

}
