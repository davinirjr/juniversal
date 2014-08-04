package org.juniversal.translator.swift.astwriters;

import org.eclipse.jdt.core.dom.*;
import org.juniversal.translator.core.ASTUtil;
import org.juniversal.translator.core.Context;

import java.util.List;


public class VariableDeclarationWriter extends ASTWriter {
	public VariableDeclarationWriter(ASTWriters astWriters) {
		super(astWriters);
	}

	@Override
	public void write(ASTNode node, Context context) {
		// Variable declaration statements & expressions are quite similar, so we handle them both
		// here together

		if (node instanceof VariableDeclarationStatement) {
			VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;

			writeVariableDeclaration(variableDeclarationStatement.modifiers(), variableDeclarationStatement.getType(),
					variableDeclarationStatement.fragments(), context);
			context.copySpaceAndComments();

			context.matchAndWrite(";");
		} else {
			VariableDeclarationExpression variableDeclarationExpression = (VariableDeclarationExpression) node;

			writeVariableDeclaration(variableDeclarationExpression.modifiers(),
					variableDeclarationExpression.getType(), variableDeclarationExpression.fragments(), context);
		}
	}

	private void writeVariableDeclaration(List<?> modifiers, Type type, List<?> fragments, Context context) {
		// Turn "final" into "const"
		if (ASTUtil.containsFinal(modifiers)) {
			context.write("const");
			context.skipModifiers(modifiers);

			context.copySpaceAndComments();
		}

		// Write the type
		getASTWriters().writeType(type, context, false);

		boolean needStar = false;
		context.setWritingVariableDeclarationNeedingStar(needStar);

		// Write the variable declaration(s)
		boolean first = true;
		for (Object fragment : fragments) {
			VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) fragment;

			context.copySpaceAndComments();
			if (!first) {
				context.matchAndWrite(",");
				context.copySpaceAndComments();
			}
			getASTWriters().writeNode(variableDeclarationFragment, context);

			first = false;
		}

		context.setWritingVariableDeclarationNeedingStar(false);
	}
}
