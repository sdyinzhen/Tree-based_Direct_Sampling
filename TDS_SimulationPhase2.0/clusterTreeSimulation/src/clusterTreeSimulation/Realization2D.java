package clusterTreeSimulation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Realization2D 
{
	public Realization2D(int a,int b)
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
	public int GetValue(int PositionY,int PositionX)
	{
		return realization[PositionY][PositionX];
	}
	public void SetValue(int PositionY,int PositionX,int value)
	{
		realization[PositionY][PositionX] = value;
		return;
	}
	public void PrintOut_categorical(String str)
	{
		int y=0,x=0,i=0;
		
		BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);		
		
		for(y=0;y!=height;++y)
		{
			for(x=0;x!=width;++x)
			{
				i=realization[y][x];
				if(i==0)
				    image.setRGB(x, y,-16777216);   //black
				else if(i==1)
					image.setRGB(x,y,-1);  // white
				else if(i==2)
					image.setRGB(x,y,-65536);   //red
				else if(i==3)
					image.setRGB(x,y,-16776961);   //blue
				else if(i==4)
					image.setRGB(x,y,-16711936);   //green
				else 
					image.setRGB(x,y,-8355712);   //gray
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
	public PatternDS GetPattern_DS(int PositionY,int PositionX,int MaxSize,int MaxRadius)
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
					if(pixel==-1)
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
					if(pixel==-1)
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
					if(pixel==-1)
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
					if(pixel==-1)
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
	public void MediumFilter()
	{
		ArrayList<Integer> set = new ArrayList<Integer>();
		int[][] temporary = new int[height][width];
		int PointY=0,PointX=0,y=0,x=0,value=0;
		int radius = 1;
		int medium = 0;
		for(PointY=0;PointY<height;++PointY)
		{
			for(PointX=0;PointX<width;++PointX)
			{
				value = realization[PointY][PointX];
				temporary[PointY][PointX] = value;
			}
		}
		for(PointY=0;PointY!=height;++PointY)
		{
			for(PointX=0;PointX!=width;++PointX)
			{
				set.clear();
				for(y=PointY-radius;y!=PointY+radius+1;++y)
				{
					for(x=PointX-radius;x!=PointX+radius+1;++x)
					{
						if(x>=0 && x<width && y>=0 && y<height)
						{
							set.add(temporary[y][x]);
						}
						
					}
				}
				set.sort(null);
				medium = set.size()/2;
				realization[PointY][PointX] = set.get(medium);
			}
		}
		return;
	}
	public void DilateDistance()
	{
		boolean isLoop = true;
		int PointY=0,PointX=0,y=0,x=0,distance=0;
		int[][] temporaryArray = new int[height][width];
		for(PointY=0;PointY!=height;++PointY)
		{
			for(PointX=0;PointX!=width;++PointX)
			{
				distance = realization[PointY][PointX];
				temporaryArray[PointY][PointX] = distance;
			}
		}
		while(isLoop==true)
		{
			for(PointY=0;PointY!=height;++PointY)
			{
				for(PointX=0;PointX!=width;++PointX)
				{
					if(realization[PointY][PointX]!=-1)
					{
						distance = realization[PointY][PointX];
						++distance;
						
						for(y=PointY-1;y!=PointY+2;++y)
						{
							for(x=PointX-1;x!=PointX+2;++x)
							{
								if(y<0 || y>=height || x<0 || x>=width)
								{
									continue;
								}
								if(realization[y][x]==-1)
								{
									temporaryArray[y][x] = distance;
								}
							}
						}
						temporaryArray[PointY][PointX] = distance-1;
					}
				}
			}
			isLoop=false;
			for(PointY=0;PointY!=height;++PointY)
			{
				for(PointX=0;PointX!=width;++PointX)
				{
					distance = temporaryArray[PointY][PointX];
					realization[PointY][PointX] = distance;
					if(distance==-1)
					{
						isLoop=true;
					}
				}
			}
		}
		return;
	}
	public int GetUnknownAmount()
	{
		int y=0,x=0,unknownAmount=0;
		for(y=0;y!=height;++y)
		{
			for(x=0;x!=width;++x)
			{
				if(realization[y][x]==-1)
					++unknownAmount;
			}
		}
		return unknownAmount;
	}
	private final int height;
	private final int width;
	private int[][] realization;
}
