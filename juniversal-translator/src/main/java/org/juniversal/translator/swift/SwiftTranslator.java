/*
 * Copyright (c) 2012-2015, Microsoft Mobile
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.juniversal.translator.swift;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.juniversal.translator.core.*;

import java.io.*;

public class SwiftTranslator extends Translator {
    @Override public void translateFile(SourceFile sourceFile) {
        CompilationUnit compilationUnit = sourceFile.getCompilationUnit();
        TypeDeclaration mainTypeDeclaration = ASTUtil.getFirstTypeDeclaration(compilationUnit);

        String typeName = mainTypeDeclaration.getName().getIdentifier();

        String fileName = typeName + ".cs";
        File file = new File(getOutputDirectory(), fileName);

        try (FileWriter writer = new FileWriter(file)) {
            SwiftSourceFileWriter swiftSourceFileWriter = new SwiftSourceFileWriter(this, sourceFile, writer);

            swiftSourceFileWriter.writeRootNode(compilationUnit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public String translateNode(SourceFile sourceFile, ASTNode astNode) {
        try (StringWriter writer = new StringWriter()) {
            SwiftSourceFileWriter swiftSourceFileWriter = new SwiftSourceFileWriter(this, sourceFile, writer);

            // Set the type declaration part of the context
            AbstractTypeDeclaration typeDeclaration = (AbstractTypeDeclaration) sourceFile.getCompilationUnit().types().get(0);
            swiftSourceFileWriter.getContext().setTypeDeclaration(typeDeclaration);

            swiftSourceFileWriter.writeRootNode(astNode);

            return writer.getBuffer().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
