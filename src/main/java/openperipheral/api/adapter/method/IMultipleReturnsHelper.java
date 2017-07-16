package openperipheral.api.adapter.method;

import openperipheral.api.IApiInterface;

public interface IMultipleReturnsHelper extends IApiInterface {

	public interface IReturnTuple2<T1, T2> extends IReturnTuple {}

	public <T1, T2> IReturnTuple2<T1, T2> wrap(T1 arg1, T2 arg2);

	public interface IReturnTuple3<T1, T2, T3> extends IReturnTuple {}

	public <T1, T2, T3> IReturnTuple3<T1, T2, T3> wrap(T1 arg1, T2 arg2, T3 arg3);

	public interface IReturnTuple4<T1, T2, T3, T4> extends IReturnTuple {}

	public <T1, T2, T3, T4> IReturnTuple4<T1, T2, T3, T4> wrap(T1 arg1, T2 arg2, T3 arg3, T4 arg4);

	public interface IReturnTuple5<T1, T2, T3, T4, T5> extends IReturnTuple {}

	public <T1, T2, T3, T4, T5> IReturnTuple5<T1, T2, T3, T4, T5> wrap(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5);
}
