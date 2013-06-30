package openperipheral.vector;

public class matrix4x4
{
	public float[] matrix;
	public matrix4x4()
	{
		matrix = new float[16];
		for(int i = 0; i < 16; i++)
			if((i % 4) == (i / 4))
				matrix[i] = 1.f;
			else
				matrix[i] = 0.f;
	}
	
	public static matrix4x4 multiplication(matrix4x4 a, matrix4x4 b)
	{
		matrix4x4 mat = new matrix4x4();
		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				mat.matrix[i * 4 + j] = 0.f;
				for(int k = 0; k < 4; k++)
					mat.matrix[i * 4 + j] += a.matrix[i * 4 + k] * b.matrix[k * 4 + j];
			}
		}
		return mat;
	}
	
	public static matrix4x4 translation(vector3 vec)
	{
		matrix4x4 mat = new matrix4x4();
		mat.matrix[3] = vec.x;
		mat.matrix[7] = vec.y;
		mat.matrix[11] = vec.z;
		return mat;
	}
	
	public static matrix4x4 rotation(vector3 axis, float angle)
	{
		matrix4x4 mat = new matrix4x4();
		float mag = axis.length();
		if(mag < 0.0001)
			return mat;
		float s = (float)Math.sin(angle);
		float c = (float)Math.cos(angle);
		vector3 v = new vector3(axis);
		v.multiple(1.f / mag);
	    float xx = v.x * v.x;
	    float yy = v.y * v.y;
	    float zz = v.z * v.z;
	    float xy = v.x * v.y;
	    float yz = v.y * v.z;
	    float zx = v.z * v.x;
	    float xs = v.x * s;
	    float ys = v.y * s;
	    float zs = v.z * s;
	    float one_c = 1.f - c;
	    mat.matrix[0] = (one_c * xx) + c;
	    mat.matrix[1] = (one_c * xy) - zs;
	    mat.matrix[2] = (one_c * zx) + ys;
	    mat.matrix[4] = (one_c * xy) + zs;
	    mat.matrix[5] = (one_c * yy) + c;
	    mat.matrix[6] = (one_c * yz) - xs;
	    mat.matrix[8] = (one_c * zx) - ys;
	    mat.matrix[9] = (one_c * yz) + xs;
	    mat.matrix[10] = (one_c * zz) + c;
	    return mat;
	}
}
