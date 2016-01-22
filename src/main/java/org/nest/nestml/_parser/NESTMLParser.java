/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package org.nest.nestml._parser;

import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.nest.nestml._ast.ASTNESTMLCompilationUnit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * HW parser that also is able
 *
 * @author plotnikov
 */
public class NESTMLParser extends NESTMLParserTOP {

  private final Optional<Path> modelPath;

  public NESTMLParser() {
    modelPath = Optional.empty();
  }

  public NESTMLParser(final Path modelPath) {
    this.modelPath = Optional.of(modelPath);
  }

  @Override
  public Optional<ASTNESTMLCompilationUnit> parseNESTMLCompilationUnit(String filename)
      throws IOException, RecognitionException {
    final Optional<ASTNESTMLCompilationUnit> res = super.parseNESTMLCompilationUnit(filename);
    if (res.isPresent()) {
      if (modelPath.isPresent()) {
        final String packageName = computePackageName(Paths.get(filename), modelPath.get());
        final String artifactName = computeArtifactName(Paths.get(filename));
        res.get().setPackageName(packageName);
        res.get().setArtifactName(artifactName);
      }
      else {
        if (filename.endsWith(".nestml")) {

          final String withoutFileExt = filename.substring(0, filename.indexOf(".nestml"));
          final String filenameAsPackage = Names.getPackageFromPath(withoutFileExt);
          final String packageName = Names.getQualifier(filenameAsPackage);
          final String artifactName = Names.getSimpleName(filenameAsPackage);

          res.get().setPackageName(packageName);
          res.get().setArtifactName(artifactName);
        }
        else {
          Log.warn("Parser doesn't set the package and artifact name.");
        }

      }

    }

    return res;
  }

  protected String computePackageName(final Path artifactPath, final Path modelPath) {

    final String pathAsString = modelPath.relativize(artifactPath).getParent().toString();

    return Names.getPackageFromPath(pathAsString);
  }

  protected String computeArtifactName(final Path artifactPath) {
    final String filename = artifactPath.getFileName().getName(0).toString();
    if (filename.endsWith(".nestml")) {
      return filename.substring(0, filename.indexOf(".nestml"));
    } else {
      return filename;
    }

  }
}