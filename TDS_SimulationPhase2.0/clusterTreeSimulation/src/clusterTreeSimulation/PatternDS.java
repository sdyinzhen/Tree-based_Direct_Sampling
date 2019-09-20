package clusterTreeSimulation;

import java.util.ArrayList;

public class PatternDS
{
	public PatternDS()
	{
		pattern = new ArrayList<Integer>();
		CoordinateY = new ArrayList<Integer>();
		CoordinateX = new ArrayList<Integer>();
		return;
	}
	public PatternDS(PatternDS other)
	{
		int size = other.GetSize();
		int index = 0;
		pattern = new ArrayList<Integer>();
		CoordinateY = new ArrayList<Integer>();
		CoordinateX = new ArrayList<Integer>();
		
		for(index=0;index!=size;++index)
		{
			pattern.add(other.GetPatternValue(index));
			CoordinateY.add(other.GetCoordinateY(index));
			CoordinateX.add(other.GetCoordinateX(index));
		}
		return;
	}
	public void AddElement(int value,int PositionY,int PositionX)
	{
		pattern.add(value);
		CoordinateY.add(PositionY);
		CoordinateX.add(PositionX);
		return;
	}
	public int GetSize()
	{
		return pattern.size();
	}
	public int GetPatternValue(int index)
	{
		return pattern.get(index);
	}
	public int GetCoordinateY(int index)
	{
		return CoordinateY.get(index);
	}
	public int GetCoordinateX(int index)
	{
		return CoordinateX.get(index);
	}
	public int GetKnownPointAmount()
	{
		return pattern.size();
	}
	public String toString()
	{
		String str = "Pattern: " + pattern.toString()+"\n";
		str += "CoordinateY: " + CoordinateY.toString() +"\n";
		str += "CoordinateX: " + CoordinateX.toString() +"\n";
		return str;
	}
	private ArrayList<Integer> pattern;
	private ArrayList<Integer> CoordinateY;
	private ArrayList<Integer> CoordinateX;
}
