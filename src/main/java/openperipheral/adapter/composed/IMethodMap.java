package openperipheral.adapter.composed;

import openperipheral.adapter.IMethodExecutor;

public interface IMethodMap {

	public interface IMethodVisitor {
		public void visit(String name, IMethodExecutor executor);
	}

	public boolean isEmpty();

	public void visitMethods(IMethodVisitor visitor);

}
