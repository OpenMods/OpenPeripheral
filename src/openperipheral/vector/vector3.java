package openperipheral.vector;

public class vector3
{
	public float x, y, z;
	
	public vector3()
	{
		x = y = z = 0.f;
	}
	
	public vector3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public vector3(vector3 oth)
	{
		this.x = oth.x;
		this.y = oth.y;
		this.z = oth.z;
	}
	
	public vector3(vector4 vec)
	{
		this.x = vec.x / vec.w;
		this.y = vec.y / vec.w;
		this.z = vec.z / vec.w;
	}
	
	public float dot(vector3 oth)
	{
		return x * oth.x + y * oth.y + z * oth.z;
	}
	
	public float length2()
	{
		return dot(this);
	}
	
	public float length()
	{
		return (float)Math.sqrt(length2());
	}
	
	public void multiple(float num)
	{
		this.x *= num;
		this.y *= num;
		this.z *= num;
	}
}
