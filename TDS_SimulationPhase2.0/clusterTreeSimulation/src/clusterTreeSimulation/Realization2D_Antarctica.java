package clusterTreeSimulation;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

public class Realization2D_Antarctica 
{
	public Realization2D_Antarctica(int a,int b)
	{
		height = a;
		width = b;
		realization = new int[height][width];
		
		int i=0,j=0;
		for(i=0;i!=height;++i)
		{
			for(j=0;j!=width;++j)
			{
				realization[i][j] = -1;
			}
		}
		
		return;
	}
	public void ReadRealizationFile(String fileName)
	{
		int value = 0, PointY=0,PointX=0;
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
	            realization[PointY][PointX] = value;
	            ++PointX;
	            if(PointX==width)
	            {
	            	++PointY;
	            	PointX = 0;
	            }
            }
        }
        System.out.println(new String("load the realization"));
		return;
	}
	public int GetValue(int PositionY,int PositionX)
	{
		return realization[PositionY][PositionX];
	}
	public void SetValue(int PositionY,int PositionX,int value)
	{
		realization[PositionY][PositionX] = value;
		return;
	}
	public int[][] GetRealizationMatrix()
	{
		return realization;
	}
	public void SetRealizationMatrix(int[][] object)
	{
		int y=0,x=0,value=0;
		for(y=0;y!=height;++y)
		{
			for(x=0;x!=width;++x)
			{
				value = object[y][x];
				realization[y][x] = value;
			}
		}
		return;
	}
	public int GetUnknownAmount(int unknownBound)
	{
		int y=0,x=0,amount=0;
		for(y=0;y!=height;++y)
		{
			for(x=0;x!=width;++x)
			{
				if(realization[y][x]<unknownBound)
					++amount;
			}
		}
		return amount;
	}
	public int GetRangeAmount(int min,int max)
	{
		int y=0,x=0,amount=0;
		for(y=0;y!=height;++y)
		{
			for(x=0;x!=width;++x)
			{
				if(realization[y][x]<max && realization[y][x]>min)
					++amount;
			}
		}
		return amount;
	}
	public PatternDS GetPattern_DS(int PositionY,int PositionX,int MaxSize,int MaxRadius, int unknownBound)
	{
		PatternDS pattern = new PatternDS();
		
		int x=0,y=0;
		int pixel = 0;
		int circle = 1;
		while(true)
		{
			// the top row
			y = -circle;
			for(x=-circle;x!=circle+1;++x)
			{
				if(PositionY+y<0 || PositionY+y>=height || PositionX+x<0 || PositionX+x>=width)
				{
					continue;
				}
				else
				{
					pixel = realization[PositionY+y][PositionX+x];
					if(pixel < unknownBound)
					{
						continue;
					}
					else
					{
						pattern.AddElement(pixel, y, x);
						if(pattern.GetSize()==MaxSize)
							return pattern;
					}
				}
			}
			// the right column
			x = circle;
			for(y=1-circle;y!=circle+1;++y)
			{
				if(PositionY+y<0 || PositionY+y>=height || PositionX+x<0 || PositionX+x>=width)
				{
					continue;
				}
				else
				{
					pixel =realization[PositionY+y][PositionX+x];
					if(pixel < unknownBound)
					{
						continue;
					}
					else
					{
						pattern.AddElement(pixel, y, x);
						if(pattern.GetSize()==MaxSize)
							return pattern;
					}
				}
			}
			// the bottom row
			y = circle;
			for (x=circle-1;x!=0-circle-1; --x)
			{
				if(PositionY+y<0 || PositionY+y>=height || PositionX+x<0 || PositionX+x>=width)
				{
					continue;
				} 
				else
				{
					pixel = realization[PositionY+y][PositionX+x];
					if(pixel < unknownBound)
					{
						continue;
					}
					else
					{
						pattern.AddElement(pixel, y, x);
						if(pattern.GetSize()==MaxSize)
							return pattern;
					}
				}
			}
			// the left column
			x = -circle;
			for(y=circle-1;y!=0-circle;--y)
			{
				if(PositionY+y<0 || PositionY+y>=height || PositionX+x<0 || PositionX+x>=width)
				{
					continue;
				}
				else
				{
					pixel = realization[PositionY+y][PositionX+x];
					if(pixel < unknownBound)
					{
						continue;
					}
					else
					{
						pattern.AddElement(pixel, y, x);
						if(pattern.GetSize()==MaxSize)
							return pattern;
					}
				}
			}
			if(circle == MaxRadius)
				break;
			else
				++circle;
		}
		
		return pattern;
	}
	public double GetPatternDistance_Euclidean(int PositionY,int PositionX, PatternDS pattern, double unknownBound,double maxElementDistance)
	{
		int index = 0,y=0,x=0;
		int Neighborhood = pattern.GetSize();
		double distance = 0.0;
		double conditioningElement = 0.0, trainingElement = 0.0;
		for(index=0;index!=Neighborhood;++index)
		{
			y = PositionY + pattern.GetCoordinateY(index);
			x = PositionX + pattern.GetCoordinateX(index);
			
			if(y<0 || y>=height || x<0 || x>=width)
			{
				distance = distance + maxElementDistance*maxElementDistance;
			}
			else
			{
				trainingElement = realization[y][x];
				if(trainingElement < unknownBound)
				{
					distance = distance + maxElementDistance*maxElementDistance;
				}
				else
				{
					conditioningElement = pattern.GetPatternValue(index);
					distance = distance + (trainingElement - conditioningElement) * (trainingElement - conditioningElement);
				}
			}
		}
		distance = distance / (double)(Neighborhood);
		distance = Math.sqrt(distance);
		distance = distance / maxElementDistance;
		return distance;
	}
	public void PrintOut_Color(String str)
	{
		int y=0,x=0;
		int R =0, G=0, B=0, RGB=0;
		
		BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);		
		
		double max = 0.0;
		double pixel = 0.0;
		for(y=0;y!=height;++y)
		{
			for(x=0;x!=width;++x)
			{
				if(realization[y][x] > max)
				{
					max = (double)(realization[y][x]);
				}
			}
		}
		//System.out.println("The maximum element is "+max);
		for(y=0;y!=height;++y)
		{
			for(x=0;x!=width;++x)
			{
				pixel=realization[y][x];
				pixel = pixel / max;
				R = (int)(pixel * 255.0);
				G = 0;
				B = (int)(255.0 - pixel*255.0);
				RGB = R<<16 | G<<8 | B;
				image.setRGB(x, y,RGB);
			}
		}
		str = str+".bmp";
		File f = new File(str);
		
		try {
			ImageIO.write(image,"bmp",f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	public void PrintOut_txt(String str)
	{
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(str));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//output to the file a line
		int y=0,x=0,value=0;
		for(y=0;y!=height;++y)
		{
			for(x=0;x!=width;++x)
			{
				value = realization[y][x];
				out.println(value);
			}
		}

		//close the file (VERY IMPORTANT!)
		out.close();
		return;
	}
	private final int height;
	private final int width;
	private int[][] realization;
}
