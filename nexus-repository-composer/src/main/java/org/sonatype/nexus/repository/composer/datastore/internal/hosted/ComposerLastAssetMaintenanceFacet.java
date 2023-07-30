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
package org.sonatype.nexus.repository.composer.datastore.internal.hosted;

import java.util.Set;

import javax.inject.Named;

import org.sonatype.nexus.repository.composer.datastore.internal.hosted.metadata.ComposerHostedMetadataFacet;
import org.sonatype.nexus.repository.content.Asset;
import org.sonatype.nexus.repository.content.Component;
import org.sonatype.nexus.repository.content.fluent.FluentAsset;
import org.sonatype.nexus.repository.content.maintenance.LastAssetMaintenanceFacet;

/**
 * Composer maintenance facet
 *
 * @since 3.31
 */
@Named
public class ComposerLastAssetMaintenanceFacet
    extends LastAssetMaintenanceFacet
{
  @Override
  public Set<String> deleteAsset(final Asset asset) {
    final Set<String> deleteAssetPaths = super.deleteAsset(asset);
    final FluentAsset fluentAsset = contentFacet().assets().with(asset);
    metadata().removePackageMetadata(fluentAsset);
    metadata().removeInReleaseIndex();
    return deleteAssetPaths;
  }

  @Override
  public Set<String> deleteComponent(final Component component) {
    Set<String> deleteAssetPaths = super.deleteComponent(component);
    metadata().removeInReleaseIndex();
    return deleteAssetPaths;
  }

  private ComposerHostedMetadataFacet metadata() {
    return facet(ComposerHostedMetadataFacet.class);
  }
}
