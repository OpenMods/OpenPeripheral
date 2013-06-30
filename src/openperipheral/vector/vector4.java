package openperipheral.vector;

public class vector4
{
	public float x, y, z, w;
	public vector4(vector3 vec)
	{
		x = vec.x;
		y = vec.y;
		z = vec.z;
		w = 1.f;
	}
	
	public void apply(matrix4x4 mat)
	{
		float x1 = x * mat.matrix[0] + y * mat.matrix[1] + z * mat.matrix[2] + w * mat.matrix[3];
		float y1 = x * mat.matrix[4] + y * mat.matrix[5] + z * mat.matrix[6] + w * mat.matrix[7];
		float z1 = x * mat.matrix[8] + y * mat.matrix[9] + z * mat.matrix[10] + w * mat.matrix[11];
		float w1 = x * mat.matrix[12] + y * mat.matrix[13] + z * mat.matrix[14] + w * mat.matrix[15];
		x = x1; y = y1; z = z1; w = w1;
	}
}
