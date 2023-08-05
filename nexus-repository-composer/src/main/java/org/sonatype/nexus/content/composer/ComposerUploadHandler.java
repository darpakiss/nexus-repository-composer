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
package org.sonatype.nexus.content.composer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.importtask.ImportFileConfiguration;
import org.sonatype.nexus.repository.composer.ComposerUploadHandlerSupport;
import org.sonatype.nexus.repository.composer.internal.ComposerFormat;
import org.sonatype.nexus.repository.rest.UploadDefinitionExtension;
import org.sonatype.nexus.repository.security.ContentPermissionChecker;
import org.sonatype.nexus.repository.security.VariableResolverAdapter;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.PartPayload;
import org.sonatype.nexus.repository.view.payloads.TempBlob;
import org.sonatype.nexus.repository.view.payloads.TempBlobPayload;

import com.google.common.collect.Lists;

/**
 * Support for uploading composer components via UI & API
 *
 * @since 3.24
 */
@Named(ComposerFormat.NAME)
@Singleton
public class ComposerUploadHandler
    extends ComposerUploadHandlerSupport
{
  @Inject
  public ComposerUploadHandler(final ContentPermissionChecker contentPermissionChecker,
                          @Named("simple") final VariableResolverAdapter variableResolverAdapter,
                          final Set<UploadDefinitionExtension> uploadDefinitionExtensions)
  {
    super(contentPermissionChecker, variableResolverAdapter, uploadDefinitionExtensions, true);
  }

  @Override
  protected List<Content> getResponseContents(final Repository repository, final Map<String, PartPayload> pathToPayload)
      throws IOException
  {
    ComposerContentFacet facet = repository.facet(ComposerContentFacet.class);

    List<Content> responseContents = Lists.newArrayList();
    for (Entry<String,PartPayload> entry : pathToPayload.entrySet()) {
      String path = entry.getKey();

      Content content = facet.put(path, entry.getValue());

      responseContents.add(content);
    }
    return responseContents;
  }

  @Override
  protected Content doPut(final ImportFileConfiguration configuration) throws IOException {
    Repository repository = configuration.getRepository();
    String path = configuration.getAssetName();
    Path contentPath = configuration.getFile().toPath();

    ComposerContentFacet contentFacet = repository.facet(ComposerContentFacet.class);
    String contentType = Files.probeContentType(contentPath);
    try (TempBlob blob = contentFacet.blobs().ingest(contentPath, contentType, ComposerContentFacet.HASHING,
        configuration.isHardLinkingEnabled())) {
      return contentFacet.put(path, new TempBlobPayload(blob, contentType));
    }
  }

  @Override
  protected String normalizePath(final String path) {
    return "/" + super.normalizePath(path);
  }
}