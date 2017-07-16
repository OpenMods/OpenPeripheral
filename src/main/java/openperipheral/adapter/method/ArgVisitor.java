package openperipheral.adapter.method;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import java.util.Iterator;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.Env;
import openperipheral.api.adapter.method.Optionals;

public abstract class ArgVisitor {

	private static enum ArgParseState {
		ENV_MANDATORY,
		ENV_OPTIONAL,
		ARG_REQUIRED,
		ARG_OPTIONAL,

	}

	public void visitArgs(Iterable<ArgWrapper> args, boolean isMethodVarArg) {
		ArgParseState state = ArgParseState.ENV_MANDATORY;

		int argIndex = 0;
		final Iterator<ArgWrapper> it = args.iterator();
		while (it.hasNext()) {
			try {
				final ArgWrapper arg = it.next();

				boolean optionalStart = arg.annotations.get(Optionals.class) != null;

				Env envArg = arg.annotations.get(Env.class);
				Arg luaArg = arg.annotations.get(Arg.class);

				Preconditions.checkState(envArg == null || luaArg == null, "@Arg and @Env are mutually exclusive");
				if (luaArg != null) {

					if (state != ArgParseState.ARG_OPTIONAL) state = ArgParseState.ARG_REQUIRED;

					if (optionalStart) {
						Preconditions.checkState(state != ArgParseState.ENV_OPTIONAL, "@Optional used more than once");
						state = ArgParseState.ARG_OPTIONAL;
					}

					boolean isLastArg = !it.hasNext();

					ArgumentBuilder builder = new ArgumentBuilder();
					builder.setVararg(isLastArg && isMethodVarArg);
					builder.setOptional(state == ArgParseState.ARG_OPTIONAL);
					builder.setNullable(luaArg.nullable());

					final Argument wrappedArg = builder.build(luaArg.name(), luaArg.description(), arg.type, argIndex);
					visitScriptArg(argIndex, wrappedArg);
				} else {
					Preconditions.checkState(state == ArgParseState.ENV_OPTIONAL || state == ArgParseState.ENV_MANDATORY, "Unannotated arg in script part (perhaps missing @Arg annotation?)");
					Preconditions.checkState(!optionalStart, "@Optionals does not work for env arguments");

					if (envArg != null) {
						Preconditions.checkState(state == ArgParseState.ENV_OPTIONAL || state == ArgParseState.ENV_MANDATORY, "@Env annotation used in script part of arguments");
						visitEnvArg(argIndex, arg.type);
						state = ArgParseState.ENV_OPTIONAL;
					} else {
						Preconditions.checkState(state == ArgParseState.ENV_MANDATORY, "Unnamed env cannot occur after named");
						visitUnnamedArg(argIndex, arg.type);
					}
				}
			} catch (Throwable t) {
				throw new ArgumentDefinitionException(argIndex, t);
			}

			argIndex++;
		}
	}

	protected abstract void visitScriptArg(int argIndex, Argument arg);

	protected abstract void visitUnnamedArg(int argIndex, TypeToken<?> type);

	protected abstract void visitEnvArg(int argIndex, TypeToken<?> type);

}
