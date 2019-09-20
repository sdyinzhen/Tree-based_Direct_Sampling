package clusterTreeSimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AvailableArea3D 
{
	public AvailableArea3D()
	{
		PointX = new ArrayList<Integer>();
		PointY = new ArrayList<Integer>();
		PointZ = new ArrayList<Integer>();
		
		return;
	}
	public void ReadClusterResult(String fileName,int height, int width)
	{
		int value = 0, pointZ=0, pointY=0, pointX=0;
		File file = new File(fileName);
		InputStreamReader reader = null;
		
		try {
			reader = new InputStreamReader(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
        BufferedReader br = new BufferedReader(reader); 
        String line = "";
        while (line != null)
        {
            try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if(line != null)
            {
	            value = (int)(Double.valueOf(line).doubleValue());
	            if(value >= 0)
	            {
	            	PointX.add(pointX);
	            	PointY.add(pointY);
	            	PointZ.add(pointZ);
	            }
	            ++pointX;
	            if(pointX==width)
	            {
	            	++pointY;
	            	pointX = 0;
	            	if(pointY==height)
	            	{
	            		++pointZ;
	            		pointY=0;
	            	}
	            }
            }
        }
		return;
	}
	public int GetSize()
	{
		return PointX.size();
	}
	public int GetPointX(int index)
	{
		return PointX.get(index);
	}
	public int GetPointY(int index)
	{
		return PointY.get(index);
	}
	public int GetPointZ(int index)
	{
		return PointZ.get(index);
	}
	ArrayList<Integer> PointX;
	ArrayList<Integer> PointY;
	ArrayList<Integer> PointZ;
}
